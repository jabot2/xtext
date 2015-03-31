package org.eclipse.xtext.idea.build.daemon

import java.io.File
import java.util.Set
import org.eclipse.xtext.builder.standalone.LanguageAccess
import org.eclipse.xtext.generator.OutputConfiguration
import org.eclipse.xtext.resource.IResourceServiceProvider

class IdeaLanguageAccess extends LanguageAccess {

	new(
		Set<OutputConfiguration> outputConfigurations,
		IResourceServiceProvider resourceServiceProvider
	) {
		super(outputConfigurations, resourceServiceProvider, false)
	}

	new(
		Set<OutputConfiguration> outputConfigurations,
		IResourceServiceProvider resourceServiceProvider,
		boolean linksAgainstJavaTypes
	) {
		super(outputConfigurations, resourceServiceProvider, linksAgainstJavaTypes)
	}
	
	override createFileSystemAccess(File baseDir) {
		resourceServiceProvider.get(BuildDaemonFileSystemAccess) => [
			outputConfigurations = super.createFileSystemAccess(baseDir).outputConfigurations
		]
	}

}