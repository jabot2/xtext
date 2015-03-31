package org.eclipse.xtext.idea.build.daemon

import java.io.InputStream
import org.eclipse.xtend.lib.annotations.Accessors
import org.eclipse.xtext.generator.JavaIoFileSystemAccess
import org.eclipse.xtext.util.RuntimeIOException

import static java.io.File.*
import java.io.File

/**
 * @author Jan Koehnlein
 */
@Accessors
class BuildDaemonFileSystemAccess extends JavaIoFileSystemAccess {

	XtextBuildResultCollector buildResultCollector

	override void generateFile(
		String fileName,
		String outputConfigName,
		CharSequence contents
	) throws RuntimeIOException {
		super.generateFile(fileName, outputConfigName, contents)
		markDirty(fileName, outputConfigName)
	}

	override generateFile(String fileName, CharSequence contents) {
		super.generateFile(fileName, contents)
		markDirty(fileName, DEFAULT_OUTPUT)
	}

	override generateFile(String fileName, InputStream content) {
		super.generateFile(fileName, content)
		markDirty(fileName, DEFAULT_OUTPUT)
	}

	override generateFile(String fileName, String outputConfigName, InputStream content) throws RuntimeIOException {
		super.generateFile(fileName, outputConfigName, content)
		markDirty(fileName, outputConfigName)
	}

	override void deleteFile(String fileName, String outputConfiguration) {
		super.deleteFile(fileName, outputConfiguration)
		markDeleted(fileName, outputConfiguration)
	}

	override deleteFile(String fileName) {
		super.deleteFile(fileName)
		markDeleted(fileName, DEFAULT_OUTPUT)
	}

	def protected void markDirty(String fileName, String outputConfigName) {
		val outputConfig = outputConfigurations.get(outputConfigName)
		if (outputConfig != null) {
			buildResultCollector.addOutputDir(outputConfig.outputDirectory)
			buildResultCollector.addChangedFile(outputConfig.outputDirectory + File.separator + fileName)
		}
	}

	def protected void markDeleted(String fileName, String outputConfigName) {
		val outputConfig = outputConfigurations.get(outputConfigName)
		if(outputConfig!=null) {
			buildResultCollector.addOutputDir(outputConfig.outputDirectory)
			buildResultCollector.addDeletedFile(outputConfig.outputDirectory + File.separator + fileName)			
		} 
	}
}