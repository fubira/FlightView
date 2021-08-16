package net.ironingot.flightview.forge;

import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.config.ModConfig;
import java.util.function.Function;

public class ForgeConfig
{
    private final ModConfig config;

    ForgeConfig(ModConfig config) {
        this.config = config;
    }

    public void save() {
        config.save();
    }

    public <T> void set(ConfigValue<T> property, T value) {
        config.getConfigData().set(property.getPath(), value);
    }

    public <T> void update(ConfigValue<T> property, Function<T, T> func) {
        set(property, func.apply(property.get()));
    }
}