package com.thexfactor117.minehackslash;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thexfactor117.minehackslash.init.ModCapabilities;
import com.thexfactor117.minehackslash.init.ModEvents;
import com.thexfactor117.minehackslash.init.ModLootTables;
import com.thexfactor117.minehackslash.loot.functions.CreateStats;
import com.thexfactor117.minehackslash.network.PacketClassGui;
import com.thexfactor117.minehackslash.network.PacketClassSelection;
import com.thexfactor117.minehackslash.network.PacketUpdatePlayerInformation;
import com.thexfactor117.minehackslash.proxies.CommonProxy;
import com.thexfactor117.minehackslash.util.GuiHandler;
import com.thexfactor117.minehackslash.util.Reference;
import com.thexfactor117.minehackslash.worldgen.MHSWorldGenerator;

import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

/**
 * 
 * @author TheXFactor117
 *
 * Test project for a potential mod.
 *
 */
@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION)
public class MineHackSlash 
{
	@Instance(Reference.MODID)
	public static MineHackSlash instance;
	@SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.COMMON_PROXY)
	public static CommonProxy proxy;
	public static final Logger LOGGER = LogManager.getLogger(Reference.NAME);
	public static SimpleNetworkWrapper network;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		ModLootTables.register();
		ModCapabilities.registerCapabilities();
		ModEvents.registerEvents();
		
		LootFunctionManager.registerFunction(new CreateStats.Serializer());
		
		proxy.preInit(event);
		
		network = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID);
		network.registerMessage(PacketClassGui.Handler.class, PacketClassGui.class, 0, Side.CLIENT);
		network.registerMessage(PacketClassSelection.Handler.class, PacketClassSelection.class, 1, Side.SERVER);
		network.registerMessage(PacketUpdatePlayerInformation.Handler.class, PacketUpdatePlayerInformation.class, 2, Side.CLIENT);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
		GameRegistry.registerWorldGenerator(new MHSWorldGenerator(), 100);
		proxy.init(event);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.postInit(event);
	}
}
