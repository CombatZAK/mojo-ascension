package com.mods.mojo.ascension.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import scala.actors.threadpool.Arrays;

public class CommandAST extends MojoCommand {
	/**
	 * Indicates if the command is an advanced (admin only) command
	 * 
	 * @param args command arguments
	 * @return true if the command is an admin-only command
	 */
	protected static boolean isAdvancedCommand(String[] args) {
		return args != null && args.length > 0; //advanced commands have arguments
	}
	
	/**
	 * Indicates if the issuer of the command is allowed to use its advanced version
	 * 
	 * @param sender sender of the command
	 * @return true if the sender is allowed to use advanced version
	 */
	protected static boolean canUseAdvancedCommand(ICommandSender sender) {
		return sender != null && (sender.getCommandSenderName() == "Rcon" || isPlayerOpped((EntityPlayer)sender)); //only ops and console are allowed to use admin command
	}
	
	/**
	 * Gets the AST count from a player's NBT data
	 * 
	 * @param playerData player's root NBT data tag
	 * @return number of ASTs the player has
	 */
	protected static int getCountFromNBT(NBTTagCompound playerData) {
		if (playerData == null || !playerData.hasKey("ascensionData")) return 0;
		playerData = playerData.getCompoundTag("ascensionData");
		if (playerData == null || !playerData.hasKey("astCount")) return 0;
		
		return playerData.getInteger("astCount");
	}
	
	/**
	 * Sets the player's AST count in their NBT data tag
	 * 
	 * @param playerData player root NBT data tag
	 * @param newCount new AST count value
	 * 
	 * @throws IllegalArgumentException thrown when the playerData argument is null
	 */
	protected static void setCountInNBT(NBTTagCompound playerData, int newCount) {
		if (playerData == null)
			throw new IllegalArgumentException("Player data tag must be initialized");
		
		NBTTagCompound ascensionData = playerData.getCompoundTag("ascensionData"); //get the ascension data tag
		ascensionData.setInteger("astCount", newCount); //set the value
		
		if (!playerData.hasKey("ascensionData")) playerData.setTag("ascensionData", ascensionData);
	}
	
	/**
	 * Gets the the player's count of ASTs remaining and posts it to the sender of the command
	 * 
	 * @param sender entity to receive information
	 * @param target player to check
	 */
	protected void doGetCount(ICommandSender sender, EntityPlayer target) {
		if (sender == null) return; //no checking if no one to send back to
		if (target == null) { //alert if no player to check
			sender.addChatMessage(new ChatComponentText("No player to check"));
			return;
		}
		
		NBTTagCompound playerData = target.getEntityData(); //get the player data
		int count = getCountFromNBT(playerData);
		
		sender.addChatMessage(new ChatComponentText(target.getDisplayName() + "'s AST count: " + count));
	}
	
	/**
	 * Processes the command
	 */
	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (isAdvancedCommand(args) && !canUseAdvancedCommand(sender)) { //handle unallowed commands
			sender.addChatMessage(new ChatComponentText("You must be an operator to use command arguments on AST."));
			return;
		}
		else if (!isAdvancedCommand(args) && sender.getCommandSenderName() == "Rcon") {
			sender.addChatMessage(new ChatComponentText("You cannot do a self check from the console."));
			return;
		}
		else if (!isAdvancedCommand(args)) {
			doGetCount(sender, (EntityPlayer)sender);
			return;
		}
		
		String commandSubtype = args[0].toLowerCase(); //get the subtype of the command
		if (commandSubtype == "get") {
			doGetCount(sender, args);
		}
		else if (commandSubtype == "give") {
			doGive(sender, args);
		}
		else if (commandSubtype == "take") {
			doTake(sender, args);
		}
	}

	/**
	 * Gets any tab-completion for the command 
	 */
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] tags) {
		List options = new ArrayList(); //get a result
		if (sender.getCommandSenderName() != "Rcon" && !isPlayerOpped((EntityPlayer)sender)) return options; //no options for non-opped players
		
		options = Arrays.asList(new String[] {"add", "take"});
		return options;
	}

	/**
	 * Indicates whether the argument at an index should be a username
	 */
	@Override
	public boolean isUsernameIndex(String[] args, int idx) {
		if (args == null || args.length == 0) return false;
		return idx == 0; //only first argument if any
	}
	
	/**
	 * Gets the usage of the command
	 */
	@Override
	public String getCommandUsage(ICommandSender sender) {
		if (sender.getCommandSenderName() != "Rcon" && !isPlayerOpped((EntityPlayer)sender)) return super.getCommandUsage(sender); //base method
		
		return "ast [<player> (give|take) [amount]]"; //operator usage
	}
	
	/**
	 * Default constructor
	 */
	public CommandAST() {
		super("ast", Arrays.asList(new String[] {"admin-save"}), "ast", ProtectionLevel.NONE);
	}

	/**
	 * Not used
	 */
	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
}
