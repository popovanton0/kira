/**
 * This code was adopted from an open source library Showkase
 * (https://github.com/airbnb/Showkase/tree/master/showkase-processor-testing), which is licensed
 * under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0)
 */

package com.popovanton0.kira.processor

import com.popovanton0.kira.processing.KiraProcessorProvider
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import java.io.File

/**
 * Temporarily set this to true to have the test runner update test resource file expected outputs
 * instead of failing tests on mismatch. Use this to easily update expected outputs.
 */
const val UPDATE_TEST_OUTPUTS = false

abstract class BaseProcessorTest {
    @Rule
    @JvmField
    val testNameRule = TestNameRule()

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
                .isEqualTo(KotlinCompilation.ExitCode.COMPILATION_ERROR)

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
        assertThat(exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val testResourcesDir = getTestResourcesDirectory(getRootResourcesDir())
        val outputDir = File(testResourcesDir, "output")

        if (UPDATE_TEST_OUTPUTS) {
            outputDir.deleteRecursively()
        }
        outputDir.mkdirs()

        val generatedSources = compilation.kspSourcesDir.walk().filter { it.isFile }.toList()

        if (UPDATE_TEST_OUTPUTS) {
            generatedSources.forEach {
                println("Generated: ${it.name}")
                it.copyTo(File(outputDir, it.name))
            }
        } else {
            /*val compilationFails = outputDir.listFiles()?.singleOrNull()
            if (compilationFails?.name == "compilationFails") {
                assertThat(exitCode)
                    .isEqualTo(KotlinCompilation.ExitCode.COMPILATION_ERROR)
                assertThat(messages)
                    .contains(compilationFails.readText())
            }*/

            assertThat(generatedSources.size)
                .isEqualTo(outputDir.listFiles()?.size ?: 0)

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
}