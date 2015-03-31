package org.eclipse.xtext.idea.build.daemon

import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Provider
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException
import org.apache.log4j.FileAppender
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.log4j.TTCCLayout
import org.eclipse.xtext.idea.build.daemon.Protocol.BuildRequest
import org.eclipse.xtext.idea.build.daemon.Protocol.StopServer

import static org.eclipse.xtext.idea.build.daemon.XtextBuildDaemon.*

class XtextBuildDaemon {

	static val LOG = Logger.getLogger(XtextBuildDaemon)
	
	def static void main(String[] args) {
		try {
			LOG.parent.addAppender(new FileAppender(new TTCCLayout(), 'xtext_builder_daemon.log', true))	
			LOG.level = Level.INFO
			val injector = Guice.createInjector(new BuildDaemonModule)
			injector.getInstance(Server).run(args.parse)	
		} catch(Exception exc) {
			System.err.println('Error ' + exc.message)
		}
	}	
	
	private static def parse(String... args) {
		val arguments = new Arguments
		val i = args.iterator
		while (i.hasNext) {
			val arg = i.next
			switch arg {
				case '-port':
					arguments.port = Integer.parseInt(i.next)
				default:
					throw new IllegalArgumentException("Invalid argument '" + arg + "'")
			}
		}
		if(arguments.port == 0)
			throw new IllegalArgumentException('Port number must be set')
		return arguments
	} 
	
	static class Arguments {
		int port
	}

	static class Server {
		@Inject Provider<Worker> workerProvider
		
		def run(Arguments arguments) {
			var ServerSocket serverSocket = null
			try {
				LOG.info('Starting xtext build daemon at port ' + arguments.port + '...')
				serverSocket = new ServerSocket(arguments.port, 0, InetAddress.getByName('127.0.0.1'))
				serverSocket.soTimeout = 5000
				var receivedRequests = false
				LOG.info('... success')
				var shutdown = false
				while(!shutdown) {
					LOG.info('Accepting connections...')
					try {
						val socket = serverSocket.accept
						receivedRequests = true
						shutdown = workerProvider.get.serve(socket)		
					} catch (SocketTimeoutException exc) {
						if(!receivedRequests) {
							LOG.info('No requests within ' + serverSocket.soTimeout + 'ms.')
							shutdown = true
						}
					}
				} 
			} catch (Exception exc) {
				LOG.error('Error during build', exc)
			}
			LOG.info('Shutting down')
			serverSocket?.close
		}
	}
	
	static class Worker {
		
		@Inject IdeaStandaloneBuilder standaloneBuilder
		
		def serve(Socket socket) {
			val msg = new ObjectInputStream(socket.inputStream).readObject
			switch(msg) {
				StopServer: {
					LOG.info('Received StopServer')					
					return true
				}
				BuildRequest:  {
					LOG.info('Received BuildRequest. Start build...')			
					val buildResult = build(msg)
					LOG.info('...finished.')
					new ObjectOutputStream(socket.outputStream).writeObject(buildResult)
					LOG.info('Result sent.')
				}
			}
			return false
		}
	
		def build(BuildRequest request) {
			val resultCollector = new XtextBuildResultCollector
			standaloneBuilder => [
				failOnValidationError = false
				languages = XtextLanguages.getLanguageAccesses
				buildData = new XtextBuildData(request)
				buildResultCollector = resultCollector
				launch
			]
			resultCollector.buildResult
		}
	}
}