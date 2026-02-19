package de.explore.claudereview.model

data class CollectedInfo(
    val filePath: String,
    val fileRelPath: String,
    val projectRoot: String,
    val diff: String,
    val contentHash: Int
)
