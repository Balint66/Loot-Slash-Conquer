package com.thexfactor117.lsc.compat.TC.parts;

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
}
