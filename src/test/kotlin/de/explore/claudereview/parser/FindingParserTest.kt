package de.explore.claudereview.parser

import de.explore.claudereview.model.Finding
import de.explore.claudereview.model.Severity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FindingParserTest {

    @Test
    fun `parses single BUG finding`() {
        val output = "51:BUG: HTTP status should be CREATED not NOT_FOUND"
        val findings = FindingParser.parse(output)
        assertEquals(
            listOf(Finding(51, Severity.BUG, "HTTP status should be CREATED not NOT_FOUND")),
            findings
        )
    }

    @Test
    fun `parses multiple findings`() {
        val output = """
            51:BUG: Wrong HTTP status
            73:WARNING: Unused parameter
            12:INFO: Consider extracting method
        """.trimIndent()
        val findings = FindingParser.parse(output)
        assertEquals(3, findings.size)
        assertEquals(Severity.BUG, findings[0].severity)
        assertEquals(Severity.WARNING, findings[1].severity)
        assertEquals(Severity.INFO, findings[2].severity)
    }

    @Test
    fun `returns empty list for OK output`() {
        val findings = FindingParser.parse("OK")
        assertEquals(emptyList<Finding>(), findings)
    }

    @Test
    fun `ignores non-matching lines`() {
        val output = """
            Here is my review:
            51:BUG: Real finding
            This is an explanation.

        """.trimIndent()
        val findings = FindingParser.parse(output)
        assertEquals(1, findings.size)
        assertEquals(51, findings[0].line)
    }

    @Test
    fun `handles empty output`() {
        assertEquals(emptyList<Finding>(), FindingParser.parse(""))
    }

    @Test
    fun `handles whitespace around lines`() {
        val output = "  51:BUG: Padded finding  "
        val findings = FindingParser.parse(output)
        assertEquals(1, findings.size)
        assertEquals("Padded finding", findings[0].message)
    }
}
