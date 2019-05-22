package com.thexfactor117.lsc.compat.TC.items.base;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.thexfactor117.lsc.compat.TC.TCEvent;
import com.thexfactor117.lsc.compat.TC.builder.MagicItemBuilder;
import com.thexfactor117.lsc.items.base.ItemMagical;
import gnu.trove.set.hash.THashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.tinkering.*;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.library.utils.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class TinkersMagicItem extends ItemMagical implements IRepairable, ITinkerable
{

    protected final PartMaterialType[] requiredComponents;
    protected final Set<Category> categories = new THashSet();

    public TinkersMagicItem(String name, int durability, PartMaterialType... components)
    {
        super(name, 5, 1.25, 10, durability);
        this.requiredComponents = components;
        this.setMaxStackSize(1);
    }

    public List<PartMaterialType> getRequiredComponents()
    {
        return ImmutableList.copyOf( this.requiredComponents);
    }

    public List<PartMaterialType> getBuildComponents()
    {
        return this.getRequiredComponents();
    }

    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Nonnull
    public Entity createEntity(World world, Entity entityIn, ItemStack itemstack) {
        EntityItem entity = new IndestructibleEntityItem(world, entityIn.posX, entityIn.posY, entityIn.posZ, itemstack);
        if (entityIn instanceof EntityItem) {
            NBTTagCompound tag = new NBTTagCompound();
            entityIn.writeToNBT(tag);
            entity.setPickupDelay(tag.getShort("PickupDelay"));
        }

        entity.motionX = entityIn.motionX;
        entity.motionY = entityIn.motionY;
        entity.motionZ = entityIn.motionZ;
        return entity;
    }

    protected void addCategory(Category... categories) {
        Collections.addAll(this.categories, categories);
    }

    public boolean hasCategory(Category category) {
        return this.categories.contains(category);
    }

    protected Category[] getCategories() {
        Category[] out = new Category[this.categories.size()];
        int i = 0;

        Category category;
        for(Iterator var3 = this.categories.iterator(); var3.hasNext(); out[i++] = category) {
            category = (Category)var3.next();
        }

        return out;
    }

    public boolean validComponent(int slot, ItemStack stack) {
        return slot <= this.requiredComponents.length && slot >= 0 && this.requiredComponents[slot].isValid(stack);
    }

    @Nonnull
    public ItemStack buildItemFromStacks(NonNullList<ItemStack> stacks) {
        long itemCount = stacks.stream().filter((stack) -> {
            return !stack.isEmpty();
        }).count();
        List<Material> materials = new ArrayList(stacks.size());
        if (itemCount != (long)this.requiredComponents.length) {
            return ItemStack.EMPTY;
        } else {
            for(int i = 0; (long)i < itemCount; ++i) {
                if (!this.validComponent(i, (ItemStack)stacks.get(i))) {
                    return ItemStack.EMPTY;
                }

                materials.add(TinkerUtil.getMaterialFromStack((ItemStack)stacks.get(i)));
            }

            return this.buildItem(materials);
        }
    }

    @Nonnull
    public ItemStack buildItem(List<Material> materials) {
        ItemStack staff = new ItemStack(this);
        staff.setTagCompound(this.buildItemNBT(materials));
        return staff;
    }

    public NBTTagCompound buildItemNBT(List<Material> materials) {
        NBTTagCompound basetag = new NBTTagCompound();
        NBTTagCompound toolTag = this.buildTag(materials);
        NBTTagCompound dataTag = this.buildData(materials);
        basetag.setTag("TinkerData", dataTag);
        basetag.setTag("Stats", toolTag);
        basetag.setTag("StatsOriginal", toolTag.copy());
        TagUtil.setCategories(basetag, this.getCategories());
        this.addMaterialTraits(basetag, materials);
        TCEvent.OnItemBuilding.fireEvent(basetag, ImmutableList.copyOf(materials), this);
        return basetag;
    }

    private NBTTagCompound buildData(List<Material> materials) {
        NBTTagCompound base = new NBTTagCompound();
        NBTTagList materialList = new NBTTagList();
        Iterator var4 = materials.iterator();

        while(var4.hasNext()) {
            Material material = (Material)var4.next();
            materialList.appendTag(new NBTTagString(material.identifier));
        }

        NBTTagList modifierList = new NBTTagList();
        modifierList.appendTag(new NBTTagString());
        modifierList.removeTag(0);
        base.setTag("Materials", materialList);
        base.setTag("Modifiers", modifierList);
        return base;
    }
    @Nonnull
    public ItemStack buildItemForRendering(List<Material> materials) {
        ItemStack tool = new ItemStack(this);
        NBTTagCompound base = new NBTTagCompound();
        base.setTag("TinkerData", this.buildData(materials));
        tool.setTagCompound(base);
        return tool;
    }

    @Nonnull
    public ItemStack buildItemForRenderingInGui() {
        List<Material> materials = (List) IntStream.range(0, this.getRequiredComponents().size()).mapToObj(this::getMaterialForPartForGuiRendering).collect(Collectors.toList());
        return this.buildItemForRendering(materials);
    }

    @SideOnly(Side.CLIENT)
    public Material getMaterialForPartForGuiRendering(int index) {
        return ClientProxy.RenderMaterials[index % ClientProxy.RenderMaterials.length];
    }

    public abstract NBTTagCompound buildTag(List<Material> var1);

    public boolean hasValidMaterials(ItemStack stack) {
        NBTTagList list = TagUtil.getBaseMaterialsTagList(stack);
        List<Material> materials = TinkerUtil.getMaterialsFromTagList(list);
        if (materials.size() != this.requiredComponents.length) {
            return false;
        } else {
            for(int i = 0; i < materials.size(); ++i) {
                Material material = (Material)materials.get(i);
                PartMaterialType required = this.requiredComponents[i];
                if (!required.isValidMaterial(material)) {
                    return false;
                }
            }

            return true;
        }
    }

    public void addMaterialTraits(NBTTagCompound root, List<Material> materials) {
        int size = this.requiredComponents.length;
        if (materials.size() < size) {
            size = materials.size();
        }

        for(int i = 0; i < size; ++i) {
            PartMaterialType required = this.requiredComponents[i];
            Material material = (Material)materials.get(i);
            Iterator var7 = required.getApplicableTraitsForMaterial(material).iterator();

            while(var7.hasNext()) {
                ITrait trait = (ITrait)var7.next();
                ToolBuilder.addTrait(root, trait, material.materialTextColor);
            }
        }

    }

    public int[] getRepairParts() {
        return new int[]{1};
    }

    public float getRepairModifierForPart(int index) {
        return 1.0F;
    }

    @Nonnull
    public ItemStack repair(ItemStack repairable, NonNullList<ItemStack> repairItems) {
        if (repairable.getItemDamage() == 0 && !ToolHelper.isBroken(repairable)) {
            return ItemStack.EMPTY;
        } else {
            List<Material> materials = TinkerUtil.getMaterialsFromTagList(TagUtil.getBaseMaterialsTagList(repairable));
            if (materials.isEmpty()) {
                return ItemStack.EMPTY;
            } else {
                NonNullList<ItemStack> items = Util.deepCopyFixedNonNullList(repairItems);
                boolean foundMatch = false;
                int[] var6 = this.getRepairParts();
                int amount = var6.length;

                for(int var8 = 0; var8 < amount; ++var8) {
                    int index = var6[var8];
                    Material material = (Material)materials.get(index);
                    if (this.repairCustom(material, items) > 0) {
                        foundMatch = true;
                    }

                    Optional<RecipeMatch.Match> match = material.matches(items);
                    if (match.isPresent()) {
                        foundMatch = true;

                        while((match = material.matches(items)).isPresent()) {
                            RecipeMatch.removeMatch(items, (RecipeMatch.Match)match.get());
                        }
                    }
                }

                if (!foundMatch) {
                    return ItemStack.EMPTY;
                } else {
                    for(int i = 0; i < repairItems.size(); ++i) {
                        if (!((ItemStack)repairItems.get(i)).isEmpty() && ItemStack.areItemStacksEqual((ItemStack)repairItems.get(i), (ItemStack)items.get(i))) {
                            return ItemStack.EMPTY;
                        }
                    }

                    ItemStack item = repairable.copy();

                    do {
                        amount = this.calculateRepairAmount(materials, repairItems);
                        if (amount <= 0) {
                            break;
                        }

                        ToolHelper.repairTool(item, this.calculateRepair(item, amount));
                        NBTTagCompound tag = TagUtil.getExtraTag(item);
                        tag.setInteger("RepairCount", tag.getInteger("RepairCount") + 1);
                        TagUtil.setExtraTag(item, tag);
                    } while(item.getItemDamage() > 0);

                    return item;
                }
            }
        }
    }

    protected int repairCustom(Material material, NonNullList<ItemStack> repairItems) {
        return 0;
    }

    protected int calculateRepairAmount(List<Material> materials, NonNullList<ItemStack> repairItems) {
        Set<Material> materialsMatched = Sets.newHashSet();
        float durability = 0.0F;
        int[] var5 = this.getRepairParts();
        int var6 = var5.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            int index = var5[var7];
            Material material = (Material)materials.get(index);
            if (!materialsMatched.contains(material)) {
                durability += (float)this.repairCustom(material, repairItems) * this.getRepairModifierForPart(index);
                Optional<RecipeMatch.Match> matchOptional = material.matches(repairItems);
                if (matchOptional.isPresent()) {
                    RecipeMatch.Match match = (RecipeMatch.Match)matchOptional.get();
                    HeadMaterialStats stats = (HeadMaterialStats)material.getStats("head");
                    if (stats != null) {
                        materialsMatched.add(material);
                        durability += (float)stats.durability * (float)match.amount * this.getRepairModifierForPart(index) / 144.0F;
                        RecipeMatch.removeMatch(repairItems, match);
                    }
                }
            }
        }

        durability *= 1.0F + ((float)materialsMatched.size() - 1.0F) / 9.0F;
        return (int)durability;
    }

    protected int calculateRepair(ItemStack tool, int amount) {
        float origDur = (float)TagUtil.getOriginalToolStats(tool).durability;
        float actualDur = (float)ToolHelper.getDurabilityStat(tool);
        float durabilityFactor = actualDur / origDur;
        float increase = (float)amount * Math.min(10.0F, durabilityFactor);
        increase = Math.max(increase, actualDur / 64.0F);
        int modifiersUsed = TagUtil.getBaseModifiersUsed(tool.getTagCompound());
        float mods = 1.0F;
        if (modifiersUsed == 1) {
            mods = 0.95F;
        } else if (modifiersUsed == 2) {
            mods = 0.9F;
        } else if (modifiersUsed >= 3) {
            mods = 0.85F;
        }

        increase *= mods;
        NBTTagCompound tag = TagUtil.getExtraTag(tool);
        int repair = tag.getInteger("RepairCount");
        float repairDimishingReturns = (float)(100 - repair / 2) / 100.0F;
        if (repairDimishingReturns < 0.5F) {
            repairDimishingReturns = 0.5F;
        }

        increase *= repairDimishingReturns;
        return (int)Math.ceil((double)increase);
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        boolean shift = Util.isShiftKeyDown();
        boolean ctrl = Util.isCtrlKeyDown();
        if (!shift && !ctrl) {
            this.getTooltip(stack, tooltip);
            tooltip.add("");
            tooltip.add(Util.translate("tooltip.tool.holdShift", new Object[0]));
            tooltip.add(Util.translate("tooltip.tool.holdCtrl", new Object[0]));
            if (worldIn != null) {
                tooltip.add(TextFormatting.BLUE + I18n.translateToLocalFormatted("attribute.modifier.plus.0", new Object[]{Util.df.format((double)ToolHelper.getActualDamage(stack, Minecraft.getMinecraft().player)), I18n.translateToLocal("attribute.name.generic.attackDamage")}));
            }
        } else if (Config.extraTooltips && shift) {
            this.getTooltipDetailed(stack, tooltip);
        } else if (Config.extraTooltips && ctrl) {
            this.getTooltipComponents(stack, tooltip);
        }

    }

    public void getTooltip(ItemStack stack, List<String> tooltips) {
        TooltipBuilder.addModifierTooltips(stack, tooltips);
    }

    @Nonnull
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.COMMON;
    }

    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    public boolean updateItemStackNBT(NBTTagCompound nbt) {
        if (nbt.hasKey("TinkerData")) {
            try {
                MagicItemBuilder.rebuildTool(nbt, this);
            } catch (TinkerGuiException var3) {
            }
        }

        return true;
    }
}

