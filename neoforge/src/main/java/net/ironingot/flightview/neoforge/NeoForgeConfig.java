package net.ironingot.flightview.neoforge;

import net.neoforged.neoforge.common.ModConfigSpec;

public class NeoForgeConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.IntValue mode = BUILDER.comment("FlightView mode").defineInRange("mode", 0, 0, 2);

    public static final ModConfigSpec SPEC = BUILDER.push("general").build();
}
