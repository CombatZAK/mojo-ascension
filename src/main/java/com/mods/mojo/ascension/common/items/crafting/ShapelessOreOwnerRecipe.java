package com.mods.mojo.ascension.common.items.crafting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class ShapelessOreOwnerRecipe extends ShapelessOreRecipe {

	/**
	 * Creates a new ShapelessOreOwnerRecipe instance 
	 */
	public ShapelessOreOwnerRecipe(ItemStack result, Object[] recipe) {
		super(result, recipe);
	}
	
	/**
	 * Checks the current crafting grid matches the recipe, and also that any owned items are owned by the current player
	 */
	@Override
	public boolean matches(InventoryCrafting grid, World world) {
		if (!super.matches(grid, world)) return false; //check super class
		EntityPlayer player = OwnerRecipeHelper.getPlayer(grid); //get the player
		
		if (player == null) return false; //no player, no match
		if (player.capabilities.isCreativeMode) return true; //creative players don't need to own items
		
		for (int i = 0; i < grid.getSizeInventory(); i++) {
			ItemStack stack = grid.getStackInSlot(i);
			String owner = OwnerRecipeHelper.getOwner(stack);
			if (owner != null && !owner.equals(player.getDisplayName())) return false; //player must own item
		}
		
		return true;
	}
	
	/**
	 * Gets a stack that is the result of the crafting recipe; sets the owner of the stack to specified player
	 */
	@Override
	public ItemStack getCraftingResult(InventoryCrafting grid) {
		ItemStack result = super.getCraftingResult(grid); //call parent method
		EntityPlayer player = OwnerRecipeHelper.getPlayer(grid); //get the current player
		
		if (result == null || player == null || player.capabilities.isCreativeMode) return result; //stop under certain conditions
		
		OwnerRecipeHelper.setOwner(result, player.getDisplayName()); //set the owner of the stack
		return result;
	}
}

