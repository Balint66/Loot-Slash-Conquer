package com.thexfactor117.lsc.compat.TC;

import com.google.common.collect.Lists;
import com.thexfactor117.lsc.compat.TC.items.base.MagicItemCore;
import com.thexfactor117.lsc.compat.TC.parts.StaffHeadMaterialStats;
import gnu.trove.set.hash.TLinkedHashSet;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.commons.lang3.tuple.Pair;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.IPattern;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.ToolPart;
import slimeknights.tconstruct.tools.TinkerMaterials;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TCRegistry
{

    public static final Set<MagicItemCore> tools;
    static List<ToolPart> toolparts = Lists.newLinkedList();
    static List<Pair<Item, ToolPart>> toolPartPatterns = Lists.newLinkedList();

    static
    {
        tools = new TLinkedHashSet<>();
    }

    public static void registerMagicTool(MagicItemCore tool) {
        tools.add(tool);
        Iterator var1 = tool.getRequiredComponents().iterator();

        while(var1.hasNext()) {
            PartMaterialType pmt = (PartMaterialType)var1.next();
            Iterator var3 = pmt.getPossibleParts().iterator();

            while(var3.hasNext()) {
                IToolPart tp = (IToolPart)var3.next();
                slimeknights.tconstruct.library.TinkerRegistry.registerToolPart(tp);
            }
        }

    }

    public static ToolPart registerToolPart(IForgeRegistry<Item> registry, ToolPart part, String name) {
        return registerToolPart(registry, part, name, TinkerTools.pattern);
    }

    private static  <T extends Item & IPattern> ToolPart registerToolPart(IForgeRegistry<Item> registry, ToolPart part, String name, T pattern) {
        ToolPart ret = (ToolPart)registerItem(registry, part, name);
        if (pattern != null) {
            toolPartPatterns.add(Pair.of(pattern, ret));
        }

        toolparts.add(ret);
        return ret;
    }

    private static <T extends Item> T registerItem(IForgeRegistry<Item> registry, T item, String name) {
        if (!name.equals(name.toLowerCase(Locale.US))) {
            throw new IllegalArgumentException(String.format("Unlocalized names need to be all lowercase! Item: %s", name));
        } else {
            item.setUnlocalizedName("lsc." + name);
            item.setRegistryName("lsc",name);
            registry.register(item);
            return item;
        }
    }

    public static void registerMaterialStats()
    {
        Material.UNKNOWN.addStats(new StaffHeadMaterialStats(0,10));
        TinkerRegistry.addMaterialStats(TinkerMaterials.wood,new StaffHeadMaterialStats(10,10));
    }

}
