package dcn.invmgmt.command;

import java.util.HashMap;

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

/**The object representing the command /clearitem. Its parameters are as follows: <br>
 * player: target player<br>
 * item: target item<br>
 * amount: amount to remove<br>
 * minimum: optional fail condition, only remove if player has at least this many<br>
 * maximum: optional fail condition, only remove if player has at most this many<br>*/
public class ClearItem extends CommandBase{

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "clearitem";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		// TODO Auto-generated method stub
		return "/clearitem [player] [item] [amount] <minimum> <maximum>";
	}

	@Override
	public int getRequiredPermissionLevel(){
		return 2;
	}
	
	@Override
	public void processForPlayer(EntityPlayer player, String[] params) {
		processForConsole(player, params);
	}

	@Override
	public void processForCommandBlock(CommandBlockLogic logic, String[] params) {
		processForConsole(logic, params);
	}

	@Override
	public void processForConsole(ICommandSender console, String[] params) {
		EntityPlayer target = null;
		for(WorldServer w : MinecraftServer.getServer().worldServers){
			if(w.getPlayerEntityByName(params[0]) != null)
				target = w.getPlayerEntityByName(params[0]);
		}
		if(target == null){
			throw new CommandException("Player not found: " + params[0], new Object[0]);
		}
		Item item = GameRegistry.findItem(params[1].substring(0, params[1].indexOf(":")), 
				params[1].substring(params[1].indexOf(":") + 1)); 
		//minecraft:dirt becomes minecraft and dirt
		if(item == null){
			throw new CommandException("Item not found: " + params[1], new Object[0]);
		}
		int count = 0;
		int countLeft = Integer.parseInt(params[2]);
		HashMap<Integer, Integer> invSlots, armorSlots;
		invSlots = new HashMap<Integer, Integer>();
		for(int i = 0; i < target.inventory.mainInventory.length; i++){
			if(target.inventory.mainInventory[i] == null)
				continue;
			ItemStack stack = target.inventory.mainInventory[i].copy();
			if(stack != null && stack.getItem() != null && stack.getItem().getUnlocalizedName().equals(item.getUnlocalizedName())){
				invSlots.put(i, Math.min(countLeft, stack.stackSize));
				countLeft -= invSlots.get(i);
				count += stack.stackSize;
			}
		}
		armorSlots = new HashMap<Integer, Integer>();
		for(int i = 0; i < target.inventory.armorInventory.length; i++){
			if(target.inventory.armorInventory[i] == null)
				continue;
			ItemStack stack = target.inventory.mainInventory[i].copy();
			if(stack != null && stack.getItem() != null && stack.getItem().getUnlocalizedName().equals(item.getUnlocalizedName())){
				armorSlots.put(i, Math.min(countLeft, stack.stackSize));
				countLeft -= armorSlots.get(i);
				count += stack.stackSize;
			}
		}
		if((params.length <= 3 || Integer.parseInt(params[3]) <= count) &&
				(params.length <= 4 || Integer.parseInt(params[4]) >= count)){
			for(int n : invSlots.keySet()){
				if(invSlots.get(n) >= target.inventory.mainInventory[n].stackSize)
					target.inventory.mainInventory[n] = null;
				else
					target.inventory.mainInventory[n].stackSize -= invSlots.get(n);
			}
			for(int n : armorSlots.keySet()){
				if(armorSlots.get(n) >= target.inventory.armorInventory[n].stackSize)
					target.inventory.armorInventory[n] = null;
				else
					target.inventory.armorInventory[n].stackSize -= armorSlots.get(n);
			}
			if(console instanceof CommandBlockLogic)
				CommandBase.func_152373_a((CommandBlockLogic) console, this, 
						params[2] + " of " + params[1] + " removed from " + params[0] + 
						"'s inventory.", new Object[0]);
			else
				console.addChatMessage(new ChatComponentText(params[2] + " of " + params[1] + " removed from " + params[0] + "'s inventory."));
		}else{
			throw new CommandException("Amount found not within minimum and maximum specified.", new Object[0]);
		}
	}
}
