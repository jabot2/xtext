package org.eclipse.xtext.idea.build.client

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.lang.management.ManagementFactory
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URLClassLoader
import java.util.regex.Pattern
import org.apache.log4j.Logger
import org.eclipse.xtext.idea.build.daemon.XtextBuildDaemon
import java.net.ConnectException

class DaemonConnector {

	static val LOG = Logger.getLogger(DaemonConnector)

	static val DAEMON_LOCK_FILE = ".xtext_build_daemon_port"
	
	static val DEFAULT_PORT = 8000

	boolean debug = true

	def Socket connect() {
		val portFile = new File(DaemonConnector.DAEMON_LOCK_FILE)
		if (portFile.exists) {
			val line = new BufferedReader(new FileReader(portFile)).readLine
			try {
				val port = Integer.parseInt(line.trim)
				val socket = new Socket()
				socket.connect(new InetSocketAddress(InetAddress.getByName('127.0.0.1'), port), 1000)
				return socket
			} catch (Exception exc) {
				// ignore and launch new process
			}
		}
		launch(portFile)
	}

	def launch(File lockFile) {
		for (port : DEFAULT_PORT .. DEFAULT_PORT + 10) {
			val runtimeMxBean = ManagementFactory.runtimeMXBean
			val java = System.getProperty('java.home') + File.separator + 'bin' + File.separator + 'java'
			val classpath = <String>newLinkedHashSet
			classpath += runtimeMxBean.classPath.split(Pattern.quote(File.pathSeparator))
			val classLoader = class.classLoader
			if (classLoader instanceof URLClassLoader) {
				classLoader.URLs.forEach [
					if (protocol == 'file')
						classpath += new File(toURI).path
				]
			}
			val command = newArrayList
			command += java
			command += '-cp'
			command += classpath.toList.reverseView.join(File.pathSeparator)
			command += runtimeMxBean.inputArguments.filter [
				!startsWith('-agentlib')
			]
			if(debug) {
				command += '-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5006'
			}
			command += XtextBuildDaemon.canonicalName
			command += '-port'
			command += port.toString
			val daemonProcess = new ProcessBuilder().command(command).start
			for(i: 0..200) {
				try {
					val socket = new Socket()
					socket.connect(new InetSocketAddress(InetAddress.getByName('127.0.0.1'), port), 1000)
					writeLockFile(lockFile, port)	
					return socket
				} catch (ConnectException exc) {
					Thread.sleep(100)
				}
			}
			LOG.warn('Failed to start daemon on port ' + port)
			daemonProcess?.destroy
		}
	}

	protected def writeLockFile(File lockFile, int port) {
		var FileWriter fileWriter = null
		try {
			fileWriter = new FileWriter(lockFile)
			fileWriter.write(port.toString)
			fileWriter.close
		} catch (IOException exc) {
			LOG.error('Error wrting port file')
		} finally {
			fileWriter?.close
		}
	}
}