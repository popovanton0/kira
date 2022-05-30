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
import org.jetbrains.kotlin.config.JvmTarget
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
        onCompilation: (
            compilation: KotlinCompilation,
            result: KotlinCompilation.Result,
            sources: List<SourceFile>
        ) -> Unit
    ) {
        val testResourcesDir = getTestResourcesDirectory(getRootResourcesDir())

        val inputDir = File(testResourcesDir, "input")
        inputDir.mkdirs()

        val sources = inputDir.listFiles()?.toList().orEmpty().map { SourceFile.fromPath(it) }
        val compilation = KotlinCompilation().apply {
            this.sources = sources
            symbolProcessorProviders = listOf(KiraProcessorProvider())
            jvmTarget = JvmTarget.JVM_11.description
            inheritClassPath = true
            messageOutputStream = System.out // see diagnostics in real time
        }

        val result = compilation.compile()
        onCompilation(compilation, result, sources)
    }

    protected fun assertCompilationFails(errorMessage: String) {
        compileInputs { _, result, _ ->
            assertThat(result.exitCode)
                .isEqualTo(COMPILATION_ERROR)

            assertThat(result.messages)
                .contains(errorMessage)
        }
    }

    protected fun compileInputsAndVerifyOutputs() {
        compileInputs { compilation, result, sources ->
            result.assertGeneratedSources(compilation, sources)
        }
    }

    /**
     * Collects the files in the "output" directory of this test's resources directory
     * and validates that they match the generated sources of this compilation result.
     */
    protected fun KotlinCompilation.Result.assertGeneratedSources(
        compilation: KotlinCompilation,
        sources: List<SourceFile>
    ) {
        val testResourcesDir = getTestResourcesDirectory(getRootResourcesDir())
        val outputDir = File(testResourcesDir, "output")

        if (UPDATE_TEST_OUTPUTS) outputDir.deleteRecursively {
            it.name != "compilationError" && it.name != "compilationSuccess"
        }
        outputDir.mkdirs()

        val generatedSources = compilation.kspSourcesDir.walk().filter { it.isFile }.toList()

        if (UPDATE_TEST_OUTPUTS) {
            generatedSources.forEach {
                println("⚙️ Generated: ${it.name}")
                it.copyTo(File(outputDir, it.name))
            }

            // compiling generated sources with original sources to ensure that they are valid
            val result = KotlinCompilation().apply {
                this@apply.sources = generatedSources.map { SourceFile.fromPath(it) } + sources
                inheritClassPath = true
                messageOutputStream = System.out // see diagnostics in real time
                verbose = false
                jvmTarget = JvmTarget.JVM_11.description
            }.compile()

            assertThat(result.exitCode)
                .withFailMessage("Generated sources do not compile")
                .isEqualTo(OK)
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
                println("⚙️ Generated: ${actualFile.name}")
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