package org.eclipse.xtext.idea.build.daemon;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtend.lib.annotations.Data;
import org.eclipse.xtext.idea.build.daemon.Protocol;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;
import org.jetbrains.jps.ModuleChunk;
import org.jetbrains.jps.ProjectPaths;
import org.jetbrains.jps.cmdline.ProjectDescriptor;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.incremental.CompilerEncodingConfiguration;
import org.jetbrains.jps.incremental.ModuleBuildTarget;
import org.jetbrains.jps.model.JpsUrlList;
import org.jetbrains.jps.model.module.JpsModule;

@Data
@SuppressWarnings("all")
public class XtextBuildData {
  private final List<File> classpath;
  
  private final List<File> sourceRoots;
  
  private final File baseDir;
  
  private final String encoding;
  
  public XtextBuildData(final ModuleChunk chunk, final CompileContext context) {
    Collection<File> _compilationClasspath = ProjectPaths.getCompilationClasspath(chunk, true);
    List<File> _list = IterableExtensions.<File>toList(_compilationClasspath);
    this.classpath = _list;
    Map<File, String> _sourceRootsWithDependents = ProjectPaths.getSourceRootsWithDependents(chunk);
    Set<File> _keySet = _sourceRootsWithDependents.keySet();
    List<File> _list_1 = IterableExtensions.<File>toList(_keySet);
    this.sourceRoots = _list_1;
    ProjectDescriptor _projectDescriptor = context.getProjectDescriptor();
    CompilerEncodingConfiguration _encodingConfiguration = _projectDescriptor.getEncodingConfiguration();
    String _preferredModuleChunkEncoding = _encodingConfiguration.getPreferredModuleChunkEncoding(chunk);
    this.encoding = _preferredModuleChunkEncoding;
    ModuleBuildTarget _representativeTarget = chunk.representativeTarget();
    JpsModule _module = _representativeTarget.getModule();
    JpsUrlList _contentRootsList = _module.getContentRootsList();
    List<String> _urls = _contentRootsList.getUrls();
    final Function1<String, URI> _function = new Function1<String, URI>() {
      @Override
      public URI apply(final String it) {
        return URI.createURI(it);
      }
    };
    List<URI> _map = ListExtensions.<String, URI>map(_urls, _function);
    final Function1<URI, Boolean> _function_1 = new Function1<URI, Boolean>() {
      @Override
      public Boolean apply(final URI it) {
        return Boolean.valueOf(it.isFile());
      }
    };
    URI _findFirst = IterableExtensions.<URI>findFirst(_map, _function_1);
    final String basePath = _findFirst.path();
    File _file = new File(basePath);
    this.baseDir = _file;
  }
  
  public XtextBuildData(final Protocol.BuildRequest request) {
    List<String> _classpath = request.getClasspath();
    final Function1<String, File> _function = new Function1<String, File>() {
      @Override
      public File apply(final String it) {
        return XtextBuildData.this.toFile(it);
      }
    };
    List<File> _map = ListExtensions.<String, File>map(_classpath, _function);
    this.classpath = _map;
    List<String> _sourceRoots = request.getSourceRoots();
    final Function1<String, File> _function_1 = new Function1<String, File>() {
      @Override
      public File apply(final String it) {
        return XtextBuildData.this.toFile(it);
      }
    };
    List<File> _map_1 = ListExtensions.<String, File>map(_sourceRoots, _function_1);
    this.sourceRoots = _map_1;
    String _baseDir = request.getBaseDir();
    File _file = this.toFile(_baseDir);
    this.baseDir = _file;
    String _encoding = request.getEncoding();
    this.encoding = _encoding;
  }
  
  protected File toFile(final String it) {
    return new File(it);
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.classpath== null) ? 0 : this.classpath.hashCode());
    result = prime * result + ((this.sourceRoots== null) ? 0 : this.sourceRoots.hashCode());
    result = prime * result + ((this.baseDir== null) ? 0 : this.baseDir.hashCode());
    result = prime * result + ((this.encoding== null) ? 0 : this.encoding.hashCode());
    return result;
  }
  
  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    XtextBuildData other = (XtextBuildData) obj;
    if (this.classpath == null) {
      if (other.classpath != null)
        return false;
    } else if (!this.classpath.equals(other.classpath))
      return false;
    if (this.sourceRoots == null) {
      if (other.sourceRoots != null)
        return false;
    } else if (!this.sourceRoots.equals(other.sourceRoots))
      return false;
    if (this.baseDir == null) {
      if (other.baseDir != null)
        return false;
    } else if (!this.baseDir.equals(other.baseDir))
      return false;
    if (this.encoding == null) {
      if (other.encoding != null)
        return false;
    } else if (!this.encoding.equals(other.encoding))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("classpath", this.classpath);
    b.add("sourceRoots", this.sourceRoots);
    b.add("baseDir", this.baseDir);
    b.add("encoding", this.encoding);
    return b.toString();
  }
  
  @Pure
  public List<File> getClasspath() {
    return this.classpath;
  }
  
  @Pure
  public List<File> getSourceRoots() {
    return this.sourceRoots;
  }
  
  @Pure
  public File getBaseDir() {
    return this.baseDir;
  }
  
  @Pure
  public String getEncoding() {
    return this.encoding;
  }
}
