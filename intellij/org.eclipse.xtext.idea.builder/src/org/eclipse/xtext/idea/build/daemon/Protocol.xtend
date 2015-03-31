package org.eclipse.xtext.idea.build.daemon

import java.io.Serializable
import java.util.List
import org.eclipse.xtend.lib.annotations.Accessors
import org.jetbrains.jps.incremental.messages.BuildMessage

class Protocol {
	
	@Accessors
	static class BuildRequest implements Serializable {
		List<String> dirtyFiles	= newArrayList
		List<String> deletedFiles = newArrayList
		List<String> classpath = newArrayList
		List<String> sourceRoots = newArrayList
		String baseDir
		String encoding
	}
	
	@Accessors
	static class BuildResult implements Serializable {
		List<String> dirtyFiles = newArrayList		
		List<String> deletedFiles = newArrayList
		List<String> outputDirs = newArrayList
		List<BuildIssue> issues = newArrayList
	}
	
	@Accessors
	static class BuildIssue implements Serializable {
		BuildMessage.Kind kind
		String message
		String path
		int startOffset
		int endOffset
		int locationOffset
		int line
		int column
	}
	
	static class StopServer implements Serializable {
	}
}