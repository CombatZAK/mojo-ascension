package com.mods.mojo.ascension.common.items.crafting;

import java.lang.reflect.Field;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import tconstruct.tools.inventory.CraftingStationContainer;

public class OwnerRecipeHelper {
	private OwnerRecipeHelper() {} //hide constructor; no instantiation
	
	/**
	 * Checks the owner for an item
	 * 
	 * @param stack ItemStack to check
	 * @return owner's name
	 */
	public static String getOwner(ItemStack stack) {
		if (stack == null) return null; //null check
		
		NBTTagCompound itemData = stack.stackTagCompound; //get the stack's data tag
		if (itemData == null || !itemData.hasKey("ascensionData")) return null; //stop if there is no data tag or no ascension data
		
		itemData = itemData.getCompoundTag("ascensionData"); //get the ascension data tag
		if (!itemData.hasKey("owner")) return null;
		
		String result = itemData.getString("owner"); //get the owner
		if (result.equals("")) return null; //no empty strings
		return result;
	}
	
	/**
	 * Sets the owner of an item
	 * 
	 * @param stack item to set
	 * @param owner owner of item
	 */
	public static void setOwner(ItemStack stack, String owner) {
		if (stack == null || owner == null || owner.equals("")) return; //skip null items, null/empty owners
		
		if (stack.stackTagCompound == null) stack.stackTagCompound = new NBTTagCompound(); //populate data tag if necessary
		NBTTagCompound itemData = stack.stackTagCompound.getCompoundTag("ascensionData"); //get the ascension tag
		itemData.setString("owner", owner); //write the owner
		
		if (!stack.stackTagCompound.hasKey("ascensionData")) //check that the key was previously populated
			stack.stackTagCompound.setTag("ascensionData", itemData);
	}
	
	/**
	 * Gets the player attempting to craft an item; lots of reflection hacks here
	 * 
	 * @param grid grid where craft is taking place
	 * @return player attempting to craft, if any
	 */
	public static EntityPlayer getPlayer(InventoryCrafting grid) {
		if (grid == null) return null; // skip null instances
		Container location = null;
		
		try {
			Field eventField = InventoryCrafting.class.getDeclaredField("eventHandler");
			eventField.setAccessible(true);
			location = (Container) eventField.get(grid); //stores the palce where the recipe is being crafted; get this by reflection
		}
		catch (Exception ex) {
			System.exit(1); //crash
		}
		
		if (location instanceof ContainerPlayer) { //player crafting grid
			try {
				Field thePlayerField = ContainerPlayer.class.getDeclaredField("thePlayer");
				thePlayerField.setAccessible(true);
				return (EntityPlayer)thePlayerField.get(location);
			}
			catch(Exception ex) { 
				System.exit(1); //crash
			}
		}
		else if (location instanceof ContainerWorkbench) {
			SlotCrafting slot = (SlotCrafting)location.getSlot(0); //get a slot from the grid
			
			try {
				Field thePlayerField = SlotCrafting.class.getDeclaredField("thePlayer");
				thePlayerField.setAccessible(true);;
				return (EntityPlayer)thePlayerField.get(slot);
			}
			catch (Exception ex) {
				System.exit(1); //crash
			}
		}
		else if (location instanceof CraftingStationContainer) {
			try {
				Field playerField = CraftingStationContainer.class.getDeclaredField("player");
				playerField.setAccessible(true);;
				return (EntityPlayer)playerField.get(location);
			}
			catch (Exception ex) {
				System.exit(1); //crash
			}
		}
		
		return null;
	}
	
	/**
	 * Indicates whether or not the player is playing creative mode
	 * 
	 * @param player player to check
	 * @return true if player's game mode is creative
	 */
	public static boolean isCreative(EntityPlayer player) {
		if (player == null) return false; //no null players
		
		return player.capabilities.isCreativeMode;
	}
}
