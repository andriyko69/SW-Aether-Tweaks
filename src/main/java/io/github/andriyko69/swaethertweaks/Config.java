package io.github.andriyko69.swaethertweaks;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue SUN_ALTAR_PERMISSION_LEVEL = BUILDER
        .comment("Required operator permission level for changing time with the Sun Altar.")
        .defineInRange("sun_altar_permission_level", 4, 0, 4);

    static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {
    }

    public static int sunAltarPermissionLevel() {
        return SUN_ALTAR_PERMISSION_LEVEL.get();
    }

    public static boolean canUseSunAltar(Player player) {
        return player.hasPermissions(sunAltarPermissionLevel());
    }
}
