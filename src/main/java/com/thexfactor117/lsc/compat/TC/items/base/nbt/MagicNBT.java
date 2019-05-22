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

    private Mana mana = new Mana();

    public MagicNBT(){mana = new Mana(10);}

    public MagicNBT(NBTTagCompound nbt){super(nbt);}

    public MagicNBT staffHead(StaffHeadMaterialStats... staffHeads)
    {
        int dur = 0;
        float man = 0;
        StaffHeadMaterialStats[] stats = staffHeads;
        int l = staffHeads.length;

        for (int i = 0; i < l; i++)
        {
            StaffHeadMaterialStats head = stats[i];
            if (head != null)
            {
                man += head.mana;
            }
        }

        man /= stats.length;

        man = Math.min(1f,man);

        mana = new Mana(Math.round(man));

        return this;

    }

    public Mana getMana(){return mana;}

    public void read(NBTTagCompound compound)
    {
        super.read(compound);
        mana.read(compound.getCompoundTag("Mana"));
    }

    public void write(NBTTagCompound compound)
    {
        super.write(compound);
        NBTTagCompound man = compound.getCompoundTag("Mana");
        if (man == null)
        {
            compound.setTag("Mana",new NBTTagCompound());
        }
        mana.write(compound.getCompoundTag("Mana"));
    }

    public static MagicNBT from(ItemStack stack){return new MagicNBT(TagUtil.getToolTag(stack));}

    public class Mana extends NBTTagCompound
    {
        private int current;
        private int max;

        public Mana()
        {
            max = 10;
            current = 10;
        }

        public Mana(int max)
        {
            this.max = max;
            current = max;
        }

        public void setCurrent(int value)
        {
            if (value > max)
            {
                current = max;
            }
            else
            {
                current = value;
            }
        }

        public int getCurrent(){return current;}
        public int getMax(){return max;}

        public void read(NBTTagCompound compound)
        {
            this.max = compound.getInteger("Max");
            this.current = compound.getInteger("Current");
        }

        public void write(NBTTagCompound compound)
        {
            compound.setInteger("Max",this.max);
            compound.setInteger("Current",this.current);
        }

    }

}
