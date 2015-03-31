package org.eclipse.xtext.idea.build.daemon;

import java.util.List;
import org.eclipse.xtend.lib.annotations.AccessorType;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.idea.build.daemon.Protocol;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public class XtextBuildResultCollector {
  @Accessors(AccessorType.PUBLIC_GETTER)
  private Protocol.BuildResult buildResult = new Protocol.BuildResult();
  
  public boolean addIssue(final Protocol.BuildIssue issue) {
    List<Protocol.BuildIssue> _issues = this.buildResult.getIssues();
    return _issues.add(issue);
  }
  
  public boolean addChangedFile(final String path) {
    List<String> _dirtyFiles = this.buildResult.getDirtyFiles();
    return _dirtyFiles.add(path);
  }
  
  public boolean addOutputDir(final String outputDir) {
    boolean _xifexpression = false;
    List<String> _outputDirs = this.buildResult.getOutputDirs();
    boolean _contains = _outputDirs.contains(outputDir);
    boolean _not = (!_contains);
    if (_not) {
      List<String> _outputDirs_1 = this.buildResult.getOutputDirs();
      _xifexpression = _outputDirs_1.add(outputDir);
    }
    return _xifexpression;
  }
  
  public boolean addDeletedFile(final String path) {
    List<String> _deletedFiles = this.buildResult.getDeletedFiles();
    return _deletedFiles.add(path);
  }
  
  @Pure
  public Protocol.BuildResult getBuildResult() {
    return this.buildResult;
  }
}
