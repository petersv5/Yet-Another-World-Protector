package de.z0rdak.yawp.mixin;

import de.z0rdak.yawp.api.events.region.FlagCheckEvent;
import de.z0rdak.yawp.api.events.region.RegionEvents;
import de.z0rdak.yawp.core.flag.RegionFlag;
import de.z0rdak.yawp.handler.flags.HandlerUtil;
import de.z0rdak.yawp.managers.data.region.DimensionRegionCache;
import de.z0rdak.yawp.managers.data.region.RegionDataManager;
import de.z0rdak.yawp.util.MessageSender;
import joptsimple.internal.Messages;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.z0rdak.yawp.core.flag.RegionFlag.*;
import static de.z0rdak.yawp.handler.flags.HandlerUtil.*;

@Mixin(HoeItem.class)
public abstract class HoeItemMixin {

    @Inject(method = "useOnBlock", at = @At(value = "HEAD"), cancellable = true, allow = 1)
    public void onUseHoeOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        if (isServerSide(context.getWorld())) {
            if (player != null) {
                FlagCheckEvent checkEvent = new FlagCheckEvent(pos, TOOL_SECONDARY_USE, getEntityDim(player), player);
                if (RegionEvents.CHECK_FLAG.invoker().checkFlag(checkEvent)) {
                    return;
                }
                HandlerUtil.processCheck(checkEvent, null, deny -> {
                    MessageSender.sendFlagMsg(deny);
                    cir.setReturnValue(ActionResult.PASS);
                });
                checkEvent = new FlagCheckEvent(pos, HOE_TILL, getEntityDim(player), player);
                if (RegionEvents.CHECK_FLAG.invoker().checkFlag(checkEvent)) {
                    return;
                }
                HandlerUtil.processCheck(checkEvent, null, deny -> {
                    MessageSender.sendFlagMsg(deny);
                    cir.setReturnValue(ActionResult.PASS);
                });
            }
        }
    }
}
