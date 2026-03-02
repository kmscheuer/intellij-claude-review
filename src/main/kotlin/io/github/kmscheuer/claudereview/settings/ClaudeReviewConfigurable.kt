package io.github.kmscheuer.claudereview.settings

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextBrowseFolderListener
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.*

class ClaudeReviewConfigurable(private val project: Project) : Configurable {

    private val enabledCheckbox = JBCheckBox("Enable Claude Review for this project")
    private val claudePathField = TextFieldWithBrowseButton().apply {
        val descriptor = FileChooserDescriptor(true, false, false, false, false, false).apply {
            title = "Select Claude CLI"
            description = "Path to the claude executable"
        }
        addBrowseFolderListener(TextBrowseFolderListener(descriptor, project))
    }
    private val timeoutField = JBTextField()
    private val bugSeverityCombo = createSeverityCombo()
    private val warningSeverityCombo = createSeverityCombo()
    private val infoSeverityCombo = createSeverityCombo()
    private val promptArea = JBTextArea(10, 60).apply {
        lineWrap = true
        wrapStyleWord = true
    }

    private val settings get() = ClaudeReviewSettings.getInstance(project)

    override fun getDisplayName() = "Claude Review"

    override fun createComponent(): JComponent {
        return FormBuilder.createFormBuilder()
            .addComponent(enabledCheckbox)
            .addSeparator()
            .addLabeledComponent(JBLabel("Claude CLI path:"), claudePathField)
            .addLabeledComponent(JBLabel("Timeout (seconds):"), timeoutField)
            .addSeparator()
            .addLabeledComponent(JBLabel("BUG severity:"), bugSeverityCombo)
            .addLabeledComponent(JBLabel("WARNING severity:"), warningSeverityCombo)
            .addLabeledComponent(JBLabel("INFO severity:"), infoSeverityCombo)
            .addSeparator()
            .addLabeledComponent(
                JBLabel("Review prompt (\${FILE}, \${PROJECT} available):"),
                JScrollPane(promptArea),
                true
            )
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    override fun isModified(): Boolean {
        val state = settings.state
        return enabledCheckbox.isSelected != state.enabled
            || claudePathField.text != state.claudePath
            || timeoutField.text != state.timeoutSeconds.toString()
            || bugSeverityCombo.selectedItem != state.bugSeverity
            || warningSeverityCombo.selectedItem != state.warningSeverity
            || infoSeverityCombo.selectedItem != state.infoSeverity
            || promptArea.text != state.prompt
    }

    override fun apply() {
        settings.loadState(
            ClaudeReviewSettings.State(
                enabled = enabledCheckbox.isSelected,
                claudePath = claudePathField.text,
                prompt = promptArea.text,
                timeoutSeconds = timeoutField.text.toIntOrNull() ?: 30,
                bugSeverity = bugSeverityCombo.selectedItem as String,
                warningSeverity = warningSeverityCombo.selectedItem as String,
                infoSeverity = infoSeverityCombo.selectedItem as String
            )
        )
    }

    override fun reset() {
        val state = settings.state
        enabledCheckbox.isSelected = state.enabled
        claudePathField.text = state.claudePath
        timeoutField.text = state.timeoutSeconds.toString()
        bugSeverityCombo.selectedItem = state.bugSeverity
        warningSeverityCombo.selectedItem = state.warningSeverity
        infoSeverityCombo.selectedItem = state.infoSeverity
        promptArea.text = state.prompt
    }

    private fun createSeverityCombo(): JComboBox<String> {
        return JComboBox(arrayOf("ERROR", "WARNING", "WEAK_WARNING", "INFO"))
    }
}
