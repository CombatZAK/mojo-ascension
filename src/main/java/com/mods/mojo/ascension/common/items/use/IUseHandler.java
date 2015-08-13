package com.mods.mojo.ascension.common.items.use;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Describes functionality for handling use of an item
 * 
 * @author CombatZAK
 *
 */
public interface IUseHandler {
	/**
	 * Handles a held right-click
	 * 
	 * @param stack Clone of item stack being held
	 * @param world World in which event took place
	 * @param player player holding the item
	 * @return Item to replace after event
	 */
	public ItemStack rightClick(ItemStack stack, World world, EntityPlayer player);
}
