package com.mods.mojo.ascension.common.items;

import java.util.List;

import com.mods.mojo.ascension.common.items.use.IUseHandler;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import scala.actors.threadpool.Arrays;

/**
 * Represents an Ascension Token or one of its derivatives and includes functionality for use and ownership
 * 
 * @author CombatZAK
 *
 */
public class TokenItem extends MojoMetaItem {
	/**
	 * Function for dealing with use of item
	 */
	private IUseHandler useHandler;
	
	/**
	 * Gets the handler for item-use
	 * 
	 * @return handler
	 */
	public IUseHandler getUseHandler() {
		return useHandler;
	}
	
	/**
	 * Sets the handler for item-use
	 * 
	 * @param value handler
	 */
	public void setUseHandler(IUseHandler value) {
		this.useHandler = value;
	}
	
	/**
	 * Self-referentially sets the handler for item use
	 * 
	 * @param value handler
	 * @return self-reference
	 */
	public TokenItem withUseHandler(IUseHandler value) {
		this.setUseHandler(value);
		return this;
	}
	
	public TokenItem(int stackSize, CreativeTabs creativeTab, String itemId, List<String> tooltips, IUseHandler useHandler) {
		super(stackSize, creativeTab, itemId, tooltips);
		this.useHandler = useHandler;
	}
	
	/**
	 * Gets the owner of a token itemstack from it's NBT data
	 * 
	 * @param stack stack to check
	 * @return owner of item
	 */
	public static String getOwner(ItemStack stack) {
		if (stack == null) return null; //no owner for null item
		NBTTagCompound itemData = stack.stackTagCompound; //get the item data
		if (itemData == null) return null; //no nbt = no owner
		
		if (!itemData.hasKey("ascensionData")) return null; //no ascension data = no owner
		NBTTagCompound ascensionData = itemData.getCompoundTag("ascensionData");
		
		if (!ascensionData.hasKey("owner")) return null; //no owner
		String owner = ascensionData.getString("owner"); //read the owner
		if (owner == "") return null; //no owner
				
		return owner;
	}
	
	/**
	 * Sets the owner of a token item
	 * 
	 * @param stack item stack to set
	 * @param player player that will own it
	 */
	public static void setOwner(ItemStack stack, EntityPlayer player) {
		if (stack == null || player == null) return; //no player/item
		
		if (stack.stackTagCompound == null) stack.stackTagCompound = new NBTTagCompound(); //gen a new tag
		NBTTagCompound stackData = stack.stackTagCompound; //get the stack data
		NBTTagCompound ascensionData = stackData.getCompoundTag("ascensionData"); //create tag if doesn't exist
		String owner = ascensionData.getString("owner"); //get the owner
		
		if (owner != "") return; //item already has an owner
		
		ascensionData.setString("owner", player.getDisplayName()); //set the owner
		if (!stackData.hasKey("ascensionData")) stackData.setTag("ascensionData", ascensionData); //if new tag, apply it to the item
		announceOwner(stack, player);
	}
	
	/**
	 * Announces that a player has found a token
	 * 
	 * @param stack item stack found
	 * @param player player who found them
	 */
	public static void announceOwner(ItemStack stack, EntityPlayer player) {
		if (stack == null || player == null) return; //no player/item
		
		ServerConfigurationManager mgr = MinecraftServer.getServer().getConfigurationManager(); //get the server config
		mgr.sendChatMsg(new ChatComponentText("§f" + player.getDisplayName() + "§r has obtained a " + stack.getDisplayName() + "!"));
	}
	
	/**
	 * Checks the item each tick while it's held; used to set ownership for unowned tokens
	 */
	@Override
	public void onUpdate(ItemStack stack, World world, Entity holder, int arg1, boolean arg2) {
		if (holder == null || !(holder instanceof EntityPlayer)) return; //if item is not held by a player, stop
		if (getOwner(stack) != null) return; //stop if the item already has an owner
		
		EntityPlayer player = (EntityPlayer)holder; //cast the holder as a player
		
		if (player.capabilities.isCreativeMode) return; //no changing ownership if player is creative
		
		setOwner(stack, player); //set the owner
	}
	
	/**
	 * Adds tooltip text based on metadata and owner
	 */
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean para4) {
		super.addInformation(stack, player, list, para4); //call parent tooltip method
		String owner = getOwner(stack); //check if the stack has an owner
		if (owner == null) return; //stop if no owner
		
		list.add(""); //line break
		list.add("§oBound to " + owner + "§r"); //display owner
	}
	
	/**
	 * Executed when player holds item and right clicks
	 */
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (world.isRemote) return stack; //do nothing unless dealing with the server
		if (stack.stackSize == 0) return stack; //do nothing with an empty stack
		
		stack = this.useHandler.rightClick(stack.copy(), world, player); //call the item's handler
		
		return stack; //adjust the item stack
	}
}
