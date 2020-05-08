package example.plugin;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ExamplePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        try {
            getLogger().info(new String(ExamplePlugin.class.getResourceAsStream("EnableMessage.txt").readAllBytes(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
