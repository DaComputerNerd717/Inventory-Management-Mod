package dcn.invmgmt;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import dcn.invmgmt.command.ClearItem;
import dcn.invmgmt.command.HasItem;

@Mod(modid = "invmgmt", name = "Inventory Management Mod", version = "1.0")
public class InvManagement {
	@Instance
	public static InvManagement instance;
	
	@EventHandler
	public void serverLoad(FMLServerStartingEvent event){
		event.registerServerCommand(new HasItem());
		event.registerServerCommand(new ClearItem());
	}
}
