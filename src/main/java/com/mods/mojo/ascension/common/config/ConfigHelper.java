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
		darkDimension = config.getInt("darkDimension", config.CATEGORY_GENERAL, 13, 0, Integer.MAX_VALUE, "Dimension number for dark pact banishment");
		darkPointX = config.getInt("darkXCoord", config.CATEGORY_GENERAL, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, "X-coordinate for banishment \"spawn\"");
		darkPointY = config.getInt("darkYCoord", config.CATEGORY_GENERAL, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, "Y-coordinate for banishment \"spawn\"");
		darkPointZ = config.getInt("darkZCoord", config.CATEGORY_GENERAL, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, "Z-coordinate for banishment \"spawn\"");
		
		config.save();
	}
}
