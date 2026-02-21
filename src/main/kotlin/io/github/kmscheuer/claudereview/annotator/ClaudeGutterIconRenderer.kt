package io.github.kmscheuer.claudereview.annotator

import com.intellij.openapi.editor.markup.GutterIconRenderer
import io.github.kmscheuer.claudereview.icons.ClaudeReviewIcons
import io.github.kmscheuer.claudereview.model.Finding
import io.github.kmscheuer.claudereview.model.Severity
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
