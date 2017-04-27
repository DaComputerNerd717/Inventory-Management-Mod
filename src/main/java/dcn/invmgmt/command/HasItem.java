package dcn.invmgmt.command;

import java.util.Arrays;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldServer;

public class HasItem extends CommandBase {

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "hasitem";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "/hasitem [player] [item] <minQuantity> <maxQuantity>";
	}
	
	@Override
	public void processForPlayer(EntityPlayer player, String[] params) {
		System.out.println(Arrays.toString(params));
		if(params.length < 2 || params.length > 4)
			return;
		boolean b = hasItem(params);
		if(b)
			player.addChatComponentMessage(new ChatComponentText("Target player has these items"));
		else
			player.addChatComponentMessage(new ChatComponentText("Target player does not have these items"));
	}

	@Override
	public void processForCommandBlock(CommandBlockLogic logic, String[] params) {
		if(params.length < 2 || params.length > 4)
			return;
		boolean b = hasItem(params);
		//curiously enough, only the one for b==true shows up in chat.
		if(b)
			CommandBase.func_152373_a(logic, this, "Target player has these items", new Object[0]);
		else
			throw new CommandException("Target player does not have these items", new Object[0]);
			
	}

	@Override
	public void processForConsole(ICommandSender console, String[] params) {
		if(params.length < 2 || params.length > 4)
			return;
		boolean b = hasItem(params);
		if(b)
			console.addChatMessage(new ChatComponentText("Target player has these items"));
		else{
			console.addChatMessage(new ChatComponentText("Target player does not have these items"));
		}
	}
	
	@Override
	public int getRequiredPermissionLevel(){
		return 2;
	}
	
	public boolean hasItem(String[] params){
		EntityPlayer target = null;
		for(WorldServer w : MinecraftServer.getServer().worldServers){
			if(w.getPlayerEntityByName(params[0]) != null)
				target = w.getPlayerEntityByName(params[0]);
		}
		if(target == null){
			return false;
		}
		Item item = GameRegistry.findItem(params[1].substring(0, params[1].indexOf(":")), 
				params[1].substring(params[1].indexOf(":") + 1)); 
		//minecraft:dirt becomes minecraft and dirt
		if(item == null){
			return false;
		}
		int count = 0;
		for(int i = 0; i < target.inventory.mainInventory.length; i++){
			if(target.inventory.mainInventory[i] == null)
				continue;
			ItemStack stack = target.inventory.mainInventory[i].copy();
			if(stack != null && stack.getItem() != null && stack.getItem().getUnlocalizedName().equals(item.getUnlocalizedName())){
				System.out.println(stack.stackSize);
				count += stack.stackSize;
			}
		}
		for(int i = 0; i < target.inventory.armorInventory.length; i++){
			if(target.inventory.armorInventory[i] == null)
				continue;
			ItemStack stack = target.inventory.mainInventory[i].copy();
			if(stack != null && stack.getItem() != null && stack.getItem().getUnlocalizedName().equals(item.getUnlocalizedName())){
				System.out.println(stack.stackSize);
				count += stack.stackSize;
			}
		}
		if(count == 0)
			return false;
		if(params.length < 3 || Integer.parseInt(params[2]) <= count)
			if(params.length < 4 || Integer.parseInt(params[3]) >= count)
				return true;
		return false;
	}
}
