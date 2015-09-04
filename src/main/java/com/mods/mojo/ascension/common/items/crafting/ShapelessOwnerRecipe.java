package com.mods.mojo.ascension.common.items.crafting;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;

/**
 * Standard shapeless recipe with owner checking
 * 
 * @author CombatZAK
 *
 */
public class ShapelessOwnerRecipe extends ShapelessRecipes {
	/**
	 * Creates a new ShaplessOwnerRecipe instance 
	 */
	public ShapelessOwnerRecipe(ItemStack p_i1918_1_, List p_i1918_2_) {
		super(p_i1918_1_, p_i1918_2_);
	}
	
	/**
	 * Generates a ShapelessOwnerRecipe
	 *  
	 * @param output output to create
	 * @param inputs inputs of recipe
	 * @return ShapelessOwnerRecipe representing arguments
	 */
	public static ShapelessOwnerRecipe createRecipe(ItemStack output, List<ItemStack> inputs) {
		return new ShapelessOwnerRecipe(output, inputs);
	}
	
	/**
	 * Checks ownership of any items in recipe
	 */
	@Override
	public boolean matches(InventoryCrafting grid, World world) {
		EntityPlayer player = OwnerRecipeHelper.getPlayer(grid); //get the player trying to craft
		if (player == null) return false; //no player = no recipe
		if (!super.matches(grid, world)) return false; //if the recipe itself doesn't match stop here
		if (player.capabilities.isCreativeMode) return true; //creative players can skip below check
		
		for (int i = 0; i < grid.getSizeInventory(); i++) { //go through all the slots in the inventory
			ItemStack craftitem = grid.getStackInSlot(i); //get the item in the slot
			String owner = OwnerRecipeHelper.getOwner(craftitem); //get the owner of the item
			if (owner != null && !owner.equals(player.getDisplayName())) return false; //player doesn't own item
		}
		
		return true;
	}
}
