package com.thexfactor117.lsc.compat.TC.items.base;

import com.thexfactor117.lsc.items.base.ItemMagical;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tconstruct.library.tinkering.IRepairable;
import slimeknights.tconstruct.library.tinkering.ITinkerable;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;

public abstract class TikersMagicItem extends ItemMagical implements IRepairable, ITinkerable
{

    protected final PartMaterialType[] requiredComponents;

    public TikersMagicItem(String name, double baseDamage, double attackSpeed, int manaPerUse, int durability, PartMaterialType... components)
    {
        super(name, baseDamage, attackSpeed, manaPerUse, durability);
        this.requiredComponents = components;
        this.setMaxStackSize(1);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        if (this.getRegistryName() != null) {
            ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
        }

    }

}
