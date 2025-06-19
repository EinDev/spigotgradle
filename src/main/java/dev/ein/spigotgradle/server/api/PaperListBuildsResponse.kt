package dev.ein.spigotgradle.server.api

import org.gradle.internal.impldep.com.google.gson.annotations.SerializedName

class PaperListBuildsResponse {
    var projectId: String? = null
    var projectName: String? = null
    var version: String? = null
    var builds: List<BuildInfo>? = null

    class BuildInfo {
        var build: Int = 0
        var time: String? = null
        var channel: String? = null
        var promoted: Boolean = false
        var downloads: Downloads? = null
    }

    class Downloads {
        var application: DownloadEntry? = null

        @SerializedName("mojang-mappings")
        var mojangMappings: DownloadEntry? = null
    }

    class DownloadEntry {
        var name: String? = null
        var sha256: String? = null
    }
}
