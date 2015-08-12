package com.mods.mojo.ascension.common.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Represents a single item (no damage values or subtypes) to be registered by Ascension
 * 
 * @author CombatZAK
 *
 */
public class MojoItem extends MojoItemBase {
	
	/**
	 * Helper text for item
	 */
	protected String tooltip;
	
	/**
	 * Gets the tooltip text for the item
	 * 
	 * @return tooltip text
	 */
	public String getTooltip() {
		return this.tooltip;
	}
	
	/**
	 * Sets the tooltip text for the item
	 * 
	 * @param value tooltip text
	 */
	public void setTooltip(String value) {
		this.tooltip = value;
	}
	
	/**
	 * Self-referentially sets the tooltip text for the item
	 * 
	 * @param value tooltip text
	 * @return self-reference
	 */
	public MojoItem withTooltip(String value) {
		this.setTooltip(value);
		return this;
	}
	
	/**
	 * Creates a new MojoItem instance
	 * 
	 * @param stackSize max stack size for item
	 * @param creativeTab creative tab on which item will appear
	 * @param itemId ID to register item with FML
	 * @param textureName path to texture
	 * @param tooltip helper text, if any
	 */
	public MojoItem(int stackSize, CreativeTabs creativeTab, String itemId, String textureName, String tooltip) {
		super(stackSize, creativeTab, itemId);
		setTextureName(textureName);
		this.tooltip = tooltip;
	}
	
	/**
	 * Default constructor
	 */
	public MojoItem() {
		this(DEFAULT_STACK_SIZE, DEFAULT_CREATIVE_TAB, DEFAULT_ITEMID, DEFAULT_TEXTURE_NAME, DEFAULT_TOOLTIP);
	}
	
	/**
	 * Adds the tooltip text to the item when it's moused over
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean para4) {
		if (tooltip == null || tooltip == "") return; //no need to add blank text
		String[] tokens = tooltip.split("\n"); //split the tooltip into lines
		for (String token : tokens)
			info.add(token);
	}
}
