package de.z0rdak.yawp.util;

import static de.z0rdak.yawp.handler.flags.HandlerUtil.checkTargetEvent;

import java.util.Optional;

import de.z0rdak.yawp.core.flag.RegionFlag;
import de.z0rdak.yawp.core.region.DimensionalRegion;
import de.z0rdak.yawp.managers.data.region.DimensionRegionCache;
import de.z0rdak.yawp.managers.data.region.RegionDataManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

public class ExplosionBehaviorInterposer extends ExplosionBehavior {
	protected ExplosionBehavior nextBehavior;
	protected DimensionalRegion dimRegion;
	
	public ExplosionBehaviorInterposer(ServerWorld world, ExplosionBehavior nextBehavior) 
	{
	    DimensionRegionCache dimCache = RegionDataManager.get().cacheFor(world.getRegistryKey());
	    if (dimCache != null) {
	        dimRegion = dimCache.getDimensionalRegion();
	    }
            this.nextBehavior = nextBehavior;
	}

	
	@Override
	public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
        if (dimRegion != null) {
            if (checkTargetEvent(pos, RegionFlag.EXPLOSION_BLOCK, dimRegion).isDenied()) {
            	return false;
            }
        }
        return nextBehavior.canDestroyBlock(explosion, world, pos, state, power);
	}

	@Override
	public boolean shouldDamage(Explosion explosion, Entity entity) {
        if (dimRegion != null) {
            if (checkTargetEvent(entity.getBlockPos(), RegionFlag.EXPLOSION_ENTITY, dimRegion).isDenied()) {
            	return false;
            }
        }
		return nextBehavior.shouldDamage(explosion, entity);
	}

	public float getKnockbackModifier(Entity entity) {
        if (dimRegion != null) {
            if (checkTargetEvent(entity.getBlockPos(), RegionFlag.EXPLOSION_ENTITY, dimRegion).isDenied()) {
            	return 0;
            }
        }
		return nextBehavior.getKnockbackModifier(entity);
	}

	
	////////////////////////////////////
	// Pass all other calls directly to the underlying ExplosionBehavior
	
	public Optional<Float> getBlastResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState) {
		return nextBehavior.getBlastResistance(explosion, world, pos, blockState, fluidState);
	}

	public float calculateDamage(Explosion explosion, Entity entity, float amount) {
		return nextBehavior.calculateDamage(explosion, entity, amount);
	}

	
}
