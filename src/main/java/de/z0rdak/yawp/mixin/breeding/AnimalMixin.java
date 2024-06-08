package de.z0rdak.yawp.mixin.breeding;

import de.z0rdak.yawp.api.events.region.FlagCheckEvent;
import de.z0rdak.yawp.api.events.region.RegionEvents;
import de.z0rdak.yawp.handler.flags.HandlerUtil;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.z0rdak.yawp.core.flag.RegionFlag.ANIMAL_BREEDING;

@Mixin(AnimalEntity.class)
public abstract class AnimalMixin {

    @Inject(method = "breed(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/AnimalEntity;)V", at = @At("HEAD"), cancellable = true, allow = 1)
    public void spawnChildFromBreeding(ServerWorld world, AnimalEntity parentB, CallbackInfo ci) {
        if (!world.isClient) {            
        AnimalEntity parentA = (AnimalEntity) (Object) this;        
            FlagCheckEvent checkEvent = new FlagCheckEvent(parentA.getBlockPos(), ANIMAL_BREEDING, world.getRegistryKey(), null);
            if (RegionEvents.CHECK_FLAG.invoker().checkFlag(checkEvent)) {
                return;
            }
            HandlerUtil.processCheck(checkEvent, null, deny -> {
                parentA.setBreedingAge(6000);
                parentB.setBreedingAge(6000);
                parentA.resetLoveTicks();
                parentB.resetLoveTicks();
                ci.cancel();
            });
        }
    }
}
