package com.thexfactor117.lsc.items.melee;

import com.thexfactor117.lsc.capabilities.api.IChunkLevel;
import com.thexfactor117.lsc.capabilities.api.IChunkLevelHolder;
import com.thexfactor117.lsc.capabilities.cap.CapabilityChunkLevel;
import com.thexfactor117.lsc.init.ModTabs;
import com.thexfactor117.lsc.items.base.ISpecial;
import com.thexfactor117.lsc.items.base.ItemAdvancedMelee;
import com.thexfactor117.lsc.loot.Attribute;
import com.thexfactor117.lsc.loot.Rarity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

/**
 * 
 * @author TheXFactor117
 *
 */
public class ItemAlakaslam extends ItemAdvancedMelee implements ISpecial
{
	public ItemAlakaslam(ToolMaterial material, String name, String type, double damageMultiplier, double speedMultiplier)
	{
		super(material, name, type, damageMultiplier, speedMultiplier);
		this.setCreativeTab(ModTabs.lscTab);
	}

	@Override
	public void createSpecial(ItemStack stack, NBTTagCompound nbt, World world, ChunkPos pos) 
	{
		IChunkLevelHolder chunkLevelHolder = world.getCapability(CapabilityChunkLevel.CHUNK_LEVEL, null);
		IChunkLevel chunkLevel = chunkLevelHolder.getChunkLevel(pos);
		int level = chunkLevel.getChunkLevel();
		
		nbt.setBoolean("IsSpecial", true);
		Rarity.setRarity(nbt, Rarity.LEGENDARY);
		nbt.setInteger("Level", level);
		
		// Attributes
		Attribute.STRENGTH.addAttribute(nbt, world.rand, 10);
		Attribute.FORTITUDE.addAttribute(nbt, world.rand, 10);
		Attribute.FROST.addAttribute(nbt, world.rand, 7);
	}
}
