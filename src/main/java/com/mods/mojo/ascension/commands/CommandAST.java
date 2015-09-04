package com.mods.mojo.ascension.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
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
		return sender != null && (sender instanceof DedicatedServer || isPlayerOpped((EntityPlayer)sender)); //only ops and console are allowed to use admin command
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
	 * Grants the target addtional ASTs
	 * 
	 * @param sender issuer of the command
	 * @param target target to receive ASTs
	 * @param amount number of ASTs to give
	 */
	protected void doGive(ICommandSender sender, EntityPlayer target, int amount) {
		if (amount < 0) { //validate amount
			sender.addChatMessage(new ChatComponentText("Cannot give a negative number of tokens."));
			return;
		}
		
		NBTTagCompound playerData = target.getEntityData(); //get the player data
		int count = getCountFromNBT(playerData); //get the current count
		count += amount; //add the new amount
		
		setCountInNBT(playerData, count);
		target.addChatMessage(new ChatComponentText("You have been granted " + amount + " additional AST(s)."));
		target.addChatMessage(new ChatComponentText("New AST count: " + count));
		
		if (target == sender) return; //no notification to sender if it was a self-targeted command
		sender.addChatMessage(new ChatComponentText("Granted " + amount + " AST(s) to " + target.getDisplayName())); //notify sender
	}
	
	/**
	 * Removes ASTs from the target
	 * 
	 * @param sender issuer of command
	 * @param target target to lose ASTs
	 * @param amount number of ASTs to deduct
	 * @param allowOverflow whether or not the deduction amount can exceed the target's total ASTs (new count is 0 if so)
	 */
	protected void doTake(ICommandSender sender, EntityPlayer target, int amount, boolean allowOverflow) {
		if (amount < 0) { //validate the amount
			sender.addChatMessage(new ChatComponentText("Cannot take a negative number of tokens."));
			return;
		}
		
		NBTTagCompound playerData = target.getEntityData(); //get the player data
		int count = getCountFromNBT(playerData); //get the current count
		
		if (!allowOverflow && amount > count) { //check if target can afford
			sender.addChatMessage(new ChatComponentText(target.getDisplayName() + " does not have enough ASTs for this operation.")); //notify sender
			return;
		}
		
		count -= amount; //update count
		if (count < 0) {
			amount += count; //update amount removed - difference
			count = 0; //floor at 0
		}
		setCountInNBT(playerData, count);
		target.addChatMessage(new ChatComponentText("You have been deducted " + amount + " AST(s)."));
		target.addChatMessage(new ChatComponentText("New AST count: " + count));
		
		if (target == sender) return; //no notification to sender if it was a self-targeted command
		sender.addChatMessage(new ChatComponentText("Removed " + amount + " AST(s) from " + target.getDisplayName()));
	}
	
	/**
	 * Gets the count of ASTS remaining for a player
	 * 
	 * @param sender player sending command
	 * @param args command arguments; 0 = command type; 1 = target player (absent for self-check)
	 */
	protected void doGetCount(ICommandSender sender, String[] args) {
		if (args.length == 1 && sender instanceof DedicatedServer) { //no player, console
			sender.addChatMessage(new ChatComponentText("You cannot do a self-check from the console."));
			return;
		}
		else if (args.length == 1) { //no player, player command
			doGetCount(sender, (EntityPlayer)sender);
			return;
		}
		
		String playerName = args[1]; //second arg is player name
		EntityPlayer target = sender.getEntityWorld().getPlayerEntityByName(playerName);
		if (target == null) { //can't find player
			sender.addChatMessage(new ChatComponentText("Player " + playerName + " was not found."));
			return;
		}
		
		doGetCount(sender, target);
	}
	
	/**
	 * Grants ASTs to a player
	 * 
	 * @param sender issuer of command
	 * @param args command arguments; 0 = command type, 1 = target, 2 = number of ASTs to give
	 */
	protected void doGive(ICommandSender sender, String[] args) {
		if (args.length == 1) { //check arguments
			sender.addChatMessage(new ChatComponentText("usage: ast give <player> [<amount>]"));
			return;
		}
		
		int amount = 1; //default amount
		if (args.length >= 3) {
			try {
				amount = Integer.parseInt(args[2]); //attempt to parse value
			}
			catch (NumberFormatException ex) { //invalid value
				sender.addChatMessage(new ChatComponentText("usage: ast give <player> [<amount>]"));
				return;
			}
		}
		
		String playerName = args[1];
		EntityPlayer target = sender.getEntityWorld().getPlayerEntityByName(playerName);
		
		if (target == null) {
			sender.addChatMessage(new ChatComponentText("Player " + playerName + " was not found."));
			return;
		}
		
		doGive(sender, target, amount);
	}
	
	/**
	 * Deducts ASTs from a player
	 * 
	 * @param sender issuer of command
	 * @param args command arguments; 0 = command type, 1 = target player, 2 = amount of ASTs to remove, 3 = overflow: amount can exceed current count
	 */
	protected void doTake(ICommandSender sender, String[] args) {
		if (args.length == 1) { //check arguments
			sender.addChatMessage(new ChatComponentText("usage: ast take <player> [<amount>] [true|false]"));
			return;
		}
		
		int amount = 1; //default amount
		if (args.length >= 3) {
			try {
				amount = Integer.parseInt(args[2]); //attempt to parse amount value
			}
			catch (NumberFormatException ex) { //invalid amount
				sender.addChatMessage(new ChatComponentText("usage: ast take <player> [<amount>] [true|false]"));
				return;
			}
		}
		
		boolean overflow = true; //allow amount to exceed current count (default true)
		if (args.length >= 4) {
			if (!args[3].equals("true") && !args[3].equals("false")) { //invalid boolean value
				sender.addChatMessage(new ChatComponentText("usage: ast take <player> [<amount>] [true|false]"));
				return;
			}
				
			overflow = Boolean.valueOf(args[3]); //parse argument
		}
		
		String playerName = args[1]; //name of player
		EntityPlayer target = sender.getEntityWorld().getPlayerEntityByName(playerName); //get the player by name
		if (target == null) {
			sender.addChatMessage(new ChatComponentText("Player " + playerName + " was not found."));
			return;
		}
		
		doTake(sender, target, amount, overflow);
	}
	
	/**
	 * Processes the command
	 */
	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (sender.getEntityWorld().isRemote) return; //server only
		String senderName = sender.getCommandSenderName();
		
		if (isAdvancedCommand(args) && !canUseAdvancedCommand(sender)) { //handle unallowed commands
			sender.addChatMessage(new ChatComponentText("You must be an operator to use command arguments on AST."));
			return;
		}
		else if (!isAdvancedCommand(args) && sender instanceof DedicatedServer) {
			sender.addChatMessage(new ChatComponentText("You cannot do a self-check from the console."));
			return;
		}
		else if (!isAdvancedCommand(args)) {
			doGetCount(sender, (EntityPlayer)sender);
			return;
		}
		
		String commandSubtype = args[0].toLowerCase(); //get the subtype of the command
		if (commandSubtype.equals("get")) {
			doGetCount(sender, args);
		}
		else if (commandSubtype.equals("give")) {
			doGive(sender, args);
		}
		else if (commandSubtype.equals("take")) {
			doTake(sender, args);
		}
		else {
			sender.addChatMessage(new ChatComponentText("usage: ast [(get|give|take) <player> [amount]]"));
			return;
		}
	}

	/**
	 * Gets any tab-completion for the command 
	 */
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] tags) {
		List options = new ArrayList(); //get a result
		if (!(sender instanceof DedicatedServer) && !isPlayerOpped((EntityPlayer)sender)) return options; //no options for non-opped players
		
		if (tags == null || tags.length <= 1) {
			return Arrays.asList(new String[] {"get", "give", "take"});
		}
		else if (tags.length == 2) {
			for (Object o : sender.getEntityWorld().playerEntities) {
				options.add(((EntityPlayer)o).getDisplayName());
			}
		}
		
		return options;
	}

	/**
	 * Indicates whether the argument at an index should be a username
	 */
	@Override
	public boolean isUsernameIndex(String[] args, int idx) {
		if (args == null || args.length == 0) return false;
		return idx == 1; //only first argument if any
	}
	
	/**
	 * Gets the usage of the command
	 */
	@Override
	public String getCommandUsage(ICommandSender sender) {
		if (!(sender instanceof DedicatedServer) && !isPlayerOpped((EntityPlayer)sender)) return super.getCommandUsage(sender); //base method
		
		return "ast [(get|give|take) <player> [<amount>]]"; //operator usage
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
