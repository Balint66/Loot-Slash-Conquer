package com.thexfactor117.lsc.compat.TC;

import com.google.common.collect.Multimap;
import com.thexfactor117.lsc.capabilities.implementation.LSCPlayerCapability;
import com.thexfactor117.lsc.events.EventContainerOpen;
import com.thexfactor117.lsc.loot.Rarity;
import com.thexfactor117.lsc.loot.generation.ItemGenerator;
import com.thexfactor117.lsc.loot.generation.ItemGeneratorHelper;
import com.thexfactor117.lsc.util.PlayerUtil;
import com.thexfactor117.lsc.util.misc.NBTHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import slimeknights.tconstruct.library.tools.SwordCore;

import java.util.Collection;
import java.util.UUID;

public class TCCompat
{

    private static final UUID ATTACK_DAMAGE = UUID.fromString("06dbc47d-eaf1-4604-9b91-926e475012c2");
    private static final UUID ATTACK_SPEED = UUID.fromString("335ede30-242d-41b6-a4f7-dd24ed2adce5");

    public static void creatAsLSCItem(ItemStack stack, NBTTagCompound nbt, World world, int level)
    {

        Item item = stack.getItem();

        if (item instanceof SwordCore)
        {
            ItemGeneratorHelper.setTypes(stack, nbt);
            nbt.setInteger("Level", level); // set level to current player level
            ItemGeneratorHelper.setRandomAttributes(stack, nbt, Rarity.getRarity(nbt));
            ItemGeneratorHelper.setAttributeModifiers(nbt, stack);
            nbt.setInteger("HideFlags", 6); // hides Attribute Modifier and Unbreakable tags
        }
    }

    public static void setTCType(ItemStack stack, NBTTagCompound nbt)
    {

        Item item = stack.getItem();

        if (item instanceof SwordCore)
        {
            //TODO:Add different sword types
            nbt.setString("Type", "sword");
        }
    }

    public static void setAttributeModifiers(NBTTagCompound nbt, ItemStack stack)
    {

        Item item = stack.getItem();

        if (item instanceof SwordCore)
        {

            SwordCore core = (SwordCore)item;

            // retrieves the default attributes, like damage and attack speed.
            @SuppressWarnings("deprecation")
            Multimap<String, AttributeModifier> map = core.getAttributeModifiers(EntityEquipmentSlot.MAINHAND,stack);
            Collection<AttributeModifier> damageCollection = map.get(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
            Collection<AttributeModifier> speedCollection = map.get(SharedMonsterAttributes.ATTACK_SPEED.getName());
            AttributeModifier damageModifier = (AttributeModifier) damageCollection.toArray()[0];
            AttributeModifier speedModifier = (AttributeModifier) speedCollection.toArray()[0];

            double baseDamage = damageModifier.getAmount();
            double baseSpeed = speedModifier.getAmount();
            double damage = ItemGeneratorHelper.getWeightedDamage(nbt.getInteger("Level"), Rarity.getRarity(nbt), baseDamage);
            double speed = ItemGeneratorHelper.getWeightedAttackSpeed(Rarity.getRarity(nbt), baseSpeed);

            ItemGeneratorHelper.setMinMaxDamage(nbt, damage);

            float realDammage = (nbt.getInteger("MinDamage") + nbt.getInteger("MaxDamage") + nbt.getCompoundTag("Stats").getFloat("Attack")) / 3f;

            nbt.getCompoundTag("Stats").setFloat("Attack",realDammage);

            // Creates new AttributeModifier's and applies them to the stack's NBT tag compound.
            AttributeModifier attackDamage = new AttributeModifier(ATTACK_DAMAGE, "attackDamage", damage, 0);
            AttributeModifier attackSpeed = new AttributeModifier(ATTACK_SPEED, "attackSpeed", speed, 0);
            NBTTagCompound damageNbt = ItemGeneratorHelper.writeAttributeModifierToNBT(SharedMonsterAttributes.ATTACK_DAMAGE, attackDamage, EntityEquipmentSlot.MAINHAND);
            NBTTagCompound speedNbt = ItemGeneratorHelper.writeAttributeModifierToNBT(SharedMonsterAttributes.ATTACK_SPEED, attackSpeed, EntityEquipmentSlot.MAINHAND);
            NBTTagList list = new NBTTagList();
            list.appendTag(damageNbt);
            list.appendTag(speedNbt);
            nbt.setTag("AttributeModifiers", list);
        }

    }

    public static void onContainerOpen(EntityPlayer player, ItemStack stack)
    {

        if (stack.getItem() instanceof SwordCore)
        {
            NBTTagCompound nbt = NBTHelper.loadStackNBT(stack);
            stack.setTagCompound(nbt);

            if (nbt != null)
            {
                if (nbt.getInteger("Level") == 0)
                {
                    if (nbt.hasKey("TagLevel"))
                    {
                        EventContainerOpen.generate(stack, nbt, player.world, nbt.getInteger("TagLevel"));
                    }
                    else
                    {
                        LSCPlayerCapability cap = PlayerUtil.getLSCPlayer(player);
                        nbt.setInteger("TagLevel", cap.getPlayerLevel());

                        EventContainerOpen.generate(stack, nbt, player.world, nbt.getInteger("TagLevel"));
                    }
                }
            }
        }


    }
}
