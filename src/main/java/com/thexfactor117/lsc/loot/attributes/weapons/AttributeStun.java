package com.thexfactor117.lsc.loot.attributes.weapons;

import java.util.Random;

import com.thexfactor117.lsc.loot.attributes.AttributeBaseWeapon;
import com.thexfactor117.lsc.util.AttributeUtil;
import com.thexfactor117.lsc.util.misc.NBTHelper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *
 * @author TheXFactor117
 *
 */
public class AttributeStun extends AttributeBaseWeapon
{
	public AttributeStun()
	{
		super("stun", "attributes.weapon.stun", 0.03, true, false, true);
	}
	
	@Override
	public void onHit(ItemStack stack, float damage, EntityLivingBase attacker, EntityLivingBase enemy)
	{
		if (Math.random() < this.getAttributeValue(NBTHelper.loadStackNBT(stack)))
		{
			enemy.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20 * 3, 10));
		}
	}
	
	@Override
	public void addAttribute(ItemStack stack, NBTTagCompound nbt, Random rand)
	{
		super.addAttribute(stack, nbt, rand);
		AttributeUtil.addPercentageAttribute(this, stack, nbt, rand, 0.6);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public String getTooltipDisplay(NBTTagCompound nbt)
	{
		int value = (int) (this.getAttributeValue(nbt) * 100);
		int minValue = (int) (this.getAttributeMinValue(nbt) * 100);
		int maxValue = (int) (this.getAttributeMaxValue(nbt) * 100);
		
		return TextFormatting.RED + " * +" + value + "% chance to stun the enemy " + TextFormatting.GRAY + "[" + minValue + "% - " + maxValue + "%]";
	}
}
