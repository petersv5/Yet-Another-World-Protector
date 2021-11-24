package de.z0rdak.regionshield.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.Nullable;

@Mixin(SlimeEntity.class)
public abstract class MixinSlimeEntity extends MobEntity implements IMob {

    protected MixinSlimeEntity(EntityType<? extends MobEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Nullable
    @Override
    public Entity changeDimension(ServerWorld server) {
        if (!ForgeHooks.onTravelToDimension(this, server.dimension())) {
            return null;
        }
        return super.changeDimension(server);
    }
}
