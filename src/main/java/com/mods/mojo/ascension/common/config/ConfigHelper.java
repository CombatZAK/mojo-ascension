package com.mods.mojo.ascension.common.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

/**
 * Contains configuration values
 * 
 * @author CombatZAK
 *
 */
public class ConfigHelper {
	/**
	 * Hide constructor; no instantiation
	 */
	private ConfigHelper() {}
	
	/**
	 * Whether or not to teleport the player upon using a seal
	 */
	public static boolean doTeleport;
	
	/**
	 * Whether or not the player should lose their inventory when a seal is used
	 */
	public static boolean doLoseInventory;
	
	/**
	 * Dimension number for banishment
	 */
	public static int darkDimension;
	
	/**
	 * X coordinate for banishment location
	 */
	public static int darkPointX;
	
	/**
	 * Y coordinate for banishment location
	 */
	public static int darkPointY;
	
	/**
	 * Z coordinate for banishment location
	 */
	public static int darkPointZ;
	
	public static void loadConfig(File configFile) {
		Configuration config = new Configuration(configFile); //attach object to config file
		
		config.load(); //attempt to load config
		
		//load values
		doTeleport = config.getBoolean("doTeleport", config.CATEGORY_GENERAL, true, "Should the forsaken be banished?");
		doLoseInventory = config.getBoolean("doLoseInventory", config.CATEGORY_GENERAL, true, "Should the forsaken give up their worldly possessions?");
		darkDimension = config.getInt("darkDimension", config.CATEGORY_GENERAL, 13, 0, Integer.MAX_VALUE, "Dimension number for dark pact banishment");
		darkPointX = config.getInt("darkXCoord", config.CATEGORY_GENERAL, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, "X-coordinate for banishment \"spawn\"");
		darkPointY = config.getInt("darkYCoord", config.CATEGORY_GENERAL, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, "Y-coordinate for banishment \"spawn\"");
		darkPointZ = config.getInt("darkZCoord", config.CATEGORY_GENERAL, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, "Z-coordinate for banishment \"spawn\"");
		
		config.save();
	}
}
