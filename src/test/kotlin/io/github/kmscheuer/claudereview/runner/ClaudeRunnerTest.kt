package io.github.kmscheuer.claudereview.runner

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.condition.OS

class ClaudeRunnerTest {

    @Test
    @DisabledOnOs(OS.WINDOWS)
    fun `returns null on timeout`() {
        val result = ClaudeRunner.run(
            command = listOf("sleep", "10"),
            stdin = "",
            workingDir = "/tmp",
            timeoutSeconds = 1
        )
        assertNull(result)
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    fun `captures stdout from process`() {
        val result = ClaudeRunner.run(
            command = listOf("echo", "51:BUG: test finding"),
            stdin = "",
            workingDir = "/tmp",
            timeoutSeconds = 5
        )
        assertNotNull(result)
        assertTrue(result!!.contains("51:BUG: test finding"))
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    fun `passes stdin to process`() {
        val result = ClaudeRunner.run(
            command = listOf("cat"),
            stdin = "hello from stdin",
            workingDir = "/tmp",
            timeoutSeconds = 5
        )
        assertEquals("hello from stdin", result?.trim())
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    fun `returns null for empty output`() {
        val result = ClaudeRunner.run(
            command = listOf("true"),
            stdin = "",
            workingDir = "/tmp",
            timeoutSeconds = 5
        )
        assertNull(result)
    }
}
