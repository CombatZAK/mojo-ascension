package com.mods.mojo.ascension.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

/**
 * Generic implementation of command
 * 
 * @author CombatZAK
 */
public abstract class MojoCommand implements ICommand {
	/**
	 * Level of accessibility for command, higher level = more restricted
	 * 
	 * @author CombatZAK
	 */
	public enum ProtectionLevel {
		NONE,
		OPERATOR,
		SERVER
	}
	
	/**
	 * Indicates whether a player is an operator
	 * 
	 * @param player player to check
	 * @return true if player is an operator
	 */
	public static boolean isPlayerOpped(EntityPlayer player) {
		if (player == null) return false;
		return MinecraftServer.getServer().getConfigurationManager().func_152596_g(player.getGameProfile());
	}
	
	/**
	 * The command name of the string
	 */
	protected String commandName;
	
	/**
	 * Any aliases the command might have
	 */
	protected List aliases;
	
	/**
	 * Gets the usage of the command
	 */
	protected String usage;
	
	/**
	 * Gets the accessbility level for the command
	 */
	protected ProtectionLevel commandProtection;
	
	/**
	 * Gets the name of the command
	 */
	@Override
	public String getCommandName() {
		return commandName;
	}
	
	/**
	 * Gets the aliases of the command
	 */
	@Override
	public List getCommandAliases() {
		return aliases;
	}
	
	/**
	 * Gets the general (operator) usage assist of the command
	 */
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return usage;
	}
	
	/**
	 * Indicates whether the sender is allowed to use the command
	 */
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		if (commandProtection == ProtectionLevel.NONE) return true; //no command protection
		String senderName = sender.getCommandSenderName(); //get the sender name
		
		if (senderName == "Rcon") return true; //server command
		if (commandProtection == ProtectionLevel.SERVER) return false; //server command only; not issued by server
		
		EntityPlayer player = (EntityPlayer)sender; //cast sender as player
		return isPlayerOpped(player); //protection level is operator, check player
	}
	
	/**
	 * Indicates whether the indicated argument should be a username
	 */
	@Override
	public boolean isUsernameIndex(String[] args, int idx) {
		return false;
	}
	
	/**
	 * Creates a new MojoCommand instance
	 */
	public MojoCommand(String commandName, List aliases, String usage, ProtectionLevel commandProtection) {
		this.commandName = commandName;
		this.aliases = aliases;
		this.usage = usage;
		this.commandProtection = commandProtection;
	}
	
	/**
	 * Creates a new MojoCommand instance
	 */
	public MojoCommand(String commandName, List aliases, String usage) {
		this(commandName, aliases, usage, ProtectionLevel.NONE);
	}
	
	/**
	 * Creates a new MojoCommand instance 
	 */
	public MojoCommand(String commandName) {
		this(commandName, new ArrayList(), "", ProtectionLevel.NONE);
	}
}
