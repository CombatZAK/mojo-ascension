package com.mods.mojo.ascension.common.items.use;

import java.util.Collection;

import com.mods.mojo.ascension.common.config.ConfigHelper;

import com.mods.mojo.ascension.common.items.TokenItem;

import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import ibxm.Player;
import micdoodle8.mods.galacticraft.api.inventory.AccessInventoryGC;
import micdoodle8.mods.galacticraft.core.inventory.InventoryExtended;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import tconstruct.api.IPlayerExtendedInventoryWrapper;
import tconstruct.api.TConstructAPI;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategoryList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncResearch;
import thaumcraft.common.lib.network.playerdata.PacketSyncWipe;
import travellersgear.api.TravellersGearAPI;

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
		if (stack.stackSize == 0) return stack; //exit conditions
		if (world.isRemote) return stack;
		
		boolean consumeToken = false; //should decrement stack?
		boolean validOwner = false;
		switch(stack.getItemDamage()) { //check metadata
			case 0: //Mark of Ascension
				return stack; //no handler
		
			case 1: //Seal of the Dark Pact
				consumeToken = checkOwner(stack, player);
				if (consumeToken) consumeToken = useSeal(player);
				if (consumeToken) doDarkTeleport(player, world);
				if (consumeToken && ConfigHelper.doLoseInventory) clearInventory(player);
				
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
	 * "Banishes" a player to the dark dimension
	 * 
	 * @param target player to banish
	 * @param world world where player currently exists
	 */
	private static void doDarkTeleport(EntityPlayer target, World world) {
		if (!ConfigHelper.doTeleport) {
			world.playSoundAtEntity(target, "ascension:evilLaugh", 1.0F, 1.0F);
			return; 
		}
		
		world.playSoundToNearExcept(target, "ascension.evilLaugh", 1.0F, 1.0F);
		
		WorldServer worldServer = (WorldServer)world;
		EntityPlayerMP targetMP = (EntityPlayerMP)target;
		targetMP.setPositionAndUpdate(ConfigHelper.darkPointX, ConfigHelper.darkPointY, ConfigHelper.darkPointZ);
		targetMP.mcServer.getConfigurationManager().transferPlayerToDimension(targetMP, ConfigHelper.darkDimension);
		targetMP.mcServer.worldServerForDimension(ConfigHelper.darkDimension).playSoundAtEntity(targetMP, "ascension:evilLaugh", 1.0F, 1.0F);		
	}
	
	/**
	 * Deletes a player's entire inventory
	 * 
	 * @param target player who should lose all items
	 */
	private static void clearInventory(EntityPlayer target) {
		//vanilla inventory
		target.inventory.clearInventory(null, -1);
		
		//Baubles inventory
		InventoryBaubles bInventory = new InventoryBaubles(target);
		PlayerHandler.setPlayerBaubles(target, bInventory);
		
		//traveler's gear inventory
		TravellersGearAPI.setExtendedInventory(target, new ItemStack[] {});
		
		//galacticraft inventory
		InventoryExtended gcInventory = (InventoryExtended)AccessInventoryGC.getGCInventoryForPlayer((EntityPlayerMP)target);
		for (int i = 0; i < gcInventory.getSizeInventory(); i++) {
			gcInventory.setInventorySlotContents(i, null);
		}
		
		//tconstruct inventory
		IPlayerExtendedInventoryWrapper tcInventories = TConstructAPI.getInventoryWrapper(target);
		IInventory knapsackInventory = tcInventories.getKnapsackInventory(target);
		IInventory accessoryInventory = tcInventories.getAccessoryInventory(target);
		for (int i = 0; i < knapsackInventory.getSizeInventory(); i++) {
			knapsackInventory.setInventorySlotContents(i, null);
		}
		for (int i = 0; i < accessoryInventory.getSizeInventory(); i++) {
			accessoryInventory.setInventorySlotContents(i, null);
		}
		
		
		//blow away entire thaumcraft history
		Thaumcraft.proxy.getPlayerKnowledge().wipePlayerKnowledge(target.getDisplayName());
		PacketHandler.INSTANCE.sendTo(new PacketSyncWipe(), (EntityPlayerMP)target);
		Collection<ResearchCategoryList> rc = ResearchCategories.researchCategories.values();
		for (ResearchCategoryList category : rc) {
			Collection<ResearchItem> ri = category.research.values();
			for (ResearchItem item : ri) {
				if (item.isAutoUnlock()) {
					Thaumcraft.proxy.getResearchManager().completeResearch(target, item.key);
				}
			}
		}
		PacketHandler.INSTANCE.sendTo(new PacketSyncResearch(target), (EntityPlayerMP)target);
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
