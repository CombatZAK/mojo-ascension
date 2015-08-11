package com.mods.mojo.ascension.common.items;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

/**
 * Represents an item with multiple, nondamageable subitems to be registered in FML
 *  
 * @author CombatZAK
 *
 */
public class MojoMetaItem extends MojoItemBase {
	/**
	 * Stores the item textures
	 */
	private List<IIcon> icons;
	
	/**
	 * Stores the item tooltips
	 */
	private List<String> tooltips;
	
	/**
	 * Gets the item textures
	 * 
	 * @return list of item textures
	 */
	public List<IIcon> getIcons() {
		return this.icons;
	}
	
	/**
	 * Sets the item textures
	 * 
	 * @param value list of item textures
	 */
	public void setIcons(List<IIcon> value) {
		this.icons = value;
	}
	
	/**
	 * Self-referentially sets the item textures
	 * 
	 * @param value list of item textures
	 * @return self-reference
	 */
	public MojoMetaItem withIcons(List<IIcon> value) {
		setIcons(value);
		return this;
	}
	
	/**
	 * Gets the item tooltips
	 * 
	 * @return list of tooltip text
	 */
	public List<String> getTooltips() {
		return this.tooltips;
	}
	
	/**
	 * Sets the item tooltips
	 * 
	 * @param value list of tooltip text
	 */
	public void setTooltips(List<String> value) {
		this.tooltips = value;
	}
	
	/**
	 * Self-referentially sets the item tooltips
	 * 
	 * @param value list of tooltip text
	 * @return self-reference
	 */
	public MojoMetaItem withTooltips(List<IIcon> value) {
		setIcons(value);
		return this;
	}
	
	/**
	 * Creates a new MojoMetaItem instance
	 * 
	 * @param stackSize max stack size for item (all sub-items)
	 * @param creativeTab creative tab on which all sub-items appear
	 * @param itemId id/unlocalized name for item
	 * @param tooltips tooltip text for each item (also shows count of subitems)
	 */
	public MojoMetaItem(int stackSize, CreativeTabs creativeTab, String itemId, List<String> tooltips) {
		super(stackSize, creativeTab, itemId);
		
		if (tooltips == null || tooltips.isEmpty())
			throw new IllegalArgumentException("Texture/tooltip list must be populated");
		
		this.tooltips = tooltips;
		this.icons = new ArrayList<IIcon>();
		this.setHasSubtypes(true); //set the subtypes bit
	}
	
	/**
	 * Registers the icons for each subitem
	 */
	@Override
	public void registerIcons(IIconRegister registry) {
		this.icons.clear(); //reset the textures
		for (int i = 0; i < this.tooltips.size(); i++) {
			this.icons.add(registry.registerIcon(getUnlocalizedName() + "." + i)); //read the icon and store it
		}
	}
	
	/**
	 * Gets the item's texture based on its damage value
	 */
	@Override
	public IIcon getIconFromDamage(int meta) {
		return this.icons.get(meta);
	}
	
	/**
	 * Gets the subitems for this meta-item
	 */
	@Override
	public void getSubItems(Item item, CreativeTabs creativeTab, List list) {
		for (int i = 0; i < this.tooltips.size(); i++) //go through all the subitems
			list.add(new ItemStack(item, 1, i));
	}
	
	/**
	 * Gets the unlocalized name to apply to the stack
	 */
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return this.getUnlocalizedName() + "." + stack.getItemDamage();
	}
}
