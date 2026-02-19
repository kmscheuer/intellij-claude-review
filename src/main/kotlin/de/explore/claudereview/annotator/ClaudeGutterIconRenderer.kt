package de.explore.claudereview.annotator

import com.intellij.openapi.editor.markup.GutterIconRenderer
import de.explore.claudereview.icons.ClaudeReviewIcons
import de.explore.claudereview.model.Finding
import de.explore.claudereview.model.Severity
import javax.swing.Icon

class ClaudeGutterIconRenderer(
    private val finding: Finding
) : GutterIconRenderer() {

    override fun getIcon(): Icon = when (finding.severity) {
        Severity.BUG -> ClaudeReviewIcons.Bug
        Severity.WARNING -> ClaudeReviewIcons.Warning
        Severity.INFO -> ClaudeReviewIcons.Info
    }

    override fun getTooltipText(): String = "${finding.severity}: ${finding.message}"

    override fun getAlignment() = Alignment.LEFT

    override fun equals(other: Any?): Boolean =
        other is ClaudeGutterIconRenderer && finding == other.finding

    override fun hashCode(): Int = finding.hashCode()
}
