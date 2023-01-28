package de.z0rdak.yawp.config.server;

import de.z0rdak.yawp.YetAnotherWorldProtector;
import de.z0rdak.yawp.core.flag.RegionFlag;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class RegionConfig {

    public static final ForgeConfigSpec CONFIG_SPEC;
    public static final String CONFIG_NAME = YetAnotherWorldProtector.MODID + "-region-defaults.toml";
    public static final ForgeConfigSpec.ConfigValue<Integer> REGION_DEFAULT_PRIORITY_INC;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> REGION_DEFAULT_FLAGS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> DIM_REGION_DISABLE_ON_CREATION;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> DIM_REGION_DEFAULT_FLAGS;
    public static final ForgeConfigSpec.ConfigValue<Integer> DEFAULT_REGION_PRIORITY;

    static {
        final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

        BUILDER.push("YetAnotherWorldProtector region configuration").build();

        DEFAULT_REGION_PRIORITY = BUILDER.comment("Default region priority for newly created regions.")
                .defineInRange("default_region_priority", 10, 0, Integer.MAX_VALUE);

        REGION_DEFAULT_FLAGS = BUILDER.comment("Default flags for new local regions.")
                .defineList("default_flags", new ArrayList<>(), RegionConfig::isValidFlag);

        DIM_REGION_DEFAULT_FLAGS = BUILDER.comment("Default flags for new dimensional regions.")
                .defineList("dim_default_flags", new ArrayList<>(), RegionConfig::isValidFlag);

        REGION_DEFAULT_PRIORITY_INC = BUILDER.comment("Default region priority increment/decrement.")
                .defineInRange("default_region_priority_inc", 5, 1, 1000);

        DIM_REGION_DISABLE_ON_CREATION = BUILDER.comment("Enable new dimensional regions on creation.")
                .define("dim_enable_new", true);
        BUILDER.pop();
        CONFIG_SPEC = BUILDER.build();
    }

    public static boolean shouldActivateNewDimRegion(){
        return RegionConfig.DIM_REGION_DISABLE_ON_CREATION.get();
    }


    public static Set<String> getDefaultFlags(){
        return  RegionConfig.REGION_DEFAULT_FLAGS.get().stream()
                .filter(Objects::nonNull)
                .map(String::toString).collect(Collectors.toSet());
    }

    public static Set<String> getDefaultDimFlags(){
        return  RegionConfig.DIM_REGION_DEFAULT_FLAGS.get().stream()
                .filter(Objects::nonNull)
                .map(String::toString).collect(Collectors.toSet());
    }

    private static boolean isValidFlag(Object flag) {
        if (flag instanceof String) {
            return RegionFlag.contains((String) flag);
        }
        return false;
    }
}
