package de.explore.claudereview.settings

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import de.explore.claudereview.model.Severity

@Service(Service.Level.PROJECT)
@State(
    name = "ClaudeReviewSettings",
    storages = [Storage("claudeReview.xml")]
)
class ClaudeReviewSettings : PersistentStateComponent<ClaudeReviewSettings.State> {

    data class State(
        var enabled: Boolean = false,
        var claudePath: String = "claude",
        var prompt: String = DEFAULT_PROMPT,
        var timeoutSeconds: Int = 30,
        var bugSeverity: String = "ERROR",
        var warningSeverity: String = "WARNING",
        var infoSeverity: String = "WEAK_WARNING"
    )

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    fun mapSeverity(severity: Severity): HighlightSeverity = when (severity) {
        Severity.BUG -> resolveSeverity(state.bugSeverity)
        Severity.WARNING -> resolveSeverity(state.warningSeverity)
        Severity.INFO -> resolveSeverity(state.infoSeverity)
    }

    fun buildPrompt(fileRelPath: String, projectName: String): String {
        return state.prompt
            .replace("\${FILE}", fileRelPath)
            .replace("\${PROJECT}", projectName)
    }

    private fun resolveSeverity(name: String): HighlightSeverity = when (name) {
        "ERROR" -> HighlightSeverity.ERROR
        "WARNING" -> HighlightSeverity.WARNING
        "WEAK_WARNING" -> HighlightSeverity.WEAK_WARNING
        "INFO" -> HighlightSeverity.INFORMATION
        else -> HighlightSeverity.WARNING
    }

    companion object {
        const val DEFAULT_PROMPT = """Review this diff for file: ${'$'}{FILE}

Output format — one line per finding, nothing else:
<line_number>:<SEVERITY>: <message>

Where SEVERITY is BUG, WARNING, or INFO.
<line_number> is the line in the original file.

If no issues: output only the word OK

Rules:
- Only genuine bugs, logic errors, significant code quality issues.
- Skip style, formatting, imports, whitespace.
- No markdown, no headers, no explanations."""

        fun getInstance(project: Project): ClaudeReviewSettings {
            return project.getService(ClaudeReviewSettings::class.java)
        }
    }
}
