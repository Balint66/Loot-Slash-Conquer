package com.thexfactor117.lsc.compat.TC;

import com.thexfactor117.lsc.capabilities.implementation.LSCPlayerCapability;
import com.thexfactor117.lsc.events.EventContainerOpen;
import com.thexfactor117.lsc.util.ItemGenerationUtil;
import com.thexfactor117.lsc.util.ItemUtil;
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
import slimeknights.tconstruct.library.tools.SwordCore;

import java.util.Collection;
import java.util.UUID;

import static com.thexfactor117.lsc.util.ItemGenerationUtil.*;

public class TCCompat
{

    private static final UUID ATTACK_DAMAGE = UUID.fromString("06dbc47d-eaf1-4604-9b91-926e475012c2");
    private static final UUID ATTACK_SPEED = UUID.fromString("335ede30-242d-41b6-a4f7-dd24ed2adce5");

    public static void createAsLSCItem(ItemStack stack, World world, int level)
    {

        Item item = stack.getItem();
        NBTTagCompound nbt = stack.getTagCompound();

        if (item instanceof SwordCore)
        {
            nbt.setInteger("Level", level);
            ItemGenerationUtil.setRandomWeaponAttributes(stack);
            ItemGenerationUtil.setPrimaryAttributes(stack);
            ItemGenerationUtil.hideFlags(nbt);
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

            double baseDamage = nbt.getCompoundTag("Stats").getFloat("Attack");
            double baseAttackSpeed = ItemUtil.getAttributeModifierValue(stack, SharedMonsterAttributes.ATTACK_SPEED, EntityEquipmentSlot.MAINHAND, ItemUtil.VANILLA_ATTACK_SPEED_MODIFIER) * nbt.getCompoundTag("Stats").getFloat("AttackSpeedMultiplier");
            double weightedDamage = getWeightedDamage(ItemUtil.getItemLevel(stack), ItemUtil.getItemRarity(stack), baseDamage);
            double weightedAttackSpeed = getWeightedAttackSpeed(ItemUtil.getItemRarity(stack), baseAttackSpeed);

            setMinMaxDamage(nbt, weightedDamage);
            nbt.setDouble("AttackSpeed", weightedAttackSpeed);

            ItemUtil.setAttributeModifierValue(stack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND), SharedMonsterAttributes.ATTACK_DAMAGE, ItemUtil.VANILLA_ATTACK_DAMAGE_MODIFIER, ItemUtil.getItemDamage(stack));
            ItemUtil.setAttributeModifierValue(stack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND), SharedMonsterAttributes.ATTACK_SPEED, ItemUtil.VANILLA_ATTACK_SPEED_MODIFIER, weightedAttackSpeed);
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
