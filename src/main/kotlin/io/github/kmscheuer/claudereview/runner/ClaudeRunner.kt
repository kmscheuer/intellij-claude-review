package io.github.kmscheuer.claudereview.runner

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

object ClaudeRunner {

    fun run(
        command: List<String>,
        stdin: String,
        workingDir: String,
        timeoutSeconds: Int
    ): String? {
        return try {
            val process = ProcessBuilder(command)
                .redirectErrorStream(true)
                .directory(File(workingDir))
                .also { it.environment().remove("CLAUDECODE") }
                .start()

            process.outputStream.bufferedWriter().use { it.write(stdin) }

            val output = process.inputStream.bufferedReader().readText()

            val finished = process.waitFor(timeoutSeconds.toLong(), TimeUnit.SECONDS)
            if (!finished) {
                process.destroyForcibly()
                return null
            }

            output.takeIf { it.isNotBlank() }
        } catch (e: IOException) {
            null
        }
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
