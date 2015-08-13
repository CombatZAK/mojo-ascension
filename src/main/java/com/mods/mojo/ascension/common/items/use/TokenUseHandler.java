package com.mods.mojo.ascension.common.items.use;

import com.mods.mojo.ascension.common.items.TokenItem;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Handles using a token
 * 
 * @author CombatZAK
 *
 */
public class TokenUseHandler implements IUseHandler {

	/**
	 * handles a held-right click 
	 */
	@Override
	public ItemStack rightClick(ItemStack stack, World world, EntityPlayer player) {
		if (world.isRemote || stack.stackSize == 0) return stack; //exit conditions
		
		boolean consumeToken = false; //should decrement stack?
		boolean validOwner = false;
		switch(stack.getItemDamage()) { //check metadata
			case 0: //Mark of Ascension
				return stack; //no handler
		
			case 1: //Seal of the Dark Pact
				consumeToken = checkOwner(stack, player);
				if (consumeToken) useSeal(player);
				break;
				
			case 2: //GM Ticket
				consumeToken = checkOwner(stack, player);
				if (consumeToken) useTicket(player);
				break;
				
			case 3: //admin-save token
				consumeToken = checkOwner(stack, player);
				if (consumeToken) useAST(player);
				break;
		}
		
		if (consumeToken && !player.capabilities.isCreativeMode) stack.stackSize--; //decrement stack size
		return stack;
	}
	
	/**
	 * Checks if a player is allowed to use the token they just clicked
	 * 
	 * @param tokenStack token being used
	 * @param holder attempting user
	 * @return true if the user is allowed to use the token
	 */
	private static boolean checkOwner(ItemStack tokenStack, EntityPlayer holder) {
		if (holder.capabilities.isCreativeMode) return true; //creative players can use whatever
		
		String holderName = holder.getDisplayName(); //get the holder's name
		String ownerName = TokenItem.getOwner(tokenStack); //get the owner
		
		if (!holderName.equals(ownerName)) {
			holder.attackEntityFrom(new DamageSource("ascensionToken"), 2); //hurt the player
			return false;
		}
		
		return true;
	}
}
