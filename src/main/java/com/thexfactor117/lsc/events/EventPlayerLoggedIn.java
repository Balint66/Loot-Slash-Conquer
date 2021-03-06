package com.thexfactor117.lsc.events;

import com.thexfactor117.lsc.LootSlashConquer;
import com.thexfactor117.lsc.capabilities.cap.CapabilityPlayerInformation;
import com.thexfactor117.lsc.capabilities.cap.CapabilityPlayerStats;
import com.thexfactor117.lsc.capabilities.implementation.PlayerInformation;
import com.thexfactor117.lsc.capabilities.implementation.PlayerStats;
import com.thexfactor117.lsc.config.Configs;
import com.thexfactor117.lsc.network.PacketClassGui;
import com.thexfactor117.lsc.network.PacketUpdatePlayerInformation;
import com.thexfactor117.lsc.network.PacketUpdatePlayerStats;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

/**
 * 
 * @author TheXFactor117
 *
 */
@Mod.EventBusSubscriber
public class EventPlayerLoggedIn 
{
	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
	{
		PlayerInformation playerInfo = (PlayerInformation) event.player.getCapability(CapabilityPlayerInformation.PLAYER_INFORMATION, null);
		PlayerStats statsCap = (PlayerStats) event.player.getCapability(CapabilityPlayerStats.PLAYER_STATS, null);

		if (playerInfo != null && playerInfo.getPlayerClass() == 0 && statsCap != null && statsCap.getMaxMana() == 0)
		{
			// send Class Selection gui to client on first join.
			LootSlashConquer.network.sendTo(new PacketClassGui(), (EntityPlayerMP) event.player);

			playerInfo.setPlayerLevel(1);
			
			// setup max mana + send it to client.
			statsCap.setMaxMana(Configs.playerCategory.maxMana);
			statsCap.setMana(statsCap.getMaxMana());
			statsCap.setManaPerSecond(Configs.playerCategory.manaPer5);
			statsCap.setHealthPerSecond(Configs.playerCategory.healthPer5);
			statsCap.setMagicalPower(0);
			statsCap.setCriticalChance(0);
			statsCap.setCriticalDamage(0);
			LootSlashConquer.network.sendTo(new PacketUpdatePlayerStats(statsCap), (EntityPlayerMP) event.player);
			LootSlashConquer.network.sendTo(new PacketUpdatePlayerInformation(playerInfo), (EntityPlayerMP) event.player);
		}
		else if (playerInfo != null && statsCap != null && playerInfo.getPlayerClass() > 0)
		{
			LootSlashConquer.network.sendTo(new PacketUpdatePlayerStats(statsCap), (EntityPlayerMP) event.player);
			LootSlashConquer.network.sendTo(new PacketUpdatePlayerInformation(playerInfo), (EntityPlayerMP) event.player);
		}
	}
}
