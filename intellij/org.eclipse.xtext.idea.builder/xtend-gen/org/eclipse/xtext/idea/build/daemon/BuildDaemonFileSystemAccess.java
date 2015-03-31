package org.eclipse.xtext.idea.build.daemon;

import com.google.common.base.Objects;
import java.io.File;
import java.io.InputStream;
import java.util.Map;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.generator.IFileSystemAccess;
import org.eclipse.xtext.generator.JavaIoFileSystemAccess;
import org.eclipse.xtext.generator.OutputConfiguration;
import org.eclipse.xtext.idea.build.daemon.XtextBuildResultCollector;
import org.eclipse.xtext.util.RuntimeIOException;
import org.eclipse.xtext.xbase.lib.Pure;

/**
 * @author Jan Koehnlein
 */
@Accessors
@SuppressWarnings("all")
public class BuildDaemonFileSystemAccess extends JavaIoFileSystemAccess {
  private XtextBuildResultCollector buildResultCollector;
  
  @Override
  public void generateFile(final String fileName, final String outputConfigName, final CharSequence contents) throws RuntimeIOException {
    super.generateFile(fileName, outputConfigName, contents);
    this.markDirty(fileName, outputConfigName);
  }
  
  @Override
  public void generateFile(final String fileName, final CharSequence contents) {
    super.generateFile(fileName, contents);
    this.markDirty(fileName, IFileSystemAccess.DEFAULT_OUTPUT);
  }
  
  @Override
  public void generateFile(final String fileName, final InputStream content) {
    super.generateFile(fileName, content);
    this.markDirty(fileName, IFileSystemAccess.DEFAULT_OUTPUT);
  }
  
  @Override
  public void generateFile(final String fileName, final String outputConfigName, final InputStream content) throws RuntimeIOException {
    super.generateFile(fileName, outputConfigName, content);
    this.markDirty(fileName, outputConfigName);
  }
  
  @Override
  public void deleteFile(final String fileName, final String outputConfiguration) {
    super.deleteFile(fileName, outputConfiguration);
    this.markDeleted(fileName, outputConfiguration);
  }
  
  @Override
  public void deleteFile(final String fileName) {
    super.deleteFile(fileName);
    this.markDeleted(fileName, IFileSystemAccess.DEFAULT_OUTPUT);
  }
  
  protected void markDirty(final String fileName, final String outputConfigName) {
    Map<String, OutputConfiguration> _outputConfigurations = this.getOutputConfigurations();
    final OutputConfiguration outputConfig = _outputConfigurations.get(outputConfigName);
    boolean _notEquals = (!Objects.equal(outputConfig, null));
    if (_notEquals) {
      String _outputDirectory = outputConfig.getOutputDirectory();
      this.buildResultCollector.addOutputDir(_outputDirectory);
      String _outputDirectory_1 = outputConfig.getOutputDirectory();
      String _plus = (_outputDirectory_1 + File.separator);
      String _plus_1 = (_plus + fileName);
      this.buildResultCollector.addChangedFile(_plus_1);
    }
  }
  
  protected void markDeleted(final String fileName, final String outputConfigName) {
    Map<String, OutputConfiguration> _outputConfigurations = this.getOutputConfigurations();
    final OutputConfiguration outputConfig = _outputConfigurations.get(outputConfigName);
    boolean _notEquals = (!Objects.equal(outputConfig, null));
    if (_notEquals) {
      String _outputDirectory = outputConfig.getOutputDirectory();
      this.buildResultCollector.addOutputDir(_outputDirectory);
      String _outputDirectory_1 = outputConfig.getOutputDirectory();
      String _plus = (_outputDirectory_1 + File.separator);
      String _plus_1 = (_plus + fileName);
      this.buildResultCollector.addDeletedFile(_plus_1);
    }
  }
  
  @Pure
  public XtextBuildResultCollector getBuildResultCollector() {
    return this.buildResultCollector;
  }
  
  public void setBuildResultCollector(final XtextBuildResultCollector buildResultCollector) {
    this.buildResultCollector = buildResultCollector;
  }
}
