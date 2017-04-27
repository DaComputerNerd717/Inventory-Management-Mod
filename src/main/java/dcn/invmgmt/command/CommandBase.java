package dcn.invmgmt.command;

import java.util.Random;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

/**Extend this in all commands. <bl>
 * This class separates out the different command senders that could be using it, to separate out the different ways of running.*/
public abstract class CommandBase extends net.minecraft.command.CommandBase {
	
	@Override
	public void processCommand(ICommandSender sender, String[] params) {
		String[] newParams = new String[params.length];
		for(int i = 0; i < params.length; i++)
			newParams[i] = params[i];
		int currentParam = 0;
		for(String s : params){
			if(s.equals("@a")){
				for(WorldServer w : MinecraftServer.getServer().worldServers){
					for(Object p : w.playerEntities){
						if(p instanceof EntityPlayer){
							newParams[currentParam] = ((EntityPlayer) p).getDisplayName();
							//run for each player, since @a applies to all of them separately. 
							processCommand(sender, newParams);
						}
					}
				}
				return;
			}
			if(s.equals("@p")){
				if(sender instanceof EntityPlayer){
					newParams[currentParam] = ((EntityPlayer) sender).getDisplayName();
					System.arraycopy(newParams, 0, params, 0, params.length);
				}else if(sender instanceof CommandBlockLogic){
					CommandBlockLogic logic = (CommandBlockLogic) sender;
					EntityPlayer p = logic.getEntityWorld().getClosestPlayer(logic.getPlayerCoordinates().posX, logic.getPlayerCoordinates().posY, logic.getPlayerCoordinates().posZ, -1.0);
					newParams[currentParam] = p.getDisplayName();
					System.arraycopy(newParams, 0, params, 0, params.length);
				}else{
					throw new CommandException("Cannot use @p from console");
				}
			}
			if(s.equals("@r")){
				Random r = new Random();
				int n = r.nextInt(MinecraftServer.getServer().getCurrentPlayerCount());
				EntityPlayer[] players = new EntityPlayer[MinecraftServer.getServer().getCurrentPlayerCount()];
				int i = 0;
				for(WorldServer world : MinecraftServer.getServer().worldServers){
					System.arraycopy(world.playerEntities.toArray(), 0, players, i, world.playerEntities.size());
					i += world.playerEntities.size();
				}
				newParams[currentParam] = players[n].getDisplayName();
				System.arraycopy(newParams, 0, params, 0, params.length);
			}
		}
		if(sender instanceof EntityPlayer) 
			processForPlayer((EntityPlayer) sender, params);
		else if(sender instanceof CommandBlockLogic) 
			processForCommandBlock((CommandBlockLogic) sender, params);
		else 
			processForConsole(sender, params);
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender){
		return true;
	}
	
	public abstract void processForPlayer(EntityPlayer player, String[] params);
	public abstract void processForCommandBlock(CommandBlockLogic logic, String[] params);
	/**This will also be used for any type of ICommandSender which is not an EntityPlayer or a TileEntityCommandBlock*/
	public abstract void processForConsole(ICommandSender console, String[] params);

}
