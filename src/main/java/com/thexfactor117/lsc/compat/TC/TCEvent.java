package com.thexfactor117.lsc.compat.TC;

import com.google.common.collect.ImmutableList;
import com.thexfactor117.lsc.compat.TC.items.base.TinkersMagicItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import slimeknights.tconstruct.library.events.TinkerEvent;
import slimeknights.tconstruct.library.materials.Material;

public class TCEvent extends Event
{
    public TCEvent()
    {

    }

    public static class OnItemBuilding extends TinkerEvent {
        public NBTTagCompound tag;
        public final ImmutableList<Material> materials;
        public final TinkersMagicItem tool;

        public OnItemBuilding(NBTTagCompound tag, ImmutableList<Material> materials, TinkersMagicItem tool) {
            this.tag = tag;
            this.materials = materials;
            this.tool = tool;
        }

        public static TCEvent.OnItemBuilding fireEvent(NBTTagCompound tag, ImmutableList<Material> materials, TinkersMagicItem tool) {
            TCEvent.OnItemBuilding event = new TCEvent.OnItemBuilding(tag, materials, tool);
            MinecraftForge.EVENT_BUS.post(event);
            return event;
        }
    }

}
