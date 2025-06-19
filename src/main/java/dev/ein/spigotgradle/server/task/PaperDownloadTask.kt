package dev.ein.spigotgradle.server.task

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import de.undercouch.gradle.tasks.download.Download
import dev.ein.spigotgradle.server.api.PaperListBuildsResponse
import org.apache.hc.core5.http.ParseException
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import java.io.IOException
import java.net.URL

abstract class PaperDownloadTask : Download(), ServerJarProvider {
    init {
        group = "spigot flavor spigot"
        description = "Uses the spigot BuildTool to build a server jar"
    }

    @TaskAction
    fun execute() {
        try {
            val url = buildString {
                    append("https://api.papermc.io/v2/projects/paper/versions/")
                    append(version)
                    append("/builds")
                }
            val meta = URL(url).readText(Charsets.UTF_8)
            val gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()
            val response = gson.fromJson(meta, PaperListBuildsResponse::class.java)
            val build = response.builds?.stream()
                ?.max(Comparator.comparing { c: PaperListBuildsResponse.BuildInfo -> c.build })
                ?.orElseThrow { GradleException("No builds found for version") }
            val buildUrl = build?.downloads?.application?.let {
                String.format(
                    "%s/%s/downloads/%s",
                    url,
                    build.build,
                    it.name
                )
            }
            System.out.printf("Downloading from %s", buildUrl)
            URL(buildUrl).openStream().use { input ->
                serverJar.asFile.map { input.copyTo(it.outputStream()) }
            }
        } catch (e: IOException) {
            throw GradleException("Unable to fetch Paper version: " + e.message)
        } catch (e: ParseException) {
            throw GradleException("Unable to fetch Paper version: " + e.message)
        }
    }
}