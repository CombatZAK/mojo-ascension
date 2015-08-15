package com.mods.mojo.ascension;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/**
 * Ascension mod class registered mod information with FML
 * 
 * @author CombatZAK
 *
 */
@Mod(modid=Ascension.MODID, name=Ascension.MOD_NAME, version=Ascension.MOD_VERSION, dependencies=Ascension.DEPENDENCIES)
public class Ascension {
	public static final String MODID = "mojo-ascension"; //unique mod id
	public static final String MOD_NAME = "Mojo-Ascension"; //Mod friendly name
	public static final String MOD_VERSION = "0.0.0"; //Mod version
	public static final String DEPENDENCIES = "required-after:Thaumcraft"; //parent and child requirements
	
	@Instance
	public static Ascension instance; //Mod singleton instance
	
	@SidedProxy(clientSide="com.mods.mojo.ascension.client.ClientProxy", serverSide="com.mods.mojo.ascension.CommonProxy")
	public static CommonProxy proxy; //proxy handle
	
	/**
	 * Handles the pre-init event
	 * 
	 * @param event args
	 */
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		proxy.preInit();
	}
	
	/**
	 * Handles the init event
	 * 
	 * @param event args
	 */
	@EventHandler
	public void load(FMLInitializationEvent event) {
		proxy.init();
	}
	
	/**
	 * Handles the post-init event
	 * 
	 * @param event args
	 */
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();
	}
}
