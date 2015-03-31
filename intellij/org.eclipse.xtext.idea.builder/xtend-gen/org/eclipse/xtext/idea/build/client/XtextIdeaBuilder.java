package org.eclipse.xtext.idea.build.client;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.idea.build.client.DaemonConnector;
import org.eclipse.xtext.idea.build.daemon.Protocol;
import org.eclipse.xtext.idea.build.daemon.XtextBuildData;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.jetbrains.jps.ModuleChunk;
import org.jetbrains.jps.builders.DirtyFilesHolder;
import org.jetbrains.jps.builders.FileProcessor;
import org.jetbrains.jps.builders.java.JavaSourceRootDescriptor;
import org.jetbrains.jps.incremental.BuilderCategory;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.incremental.FSOperations;
import org.jetbrains.jps.incremental.ModuleBuildTarget;
import org.jetbrains.jps.incremental.ModuleLevelBuilder;
import org.jetbrains.jps.incremental.ProjectBuildException;
import org.jetbrains.jps.incremental.fs.CompilationRound;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.messages.CompilerMessage;
import org.jetbrains.jps.model.java.JavaSourceRootProperties;
import org.jetbrains.jps.model.java.JavaSourceRootType;
import org.jetbrains.jps.model.module.JpsModule;
import org.jetbrains.jps.model.module.JpsModuleSourceRoot;
import org.jetbrains.jps.model.module.JpsTypedModuleSourceRoot;

@SuppressWarnings("all")
public class XtextIdeaBuilder extends ModuleLevelBuilder {
  private final static Logger LOG = Logger.getLogger(XtextIdeaBuilder.class);
  
  @Inject
  private DaemonConnector connector;
  
  public XtextIdeaBuilder() {
    super(BuilderCategory.SOURCE_GENERATOR);
  }
  
