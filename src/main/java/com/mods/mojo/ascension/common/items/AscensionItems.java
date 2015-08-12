package com.mods.mojo.ascension.common.items;

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
					"Using this item will banish you to the outer dimensions." +
					"\nYou will leave your inventory behind,\n" +
					"along with your sanity.\n" +
					"Refer to the Dark Pact document for more details.\n\n" +
					"§4§oFeature not yet implemented; coming soon.§r", //seal of the dark pact
			"§f§oThe ending has not yet been written...§r\n\n" +
					"Using this item will grant you op privileges\n" +
					"on the server §lfor GM purposes only§r§f.\n" +
					"Abusing the privilege will result in a permanent\n" +
					"administrative ban from this and all future versions.\n" +
					"of ZakRealms\n" +
					"Refer to the GM Ticket document for more details.\n\n" +
					"§4§oFeature not yet implemented; coming soon.§r", //GM Ticket
			"§9§oEveryone needs a little help sometimes.§r\n\n" +
					"Using this item will consume it and\n"+
					"grant you an admin-save to be used at your discretion.\n\n" +
					"§4§oFeature not yet implemented; coming soon§r" //admin save token
		})
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
