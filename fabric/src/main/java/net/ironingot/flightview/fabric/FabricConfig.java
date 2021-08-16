package net.ironingot.flightview.fabric;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

@Config(name = "flightview")
public class FabricConfig implements ConfigData {
    private static ConfigHolder<FabricConfig> configHolder;

    public static FabricConfig register(){
        FabricConfig.configHolder = AutoConfig.register(
            FabricConfig.class,
            GsonConfigSerializer::new
        );

        return FabricConfig.configHolder.getConfig();
    }

    public void save() {
        FabricConfig.configHolder.save();
    }

    public void load() {
        FabricConfig.configHolder.load();
    }

    public int mode = 0;
}
