package com.thexfactor117.lsc.compat.TC;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Locale;

public class TCUtil
{


    public static String getPrefixedName(String name) {
        if (!name.equals(name.toLowerCase(Locale.US))) {
            throw new IllegalArgumentException(String.format("Non-lowercase unlocalized name detected! %s", name));
        } else {
            return "lsc." + name;
        }
    }

    public static <T extends Item> T registerItem(IForgeRegistry<Item> registry, T item, String name) {
        item.setUnlocalizedName(getPrefixedName(name));
        register(registry, item, (String)name);
        return item;
    }

    private static <T extends IForgeRegistryEntry<T>> void register(IForgeRegistry<T> registry, T toRegister, String name) {
        register(registry, toRegister, new ResourceLocation("lsc",name));
    }

    private static <T extends IForgeRegistryEntry<T>> void register(IForgeRegistry<T> registry, T toRegister, ResourceLocation name) {
        toRegister.setRegistryName(name);
        registry.register(toRegister);
    }
}
