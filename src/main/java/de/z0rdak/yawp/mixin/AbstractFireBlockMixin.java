package de.z0rdak.yawp.mixin;

import de.z0rdak.yawp.handler.flags.FlagCheckEvent;
import de.z0rdak.yawp.managers.data.region.DimensionRegionCache;
import de.z0rdak.yawp.managers.data.region.RegionDataManager;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.NetherPortal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import com.llamalad7.mixinextras.sugar.Local;

import java.util.Optional;

import static de.z0rdak.yawp.core.flag.RegionFlag.SPAWN_PORTAL;
import static de.z0rdak.yawp.handler.flags.HandlerUtil.checkTargetEvent;

@Mixin(AbstractFireBlock.class)
public abstract class AbstractFireBlockMixin {

    @ModifyVariable(method = "onBlockAdded", at = @At(value = "STORE", target = "Lnet/minecraft/world/dimension/NetherPortal;getNewPortal(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction$Axis;)Ljava/util/Optional;"))
    private Optional<NetherPortal> onSpawnPortal(Optional<NetherPortal> optional, @Local(ordinal = 0) World world, @Local(ordinal = 0) BlockPos pos) {
        if (!world.isClient) {
            DimensionRegionCache dimCache = RegionDataManager.get().cacheFor(world.getRegistryKey());
            FlagCheckEvent flagCheckEvent = checkTargetEvent(pos, SPAWN_PORTAL, dimCache.getDimensionalRegion());
            if (flagCheckEvent.isDenied()) {
                optional = Optional.empty();
            }
        }
        return optional;
    }
}