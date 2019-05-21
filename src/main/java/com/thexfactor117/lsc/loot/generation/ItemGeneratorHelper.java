package com.thexfactor117.lsc.loot.generation;

import java.util.Collection;
import java.util.Random;
import java.util.UUID;

import com.google.common.collect.Multimap;
import com.thexfactor117.lsc.LootSlashConquer;
import com.thexfactor117.lsc.compat.TC.TCCompat;
import com.thexfactor117.lsc.config.Configs;
import com.thexfactor117.lsc.entities.projectiles.Rune;
import com.thexfactor117.lsc.items.base.ItemBauble;
import com.thexfactor117.lsc.items.base.ItemMagical;
import com.thexfactor117.lsc.items.base.ItemMelee;
import com.thexfactor117.lsc.loot.Attribute;
import com.thexfactor117.lsc.loot.Rarity;
import com.thexfactor117.lsc.loot.attributes.AttributeBase;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * 
 * @author TheXFactor117
 *
 */
public class ItemGeneratorHelper 
{
	private static final UUID ATTACK_DAMAGE = UUID.fromString("06dbc47d-eaf1-4604-9b91-926e475012c2");
	private static final UUID ATTACK_SPEED = UUID.fromString("335ede30-242d-41b6-a4f7-dd24ed2adce5");
	private static final UUID ARMOR = UUID.fromString("81a2ee21-fe83-41fb-8b2f-bf5ef33a71a8");
	
	public static Random rand = new Random();
	
	/** Set the type of the item to NBT for use in generating names. */
	public static void setTypes(ItemStack stack, NBTTagCompound nbt)
	{
		if (stack.getItem() instanceof ItemSword)
		{
			if (stack.getItem() instanceof ItemMelee)
			{
				ItemMelee item = (ItemMelee) stack.getItem();
				
				nbt.setString("Type", item.getType());
			}
			else nbt.setString("Type", "sword");
		}
		else if (stack.getItem() instanceof ItemArmor)
		{
			if (((ItemArmor) stack.getItem()).armorType == EntityEquipmentSlot.HEAD) nbt.setString("Type", "helmet");
			else if (((ItemArmor) stack.getItem()).armorType == EntityEquipmentSlot.CHEST) nbt.setString("Type", "chestplate");
			else if (((ItemArmor) stack.getItem()).armorType == EntityEquipmentSlot.LEGS) nbt.setString("Type", "leggings");
			else if (((ItemArmor) stack.getItem()).armorType == EntityEquipmentSlot.FEET) nbt.setString("Type", "boots");
		}
		else if (stack.getItem() instanceof ItemBow)
		{
			nbt.setString("Type", "bow");
		}
		else if (stack.getItem() instanceof ItemMagical)
		{
			nbt.setString("Type", "staff");
		}
		else if (stack.getItem() instanceof ItemBauble)
		{
			ItemBauble item = (ItemBauble) stack.getItem();
			
			nbt.setString("Type", item.getBaubleType(stack).toString().toLowerCase());
		}
		else if (LootSlashConquer.instance.IsTCLoaded())
		{
			TCCompat.setTCType(stack,nbt);
		}
	}
	
