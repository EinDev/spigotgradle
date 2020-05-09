package example.plugin;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ExamplePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        try (InputStream in = ExamplePlugin.class.getResourceAsStream("EnableMessage.txt")) {
            byte[] bytes = new byte[in.available()];
            int length = in.read(bytes);
            getLogger().info(new String(bytes, 0, length, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
