package com.thexfactor117.lsc.loot.attributes.weapons;

import java.util.Random;

import com.thexfactor117.lsc.loot.attributes.AttributeBaseWeapon;
import com.thexfactor117.lsc.util.AttributeUtil;
import com.thexfactor117.lsc.util.misc.NBTHelper;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
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
public class AttributeLifeSteal extends AttributeBaseWeapon
{
	public AttributeLifeSteal()
	{
		super("life_steal", "attributes.weapon.life_steal", 0.05, false, false, true);
	}
	
	@Override
	public void onHit(ItemStack stack, float damage, EntityLivingBase attacker, EntityLivingBase enemy)
	{
		attacker.setHealth((float) (attacker.getHealth() + (damage * this.getAttributeValue(NBTHelper.loadStackNBT(stack)))));
	}
	
	@Override
	public void addAttribute(ItemStack stack, NBTTagCompound nbt, Random rand)
	{
		super.addAttribute(stack, nbt, rand);
		AttributeUtil.addPercentageAttribute(this, stack, nbt, rand, 1.2);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public String getTooltipDisplay(NBTTagCompound nbt)
	{
		int value = (int) (this.getAttributeValue(nbt) * 100);
		
		return TextFormatting.RED + " * +" + value + "% " + I18n.format(this.getKey());
	}
}