	/**
	 * Sets a certain amount of random attributes to the stack depending on the Rarity.
	 * @param stack
	 * @param nbt
	 * @param rarity
	 * @param player
	 */
	public static void setRandomAttributes(ItemStack stack, NBTTagCompound nbt, Rarity rarity)
	{
		AttributeBase.FIRE_DAMAGE.addAttribute(nbt, rand);
		
		/*
		// TODO: add config options for the amount of attributes per rarity.
		int amount = 0;
		// sets the amount of attributes should be generated depending on rarity.
		if (rarity == Rarity.COMMON) amount = (int) (Math.random() * 2); // max 1
		else if (rarity == Rarity.UNCOMMON) amount = (int) (Math.random() * 2 + 1); // max 2
		else if (rarity == Rarity.RARE) amount = (int) (Math.random() * 3 + 1); // max 3
		else if (rarity == Rarity.EPIC) amount = (int) (Math.random() * 3 + 2); // max 4
		else if (rarity == Rarity.LEGENDARY) amount = (int) (Math.random() * 3 + 3); // max 5

		for (int i = 0; i < amount; i++)
		{
			if (stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemBow || stack.getItem() instanceof ItemMagical)
			{
				Attribute attribute = Attribute.getRandomWeaponAttribute(rand); // generate random rarity.
				
				if (attribute.hasAttribute(nbt))
					i--; // subtract 1 if we already have an attribute to "re-roll"
				else
					attribute.addAttribute(nbt, rand); // add attribute - this method will handle setting up all the attribute unique modifiers, such as custom rarity, values, etc...
			}
			else if (stack.getItem() instanceof ItemArmor)
			{
				Attribute attribute = Attribute.getRandomArmorAttribute(rand); // generate random rarity.
				
				if (attribute.hasAttribute(nbt))
					i--; // subtract 1 if we already have an attribute to "re-roll"
				else
					attribute.addAttribute(nbt, rand); // add attribute - this method will handle setting up all the attribute unique modifiers, such as custom rarity, values, etc...
			}
			else if (stack.getItem() instanceof ItemBauble)
			{
				Attribute attribute = Attribute.getRandomArmorAttribute(rand); // generate random rarity.
				
				if (attribute.hasAttribute(nbt))
					i--; // subtract 1 if we already have an attribute to "re-roll"
				else
					attribute.addAttribute(nbt, rand); // add attribute - this method will handle setting up all the attribute unique modifiers, such as custom rarity, values, etc...
			}
		}*/
	}
	
