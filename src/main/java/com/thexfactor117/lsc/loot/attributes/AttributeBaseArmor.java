package com.thexfactor117.lsc.loot.attributes;

import com.thexfactor117.lsc.capabilities.implementation.LSCPlayerCapability;

import net.minecraft.item.ItemStack;

/**
 *
 * @author TheXFactor117
 *
 */
public class AttributeBaseArmor extends AttributeBase
{
	public AttributeBaseArmor(String name, String key, double baseValue, boolean upgradeable, boolean isBonus)
	{
		super(name, key, baseValue, upgradeable, isBonus);
	}

	public void onEquip(LSCPlayerCapability cap, ItemStack stack) { }
	
	public void onUnequip(LSCPlayerCapability cap, ItemStack stack) { }
}
