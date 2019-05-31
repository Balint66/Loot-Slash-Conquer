package com.thexfactor117.lsc.compat.TC.items.base.nbt;

import com.thexfactor117.lsc.compat.TC.items.base.MagicItemToolCore;
import com.thexfactor117.lsc.compat.TC.parts.StaffHead;
import com.thexfactor117.lsc.compat.TC.parts.StaffHeadMaterialStats;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.TagUtil;

public class MagicNBT extends ToolNBT
{

    private int manaCost = 10;

    public MagicNBT(){manaCost = 10;}

    public MagicNBT(NBTTagCompound nbt){super(nbt);}

    public MagicNBT staffHead(StaffHeadMaterialStats... staffHeads)
    {
        float dur = 0;
        float man = 0;
        StaffHeadMaterialStats[] stats = staffHeads;
        int l = staffHeads.length;

        for (int i = 0; i < l; i++)
        {
            StaffHeadMaterialStats head = stats[i];
            if (head != null)
            {
                man += head.mana;
                dur += head.durability;
            }
        }

        dur /= stats.length;
        man /= stats.length;

        this.durability = Math.round(Math.max(1f,dur));
        manaCost = Math.round(Math.max(1f,man));


        return this;

    }

    public int getMana(){return manaCost;}

    public void read(NBTTagCompound compound)
    {
        super.read(compound);
        manaCost = compound.getInteger("ManaCost");
    }

    public void write(NBTTagCompound compound)
    {
        super.write(compound);
        compound.setInteger("ManaCost", manaCost);
    }

    public static MagicNBT from(ItemStack stack){return new MagicNBT(TagUtil.getToolTag(stack));}


}
