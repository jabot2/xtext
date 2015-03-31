package org.eclipse.xtext.idea.build.client

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.jetbrains.jps.incremental.ModuleLevelBuilder

class XtextIdeaBuilderModule extends AbstractModule {
	
	override protected configure() {
		bind(ModuleLevelBuilder).to(XtextIdeaBuilder).in(Scopes.SINGLETON);
	}	
}