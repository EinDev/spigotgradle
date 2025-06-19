package dev.ein.spigotgradle.server.api;

import org.gradle.internal.impldep.com.google.gson.annotations.SerializedName;

import java.util.List;

public class PaperListBuildsResponse {
    public String projectId;
    public String projectName;
    public String version;
    public List<BuildInfo> builds;

    public static class BuildInfo {
        public int build;
        public String time;
        public String channel;
        public boolean promoted;
        public Downloads downloads;
    }

    public static class Downloads {
        public DownloadEntry application;
        @SerializedName("mojang-mappings")
        public DownloadEntry mojangMappings;
    }

    public static class DownloadEntry {
        public String name;
        public String sha256;
    }
}