  @Override
  public ModuleLevelBuilder.ExitCode build(final CompileContext context, final ModuleChunk chunk, final DirtyFilesHolder<JavaSourceRootDescriptor, ModuleBuildTarget> dirtyFilesHolder, final ModuleLevelBuilder.OutputConsumer outputConsumer) throws ProjectBuildException, IOException {
    boolean _and = false;
    boolean _hasDirtyFiles = dirtyFilesHolder.hasDirtyFiles();
    boolean _not = (!_hasDirtyFiles);
    if (!_not) {
      _and = false;
    } else {
      boolean _hasRemovedFiles = dirtyFilesHolder.hasRemovedFiles();
      boolean _not_1 = (!_hasRemovedFiles);
      _and = _not_1;
    }
    if (_and) {
      return ModuleLevelBuilder.ExitCode.NOTHING_DONE;
    }
    try {
      final ArrayList<String> dirtyFiles = CollectionLiterals.<String>newArrayList();
      final FileProcessor<JavaSourceRootDescriptor, ModuleBuildTarget> _function = new FileProcessor<JavaSourceRootDescriptor, ModuleBuildTarget>() {
        @Override
        public boolean apply(final ModuleBuildTarget target, final File file, final JavaSourceRootDescriptor root) throws IOException {
          boolean _xblockexpression = false;
          {
            String _path = file.getPath();
            dirtyFiles.add(_path);
            _xblockexpression = true;
          }
          return _xblockexpression;
        }
      };
      dirtyFilesHolder.processDirtyFiles(_function);
      ModuleBuildTarget _representativeTarget = chunk.representativeTarget();
      final ArrayList<ModuleBuildTarget> deletedFiles = CollectionLiterals.<ModuleBuildTarget>newArrayList(_representativeTarget);
      final XtextBuildData xtextBuildData = new XtextBuildData(chunk, context);
      Protocol.BuildRequest _buildRequest = new Protocol.BuildRequest();
      final Procedure1<Protocol.BuildRequest> _function_1 = new Procedure1<Protocol.BuildRequest>() {
        @Override
        public void apply(final Protocol.BuildRequest it) {
          Iterables.<String>addAll(dirtyFiles, dirtyFiles);
          Iterables.<ModuleBuildTarget>addAll(deletedFiles, deletedFiles);
          List<String> _classpath = it.getClasspath();
          List<File> _classpath_1 = xtextBuildData.getClasspath();
          final Function1<File, String> _function = new Function1<File, String>() {
            @Override
            public String apply(final File it) {
              return it.getPath();
            }
          };
          List<String> _map = ListExtensions.<File, String>map(_classpath_1, _function);
          Iterables.<String>addAll(_classpath, _map);
          List<String> _sourceRoots = it.getSourceRoots();
          List<File> _sourceRoots_1 = xtextBuildData.getSourceRoots();
          final Function1<File, String> _function_1 = new Function1<File, String>() {
            @Override
            public String apply(final File it) {
              return it.getPath();
            }
          };
          List<String> _map_1 = ListExtensions.<File, String>map(_sourceRoots_1, _function_1);
          Iterables.<String>addAll(_sourceRoots, _map_1);
          String _encoding = xtextBuildData.getEncoding();
          it.setEncoding(_encoding);
          File _baseDir = xtextBuildData.getBaseDir();
          String _path = _baseDir.getPath();
          it.setBaseDir(_path);
        }
      };
      final Protocol.BuildRequest buildRequest = ObjectExtensions.<Protocol.BuildRequest>operator_doubleArrow(_buildRequest, _function_1);
      final Socket socket = this.connector.connect();
      OutputStream _outputStream = socket.getOutputStream();
      final ObjectOutputStream out = new ObjectOutputStream(_outputStream);
      out.writeObject(buildRequest);
      InputStream _inputStream = socket.getInputStream();
      final ObjectInputStream inp = new ObjectInputStream(_inputStream);
      final Object result = inp.readObject();
      boolean _matched = false;
      if (!_matched) {
        if (result instanceof Protocol.BuildResult) {
          _matched=true;
          this.process(((Protocol.BuildResult)result), context, chunk, outputConsumer);
        }
      }
      return ModuleLevelBuilder.ExitCode.OK;
    } catch (final Throwable _t) {
      if (_t instanceof Exception) {
        final Exception exc = (Exception)_t;
        XtextIdeaBuilder.LOG.error("Error in build", exc);
        return ModuleLevelBuilder.ExitCode.ABORT;
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
  }
  
  public void process(final Protocol.BuildResult result, final CompileContext context, final ModuleChunk chunk, final ModuleLevelBuilder.OutputConsumer outputConsumer) {
    ModuleBuildTarget _representativeTarget = chunk.representativeTarget();
    final JpsModule module = _representativeTarget.getModule();
    List<String> _outputDirs = result.getOutputDirs();
    final Procedure1<String> _function = new Procedure1<String>() {
      @Override
      public void apply(final String it) {
        XtextIdeaBuilder.this.createSourceRoot(it, module);
      }
    };
    IterableExtensions.<String>forEach(_outputDirs, _function);
    List<String> _dirtyFiles = result.getDirtyFiles();
    final Procedure1<String> _function_1 = new Procedure1<String>() {
      @Override
      public void apply(final String it) {
        try {
          File _file = new File(it);
          FSOperations.markDirty(context, CompilationRound.CURRENT, _file);
        } catch (Throwable _e) {
          throw Exceptions.sneakyThrow(_e);
        }
      }
    };
    IterableExtensions.<String>forEach(_dirtyFiles, _function_1);
    List<String> _deletedFiles = result.getDeletedFiles();
    final Procedure1<String> _function_2 = new Procedure1<String>() {
      @Override
      public void apply(final String it) {
        try {
          File _file = new File(it);
          FSOperations.markDeleted(context, _file);
        } catch (Throwable _e) {
          throw Exceptions.sneakyThrow(_e);
        }
      }
    };
    IterableExtensions.<String>forEach(_deletedFiles, _function_2);
    List<Protocol.BuildIssue> _issues = result.getIssues();
    final Procedure1<Protocol.BuildIssue> _function_3 = new Procedure1<Protocol.BuildIssue>() {
      @Override
      public void apply(final Protocol.BuildIssue it) {
        String _presentableName = XtextIdeaBuilder.this.getPresentableName();
        BuildMessage.Kind _kind = it.getKind();
        String _message = it.getMessage();
        String _path = it.getPath();
        int _startOffset = it.getStartOffset();
        int _endOffset = it.getEndOffset();
        int _locationOffset = it.getLocationOffset();
        int _line = it.getLine();
        int _column = it.getColumn();
        CompilerMessage _compilerMessage = new CompilerMessage(_presentableName, _kind, _message, _path, _startOffset, _endOffset, _locationOffset, _line, _column);
        context.processMessage(_compilerMessage);
      }
    };
    IterableExtensions.<Protocol.BuildIssue>forEach(_issues, _function_3);
  }
  
  protected JpsModuleSourceRoot createSourceRoot(final String outputDir, final JpsModule module) {
    JpsModuleSourceRoot _xblockexpression = null;
    {
      URI _createFileURI = URI.createFileURI(outputDir);
      final String outletUrl = _createFileURI.toString();
      JpsModuleSourceRoot _xifexpression = null;
      Iterable<JpsTypedModuleSourceRoot<JavaSourceRootProperties>> _sourceRoots = module.<JavaSourceRootProperties>getSourceRoots(JavaSourceRootType.SOURCE);
      final Function1<JpsTypedModuleSourceRoot<JavaSourceRootProperties>, Boolean> _function = new Function1<JpsTypedModuleSourceRoot<JavaSourceRootProperties>, Boolean>() {
        @Override
        public Boolean apply(final JpsTypedModuleSourceRoot<JavaSourceRootProperties> it) {
          String _url = it.getUrl();
          return Boolean.valueOf(Objects.equal(_url, outletUrl));
        }
      };
      boolean _exists = IterableExtensions.<JpsTypedModuleSourceRoot<JavaSourceRootProperties>>exists(_sourceRoots, _function);
      boolean _not = (!_exists);
      if (_not) {
        _xifexpression = module.<JavaSourceRootProperties>addSourceRoot(outletUrl, JavaSourceRootType.SOURCE);
      }
      _xblockexpression = _xifexpression;
    }
    return _xblockexpression;
  }
  
  @Override
  public List<String> getCompilableFileExtensions() {
    return Collections.<String>unmodifiableList(CollectionLiterals.<String>newArrayList("xtend"));
  }
  
  @Override
  public String getPresentableName() {
    return "Xtext";
  }
}
