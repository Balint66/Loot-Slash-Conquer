package com.thexfactor117.lsc.compat.TC.parts;

import com.google.common.collect.Lists;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.CustomFontColor;
import slimeknights.tconstruct.library.materials.AbstractMaterialStats;

import java.util.List;

public class StaffHeadMaterialStats extends AbstractMaterialStats
{

    public static final String LOC_Mana = "stat.staff.mana.name";
    public static final String LOC_ManaDesc = "stat.staff.mana.desc";
    public static final String COLOR_Mana = CustomFontColor.encodeColor(112,71,209);
    public final int mana;

    public StaffHeadMaterialStats(int mana)
    {
        super("staff");
        this.mana = mana;
    }

    @Override
    public List<String> getLocalizedInfo()
    {
        List<String> info = Lists.newArrayList();
        info.add(formatNumber(LOC_Mana,COLOR_Mana,this.mana));
        return info;
    }

    @Override
    public List<String> getLocalizedDesc()
    {
        List<String> info = Lists.newArrayList();
        info.add(Util.translate(LOC_ManaDesc,new Object[0]));
        return info;
    }
}
