package com.lsc.items.melee;

import com.lsc.capabilities.chunk.CapabilityChunkLevel;
import com.lsc.capabilities.chunk.IChunkLevel;
import com.lsc.capabilities.chunk.IChunkLevelHolder;
import com.lsc.init.ModTabs;
import com.lsc.items.base.ISpecial;
import com.lsc.items.base.ItemMelee;
import com.lsc.loot.Rarity;
import com.lsc.loot.WeaponAttribute;
import com.lsc.loot.generation.ItemGeneratorHelper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class ItemRequiem extends ItemMelee implements ISpecial
{
	public ItemRequiem(ToolMaterial material, String name, String type)
	{
		super(material, name, type);
		this.setCreativeTab(ModTabs.tabLE);
	}

	@Override
	public void createSpecial(ItemStack stack, NBTTagCompound nbt, World world, ChunkPos pos) 
	{
		IChunkLevelHolder chunkLevelHolder = world.getCapability(CapabilityChunkLevel.CHUNK_LEVEL, null);
		IChunkLevel chunkLevel = chunkLevelHolder.getChunkLevel(pos);
		int level = chunkLevel.getChunkLevel();
		
		nbt.setBoolean("IsSpecial", true);
		Rarity.setRarity(nbt, Rarity.EPIC);
		nbt.setInteger("Level", level);
		
		// Attributes
		WeaponAttribute.AGILITY.addAttribute(nbt, 5);
		WeaponAttribute.DEXTERITY.addAttribute(nbt, 5);
		WeaponAttribute.LIFE_STEAL.addAttribute(nbt, 0.05);
		WeaponAttribute.MANA_STEAL.addAttribute(nbt, 0.05);
		
		ItemGeneratorHelper.setAttributeModifiers(nbt, stack);
	}
}
