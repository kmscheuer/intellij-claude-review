---
paths:
  - "**/*.kt"
  - "**/*.java"
---

# ProcessBuilder in IntelliJ Plugins

- Always wrap `ProcessBuilder.start()` in try-catch for `IOException` — the executable may not exist or not be on PATH
- Use absolute paths for CLI tool references in plugin defaults (IntelliJ processes don't inherit the user's terminal PATH)
- Return null / no-op on failure instead of letting exceptions propagate through the IntelliJ daemon
