package io.github.kmscheuer.claudereview.icons

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object ClaudeReviewIcons {
    val Bug: Icon = IconLoader.getIcon("/icons/claude-bug.svg", ClaudeReviewIcons::class.java)
    val Warning: Icon = IconLoader.getIcon("/icons/claude-warning.svg", ClaudeReviewIcons::class.java)
    val Info: Icon = IconLoader.getIcon("/icons/claude-info.svg", ClaudeReviewIcons::class.java)
}
