package org.eclipse.xtext.idea.build.client;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.ScopedBindingBuilder;
import org.eclipse.xtext.idea.build.client.XtextIdeaBuilder;
import org.jetbrains.jps.incremental.ModuleLevelBuilder;

@SuppressWarnings("all")
public class XtextIdeaBuilderModule extends AbstractModule {
  @Override
  protected void configure() {
    AnnotatedBindingBuilder<ModuleLevelBuilder> _bind = this.<ModuleLevelBuilder>bind(ModuleLevelBuilder.class);
    ScopedBindingBuilder _to = _bind.to(XtextIdeaBuilder.class);
    _to.in(Scopes.SINGLETON);
  }
}
