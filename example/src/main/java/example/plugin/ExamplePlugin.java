package example.plugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Plugin;

@Plugin(name = "@@name@@", version = "@@version@@")
@ApiVersion(ApiVersion.Target.v1_20)
public class ExamplePlugin extends JavaPlugin {

    @Override
    public void onEnable() {

    }
}
