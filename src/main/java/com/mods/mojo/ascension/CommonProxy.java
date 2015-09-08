package com.mods.mojo.ascension;

import com.mods.mojo.ascension.common.items.AscensionItems;
import com.mods.mojo.ascension.common.items.crafting.RecipeHandler;
import com.mods.mojo.ascension.events.handlers.LivingSetAttackTargetHandler;

import net.minecraftforge.common.MinecraftForge;

/**
 * Performs environment-specific actions for Ascension mod; currently acts as server-side proxy
 * 
 * @author CombatZAK
 *
 */
public class CommonProxy {
	/**
	 * Run during mod pre-initialization
	 */
	public void preInit() {
		AscensionItems.register();
	}
	
	/**
	 * Run during mod initialization
	 */
	public void init() {
		RecipeHandler.registerRecipes(); //register the new recipes
	}
	
	/**
	 * Run during mod post-initialization
	 */
	public void postInit() {
		MinecraftForge.EVENT_BUS.register(new LivingSetAttackTargetHandler()); //register the overrides to hostility
	}
}
