package me.duckdoom5.RpgEssentials.RpgEntities.entities;

import java.io.Serializable;

import net.minecraft.server.v1_4_R1.Entity;
import net.minecraft.server.v1_4_R1.EntityChicken;
import net.minecraft.server.v1_4_R1.EntityTameableAnimal;
import net.minecraft.server.v1_4_R1.Packet18ArmAnimation;
import net.minecraft.server.v1_4_R1.WorldServer;

import org.bukkit.Location;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class RpgChicken extends RpgEntity implements Serializable{
	private static final long serialVersionUID = 4187071714471932304L;
	
	public RpgChicken(Entity entity){
		super(entity, EntityType.CHICKEN);
	}
	
	public void echo(){
		System.out.println("I'm a RpgChicken");
	}
	
	public void actAsHurt() {
		((WorldServer) getEntity().world).tracker.a(getEntity(), new Packet18ArmAnimation(getEntity(), 2));
	}
	
	public void setAge(int age){
		((Chicken)getBukkitEntity()).setAge(age);
	}
	
	public void lockAge(boolean lock){
		((Chicken)getBukkitEntity()).setAgeLock(lock);
	}
	
	public void setSprinting(boolean sprint){
		getEntity().setSprinting(sprint);
		sprinting = sprint;
		save();
	}
	
	public void setSneaking(boolean sneak) {
		getEntity().setSneaking(sneak);
		sneaking = sneak;
		save();
	}
	
	public void setSitting(boolean sit){
		((EntityTameableAnimal)getEntity()).setSitting(sit);
		sitting = sit;
		save();
	}
	
	public EntityType getType(){
		return EntityType.CHICKEN;
	}
	
	public void lookAtPoint(Location point) {
		if (getEntity().getBukkitEntity().getWorld() != point.getWorld()) {
			return;
		}
		Location npcLoc = ((LivingEntity) getEntity().getBukkitEntity()).getEyeLocation();
		double xDiff = point.getX() - npcLoc.getX();
		double yDiff = point.getY() - npcLoc.getY();
		double zDiff = point.getZ() - npcLoc.getZ();
		double DistanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
		double DistanceY = Math.sqrt(DistanceXZ * DistanceXZ + yDiff * yDiff);
		double newYaw = Math.acos(xDiff / DistanceXZ) * 180 / Math.PI;
		double newPitch = Math.acos(yDiff / DistanceY) * 180 / Math.PI - 90;
		if (zDiff < 0.0) {
			newYaw = newYaw + Math.abs(180 - newYaw) * 2;
		}
		getEntity().yaw = (float) (newYaw - 90);
		getEntity().pitch = (float) newPitch;
		((EntityChicken)getEntity()).az = (float)(newYaw - 90);
	}
}
