package org.eclipse.xtext.idea.build.daemon

import org.eclipse.xtend.lib.annotations.Accessors
import org.eclipse.xtext.builder.standalone.IIssueHandler
import org.eclipse.xtext.diagnostics.Severity
import org.eclipse.xtext.idea.build.daemon.Protocol.BuildIssue
import org.eclipse.xtext.validation.Issue
import org.jetbrains.jps.incremental.messages.BuildMessage.Kind

class IdeaIssueHandler implements IIssueHandler {
	
	@Accessors XtextBuildResultCollector buildResultCollector
	
	override handleIssue(Iterable<Issue> issues) {
		var errorFree = true
		for(issue: issues.filter[severity != Severity.IGNORE]) {
			buildResultCollector.addIssue(new BuildIssue() => [
				kind = issue.kind
				message = issue.message
				path = issue.uriToProblem.path
				startOffset = issue.offset
				endOffset = issue.offset + issue.length
				locationOffset = issue.offset
				line = issue.lineNumber
				column = 0 // FIXME we have to find out the column :-(
			])
			errorFree = errorFree && issue.severity != Severity.ERROR
		}
		return errorFree
	}
	
	private def getKind(Issue issue) {
		switch issue.severity {
			case ERROR:
				Kind.ERROR
			case WARNING:
				Kind.WARNING
			case INFO:
				Kind.INFO
			case IGNORE:
				Kind.PROGRESS
		}
	}
}