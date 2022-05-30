/**
 * This code was adopted from an open source library Showkase
 * (https://github.com/airbnb/Showkase/tree/master/showkase-processor-testing), which is licensed
 * under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
 */

package com.popovanton0.kira.processortest.base

import com.popovanton0.kira.processing.KiraProcessorProvider
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode.COMPILATION_ERROR
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode.OK
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import java.io.File

abstract class BaseProcessorTest(
    /**
     * Temporarily set this to true to have the test runner update test resource file expected outputs
     * instead of failing tests on mismatch. Use this to easily update expected outputs.
     */
    private val UPDATE_TEST_OUTPUTS: Boolean = false
) {
    @Rule
    @JvmField
    val testNameRule = TestNameRule()

    private val soutNormalizationRegex: Regex = " (\n\r?)".toRegex()

    /**
     * Collects the files in the "input" directory of this test's resources directory
     * and compiles them with Kotlin, returning the result.
     */
    protected fun compileInputs(
        onCompilation: (compilation: KotlinCompilation, result: KotlinCompilation.Result) -> Unit
    ) {
        val testResourcesDir = getTestResourcesDirectory(getRootResourcesDir())

        val inputDir = File(testResourcesDir, "input")
        inputDir.mkdirs()

        val compilation = KotlinCompilation().apply {
            sources = inputDir.listFiles()?.toList().orEmpty().map { SourceFile.fromPath(it) }

            symbolProcessorProviders = listOf(KiraProcessorProvider())

            inheritClassPath = true
            messageOutputStream = System.out // see diagnostics in real time
        }

        val result = compilation.compile()
        onCompilation(compilation, result)
    }

    protected fun assertCompilationFails(errorMessage: String) {
        compileInputs { _, result ->
            assertThat(result.exitCode)
                .isEqualTo(COMPILATION_ERROR)

            assertThat(result.messages)
                .contains(errorMessage)
        }
    }

    protected fun compileInputsAndVerifyOutputs() {
        compileInputs { compilation, result -> result.assertGeneratedSources(compilation) }
    }

    /**
     * Collects the files in the "output" directory of this test's resources directory
     * and validates that they match the generated sources of this compilation result.
     */
    protected fun KotlinCompilation.Result.assertGeneratedSources(compilation: KotlinCompilation) {
        val testResourcesDir = getTestResourcesDirectory(getRootResourcesDir())
        val outputDir = File(testResourcesDir, "output")

        if (UPDATE_TEST_OUTPUTS) outputDir.deleteRecursively {
            it.name != "compilationError" && it.name != "compilationSuccess"
        }
        outputDir.mkdirs()

        val generatedSources = compilation.kspSourcesDir.walk().filter { it.isFile }.toList()

        if (UPDATE_TEST_OUTPUTS) {
            generatedSources.forEach {
                println("Generated: ${it.name}")
                it.copyTo(File(outputDir, it.name))
            }
        } else {
            val expectedFiles = outputDir.listFiles() ?: emptyArray<File>()
            if (expectedFiles.isEmpty()) {
                assertThat(exitCode).isEqualTo(OK)
            }
            val compilationErrorFile = expectedFiles.singleOrNull()
            val markerFileName = compilationErrorFile?.name
            if (markerFileName == "compilationError" || markerFileName == "compilationSuccess") {
                assertThat(exitCode)
                    .isEqualTo(if (markerFileName.contains("Error")) COMPILATION_ERROR else OK)

                val actual = messages.replace(soutNormalizationRegex, "$1")
                val expected = compilationErrorFile.readText().replace(soutNormalizationRegex, "$1")

                assertThat(actual).contains(expected)
                return
            }

            assertThat(generatedSources.size).isEqualTo(expectedFiles.size)

            generatedSources.forEach { actualFile ->
                println("Generated: ${actualFile.name}")
                val expectedFile = File(outputDir, actualFile.name)
                assertThat(expectedFile).exists()
                assertThat(actualFile).hasSameTextualContentAs(expectedFile)
            }
        }
    }

    private fun getRootResourcesDir(): File {
        val path = this::class.java.getResource("")!!
            .path
            .substringBefore("/build/")
            .plus("/src/test/resources")

        return File(path)
    }

    protected open fun getTestResourcesDirectory(rootResourcesDir: File): File {
        val methodName = testNameRule.methodName
        val className = testNameRule.className.substringAfterLast(".")
        return File(rootResourcesDir, "$className/${methodName}")
    }

    private inline fun File.deleteRecursively(predicate: (File) -> Boolean) =
        walkBottomUp().forEach { if (predicate(it)) it.delete() }
}