package com.mods.mojo.ascension.common.items.crafting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

/**
 * 
 * @author CombatZAK
 *
 */
public class ShapedOwnerRecipe extends ShapedRecipes {

	/**
	 * Creates a new ShapedOwnerRecipe object 
	 */
	public ShapedOwnerRecipe(int p_i1917_1_, int p_i1917_2_, ItemStack[] p_i1917_3_, ItemStack p_i1917_4_) {
		super(p_i1917_1_, p_i1917_2_, p_i1917_3_, p_i1917_4_);

	}
	
	/**
	 * Generates a ShapedOwnerRecipe based on inputs and output
	 * 
	 * @param output result of recipe
	 * @param inputs pattern, labels and item inputs
	 * 
	 * @return recipe represented by arguments
	 */
	public static ShapedOwnerRecipe createRecipe(ItemStack output, Object[] inputs) {
		int recipeHeight = 0; //recipe data
		int recipeWidth = 0;
		List<ItemStack> inputList = new ArrayList<ItemStack>();
		
		String[] pattern = (String[])inputs[0]; //get the pattern set
		recipeHeight = pattern.length; //number of rows
		recipeWidth = pattern[0].length(); //number of columns
		
		HashMap<Character, ItemStack> inputHash = new HashMap<Character, ItemStack>(); //stores the map of characters to items
		for (int idx = 1; idx < inputs.length; idx += 2) { //go through all the inputs following the first
			char label = (Character)inputs[idx];
			ItemStack ingredient = (ItemStack)inputs[idx + 1];
			inputHash.put(label, ingredient); //add the ingredient/label pair to the map
		}
		
		for (int rowIdx = 0; rowIdx < recipeHeight; rowIdx++) { //go through each row
			for (int colIdx = 0; colIdx < recipeWidth; colIdx++) { //go through each column
				char patternChar = pattern[rowIdx].charAt(colIdx); //get the label
				
				if (patternChar == ' ') //spaces are empty
					inputList.add(null);
				else inputList.add(inputHash.get(patternChar)); //get the item from the map
			}
		}
		
		return new ShapedOwnerRecipe(recipeWidth, recipeHeight, inputList.toArray(new ItemStack[0]), output); //generate the recipe
	}
	
	/**
	 * Checks whether an inventory matches the recipe; need to override this method to use new checkMatch operation
	 */
	@Override
	public boolean matches(InventoryCrafting inventory, World world) {
		for (int i = 0; i <= 3 - this.recipeWidth; ++i)
        {
            for (int j = 0; j <= 3 - this.recipeHeight; ++j)
            {
                if (this.checkMatch(inventory, i, j, true))
                {
                    return true;
                }

                if (this.checkMatch(inventory, i, j, false))
                {
                    return true;
                }
            }
        }

        return false;
	}
	
	/**
	 * Effective override of ShapedRecipes.checkMatch 
	 */
	protected boolean checkMatch(InventoryCrafting grid, int i, int j, boolean reversed) {
		EntityPlayer player = OwnerRecipeHelper.getPlayer(grid);
		if (player == null) return false; //player must manually craft
		for (int k = 0; k < 3; ++k)
        {
            for (int l = 0; l < 3; ++l)
            {
                int i1 = k - i;
                int j1 = l - j;
                ItemStack itemstack = null;

                if (i1 >= 0 && j1 >= 0 && i1 < this.recipeWidth && j1 < this.recipeHeight)
                {
                    if (reversed)
                    {
                        itemstack = this.recipeItems[this.recipeWidth - i1 - 1 + j1 * this.recipeWidth];
                    }
                    else
                    {
                        itemstack = this.recipeItems[i1 + j1 * this.recipeWidth];
                    }
                }

                ItemStack itemstack1 = grid.getStackInRowAndColumn(k, l);

                if (itemstack1 != null || itemstack != null)
                {
                    if (itemstack1 == null && itemstack != null || itemstack1 != null && itemstack == null)
                    {
                        return false;
                    }

                    if (itemstack.getItem() != itemstack1.getItem())
                    {
                        return false;
                    }

                    if (itemstack.getItemDamage() != 32767 && itemstack.getItemDamage() != itemstack1.getItemDamage())
                    {
                        return false;
                    }
                    
                    String owner = OwnerRecipeHelper.getOwner(itemstack1);
                    if (!OwnerRecipeHelper.isCreative(player) && owner != null && !owner.equals(player.getDisplayName())) return false; //if there is not a player crafting and the player is not creative and doesn't own the item; reject
                }
            }
        }
		
		return true;
	}
	
	/**
	 * Gets the resulting item, with owner name applied
	 */
	@Override
	public ItemStack getCraftingResult(InventoryCrafting grid) {
		EntityPlayer player = OwnerRecipeHelper.getPlayer(grid); //get the player crafting
		if (player == null) return null; //no item unless player is manually crafting
		
		ItemStack result = super.getCraftingResult(grid); //call the super function to get output
		if (!OwnerRecipeHelper.isCreative(player)) OwnerRecipeHelper.setOwner(result, player.getDisplayName()); //apply the owner to the item
		
		return result;
	}
}
