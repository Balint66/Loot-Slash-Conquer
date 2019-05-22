package com.thexfactor117.lsc.compat.TC.items.weapons;

import com.thexfactor117.lsc.compat.TC.items.base.MagicItemToolCore;
import com.thexfactor117.lsc.compat.TC.items.base.nbt.MagicNBT;
import com.thexfactor117.lsc.compat.TC.parts.StaffHead;
import com.thexfactor117.lsc.compat.TC.parts.StaffHeadMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.List;

public class Staff extends MagicItemToolCore
{

    public Staff()
    {
        this(StaffHead.staffHead,PartMaterialType.handle(TinkerTools.toolRod),PartMaterialType.handle(TinkerTools.toolRod));
    }

    public Staff(PartMaterialType... components)
    {
        super("tinker_staff",100, components);
    }

    @Override
    protected MagicNBT buildTagData(List<Material> materials) {
        MagicNBT nbt = new MagicNBT();
        HeadMaterialStats head = (HeadMaterialStats)((Material)materials.get(0)).getStatsOrUnknown("head");
        StaffHeadMaterialStats staffHead = (StaffHeadMaterialStats)((Material)materials.get(0)).getStatsOrUnknown("staff");
        HandleMaterialStats handle1 = ((Material)materials.get(1)).getStatsOrUnknown("handle");
        HandleMaterialStats handle2 = ((Material)materials.get(2)).getStatsOrUnknown("handle");
        nbt.head(head);
        nbt.staffHead(staffHead);
        nbt.handle(handle1,handle2);
        return nbt;
    }
}
