# Claude Review - IntelliJ Plugin

[![JetBrains Marketplace](https://img.shields.io/jetbrains/plugin/v/30307-claude-review.svg)](https://plugins.jetbrains.com/plugin/30307-claude-review)

AI-powered code review on every file save. Runs [Claude Code](https://claude.ai/code) on your unstaged git diffs and shows findings as inline editor annotations with gutter icons.

**[Install from JetBrains Marketplace](https://plugins.jetbrains.com/plugin/30307-claude-review)**

## Features

- Automatic review on file save via `claude -p`
- Inline annotations with severity-based underlines (bug, warning, info)
- Gutter icons for quick visual scanning
- Content-hash caching to skip unchanged files
- Configurable review prompt with `${FILE}` and `${PROJECT}` variables
- Per-project enable/disable toggle
- Customizable severity mapping

## Requirements

- [Claude Code CLI](https://docs.anthropic.com/en/docs/claude-code) installed
- Git-tracked project
- IntelliJ IDEA 2023.1+

## Installation

### From Marketplace

Search for "Claude Review" in **Settings > Plugins > Marketplace**.

### From ZIP

1. Download the latest release from [Releases](https://github.com/kmscheuer/intellij-claude-review/releases)
2. **Settings > Plugins > Gear icon > Install Plugin from Disk**
3. Select the downloaded ZIP

## Setup

1. Go to **Settings > Tools > Claude Review**
2. Check **Enable Claude Review for this project**
3. Verify the Claude CLI path (auto-detected from common locations)
4. Adjust the review prompt and severity mapping as needed

## How It Works

The plugin uses IntelliJ's `ExternalAnnotator` API:

1. **On save**: collects the `git diff` for the changed file
2. **Background thread**: pipes the diff to `claude -p` with your configured prompt
3. **Editor**: parses Claude's response and creates inline annotations

Claude is expected to return findings in this format:
```
<line_number>:<SEVERITY>: <message>
```

Where `SEVERITY` is `BUG`, `WARNING`, or `INFO`. If no issues are found, Claude returns `OK`.

## Building from Source

```bash
./gradlew buildPlugin
```

The plugin ZIP will be in `build/distributions/`.

## License

MIT
