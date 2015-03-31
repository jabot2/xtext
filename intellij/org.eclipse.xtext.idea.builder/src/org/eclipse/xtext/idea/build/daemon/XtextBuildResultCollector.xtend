package org.eclipse.xtext.idea.build.daemon

import org.eclipse.xtend.lib.annotations.Accessors
import org.eclipse.xtext.idea.build.daemon.Protocol.BuildIssue
import org.eclipse.xtext.idea.build.daemon.Protocol.BuildResult

class XtextBuildResultCollector {
	
	@Accessors(PUBLIC_GETTER)
	BuildResult buildResult = new BuildResult
	
	def addIssue(BuildIssue issue) {
		buildResult.issues += issue
	}
	
	def addChangedFile(String path) {
		buildResult.dirtyFiles += path
	} 
	
	def addOutputDir(String outputDir) {
		if(!buildResult.outputDirs.contains(outputDir)) {
			buildResult.outputDirs.add(outputDir)
		} 
	}

	def addDeletedFile(String path) {
		buildResult.deletedFiles += path
	}
}