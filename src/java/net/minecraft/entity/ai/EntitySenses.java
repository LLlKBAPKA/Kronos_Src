package net.minecraft.entity.ai;

import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;

import java.util.List;

public class EntitySenses {
    private final MobEntity entity;
    private final List<Entity> seenEntities = Lists.newArrayList();
    private final List<Entity> unseenEntities = Lists.newArrayList();

    public EntitySenses(MobEntity entityIn) {
        this.entity = entityIn;
    }

    /**
     * Clears canSeeCachePositive and canSeeCacheNegative.
     */
    public void tick() {
        this.seenEntities.clear();
        this.unseenEntities.clear();
    }

    /**
     * Checks, whether 'our' entity can see the entity given as argument (true) or not (false), caching the result.
     */
    public boolean canSee(Entity entityIn) {
        if (this.seenEntities.contains(entityIn)) {
            return true;
        } else if (this.unseenEntities.contains(entityIn)) {
            return false;
        } else {
            boolean flag = this.entity.canEntityBeSeen(entityIn);
            if (flag) {
                this.seenEntities.add(entityIn);
            } else {
                this.unseenEntities.add(entityIn);
            }

            return flag;
        }
    }
}
