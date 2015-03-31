package org.eclipse.xtext.idea.build.daemon;

import org.eclipse.xtext.builder.standalone.IIssueHandler;
import org.eclipse.xtext.builder.standalone.StandaloneBuilderModule;
import org.eclipse.xtext.idea.build.daemon.IdeaIssueHandler;

@SuppressWarnings("all")
public class BuildDaemonModule extends StandaloneBuilderModule {
  @Override
  public Class<? extends IIssueHandler> bindIIssueHandler() {
    return IdeaIssueHandler.class;
  }
}
