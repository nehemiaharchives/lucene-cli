## Project Overview
This project is lucene-cli, a wrapper of lucene-kmp and ai-agent-first-cli-tool for quick iteration of indexing creation and search result evaluation by exposing lucene-kmp api via rapid editing of lucene-cli.json config file by AI Agents.

After finding out best `lucene-cli.json` AI agents are expected to implement kmp search application using lucene-kmp. In this way, lucene-cli is expected to speed up search software development based on lucene-kmp.

The other usage of lucene-cli can be ai agents default quick and efficient local search provider for anyting in the user's computer.

The command line tool is `lc` which is single bianry Kotlin/Native executable targeting macos/linux/windows while Kotlin/JVM target enabled for none-production quick testing/development purpose.

lc should comes with these commands:

```bash
lc index create
lc index add
lc index update
lc index delete
lc search
```

with options and arguments.

We value:
1. Ease of Use
2. Convention over configuration
3. Natural language style command composition like git
4. Unix Philosophy "Do one thing, do it well"

So when you make technical decision, plan or design functionality, always follow this value.

## Language

Use **English** to communicate with me.

## Agent‑Human Coworking Flow

### Step 1: Suggest & Discuss

* Propose multiple solutions using your built‑in knowledge.
* Only perform external research if:
    * You are uncertain about an API or behavior, **or**
    * I explicitly request official documentation or references.
* Otherwise, skip research and move straight to proposing fixes.

### Step 2: Code, Run, Debug

1. Apply the chosen code changes. when you code be careful of writing code in platform agnostic kotlin common code. avoid expect/actual pattern as much as possible. do not mix platform specific code such as jvm code in commonMain/commonTest.
2. After any code change, run JetBrains `open_file_in_editor` on the edited file, wait 2 seconds, then run `get_file_problems` on that same file and fix compilation errors immediately. iterate over until you solve all errors.
3. Use `get_run_configuration` tool `jetbrains` MCP server to find proper run configuration. to run specific unit test and use execute_run_configuration to run tests. if any test fail, find out root cause, iterate over until you fix all of them
4. Perform internet search **only** if an error is unclear and you need confirmation of a fix. If you are confident in the solution, skip research and proceed.

## Tool Use Priority

### Priority 1, jetbrains MCP Server (always)
When you have access to jetbrains MCP server, you should use the IDEA's internal test runner. `.run` dir contains.
Example agent runtime environment: locally running ai coding agent in desktop/laptop of a developer such as codex cli, GitHub Copilot Agent.

### Priority 2, Gradle command line (avoid as much as possible)
When you don't have access to jetbrains MCP server, first ask Human developer to enable it and wait until it is enabled! Never use Gradle wrapper (./gradlew).
Assume that GRADLE_USER_HOME is already set system-wide so just use it without overriding.
If you are in cloud environment where you have NO access to jetbrains MCP server, you are allowed to use the command line Gradle wrapper (./gradlew) to compile and run tests.
Example agent runtime environment: desktop/laptop but human developer forgot to launch JetBrains IDEs, or cloud coding agent such as codex web, Google Jules.


### Test workflow

When you run a Gradle test command, do not rely only on your own quick reading of the output.

After the test command finishes, spawn `test_result_reviewer` and give it the complete command, exit code, and relevant stdout/stderr. Wait for its report. Then tell the user:

- whether the test passed or failed
- the failing Gradle task, test class, or exception if any
- the next recommended action

## Git Commit Policy (GPG Signed)

- When user asks to commit, always create a GPG-signed commit.
- Use per-command unsandboxed execution (escalated command) for signing commands.
- Run commit command in a PTY and export `GPG_TTY=$(tty)` in the same command.
- Standard commit flow:
1. `git add <intended files only>`
2. `export GPG_TTY=$(tty) && git commit -S -m "<message>"`
3. `git log --show-signature -1` and confirm `Good signature`.
- Do not fall back to unsigned commit unless the user explicitly asks for unsigned commit.

## Internet Research Guidelines
* Use official sources **only when needed**:
    * Kotlin: [https://kotlinlang.org/docs/](https://kotlinlang.org/docs/) and [https://github.com/JetBrains/kotlin](https://github.com/JetBrains/kotlin)
    * Android: [https://developer.android.com](https://developer.android.com) and [https://android.googlesource.com/platform/frameworks/base](https://android.googlesource.com/platform/frameworks/base)
    * Apple platforms: [https://developer.apple.com/documentation](https://developer.apple.com/documentation)
* When fetching open source code, use [https://raw.githubusercontent.com](https://raw.githubusercontent.com).
* Third‑party sources (Stack Overflow, blogs, etc.) are acceptable only as a last resort.
* Confirm the relevant versions (Kotlin, Gradle, macOS, Xcode, JDK, Clang/LLVM, etc.) before suggesting a solution to ensure compatibility with this project.


## jetbrains-index MCP
IMPORTANT: When applicable, prefer using jetbrains-index MCP tools for code navigation and refactoring.

## lucene-kmp
lucene-kmp source code can be found in ../lucene-kmp so read it if you need.
