package com.thexfactor117.lsc.loot.attributes.armor;

import java.util.Random;

import com.thexfactor117.lsc.capabilities.implementation.LSCPlayerCapability;
import com.thexfactor117.lsc.loot.attributes.AttributeBaseArmor;
import com.thexfactor117.lsc.loot.attributes.AttributeUtil;
import com.thexfactor117.lsc.util.misc.NBTHelper;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *
 * @author TheXFactor117
 *
 */
public class AttributeFrostResistance extends AttributeBaseArmor
{
	public AttributeFrostResistance()
	{
		super("frost_resistance", "attributes.armor.frost_resistance", 1, true, false);
	}
	
	@Override
	public void onEquip(LSCPlayerCapability cap, ItemStack stack)
	{
		NBTTagCompound nbt = NBTHelper.loadStackNBT(stack);
		int value = (int) this.getAttributeValue(nbt);
		
		cap.setFrostResistance(cap.getFrostResistance() + value);
	}
	
	@Override
	public void onUnequip(LSCPlayerCapability cap, ItemStack stack)
	{
		NBTTagCompound nbt = NBTHelper.loadStackNBT(stack);
		int value = (int) this.getAttributeValue(nbt);
		
		cap.setFrostResistance(cap.getFrostResistance() - value);
	}
	
	@Override
	public void addAttribute(ItemStack stack, NBTTagCompound nbt, Random rand)
	{
		super.addAttribute(stack, nbt, rand);
		AttributeUtil.addResistanceAttribute(this, stack, nbt);
	}
	
	@SideOnly(Side.CLIENT)
	public String getTooltipDisplay(NBTTagCompound nbt)
	{
		int value = (int) this.getAttributeValue(nbt);
		int minValue = (int) this.getAttributeMinValue(nbt);
		int maxValue = (int) this.getAttributeMaxValue(nbt);
		
		return TextFormatting.BLUE + " * +" + value + " " + I18n.format(this.getKey()) + TextFormatting.GRAY + " [" + minValue + " - " + maxValue + "]";
	}
}
