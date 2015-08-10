package com.mods.mojo.ascension;

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
		//TODO: stub
	}
	
	/**
	 * Run during mod post-initialization
	 */
	public void postInit() {
		//TODO: stub
	}
}
