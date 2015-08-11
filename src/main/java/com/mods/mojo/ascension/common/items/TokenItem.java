package com.mods.mojo.ascension.common.items;

import net.minecraft.creativetab.CreativeTabs;
import scala.actors.threadpool.Arrays;

/**
 * Represents an Ascension Token or one of its derivatives
 * 
 * @author CombatZAK
 *
 */
public class TokenItem extends MojoMetaItem {
	public TokenItem() {
		super(DEFAULT_STACK_SIZE, CreativeTabs.tabMaterials, "token",
			Arrays.asList(new String[] {
				"I always knew you were meant for great things.", //mark of ascension
				"Care to join the winning side?", //seal of the dark pact
				"Everyone needs a little help sometimes." //admin-save token
			})
		);
	}
}
