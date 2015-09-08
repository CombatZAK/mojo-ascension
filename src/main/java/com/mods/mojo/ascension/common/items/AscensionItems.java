package com.mods.mojo.ascension.common.items;

import com.mods.mojo.ascension.common.items.use.TokenUseHandler;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import scala.actors.threadpool.Arrays;

/**
 * Stores definition of items
 * 
 * @author CombatZAK
 *
 */
public class AscensionItems {
	/**
	 * Initialization of the token item
	 */
	private static TokenItem token = new TokenItem(64, CreativeTabs.tabMaterials, "token",
		Arrays.asList(new String[] {
			"§oI always knew you were destined for great things.§r", //mark of ascension
			"§8§oCare to join the winning side?§r\n\n" +
					"Using this object has... consequences." +
					"\nYou should do your research beforehand.", //seal of the dark pact
			"§f§oThe ending has not yet been written...§r" +
					"Using this item will grant you op privileges\n" +
					"on the server §lfor GM purposes only§r§f.", //GM Ticket
			"§9§oEveryone needs a little help sometimes.§r\n\n" +
					"Use to gain an additional admin-save." //admin save token
		}), new TokenUseHandler()
	);
	
	/**
	 * Mark of Ascension public reference
	 */
	public static ItemStack tokenAscension = new ItemStack(token, 1, 0);
	
	/**
	 * Seal of the Dark Pact public reference
	 */
	public static ItemStack tokenPact = new ItemStack(token, 1, 1);
	
	/**
	 * GM Ticket public reference
	 */
	public static ItemStack tokenGmTicket = new ItemStack(token, 1, 2);
	
	/**
	 * Admin-Save Token public reference
	 */
	public static ItemStack tokenAst = new ItemStack(token, 1, 3);
	
	/**
	 * Registers the mod items with FML
	 */
	public static void register() {
		GameRegistry.registerItem(token, token.getItemId());
	}
}
