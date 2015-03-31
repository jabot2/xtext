package org.eclipse.xtext.idea.build.daemon

import java.io.File
import java.util.List
import org.eclipse.emf.common.util.URI
import org.eclipse.xtend.lib.annotations.Data
import org.jetbrains.jps.ModuleChunk
import org.jetbrains.jps.ProjectPaths
import org.jetbrains.jps.incremental.CompileContext
import org.eclipse.xtext.idea.build.daemon.Protocol.BuildRequest

@Data
class XtextBuildData {

	List<File> classpath
	List<File> sourceRoots
	File baseDir
	String encoding

	new(ModuleChunk chunk, CompileContext context) {
		classpath = ProjectPaths.getCompilationClasspath(chunk, true).toList
		sourceRoots = ProjectPaths.getSourceRootsWithDependents(chunk).keySet.toList
		encoding = context.projectDescriptor.encodingConfiguration.getPreferredModuleChunkEncoding(chunk)
		val basePath = chunk.representativeTarget.module.contentRootsList.urls.map [
				URI.createURI(it)
			].findFirst [
				isFile
			].path
		baseDir = new File(basePath)
	}
	
	new(BuildRequest request) {
		classpath =  request.classpath.map[toFile]
		sourceRoots = request.sourceRoots.map[toFile]
		baseDir = request.baseDir.toFile
		encoding = request.encoding
	}
	
	protected def toFile(String it) {
		new File(it)
	}
}