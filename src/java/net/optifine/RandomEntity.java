package net.optifine;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

import java.util.UUID;

public class RandomEntity implements IRandomEntity {
    private Entity entity;

    public int getId() {
        UUID uuid = this.entity.getUniqueID();
        long i = uuid.getLeastSignificantBits();
        return (int) (i & 2147483647L);
    }

    public BlockPos getSpawnPosition() {
        return this.entity.getDataManager().spawnPosition;
    }

    public Biome getSpawnBiome() {
        return this.entity.getDataManager().spawnBiome;
    }

    public String getName() {
        return this.entity.hasCustomName() ? this.entity.getCustomName().getString() : null;
    }

    public int getHealth() {
        if (!(this.entity instanceof LivingEntity livingentity)) {
            return 0;
        } else {
            return (int) livingentity.getHealth();
        }
    }

    public int getMaxHealth() {
        if (!(this.entity instanceof LivingEntity livingentity)) {
            return 0;
        } else {
            return (int) livingentity.getMaxHealth();
        }
    }

    public Entity getEntity() {
        return this.entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}
