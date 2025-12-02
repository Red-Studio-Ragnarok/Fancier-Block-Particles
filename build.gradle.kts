import org.jetbrains.gradle.ext.settings
import org.gradle.plugins.ide.idea.model.IdeaLanguageLevel
import org.jetbrains.gradle.ext.runConfigurations

plugins {
	id("com.gtnewhorizons.retrofuturagradle") version "2.0.2"
	id("org.jetbrains.gradle.plugin.idea-ext") version "1.3"
	id("com.github.gmazzo.buildconfig") version "6.0.6"
	id("io.freefair.lombok") version "9.1.0"
}

group = "dev.redstudio"
version = "0.8-Dev-19" // Versioning must follow Ragnarök versioning convention: https://github.com/Red-Studio-Ragnarok/Commons/blob/main/Ragnar%C3%B6k%20Versioning%20Convention.md

val id = "fbp"
val plugin = "${project.group}.${id}.asm.FBPPlugin"

val jvmCommonArgs = "-Dfile.encoding=UTF-8 -XX:+UseStringDeduplication"

val redCoreVersion = "1.8-1.12-" + "0.6"

val mixinBooterVersion = "10.7"
val matrixUtilVersion = "1.5.0"
val memoryUtilVersion = "1.1.0"

minecraft {
	mcVersion = "1.12.2"
	username = "Desoroxxx"
	extraRunJvmArguments = listOf("-Dforge.logging.console.level=debug", "-Dfml.coreMods.load=${plugin}", "-Dmixin.checks.mixininterfaces=true", "-Dmixin.debug.export=true", "-XX:+UseStringDeduplication") + jvmCommonArgs.split(" ")
}

repositories {
	arrayOf("Release", "Beta", "Dev").forEach { repoType ->
		maven {
			name = "Red Studio - $repoType"
			url = uri("https://repo.redstudio.dev/${repoType.lowercase()}")
			content {
				includeGroup("dev.redstudio")
			}
		}
	}

	maven {
		name = "Cleanroom"
		url = uri("https://maven.cleanroommc.com")
		content {
			includeGroup("zone.rong")
			includeGroup("com.cleanroommc")
		}
	}

	ivy {
		name = "Meldexun GitHub Releases"
		url = uri("https://github.com/")
		content {
			includeGroup("meldexun")
		}
		patternLayout {
			artifact("[organisation]/[module]/releases/download/v[revision]/[module]-[revision].[ext]")
		}
		metadataSources {
			artifact()
		}
	}

	mavenCentral()
	mavenLocal()
}

dependencies {
	implementation("dev.redstudio:Red-Core-MC:$redCoreVersion")

	implementation("meldexun:MatrixUtil:$matrixUtilVersion")
	implementation("meldexun:MemoryUtil:$memoryUtilVersion")

	annotationProcessor("org.ow2.asm:asm-debug-all:5.2")
	annotationProcessor("com.google.guava:guava:32.1.2-jre")
	annotationProcessor("com.google.code.gson:gson:2.8.9")

	val mixinBooter: String = modUtils.enableMixins("zone.rong:mixinbooter:$mixinBooterVersion", "mixins.${id}.refmap.json") as String
	api(mixinBooter) {
		isTransitive = false
	}
	annotationProcessor(mixinBooter) {
		isTransitive = false
	}
}

buildConfig {
	packageName("${project.group}.${id}")
	className("ProjectConstants")
	documentation.set("This class defines constants for ${project.name}.\n<p>\nThey are automatically updated by Gradle.")
	useJavaOutput()

	// Details
	buildConfigField("ID", id)
	buildConfigField("NAME", project.name)
	buildConfigField("VERSION", project.version.toString())

	// Loggers
	buildConfigField("org.apache.logging.log4j.Logger", "LOGGER", "org.apache.logging.log4j.LogManager.getLogger(NAME)")
	buildConfigField("dev.redstudio.redcore.logging.RedLogger", "RED_LOGGER", """new RedLogger(NAME, "", LOGGER, "Hang in there, just a minor bump on the road to particle greatness!")""")
}

// Set the toolchain version to decouple the Java we run Gradle with from the Java used to compile and run the mod
java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(8))
		vendor.set(JvmVendorSpec.ADOPTIUM)
	}
	if (!project.version.toString().contains("Dev"))
		withSourcesJar() // Generate sources jar, for releases
}

tasks {
	arrayOf(deobfuscateMergedJarToSrg, srgifyBinpatchedJar).forEach {
		it.configure {
			accessTransformerFiles.from(project.files("src/main/resources/META-INF/${id}_at.cfg"))
		}
	}

	processResources.configure {
		val expandProperties = mapOf(
			"version" to project.version,
			"name" to project.name,
			"id" to id
		)

		inputs.properties(expandProperties)

		filesMatching("mcmod.info") {
			expand(expandProperties)
		}
	}

	withType<Jar>  {
		manifest {
			attributes(
				"ModSide" to "BOTH",
				"FMLAT" to "${id}_at.cfg",
				"FMLCorePlugin" to plugin,
				"FMLCorePluginContainsFMLMod" to "true",
				"ForceLoadAsMod" to "true"
			)
		}

		archiveBaseName.set(archiveBaseName.get().replace(" ", "-"))

        from({
            configurations.runtimeClasspath.get().filter { it.name.contains("MatrixUtil") || it.name.contains("MemoryUtil") }.map { if (it.isDirectory) it else zipTree(it) }
        })
	}

	withType<JavaCompile>{
		options.encoding = "UTF-8"

		options.isFork = true
		options.forkOptions.jvmArgs = jvmCommonArgs.split(" ")
	}
}

idea {
	module {
		inheritOutputDirs = true
		excludeDirs.addAll(setOf(".github", ".gradle", ".idea", "build", "gradle", "run", "gradlew", "gradlew.bat", "desktop.ini").map(::file))
	}

	project {
		settings {
			jdkName = "1.8"
			languageLevel = IdeaLanguageLevel("JDK_1_8")

			runConfigurations {
				listOf("Client", "Server", "Obfuscated Client", "Obfuscated Server", "Vanilla Client", "Vanilla Server").forEach { name ->
					create(name, org.jetbrains.gradle.ext.Gradle::class.java) {
						val prefix = name.substringBefore(" ").let { if (it == "Obfuscated") "Obf" else it }
						val suffix = name.substringAfter(" ").takeIf { it != prefix } ?: ""
						taskNames = setOf("run$prefix$suffix")

						jvmArgs = jvmCommonArgs
					}
				}
			}
		}
	}
}