	/**
	 * Creates a new Attribute Modifier tag list and adds it to the NBTTagCompound. Overrides default vanilla implementation.
	 * @param nbt
	 * @param sword
	 */
	public static void setAttributeModifiers(NBTTagCompound nbt, ItemStack stack)
	{
		Item item = stack.getItem();
		
		if (item instanceof ItemSword)
		{	
			// retrieves the default attributes, like damage and attack speed.
			@SuppressWarnings("deprecation")
			Multimap<String, AttributeModifier> map = item.getItemAttributeModifiers(EntityEquipmentSlot.MAINHAND);
			Collection<AttributeModifier> damageCollection = map.get(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
			Collection<AttributeModifier> speedCollection = map.get(SharedMonsterAttributes.ATTACK_SPEED.getName());
			AttributeModifier damageModifier = (AttributeModifier) damageCollection.toArray()[0];
			AttributeModifier speedModifier = (AttributeModifier) speedCollection.toArray()[0];

			double baseDamage = damageModifier.getAmount();
			double baseSpeed = speedModifier.getAmount();
			double damage = getWeightedDamage(nbt.getInteger("Level"), Rarity.getRarity(nbt), baseDamage);
			double speed = getWeightedAttackSpeed(Rarity.getRarity(nbt), baseSpeed);

			setMinMaxDamage(nbt, damage);
			
			// Creates new AttributeModifier's and applies them to the stack's NBT tag compound.
			AttributeModifier attackDamage = new AttributeModifier(ATTACK_DAMAGE, "attackDamage", damage, 0);
			AttributeModifier attackSpeed = new AttributeModifier(ATTACK_SPEED, "attackSpeed", speed, 0);
			NBTTagCompound damageNbt = writeAttributeModifierToNBT(SharedMonsterAttributes.ATTACK_DAMAGE, attackDamage, EntityEquipmentSlot.MAINHAND);
			NBTTagCompound speedNbt = writeAttributeModifierToNBT(SharedMonsterAttributes.ATTACK_SPEED, attackSpeed, EntityEquipmentSlot.MAINHAND);
			NBTTagList list = new NBTTagList();
			list.appendTag(damageNbt);
			list.appendTag(speedNbt);
			nbt.setTag("AttributeModifiers", list);
		}
		else if (item instanceof ItemArmor)
		{
			Multimap<String, AttributeModifier> map = ((ItemArmor) item).getAttributeModifiers(((ItemArmor) item).armorType, stack);
			Collection<AttributeModifier> armorCollection = map.get(SharedMonsterAttributes.ARMOR.getName());
			AttributeModifier armorModifier = (AttributeModifier) armorCollection.toArray()[0];
			
			double baseArmor = armorModifier.getAmount();
			double newArmor = getWeightedArmor(Rarity.getRarity(nbt), nbt.getInteger("Level"), baseArmor);
			
			nbt.setDouble("ArmorPoints", newArmor);
			
			// Creates new AttributeModifier's and applies them to the stack's NBT tag compound.
			AttributeModifier armor = new AttributeModifier(ARMOR, "armor", newArmor, 0);
			NBTTagCompound armorNbt = writeAttributeModifierToNBT(SharedMonsterAttributes.ARMOR, armor, ((ItemArmor) item).armorType);
			NBTTagList list = new NBTTagList();
			list.appendTag(armorNbt);
			nbt.setTag("AttributeModifiers", list);
		}
		else if(LootSlashConquer.instance.IsTCLoaded())
		{
			TCCompat.setAttributeModifiers(nbt,stack);
		}
	}

	/** Sets the rune of the current magical weapon. Only used for magical weapons. */
	public static void setRune(NBTTagCompound nbt)
	{
		Rune.setRune(nbt, Rune.getRandomRune(nbt, rand));
	}
	
	/**
	 * Sets the minimum and maximum damage an item can deal and sets it to NBT.
	 * @param nbt
	 * @param damage
	 */
	public static void setMinMaxDamage(NBTTagCompound nbt, double damage)
	{
		// min/max rand factor control the range of the random decimal. The higher the factors, the bigger range
		double minRandFactor = Configs.weaponCategory.rangeMinRandFactor; 
		double maxRandFactor = Configs.weaponCategory.rangeMaxRandFactor;
		double multiplier = (Math.random() * (maxRandFactor - minRandFactor) + minRandFactor);
		// scales the range by level (higher level items have greater ranges
		double rangeMultiplier = nbt.getInteger("Level") * Configs.weaponCategory.rangeMultiplier;
		// the actual range is calculated here
		// take the random multiplier and multiply it by some value (the higher the value, the bigger the range)
		// add 0.5 so it can't be zero
		// multiply it by the range multiplier (increases the range dependent on the level)
		int range = (int) ((multiplier * 4 + 0.5) * rangeMultiplier);
		// set the min/max by subtracting/adding half the range to the new damage value.
		int minDamage = (int) (damage - (range / 2));
		int maxDamage = (int) (damage + (range / 2));
		
		if (Attribute.MIN_DAMAGE.hasAttribute(nbt)) minDamage += Attribute.MIN_DAMAGE.getAmount(nbt);
		else if (Attribute.MAX_DAMAGE.hasAttribute(nbt)) maxDamage += Attribute.MAX_DAMAGE.getAmount(nbt);
		
		if (minDamage == maxDamage) minDamage -= 1;
		if (minDamage <= 0) minDamage = 1;
		while (minDamage >= maxDamage) maxDamage += 1;
		
		nbt.setInteger("MinDamage", minDamage);
		nbt.setInteger("MaxDamage", maxDamage);
	}
	
	/**
	 * Returns a weighted damage value dependent on the Rarity and Level of the item.
	 * @param nbt
	 * @param rarity
	 * @param base
	 * @return
	 */
	public static double getWeightedDamage(int level, Rarity rarity, double base)
	{
		double baseFactor = Configs.weaponCategory.damageBaseFactor;
		// min/max rand factor controls the range of the random decimal (this creates a sort of range for the damage to fall in,
		// based on the base damage.
		double minRandFactor = Configs.weaponCategory.damageMinRandFactor;
		double maxRandFactor = Configs.weaponCategory.damageMaxRandFactor;
		double multiplier = (Math.random() * (maxRandFactor - minRandFactor) + minRandFactor);
		
		// set the new damage equal to the base multiplied by the multiplier and the rarity factor.
		switch (rarity)
		{
			case COMMON:
				return (Math.pow(baseFactor, level) * (base * (Configs.weaponCategory.commonFactor * multiplier)));
			case UNCOMMON:
				return (Math.pow(baseFactor, level) * (base * (Configs.weaponCategory.uncommonFactor * multiplier)));
			case RARE:
				return (Math.pow(baseFactor, level) * (base * (Configs.weaponCategory.rareFactor * multiplier)));
			case EPIC:
				return (Math.pow(baseFactor, level) * (base * (Configs.weaponCategory.epicFactor * multiplier)));
			case LEGENDARY:
				return (Math.pow(baseFactor, level) * (base * (Configs.weaponCategory.legendaryFactor * multiplier)));
			default:
				return base;
		}
	}
	
	// TODO: add attack speed values to config.
	public static double getWeightedAttackSpeed(Rarity rarity, double base)
	{
		double speed = base;
		double range = 0;
		
		switch (rarity)
		{
			case COMMON:
				range = 0.1;
				speed = Math.random() * range + (base - 0.1);
				break;
			case UNCOMMON:
				range = 0.15;
				speed = Math.random() * range + (base - 0.05);
				break;
			case RARE:
				range = 0.25;
				speed = Math.random() * range + (base);
				break;
			case EPIC:
				range = 0.4;
				speed = Math.random() * range + (base + 0.1);
				break;
			case LEGENDARY:
				range = 0.65;
				speed = Math.random() * range + (base + 0.2);
				break;
			default:
				break;
		}
		
		return speed;
	}
	
	// TODO: add armor values to config.
	public static double getWeightedArmor(Rarity rarity, int level, double base)
	{
		double baseFactor = 1.05;
		double minRandFactor = 0.5;
		double maxRandFactor = 0.7;
		double multiplier = (Math.random() * (maxRandFactor - minRandFactor) + minRandFactor);
		
		switch (rarity)
		{
			case COMMON:
				return (Math.pow(baseFactor, level) * (base * (Configs.weaponCategory.commonFactor * multiplier)));
			case UNCOMMON:
				return (Math.pow(baseFactor, level) * (base * (Configs.weaponCategory.uncommonFactor * multiplier)));
			case RARE:
				return (Math.pow(baseFactor, level) * (base * (Configs.weaponCategory.rareFactor * multiplier)));
			case EPIC:
				return (Math.pow(baseFactor, level) * (base * (Configs.weaponCategory.epicFactor * multiplier)));
			case LEGENDARY:
				return (Math.pow(baseFactor, level) * (base * (Configs.weaponCategory.legendaryFactor * multiplier)));
			default:
				return base;
		}
	}
	
	/**
	 * Helper method for writing new attribute modifiers.
	 * @param attribute
	 * @param modifier
	 * @param slot
	 * @return
	 */
	public static NBTTagCompound writeAttributeModifierToNBT(IAttribute attribute, AttributeModifier modifier, EntityEquipmentSlot slot) 
	{
		NBTTagCompound nbt = new NBTTagCompound();
		
		nbt.setString("AttributeName", attribute.getName());
		nbt.setString("Name", modifier.getName());
		nbt.setString("Slot", slot.getName());
		nbt.setDouble("Amount", modifier.getAmount());
		nbt.setInteger("Operation", modifier.getOperation());
		nbt.setLong("UUIDMost", modifier.getID().getMostSignificantBits());
		nbt.setLong("UUIDLeast", modifier.getID().getLeastSignificantBits());
		
		return nbt;
	}
}
