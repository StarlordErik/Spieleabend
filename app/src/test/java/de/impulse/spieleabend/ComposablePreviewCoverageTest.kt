package de.impulse.spieleabend

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class ComposablePreviewCoverageTest {
    @Test
    fun everyComposableHasPreview() {
        val sourceRoot = sourceRoot()
        val moduleRoot = sourceRoot.moduleRoot()
        val missingPreviews = sourceRoot
            .walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .flatMap { sourceFile -> sourceFile.findComposablesWithoutPreview(moduleRoot) }
            .toList()

        assertTrue(
            missingPreviews.failureMessage(),
            missingPreviews.isEmpty(),
        )
    }

    private fun sourceRoot(): File {
        val candidates = listOf(
            File("src/main/java"),
            File("app/src/main/java"),
        )

        return candidates.firstOrNull { it.isDirectory }?.canonicalFile
            ?: error("Could not find Compose source root.")
    }

    private fun File.findComposablesWithoutPreview(moduleRoot: File): Sequence<MissingPreview> {
        val text = readText()
        val composableFunctions = functionRegex
            .findAll(text)
            .map { match ->
                SourceFunction(
                    name = match.groupValues[2],
                    annotations = match.groupValues[1],
                    line = text.lineNumberAt(match.range.first),
                )
            }
            .filter { sourceFunction -> sourceFunction.isComposable }
            .toList()
        val previewFunctionNames = composableFunctions
            .filter { sourceFunction -> sourceFunction.hasPreview }
            .map { sourceFunction -> sourceFunction.name }
            .toSet()

        return composableFunctions
            .asSequence()
            .filterNot { sourceFunction -> sourceFunction.hasPreview }
            .filterNot { sourceFunction -> "${sourceFunction.name}Preview" in previewFunctionNames }
            .map { sourceFunction ->
                MissingPreview(
                    filePath = relativeTo(moduleRoot).path,
                    functionName = sourceFunction.name,
                    line = sourceFunction.line,
                )
            }
    }

    private fun String.lineNumberAt(index: Int): Int =
        substring(0, index).count { character -> character == '\n' } + 1

    private fun File.moduleRoot(): File =
        requireNotNull(parentFile?.parentFile?.parentFile) {
            "Could not resolve module root for $path."
        }

    private fun List<MissingPreview>.failureMessage(): String = buildString {
        appendLine("Each @Composable must have a sibling @Preview function named <ComposableName>Preview.")
        appendLine("Missing previews:")
        this@failureMessage.forEach { missingPreview ->
            appendLine(
                "${missingPreview.filePath}:${missingPreview.line} " +
                    "${missingPreview.functionName} -> ${missingPreview.functionName}Preview",
            )
        }
    }

    private data class SourceFunction(
        val name: String,
        val annotations: String,
        val line: Int,
    ) {
        val isComposable: Boolean = composableAnnotationRegex.containsMatchIn(annotations)
        val hasPreview: Boolean = previewAnnotationRegex.containsMatchIn(annotations)
    }

    private data class MissingPreview(
        val filePath: String,
        val functionName: String,
        val line: Int,
    )

    private companion object {
        val functionRegex = Regex(
            pattern = ANNOTATIONS_PATTERN + VISIBILITY_PATTERN + FUNCTION_PATTERN,
            options = setOf(RegexOption.DOT_MATCHES_ALL),
        )
        val composableAnnotationRegex = Regex("""@(?:[\w.]+\.)?Composable\b""")
        val previewAnnotationRegex = Regex("""@(?:[\w.]+\.)?Preview\b""")

        private const val ANNOTATIONS_PATTERN = """((?:\s*@[\w.]+(?:\([^)]*\))?\s*)+)"""
        private const val VISIBILITY_PATTERN = """\s*(?:private\s+|internal\s+|public\s+)?"""
        private const val FUNCTION_PATTERN = """fun\s+(?:<[^>]+>\s*)?([A-Za-z_][A-Za-z0-9_]*)\s*\("""
    }
}
