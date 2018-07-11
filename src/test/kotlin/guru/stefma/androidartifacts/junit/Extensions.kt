package guru.stefma.androidartifacts.junit

import org.junit.jupiter.api.extension.*
import java.io.File

/**
 * A annotation for the [TempDirectory] extension.
 */
annotation class TempDir

/**
 * Creates a temp dir which can be added to the test method.
 *
 * The [File] should be annotated with [TempDir].
 */
open class TempDirectory : Extension, AfterAllCallback, ParameterResolver {

    protected var tempDir = createTempDir()

    override fun afterAll(context: ExtensionContext) {
        tempDir.deleteRecursively()
    }

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean {
        return (parameterContext.parameter.type == File::class.java) && parameterContext.isAnnotated(TempDir::class.java)
    }

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any {
        return tempDir
    }

}

/**
 * A annotation for the [AndroidTempDirectory] extension
 */
annotation class AndroidBuildScript

/**
 * An extension over [TempDirectory] which creates a default **build.gradle** file
 * and a valid **AndroidManifest**.
 *
 * You can get a copy of the **build.grade** file by adding a [File] with the [AndroidBuildScript] annotation
 * at the test parameter...
 * You can modify it like you want. The files got created for each test (and reseted for each)
 */
class AndroidTempDirectory : TempDirectory(), BeforeAllCallback, BeforeEachCallback, AfterEachCallback {

    var buildScript: File? = null

    override fun beforeAll(context: ExtensionContext) {
        File(tempDir, "/src/main/AndroidManifest.xml").apply {
            parentFile.mkdirs()
            writeText("<manifest package=\"guru.stefma.androidartifacts.test\"/>")
        }
    }

    override fun beforeEach(context: ExtensionContext?) {
        buildScript = File(tempDir, "build.gradle").apply {
            writeText(
                    """
                            plugins {
                                id "com.android.library"
                                id "guru.stefma.androidartifacts"
                            }

                            android {
                                compileSdkVersion 27
                                defaultConfig {
                                    minSdkVersion 21
                                    targetSdkVersion 28
                                }
                            }
                        """
            )
        }
    }

    override fun afterEach(context: ExtensionContext?) {
        buildScript!!.delete()
    }

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean {
        return super.supportsParameter(parameterContext, extensionContext)
                || (parameterContext.parameter.type == File::class.java) && parameterContext.isAnnotated(AndroidBuildScript::class.java)
    }

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any {
        if (parameterContext.isAnnotated(AndroidBuildScript::class.java)) {
            return buildScript!!
        }
        return super.resolveParameter(parameterContext, extensionContext)
    }

}