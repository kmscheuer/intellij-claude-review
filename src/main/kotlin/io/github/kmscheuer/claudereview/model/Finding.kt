package io.github.kmscheuer.claudereview.model

data class Finding(
    val line: Int,
    val severity: Severity,
    val message: String
)
