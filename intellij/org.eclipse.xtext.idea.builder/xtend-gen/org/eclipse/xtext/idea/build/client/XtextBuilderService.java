package org.eclipse.xtext.idea.build.client;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import java.util.Collections;
import java.util.List;
import org.eclipse.xtext.idea.build.client.XtextIdeaBuilderModule;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.jetbrains.jps.incremental.BuilderService;
import org.jetbrains.jps.incremental.ModuleLevelBuilder;

@SuppressWarnings("all")
public class XtextBuilderService extends BuilderService {
  private static Injector INJECTOR = Guice.createInjector(new XtextIdeaBuilderModule());
  
  @Inject
  private ModuleLevelBuilder moduleLevelBuilder;
  
  public XtextBuilderService() {
    XtextBuilderService.INJECTOR.injectMembers(this);
  }
  
  @Override
  public List<? extends ModuleLevelBuilder> createModuleLevelBuilders() {
    return Collections.<ModuleLevelBuilder>unmodifiableList(CollectionLiterals.<ModuleLevelBuilder>newArrayList(this.moduleLevelBuilder));
  }
}
