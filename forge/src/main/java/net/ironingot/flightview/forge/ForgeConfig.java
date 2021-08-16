package net.ironingot.flightview.forge;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ForgeConfig {
    public static final ForgeConfigSpec spec;
    public static final IntValue mode;

    public static void register(ModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, spec);
    }

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("general");

        mode = builder.defineInRange("mode", 0, 0, 2);

        builder.pop();
        spec = builder.build();
    }
}