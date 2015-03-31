package org.eclipse.xtext.idea.build.daemon;

import java.io.Serializable;
import java.util.List;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Pure;
import org.jetbrains.jps.incremental.messages.BuildMessage;

@SuppressWarnings("all")
public class Protocol {
  @Accessors
  public static class BuildRequest implements Serializable {
    private List<String> dirtyFiles = CollectionLiterals.<String>newArrayList();
    
    private List<String> deletedFiles = CollectionLiterals.<String>newArrayList();
    
    private List<String> classpath = CollectionLiterals.<String>newArrayList();
    
    private List<String> sourceRoots = CollectionLiterals.<String>newArrayList();
    
    private String baseDir;
    
    private String encoding;
    
    @Pure
    public List<String> getDirtyFiles() {
      return this.dirtyFiles;
    }
    
    public void setDirtyFiles(final List<String> dirtyFiles) {
      this.dirtyFiles = dirtyFiles;
    }
    
    @Pure
    public List<String> getDeletedFiles() {
      return this.deletedFiles;
    }
    
    public void setDeletedFiles(final List<String> deletedFiles) {
      this.deletedFiles = deletedFiles;
    }
    
    @Pure
    public List<String> getClasspath() {
      return this.classpath;
    }
    
    public void setClasspath(final List<String> classpath) {
      this.classpath = classpath;
    }
    
    @Pure
    public List<String> getSourceRoots() {
      return this.sourceRoots;
    }
    
    public void setSourceRoots(final List<String> sourceRoots) {
      this.sourceRoots = sourceRoots;
    }
    
    @Pure
    public String getBaseDir() {
      return this.baseDir;
    }
    
    public void setBaseDir(final String baseDir) {
      this.baseDir = baseDir;
    }
    
    @Pure
    public String getEncoding() {
      return this.encoding;
    }
    
    public void setEncoding(final String encoding) {
      this.encoding = encoding;
    }
  }
  
  @Accessors
  public static class BuildResult implements Serializable {
    private List<String> dirtyFiles = CollectionLiterals.<String>newArrayList();
    
    private List<String> deletedFiles = CollectionLiterals.<String>newArrayList();
    
    private List<String> outputDirs = CollectionLiterals.<String>newArrayList();
    
    private List<Protocol.BuildIssue> issues = CollectionLiterals.<Protocol.BuildIssue>newArrayList();
    
    @Pure
    public List<String> getDirtyFiles() {
      return this.dirtyFiles;
    }
    
    public void setDirtyFiles(final List<String> dirtyFiles) {
      this.dirtyFiles = dirtyFiles;
    }
    
    @Pure
    public List<String> getDeletedFiles() {
      return this.deletedFiles;
    }
    
    public void setDeletedFiles(final List<String> deletedFiles) {
      this.deletedFiles = deletedFiles;
    }
    
    @Pure
    public List<String> getOutputDirs() {
      return this.outputDirs;
    }
    
    public void setOutputDirs(final List<String> outputDirs) {
      this.outputDirs = outputDirs;
    }
    
    @Pure
    public List<Protocol.BuildIssue> getIssues() {
      return this.issues;
    }
    
    public void setIssues(final List<Protocol.BuildIssue> issues) {
      this.issues = issues;
    }
  }
  
  @Accessors
  public static class BuildIssue implements Serializable {
    private BuildMessage.Kind kind;
    
    private String message;
    
    private String path;
    
    private int startOffset;
    
    private int endOffset;
    
    private int locationOffset;
    
    private int line;
    
    private int column;
    
    @Pure
    public BuildMessage.Kind getKind() {
      return this.kind;
    }
    
    public void setKind(final BuildMessage.Kind kind) {
      this.kind = kind;
    }
    
    @Pure
    public String getMessage() {
      return this.message;
    }
    
    public void setMessage(final String message) {
      this.message = message;
    }
    
    @Pure
    public String getPath() {
      return this.path;
    }
    
    public void setPath(final String path) {
      this.path = path;
    }
    
    @Pure
    public int getStartOffset() {
      return this.startOffset;
    }
    
    public void setStartOffset(final int startOffset) {
      this.startOffset = startOffset;
    }
    
    @Pure
    public int getEndOffset() {
      return this.endOffset;
    }
    
    public void setEndOffset(final int endOffset) {
      this.endOffset = endOffset;
    }
    
    @Pure
    public int getLocationOffset() {
      return this.locationOffset;
    }
    
    public void setLocationOffset(final int locationOffset) {
      this.locationOffset = locationOffset;
    }
    
    @Pure
    public int getLine() {
      return this.line;
    }
    
    public void setLine(final int line) {
      this.line = line;
    }
    
    @Pure
    public int getColumn() {
      return this.column;
    }
    
    public void setColumn(final int column) {
      this.column = column;
    }
  }
  
  public static class StopServer implements Serializable {
  }
}
