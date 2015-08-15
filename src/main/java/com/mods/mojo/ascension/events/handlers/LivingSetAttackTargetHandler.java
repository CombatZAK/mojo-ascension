package com.mods.mojo.ascension.events.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;

/**
 * Handles the living set attack target event
 * @author CombatZAK
 *
 */
public class LivingSetAttackTargetHandler {
	/**
	 * Indicates whether or not the entity can be redirected
	 * 
	 * @param entity entity to redirect
	 * @return true if the entity can be redirected
	 */
	protected static boolean canRetarget(EntityLiving entity) {
		if (entity instanceof EntityZombie) return true;
		if (entity instanceof EntitySkeleton && ((EntitySkeleton)entity).getSkeletonType() != 1) return true;
		if (entity instanceof EntityEldritchGuardian) return true;
		
		return false;
	}
	
	/**
	 * Indicates whether some mobs should be neutral to the player
	 * 
	 * @param player player to check
	 * @return true if the player is evil
	 */
	protected static boolean isUnhostile(EntityPlayer player) {
		NBTTagCompound data = player.getEntityData(); //get the player data
		if (!data.hasKey("ascensionData")) return false; //skip if no ascension data
		
		data = data.getCompoundTag("ascensionData"); //get the ascension data
		if (!data.hasKey("isEvil")) return false; //not evil
		
		return data.getBoolean("isEvil"); //read the tag
	}
	
	/**
	 * Called when a mob locks onto a target
	 * 
	 * @param event arguments
	 */
	@SubscribeEvent
	public void onLivingSetAttackTarget(LivingSetAttackTargetEvent event) {
		if (event == null) return; //ignore misfires
		
		Entity target = event.target;
		if (!(target instanceof EntityPlayer)) return; //skip non-player targets
		EntityPlayer player = (EntityPlayer)target; //cast the target
		
		EntityLiving entity = (EntityLiving)event.entityLiving; //get the entity that is targetting
		if (entity.func_142015_aE() > 0) return;
		if (!canRetarget(entity)) return; //skip any invalid enemy
		if (!isUnhostile(player)) return; //skip non-evil player
		
		entity.setAttackTarget(null); //untarget
	}
}
