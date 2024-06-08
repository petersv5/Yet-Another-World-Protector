package de.z0rdak.yawp.mixin;

import de.z0rdak.yawp.api.events.region.FlagCheckEvent;
import de.z0rdak.yawp.api.events.region.RegionEvents;
import de.z0rdak.yawp.handler.flags.HandlerUtil;
import de.z0rdak.yawp.managers.data.region.DimensionRegionCache;
import de.z0rdak.yawp.managers.data.region.RegionDataManager;
import de.z0rdak.yawp.util.MessageSender;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.z0rdak.yawp.core.flag.RegionFlag.ENDERMAN_TELEPORT_FROM_REGION;
import static de.z0rdak.yawp.handler.flags.HandlerUtil.getEntityDim;

@Mixin(EndermanEntity.class)
public abstract class EnderManEntityMixin {
    @Inject(method = "teleportTo(DDD)Z", at = @At(value = "HEAD"), cancellable = true, allow = 1)
    public void onEndermanTeleport(double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        EndermanEntity self = (EndermanEntity) (Object) this;
        if (!self.getWorld().isClient) {
            FlagCheckEvent checkEvent = new FlagCheckEvent(self.getBlockPos(), ENDERMAN_TELEPORT_FROM_REGION, getEntityDim(self), null);
            if (RegionEvents.CHECK_FLAG.invoker().checkFlag(checkEvent)) {
                return;
            }
            HandlerUtil.processCheck(checkEvent, null, deny -> {
                MessageSender.sendFlagMsg(deny);
                cir.setReturnValue(false);
            });
        }
    }
}
