package org.eclipse.xtext.idea.build.daemon

import org.eclipse.xtext.builder.standalone.StandaloneBuilderModule

class BuildDaemonModule extends StandaloneBuilderModule {
	
	override bindIIssueHandler() {
		IdeaIssueHandler
	}
}