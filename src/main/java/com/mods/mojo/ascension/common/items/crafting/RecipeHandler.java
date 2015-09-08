package com.mods.mojo.ascension.common.items.crafting;

import com.mods.mojo.ascension.common.items.AscensionItems;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import scala.actors.threadpool.Arrays;

/**
 * Registers recipes for ascension
 * 
 * @author CombatZAK
 *
 */
public class RecipeHandler {
	private RecipeHandler() {} //hide the constructor; no instantiation
	
	public static void registerRecipes() {
		GameRegistry.addRecipe(new ShapelessOreOwnerRecipe(AscensionItems.tokenAst, AscensionItems.tokenAscension, "dyeLime", new ItemStack(Blocks.red_mushroom)));
		GameRegistry.addRecipe(new ShapelessOwnerRecipe(AscensionItems.tokenGmTicket, Arrays.asList(new ItemStack[] { AscensionItems.tokenAscension, new ItemStack(Items.writable_book) })));
		GameRegistry.addRecipe(new ShapelessOreOwnerRecipe(AscensionItems.tokenPact, AscensionItems.tokenAscension, new ItemStack(Items.ghast_tear), new ItemStack(Items.blaze_powder), "dustObsidian", "dustCoal"));
	}
}
