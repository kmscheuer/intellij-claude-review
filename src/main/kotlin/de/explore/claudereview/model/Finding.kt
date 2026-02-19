package de.explore.claudereview.model

data class Finding(
    val line: Int,
    val severity: Severity,
    val message: String
)
