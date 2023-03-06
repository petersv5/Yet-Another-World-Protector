package de.z0rdak.yawp.mixin;

import de.z0rdak.yawp.core.flag.RegionFlag;
import de.z0rdak.yawp.handler.flags.FlagCheckEvent;
import de.z0rdak.yawp.managers.data.region.DimensionRegionCache;
import de.z0rdak.yawp.managers.data.region.RegionDataManager;
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

import static de.z0rdak.yawp.handler.flags.HandlerUtil.*;

@Mixin(HoeItem.class)
public abstract class HoeItemMixin {

    @Inject(method = "useOnBlock", at = @At(value = "HEAD"), cancellable = true, allow = 1)
    public void onUseHoeOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        if (!world.isClient) {
            if (player != null) {
                DimensionRegionCache dimCache = RegionDataManager.get().cacheFor(getEntityDim(player));
                FlagCheckEvent.PlayerFlagEvent flagCheck = checkPlayerEvent(player, blockPos, RegionFlag.TOOL_SECONDARY_USE, dimCache.getDimensionalRegion());
                if (flagCheck.isDenied()) {
                    sendFlagDeniedMsg(flagCheck);
                    cir.setReturnValue(ActionResult.PASS);
                }

                flagCheck = checkPlayerEvent(player, blockPos, RegionFlag.HOE_TILL, dimCache.getDimensionalRegion());
                if (flagCheck.isDenied()) {
                    sendFlagDeniedMsg(flagCheck);
                    cir.setReturnValue(ActionResult.PASS);
                }

            }

        }
    }
}
