package org.eclipse.xtext.idea.build.daemon

import java.io.File
import org.apache.log4j.Logger
import org.eclipse.xtext.builder.standalone.LanguageAccess
import org.eclipse.xtext.builder.standalone.StandaloneBuilder
import org.eclipse.xtext.generator.JavaIoFileSystemAccess

class IdeaStandaloneBuilder extends StandaloneBuilder {

	// TODO find the right way to log
	static val LOG = Logger.getLogger(IdeaStandaloneBuilder)

	XtextBuildData buildData
	XtextBuildResultCollector buildResultCollector

	def setBuildData(XtextBuildData buildData) {
		this.buildData = buildData
		this.baseDir = buildData.baseDir.path
		this.classPathEntries = buildData.classpath.map[path]
		this.encoding = buildData.encoding
		this.tempDir = getOrCreateTmpDir.path
		this.sourceDirs = buildData.sourceRoots.map[path]
	}
	
	def setBuildResultCollector(XtextBuildResultCollector buildResultCollector) {
		this.buildResultCollector = buildResultCollector;
		(issueHandler as IdeaIssueHandler).buildResultCollector = buildResultCollector
	}
	
	override protected configureFileSystemAccess(JavaIoFileSystemAccess fsa, LanguageAccess language) {
		if(fsa instanceof BuildDaemonFileSystemAccess) {
			fsa.buildResultCollector = buildResultCollector
		}
		fsa
	}
	
	private def getOrCreateTmpDir() {
		val contentRoot = baseDir
		val tmpDir = new File(contentRoot, 'xtend-stubs')
		if(!tmpDir.exists) 
			tmpDir.mkdir
		else if(!tmpDir.isDirectory)
			LOG.error('Compilation tmpDir ' + tmpDir + ' exists and is a file')
		return tmpDir
	}
}