package de.z0rdak.yawp.mixin;

import de.z0rdak.yawp.core.flag.RegionFlag;
import de.z0rdak.yawp.core.region.DimensionalRegion;
import de.z0rdak.yawp.managers.data.region.DimensionRegionCache;
import de.z0rdak.yawp.managers.data.region.RegionDataManager;
import de.z0rdak.yawp.util.ExplosionBehaviorInterposer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.world.explosion.ExplosionImpl;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static de.z0rdak.yawp.handler.flags.HandlerUtil.checkTargetEvent;

@Mixin(ExplosionImpl.class)
public abstract class ExplosionImplMixin {

    @Shadow
    @Final
    private ServerWorld world;
    
    @Mutable
    @Shadow
    private ExplosionBehavior behavior;
    
    // Note: part of the explosion flag handling system is in ServerWorldMixin

    @Inject(method = "<init>",  at = @At("TAIL"))
    private void interposeExplosionBehavior(
		ServerWorld world,
		@Nullable Entity entity,
		@Nullable DamageSource damageSource,
		@Nullable ExplosionBehavior behavior,
		Vec3d pos,
		float power,
		boolean createFire,
		Explosion.DestructionType destructionType,
		CallbackInfo ci
		) {
        this.behavior = new ExplosionBehaviorInterposer(world, this.behavior);
    }
    
}
