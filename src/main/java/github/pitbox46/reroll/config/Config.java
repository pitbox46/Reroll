package github.pitbox46.reroll.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final String CATEGORY_GENERAL = "general";

    public static ForgeConfigSpec SERVER_CONFIG;

    public static ForgeConfigSpec.IntValue LEVELS_PER_REROLL;
    public static ForgeConfigSpec.IntValue LAPIS_PER_REROLL;

    static {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

        SERVER_BUILDER.comment("General Settings").push(CATEGORY_GENERAL);

        LEVELS_PER_REROLL = SERVER_BUILDER.comment("Levels required/consumed per reroll in an Enchantment Table.")
                .defineInRange("levels_per_reroll", 1, 0, Integer.MAX_VALUE);
        LAPIS_PER_REROLL = SERVER_BUILDER.comment("Lapis Lazuli required/consumed per reroll in an Enchantment Table.")
                .defineInRange("lapis_per_reroll", 0, 0, Integer.MAX_VALUE);

        SERVER_BUILDER.pop();
        SERVER_CONFIG = SERVER_BUILDER.build();
    }
}
