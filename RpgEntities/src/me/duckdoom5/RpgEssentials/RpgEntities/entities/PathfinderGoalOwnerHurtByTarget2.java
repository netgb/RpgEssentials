package me.duckdoom5.RpgEssentials.RpgEntities.entities;

import net.minecraft.server.v1_6_R3.EntityCreature;
import net.minecraft.server.v1_6_R3.EntityLiving;
import net.minecraft.server.v1_6_R3.PathfinderGoalTarget;

public class PathfinderGoalOwnerHurtByTarget2 extends PathfinderGoalTarget {
    EntityCreature a;
    EntityLiving b;
    private int e;

    public PathfinderGoalOwnerHurtByTarget2(EntityCreature entitytameableanimal) {
        super(entitytameableanimal, false);
        this.a = entitytameableanimal;
        this.a(1);
    }

    @Override
    public boolean a() {
        if (!((EntityRpg)this.a).isTamed()) {
            return false;
        }
        EntityLiving entityliving = ((EntityRpg)this.a).getOwner();

        if (entityliving == null) {
            return false;
        }
        this.b = entityliving.getLastDamager();
        int i = entityliving.aF();

        return i != this.e && this.a(this.b, false) && ((EntityRpg)this.a).canTarget(this.b, entityliving);
    }

    @Override
    public void c() {
        this.c.setGoalTarget(this.b);
        EntityLiving entityliving = ((EntityRpg)this.a).getOwner();

        if (entityliving != null) {
            this.e = entityliving.aF();
        }

        super.c();
    }
}
