package de.z0rdak.yawp.mixin.flag.player;

import de.z0rdak.yawp.api.events.region.FlagCheckEvent;
import de.z0rdak.yawp.core.flag.IFlag;
import de.z0rdak.yawp.managers.data.region.RegionDataManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TeleportTarget;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.z0rdak.yawp.api.events.region.RegionEvents.post;
import static de.z0rdak.yawp.core.flag.RegionFlag.*;
import static de.z0rdak.yawp.handler.flags.HandlerUtil.*;
import static de.z0rdak.yawp.util.MessageSender.sendFlagMsg;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerMixin {

    @Inject(method = "dropSelectedItem", at = @At(value = "TAIL"), allow = 1, cancellable = true)
    private void onDropItem(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if (isServerSide(player)) {
            FlagCheckEvent checkEvent = new FlagCheckEvent(player.getBlockPos(), ITEM_DROP, getDimKey(player), player);
            if (post(checkEvent)) 
                return;
            processCheck(checkEvent, null, deny -> {
                sendFlagMsg(deny);
                cir.setReturnValue(false);
            });
        }
    }

    /**
     * TODO: Fix ENTER_DIM for local regions
     */
    @Inject(method = "moveToWorld", at = @At(value = "HEAD"), allow = 1, cancellable = true)
    private void onChangeDimension(ServerWorld destination, CallbackInfoReturnable<Entity> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (isServerSide(player)) {            
            RegionDataManager.onPlayerChangeWorldAddDimKey(player, (ServerWorld) player.getWorld(), destination);
            
            FlagCheckEvent checkEvent = new FlagCheckEvent(player.getBlockPos(), USE_PORTAL_PLAYERS, getDimKey(player), player);
            if (post(checkEvent)) 
                return;
            processCheck(checkEvent, null, deny -> {
                sendFlagMsg(deny);
                cir.setReturnValue(null);
            });

            checkEvent = new FlagCheckEvent(player.getBlockPos(), ENTER_DIM, getDimKey(destination), player);
            if (post(checkEvent))
                return;
            processCheck(checkEvent, null, deny -> {
                sendFlagMsg(deny);
                cir.setReturnValue(null);
            });
        }
    }

    @Inject(method = "teleport(Lnet/minecraft/server/world/ServerWorld;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getServerWorld()Lnet/minecraft/server/world/ServerWorld;"), allow = 1, cancellable = true)
    private void onTeleportToDimension(ServerWorld destination, double x, double y, double z, float yaw, float pitch, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (isServerSide(player)) {
            FlagCheckEvent checkEvent = new FlagCheckEvent(player.getBlockPos(), USE_PORTAL_PLAYERS, player.getWorld().getRegistryKey(), player);
            if (post(checkEvent)) {
                return;
            }
            processCheck(checkEvent, null, deny -> {
                sendFlagMsg(deny);
                ci.cancel();
            });
        }
    }
}
