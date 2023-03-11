package de.z0rdak.yawp;

import com.mojang.brigadier.CommandDispatcher;
import de.z0rdak.yawp.commands.CommandRegistry;
import de.z0rdak.yawp.config.server.CommandPermissionConfig;
import de.z0rdak.yawp.config.server.FlagConfig;
import de.z0rdak.yawp.config.server.RegionConfig;
import de.z0rdak.yawp.handler.CommonEvents;
import de.z0rdak.yawp.handler.flags.PlayerFlagHandler;
import de.z0rdak.yawp.managers.data.region.RegionDataManager;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.api.fml.event.config.ModConfigEvents;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

public class YetAnotherWorldProtector implements DedicatedServerModInitializer {

    public static final String MODID = "yawp";
    public static final Logger LOGGER = LogManager.getLogger("YAWP");

    private static CommandDispatcher<ServerCommandSource> cmdDispatcher;

    private static void registerCommands(CommandDispatcher<ServerCommandSource> commandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        if (registrationEnvironment.dedicated) {
            cmdDispatcher = commandDispatcher;
        }
    }

    private static void onModReloading(ModConfig modConfig) {
        if (modConfig.getModId().equals(MODID)) {
            YetAnotherWorldProtector.LOGGER.info("Reloaded: '" + modConfig.getFileName() + "'");
        }
    }

    private static void onModLoading(ModConfig modConfig) {
        if (modConfig.getModId().equals(MODID)) {
            switch (modConfig.getFileName()) {
                case CommandPermissionConfig.CONFIG_NAME: {
                    CommandPermissionConfig.BASE_CMD = CommandPermissionConfig.WP_CMDS[CommandPermissionConfig.WP_COMMAND_ALTERNATIVE.get()];
                    YetAnotherWorldProtector.LOGGER.info("Set mod base command to '/" + CommandPermissionConfig.BASE_CMD + "'");
                    int numOfUuidsWithPermission = CommandPermissionConfig.UUIDsWithPermission().size();
                    String uuidsWithPermission = (numOfUuidsWithPermission > 0
                            ? ": " + CommandPermissionConfig.UUIDsWithPermission().stream().map(uuid -> "'" + uuid + "'").collect(Collectors.joining(", "))
                            : "");
                    YetAnotherWorldProtector.LOGGER.info(numOfUuidsWithPermission + " UUID(s) with permission read from config" + uuidsWithPermission);
                    CommandRegistry.init(cmdDispatcher, CommandPermissionConfig.BASE_CMD);
                }
                break;
                case RegionConfig.CONFIG_NAME: {
                    int numLocalDefaultFlags = RegionConfig.getDefaultFlags().size();
                    String loadedLocalFlags = (numLocalDefaultFlags > 0
                            ? ": " + RegionConfig.getDefaultFlags().stream().map(flag -> "'" + flag + "'").collect(Collectors.joining(", "))
                            : "");
                    YetAnotherWorldProtector.LOGGER.info(numLocalDefaultFlags + " default flag(s) for Local Regions read from config" + loadedLocalFlags);

                    int numDimDefaultFlags = RegionConfig.getDefaultDimFlags().size();
                    String loadedDimFlags = (numDimDefaultFlags > 0
                            ? ": " + RegionConfig.getDefaultDimFlags().stream().map(flag -> "'" + flag + "'").collect(Collectors.joining(", "))
                            : "");
                    YetAnotherWorldProtector.LOGGER.info(numDimDefaultFlags + " default flag(s) for Dimensional Regions read from config" + loadedDimFlags);
                }
                break;
                case FlagConfig.CONFIG_NAME: {
                    int numBreakEntityEntries = FlagConfig.getBreakFlagEntities().size();
                    String loadedBreakEntities = (numBreakEntityEntries > 0
                            ? ": " + FlagConfig.getBreakFlagEntities().stream().map(entry -> "'" + entry + "'").collect(Collectors.joining(", "))
                            : "");
                    YetAnotherWorldProtector.LOGGER.info(numBreakEntityEntries + " Block Entity entries read from config" + loadedBreakEntities);

                    int numBreakEntityTagEntries = FlagConfig.getBreakFlagEntityTags().size();
                    String loadedBreakEntityTags = (numBreakEntityTagEntries > 0
                            ? ": " + FlagConfig.getBreakFlagEntityTags().stream().map(tag -> "'" + tag + "'").collect(Collectors.joining(", "))
                            : "");
                    YetAnotherWorldProtector.LOGGER.info(numBreakEntityTagEntries + " Block Entity tag entries read from config" + loadedBreakEntityTags);
                }
                break;
            }
        }
    }

    @Override
    public void onInitializeServer() {
        // callback to register commands
        CommandRegistrationCallback.EVENT.register(YetAnotherWorldProtector::registerCommands);

        /* Register event handler for managing persistent region data */
        ServerLifecycleEvents.SERVER_STARTING.register(RegionDataManager::initServerInstance);
        ServerLifecycleEvents.SERVER_STARTING.register(CommandPermissionConfig::initServerInstance);
        ServerWorldEvents.LOAD.register(RegionDataManager::loadRegionDataForWorld);
        ServerEntityEvents.ENTITY_LOAD.register(RegionDataManager::onPlayerLoadAddDimKey);
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(RegionDataManager::onPlayerChangeWorldAddDimKey);

        ModConfigEvents.loading(MODID).register(YetAnotherWorldProtector::onModLoading);
        ModConfigEvents.reloading(MODID).register(YetAnotherWorldProtector::onModReloading);

        // registering configuration
        ModLoadingContext.registerConfig(MODID, ModConfig.Type.SERVER, CommandPermissionConfig.CONFIG_SPEC, CommandPermissionConfig.CONFIG_NAME);
        ModLoadingContext.registerConfig(MODID, ModConfig.Type.SERVER, FlagConfig.CONFIG_SPEC, FlagConfig.CONFIG_NAME);
        ModLoadingContext.registerConfig(MODID, ModConfig.Type.SERVER, RegionConfig.CONFIG_SPEC, RegionConfig.CONFIG_NAME);

        CommonEvents.register();
        PlayerFlagHandler.registerEventHandler();

    }


}
