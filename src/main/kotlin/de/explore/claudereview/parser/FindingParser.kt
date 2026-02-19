package de.explore.claudereview.parser

import de.explore.claudereview.model.Finding
import de.explore.claudereview.model.Severity

object FindingParser {

    private val PATTERN = Regex("""^(\d+):(BUG|WARNING|INFO):\s+(.+)$""")

    fun parse(output: String): List<Finding> {
        return output.lines()
            .mapNotNull { line ->
                PATTERN.matchEntire(line.trim())?.let { match ->
                    Finding(
                        line = match.groupValues[1].toInt(),
                        severity = Severity.valueOf(match.groupValues[2]),
                        message = match.groupValues[3].trim()
                    )
                }
            }
    }
}
