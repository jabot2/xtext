package org.eclipse.xtext.idea.build.client

import com.google.inject.Inject
import java.io.File
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import org.apache.log4j.Logger
import org.eclipse.xtext.idea.build.daemon.Protocol.BuildRequest
import org.eclipse.xtext.idea.build.daemon.Protocol.BuildResult
import org.eclipse.xtext.idea.build.daemon.XtextBuildData
import org.jetbrains.jps.ModuleChunk
import org.jetbrains.jps.builders.DirtyFilesHolder
import org.jetbrains.jps.builders.java.JavaSourceRootDescriptor
import org.jetbrains.jps.incremental.CompileContext
import org.jetbrains.jps.incremental.FSOperations
import org.jetbrains.jps.incremental.ModuleBuildTarget
import org.jetbrains.jps.incremental.ModuleLevelBuilder
import org.jetbrains.jps.incremental.ProjectBuildException
import org.jetbrains.jps.incremental.fs.CompilationRound
import org.jetbrains.jps.incremental.messages.CompilerMessage

import static org.jetbrains.jps.incremental.BuilderCategory.*
import static org.jetbrains.jps.incremental.ModuleLevelBuilder.ExitCode.*
import org.eclipse.emf.common.util.URI
import org.jetbrains.jps.model.module.JpsModule
import org.jetbrains.jps.model.java.JavaSourceRootType

class XtextIdeaBuilder extends ModuleLevelBuilder {

	static val LOG = Logger.getLogger(XtextIdeaBuilder)

	@Inject DaemonConnector connector
	
	public new() {
		super(SOURCE_GENERATOR)
	}
	
	override build(CompileContext context, ModuleChunk chunk, DirtyFilesHolder<JavaSourceRootDescriptor, ModuleBuildTarget> dirtyFilesHolder, OutputConsumer outputConsumer) throws ProjectBuildException, IOException {
		if(!dirtyFilesHolder.hasDirtyFiles && !dirtyFilesHolder.hasRemovedFiles)
			return NOTHING_DONE;
		try {
			val dirtyFiles = <String>newArrayList
			dirtyFilesHolder.processDirtyFiles [ target, file, root |
				dirtyFiles.add(file.path)
				true
			]
			val deletedFiles = newArrayList(chunk.representativeTarget)
			
			val xtextBuildData = new XtextBuildData(chunk, context)
			val buildRequest = new BuildRequest() => [
				dirtyFiles += dirtyFiles
				deletedFiles += deletedFiles
				classpath += xtextBuildData.classpath.map[path]
				sourceRoots += xtextBuildData.sourceRoots.map[path]
				encoding = xtextBuildData.encoding 
				baseDir = xtextBuildData.baseDir.path
			]
			val socket = connector.connect
			val out = new ObjectOutputStream(socket.outputStream)
			out.writeObject(buildRequest)
			val inp = new ObjectInputStream(socket.inputStream)
			val result = inp.readObject()
			switch result {
				BuildResult: 
					process(result, context, chunk, outputConsumer)
			}
			return OK
		} catch(Exception exc) {
			LOG.error('Error in build', exc)
			return ABORT
		}
	}
	
	def process(BuildResult result, CompileContext context, ModuleChunk chunk, OutputConsumer outputConsumer) {
		val module = chunk.representativeTarget.module
		result.outputDirs.forEach [
			createSourceRoot(module)
		]
		result.dirtyFiles.forEach [
			FSOperations.markDirty(context, CompilationRound.CURRENT, new File(it))
		]
		result.deletedFiles.forEach [
			FSOperations.markDeleted(context, new File(it))
		]
		result.issues.forEach [
			context.processMessage(new CompilerMessage(presentableName,
				kind, message, path, startOffset, endOffset, locationOffset, line, column
			))
		]
	}
	
	protected def createSourceRoot(String outputDir, JpsModule module) {
		val outletUrl = URI.createFileURI(outputDir).toString
		if (!module.getSourceRoots(JavaSourceRootType.SOURCE).exists[url == outletUrl])
			module.addSourceRoot(outletUrl, JavaSourceRootType.SOURCE)
	}
	
	override getCompilableFileExtensions() {
		#['xtend'] // https://bugs.eclipse.org/bugs/show_bug.cgi?id=463202 
	}
	
//	private def getFileExtension(File file) {
//		val fileName = if(SystemInfo.isFileSystemCaseSensitive)
//				file.name
//			else
//				file.name.toLowerCase 
//		val index = fileName.lastIndexOf('.')
//		if(index != -1) 
//			return file.name.substring(file.name.lastIndexOf('.') + 1)
//		else 
//			return null
//	}
	
	override getPresentableName() {
		'Xtext'
	}
	
}