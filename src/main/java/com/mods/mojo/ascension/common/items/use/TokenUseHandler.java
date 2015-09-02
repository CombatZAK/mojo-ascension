package com.mods.mojo.ascension.common.items.use;

import com.mods.mojo.ascension.common.items.TokenItem;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;
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
				if (consumeToken) consumeToken = useSeal(player);
				break;
				
			case 2: //GM Ticket
				consumeToken = checkOwner(stack, player);
				if (consumeToken) consumeToken = useTicket(player);
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
			holder.attackEntityFrom(new DamageSource("ascensionToken").setDamageBypassesArmor().setDamageIsAbsolute(), 2); //hurt the player
			holder.addChatMessage(new ChatComponentText("§4§oThis does not belong to you.§r"));
			return false;
		}
		
		return true;
	}
	
	/**
	 * Invokes the Dark Pact, turning the player into an Enemy
	 * 
	 * @param player player to change
	 * @return true if change is successful
	 */
	private static boolean useSeal(EntityPlayer player) {
		NBTTagCompound customData = player.getEntityData(); //get the custom data tag
		NBTTagCompound ascensionData = customData.getCompoundTag("ascensionData"); //get the ascension data tag
		if (ascensionData.hasKey("isEvil") && ascensionData.getBoolean("isEvil")) {
			player.addChatMessage(new ChatComponentText("§4§oOur arrangement has already been struck.§r"));
			return false;
		}
		
		ascensionData.setBoolean("isEvil", true); //set the tag
		MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentText("§4§o" + player.getDisplayName() + " has consigned themselves to madness.§r"));
		
		//TODO: implement teleport
		
		if (!customData.hasKey("ascensionData")) customData.setTag("ascensionData",  ascensionData); //populate the tag if it isn't already
		
		return true;
	}
	
	/**
	 * OPs the player for GM purposes
	 *  
	 * @param player Player to make GM
	 * @return true if the change was successful
	 */
	private static boolean useTicket(EntityPlayer player) {
		ServerConfigurationManager mgr = MinecraftServer.getServer().getConfigurationManager();
		
		if (mgr.func_152603_m().func_152700_a(player.getDisplayName()) != null) {
			player.addChatMessage(new ChatComponentText("§fYou are already OPed§r"));
			return false;
		}
		mgr.func_152605_a(player.getGameProfile()); //ops the player
		player.addChatMessage(new ChatComponentText("§fYou have been OPed; don't screw it up.§r"));
		mgr.sendChatMsg(new ChatComponentText(player.getDisplayName() + " is now a Game Master."));
		
		return true;
	}
	
	/**
	 * Adds an AST to the player
	 * 
	 * @param player player to grant
	 * @return true if grant was successful
	 */
	private static boolean useAST(EntityPlayer player) {
		NBTTagCompound playerData = player.getEntityData(); //get the custom data
		NBTTagCompound ascensionData = playerData.getCompoundTag("ascensionData"); //get the ascension data tag
		
		int astCount = 0; //record ast count
		if (ascensionData.hasKey("astCount")) astCount = ascensionData.getInteger("astCount"); //read from the tag if it exists
		astCount++; //increment count
		
		ascensionData.setInteger("astCount", astCount);
		if (!playerData.hasKey("ascensionData")) playerData.setTag("ascensionData", ascensionData); //add the ascension tag if it wasn't there
		player.addChatMessage(new ChatComponentText("§fYou gained an Admin-Save; you now have " + astCount + ".§r")); //inform the player
		
		return true;
	}
}
