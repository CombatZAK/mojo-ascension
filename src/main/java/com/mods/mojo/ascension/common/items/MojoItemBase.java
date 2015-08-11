package com.mods.mojo.ascension.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public abstract class MojoItemBase extends Item {
	/**
	 * Default max stack size
	 */
	protected static final int DEFAULT_STACK_SIZE = 64;
	
	/**
	 * Default tab on which the item appears in creative
	 */
	protected  static final CreativeTabs DEFAULT_CREATIVE_TAB = CreativeTabs.tabMisc;
	
	/**
	 * Default unlocalized name
	 */
	private static final String DEFAULT_UNLOCALIZED_NAME = "mojo.unlocalized";
	
	/**
	 * Default item id
	 */
	protected static final String DEFAULT_ITEMID = "mojo.itemid";
	
	/**
	 * default path to texture
	 */
	protected static final String DEFAULT_TEXTURE_NAME = "mojo:texturename";
	
	/**
	 * Stores the default tooltip text
	 */
	protected static final String DEFAULT_TOOLTIP = null;
	
	/**
	 * The ID registered with Forge when adding the item
	 */
	protected String itemId;
	
	/**
	 * Gets the itemId registered with Forge
	 * 
	 * @return registered itemId
	 */
	public String getItemId() {
		return itemId;
	}
	
	/**
	 * Sets the itemId registered with Forge (should not be use after item is added)
	 * 
	 * @param value regsitered itemId
	 */
	public void setItemId(String value) {
		this.itemId = value;
	}
	
	/**
	 * Self-referentially sets the itemId registered with Forge
	 * 
	 * @param value registered itemId
	 * @return self-reference
	 */
	public MojoItemBase withItemId(String value) {
		this.setItemId(value);
		return this;
	}
	
	/**
	 * Creates a new MojoItemBase instance
	 * 
	 * @param stackSize max stack size for item
	 * @param creativeTab creative tab for item
	 * @param unlocalizedName unlocalized name for item
	 * @param itemId item ID registered with Forge
	 */
	public MojoItemBase(int stackSize, CreativeTabs creativeTab, String itemId) {
		super();
		setMaxStackSize(stackSize);
		setCreativeTab(creativeTab);
		setUnlocalizedName(itemId);
		this.itemId = itemId;
	}
	
	/**
	 * Default constructor
	 */
	public MojoItemBase() {
		this(DEFAULT_STACK_SIZE, DEFAULT_CREATIVE_TAB, DEFAULT_ITEMID);
	}
}
