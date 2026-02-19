package de.explore.claudereview.runner

import java.io.File
import java.util.concurrent.TimeUnit

object ClaudeRunner {

    fun run(
        command: List<String>,
        stdin: String,
        workingDir: String,
        timeoutSeconds: Int
    ): String? {
        val process = ProcessBuilder(command)
            .redirectErrorStream(true)
            .directory(File(workingDir))
            .also { it.environment().remove("CLAUDECODE") }
            .start()

        process.outputStream.bufferedWriter().use { it.write(stdin) }

        // Read stdout completely BEFORE calling waitFor() to avoid deadlock
        // if the stdout buffer fills up.
        val output = process.inputStream.bufferedReader().readText()

        val finished = process.waitFor(timeoutSeconds.toLong(), TimeUnit.SECONDS)
        if (!finished) {
            process.destroyForcibly()
            return null
        }

        return output.takeIf { it.isNotBlank() }
    }

    fun review(
        claudePath: String,
        prompt: String,
        diff: String,
        workingDir: String,
        timeoutSeconds: Int
    ): String? {
        return run(
            command = listOf(claudePath, "-p", prompt),
            stdin = diff,
            workingDir = workingDir,
            timeoutSeconds = timeoutSeconds
        )
    }
}
