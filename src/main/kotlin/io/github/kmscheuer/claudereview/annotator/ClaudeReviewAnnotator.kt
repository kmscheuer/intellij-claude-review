package io.github.kmscheuer.claudereview.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import io.github.kmscheuer.claudereview.model.CollectedInfo
import io.github.kmscheuer.claudereview.model.Finding
import io.github.kmscheuer.claudereview.parser.FindingParser
import io.github.kmscheuer.claudereview.runner.ClaudeRunner
import io.github.kmscheuer.claudereview.settings.ClaudeReviewSettings
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class ClaudeReviewAnnotator : ExternalAnnotator<CollectedInfo, List<Finding>>() {

    private val cache = ConcurrentHashMap<String, Pair<Int, List<Finding>>>()

    override fun collectInformation(file: PsiFile, editor: Editor, hasErrors: Boolean): CollectedInfo? {
        val project = file.project
        val settings = ClaudeReviewSettings.getInstance(project)

        if (!settings.state.enabled) return null

        val virtualFile = file.virtualFile ?: return null
        val filePath = virtualFile.path
        val projectRoot = project.basePath ?: return null
        val fileRelPath = virtualFile.path.removePrefix("$projectRoot/")

        val diff = runGitDiff(projectRoot, fileRelPath) ?: return null
        if (diff.isBlank()) return null

        val contentHash = file.text.hashCode()

        return CollectedInfo(
            filePath = filePath,
            fileRelPath = fileRelPath,
            projectRoot = projectRoot,
            diff = diff,
            contentHash = contentHash
        )
    }

    override fun doAnnotate(info: CollectedInfo): List<Finding>? {
        val cached = cache[info.fileRelPath]
        if (cached != null && cached.first == info.contentHash) {
            return cached.second
        }

        val project = com.intellij.openapi.project.ProjectManager.getInstance().openProjects
            .firstOrNull { it.basePath == info.projectRoot } ?: return null
        val settings = ClaudeReviewSettings.getInstance(project)

        val prompt = settings.buildPrompt(info.fileRelPath, project.name)

        val output = ClaudeRunner.review(
            claudePath = settings.state.claudePath,
            prompt = prompt,
            diff = info.diff,
            workingDir = info.projectRoot,
            timeoutSeconds = settings.state.timeoutSeconds
        ) ?: return null

        val findings = FindingParser.parse(output)
        cache[info.fileRelPath] = info.contentHash to findings
        return findings
    }

    override fun apply(file: PsiFile, findings: List<Finding>, holder: AnnotationHolder) {
        val doc = PsiDocumentManager.getInstance(file.project).getDocument(file) ?: return
        val settings = ClaudeReviewSettings.getInstance(file.project)

        for (finding in findings) {
            val lineIdx = finding.line - 1
            if (lineIdx < 0 || lineIdx >= doc.lineCount) continue

            val range = TextRange(
                doc.getLineStartOffset(lineIdx),
                doc.getLineEndOffset(lineIdx)
            )

            val severity = settings.mapSeverity(finding.severity)

            holder.newAnnotation(severity, "Claude: ${finding.message}")
                .range(range)
                .tooltip("<html><b>${finding.severity}</b>: ${finding.message}</html>")
                .gutterIconRenderer(ClaudeGutterIconRenderer(finding))
                .create()
        }
    }

    private fun runGitDiff(projectRoot: String, fileRelPath: String): String? {
        return try {
            val process = ProcessBuilder("git", "diff", "--", fileRelPath)
                .directory(File(projectRoot))
                .redirectErrorStream(true)
                .start()

            val output = process.inputStream.bufferedReader().readText()
            val finished = process.waitFor(5L, TimeUnit.SECONDS)

            if (finished) output else null
        } catch (e: Exception) {
            null
        }
    }
}
