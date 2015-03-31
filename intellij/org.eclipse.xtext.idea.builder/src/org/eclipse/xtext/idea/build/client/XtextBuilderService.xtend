package org.eclipse.xtext.idea.build.client

import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Injector
import org.jetbrains.jps.incremental.BuilderService
import org.jetbrains.jps.incremental.ModuleLevelBuilder

class XtextBuilderService extends BuilderService {
	
	static Injector INJECTOR = Guice.createInjector(new XtextIdeaBuilderModule)
	
	@Inject ModuleLevelBuilder moduleLevelBuilder

	new() {
		INJECTOR.injectMembers(this) 
	}

	override createModuleLevelBuilders() {
		return #[moduleLevelBuilder] 
	}
}