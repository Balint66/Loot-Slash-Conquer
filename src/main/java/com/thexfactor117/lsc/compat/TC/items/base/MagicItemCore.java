package com.thexfactor117.lsc.compat.TC.items.base;

import com.google.common.collect.Sets;
import com.thexfactor117.lsc.compat.TC.TCRegistry;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.IMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.library.utils.TooltipBuilder;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class MagicItemCore extends TinkersMagicItem
{

    public MagicItemCore(String name, int durability, PartMaterialType... components) {
        super(name, durability, components);
        this.setCreativeTab(TinkerRegistry.tabTools);
        this.setNoRepair();
        TCRegistry.registerMagicTool(this);
        this.addCategory(Category.WEAPON,Category.LAUNCHER,Category.NO_MELEE);
    }

    public int getMaxDamage(ItemStack stack) {
        return ToolHelper.getDurabilityStat(stack);
    }

    public void setDamage(ItemStack stack, int damage) {
        int max = this.getMaxDamage(stack);
        super.setDamage(stack, Math.min(max, damage));
        if (this.getDamage(stack) == max) {
            ToolHelper.breakTool(stack, (EntityLivingBase)null);
        }

    }

    public boolean isDamageable() {
        return true;
    }

    public boolean showDurabilityBar(ItemStack stack) {
        return super.showDurabilityBar(stack) && !ToolHelper.isBroken(stack);
    }

    @Override
    public void getTooltipDetailed(ItemStack stack, List<String> tooltips)
    {
        tooltips.addAll(this.getInformation(stack, false));
    }

    @Override
    public void getTooltipComponents(ItemStack stack, List<String> tooltips) {
        List<Material> materials = TinkerUtil.getMaterialsFromTagList(TagUtil.getBaseMaterialsTagList(stack));
        List<PartMaterialType> component = this.getRequiredComponents();
        if (materials.size() >= component.size()) {
            label47:
            for(int i = 0; i < component.size(); ++i) {
                PartMaterialType pmt = (PartMaterialType)component.get(i);
                Material material = (Material)materials.get(i);
                Iterator<IToolPart> partIter = pmt.getPossibleParts().iterator();
                if (partIter.hasNext()) {
                    IToolPart part = (IToolPart)partIter.next();
                    ItemStack partStack = part.getItemstackWithMaterial(material);
                    if (partStack != null) {
                        tooltips.add(material.getTextColor() + TextFormatting.UNDERLINE + partStack.getDisplayName());
                        Set<ITrait> usedTraits = Sets.newHashSet();
                        Iterator var12 = material.getAllStats().iterator();

                        while(true) {
                            IMaterialStats stats;
                            do {
                                if (!var12.hasNext()) {
                                    tooltips.add("");
                                    continue label47;
                                }

                                stats = (IMaterialStats)var12.next();
                            } while(!pmt.usesStat(stats.getIdentifier()));

                            tooltips.addAll(stats.getLocalizedInfo());
                            Iterator var14 = pmt.getApplicableTraitsForMaterial(material).iterator();

                            while(var14.hasNext()) {
                                ITrait trait = (ITrait)var14.next();
                                if (!usedTraits.contains(trait)) {
                                    tooltips.add(material.getTextColor() + trait.getLocalizedName());
                                    usedTraits.add(trait);
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    public List<String> getInformation(ItemStack stack, boolean detailed) {
        TooltipBuilder info = new TooltipBuilder(stack);
        info.addDurability(!detailed);
        if (this.hasCategory(Category.HARVEST)) {
            info.addHarvestLevel();
            info.addMiningSpeed();
        }

        if (this.hasCategory(Category.LAUNCHER)) {
            info.addDrawSpeed();
            info.addRange();
            info.addProjectileBonusDamage();
        }

        info.addAttack();
        if (ToolHelper.getFreeModifiers(stack) > 0) {
            info.addFreeModifiers();
        }

        if (detailed) {
            info.addModifierInfo();
        }

        NBTTagCompound mana = stack.getTagCompound().getCompoundTag("Stats");

        info.add("Mana cost: " + mana.getInteger("ManaCost"));

        return info.getTooltip();
    }

    @Nonnull
    public String getItemStackDisplayName(@Nonnull ItemStack stack) {
        List<Material> materials = TinkerUtil.getMaterialsFromTagList(TagUtil.getBaseMaterialsTagList(stack));
        Set<Material> nameMaterials = Sets.newLinkedHashSet();
        int[] var4 = this.getRepairParts();
        int var5 = var4.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            int index = var4[var6];
            if (index < materials.size()) {
                nameMaterials.add(materials.get(index));
            }
        }

        return Material.getCombinedItemName(super.getItemStackDisplayName(stack), nameMaterials);
    }

}
