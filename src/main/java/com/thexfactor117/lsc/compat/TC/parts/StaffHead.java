package com.thexfactor117.lsc.compat.TC.parts;

import com.thexfactor117.lsc.compat.TC.TCRegistry;
import com.thexfactor117.lsc.compat.TC.items.base.MagicItemCore;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ToolPart;

public class StaffHead extends ToolPart
{

    public static final ToolPart toolPartStaffHead = new StaffHead(288);
    public static PartMaterialType staffHead = new PartMaterialType(toolPartStaffHead, "staff","head");

    public StaffHead(int cost)
    {
        super(cost);
    }

    @Override
    public boolean canUseMaterial(Material mat) {
        if (!super.canUseMaterial(mat))
        {
            for(MagicItemCore core : TCRegistry.tools.toArray(new MagicItemCore[]{}))
            {
                for (PartMaterialType pmt : core.getRequiredComponents())
                {
                    if (pmt.isValid(this,mat))
                    {
                        return true;
                    }
                }
            }
        }
        else
        {
            return true;
        }
        return false;
    }
}
