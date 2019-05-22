package com.thexfactor117.lsc.compat.TC.builder;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.thexfactor117.lsc.compat.TC.TCEvent;
import com.thexfactor117.lsc.compat.TC.items.base.MagicItemCore;
import com.thexfactor117.lsc.compat.TC.items.base.TinkersMagicItem;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;
import slimeknights.mantle.util.ItemStackList;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.events.TinkerEvent;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.tinkering.IRepairable;
import slimeknights.tconstruct.library.tinkering.MaterialItem;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.Pattern;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.library.utils.ListUtil;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;

import javax.annotation.Nonnull;
import java.util.*;

public class MagicItemBuilder
{
    private MagicItemBuilder(){}
    public static ItemStack tryBuildTool(NonNullList<ItemStack> stacks, String name, Collection<MagicItemCore> possibleTools) {
        int length = -1;

        for(int i = 0; i < stacks.size(); ++i) {
            if (((ItemStack)stacks.get(i)).isEmpty()) {
                if (length < 0) {
                    length = i;
                }
            } else if (length >= 0) {
                return ItemStack.EMPTY;
            }
        }

        if (length < 0) {
            return ItemStack.EMPTY;
        } else {
            NonNullList<ItemStack> input = ItemStackList.of(stacks);
            Iterator var8 = possibleTools.iterator();

            while(var8.hasNext()) {
                Item item = (Item)var8.next();
                if (item instanceof ToolCore) {
                    ItemStack output = ((MagicItemCore)item).buildItemFromStacks(input);
                    if (!output.isEmpty()) {
                        if (name != null && !name.isEmpty()) {
                            output.setStackDisplayName(name);
                        }

                        return output;
                    }
                }
            }

            return ItemStack.EMPTY;
        }
    }

    public static void addTrait(NBTTagCompound rootCompound, ITrait trait, int color) {
        if (TinkerRegistry.getTrait(trait.getIdentifier()) == null) {
            //TODO:Logging
            //log.error("addTrait: Trying to apply unregistered Trait {}", trait.getIdentifier());
        } else {
            IModifier modifier = TinkerRegistry.getModifier(trait.getIdentifier());
            if (modifier != null && modifier instanceof AbstractTrait) {
                AbstractTrait traitModifier = (AbstractTrait)modifier;
                NBTTagCompound tag = new NBTTagCompound();
                NBTTagList tagList = TagUtil.getModifiersTagList(rootCompound);
                int index = TinkerUtil.getIndexInList(tagList, traitModifier.getModifierIdentifier());
                if (index < 0) {
                    traitModifier.updateNBTforTrait(tag, color);
                    tagList.appendTag(tag);
                    TagUtil.setModifiersTagList(rootCompound, tagList);
                } else {
                    tag = tagList.getCompoundTagAt(index);
                }

                traitModifier.applyEffect(rootCompound, tag);
            } else {
                //TODO:Logging
                //log.error("addTrait: No matching modifier for the Trait {} present", trait.getIdentifier());
            }
        }
    }

    @Nonnull
    public static ItemStack tryRepairTool(NonNullList<ItemStack> stacks, ItemStack toolStack, boolean removeItems) {
        if (toolStack != null && toolStack.getItem() instanceof IRepairable) {
            if (!removeItems) {
                stacks = Util.deepCopyFixedNonNullList(stacks);
            }

            return ((IRepairable)toolStack.getItem()).repair(toolStack, stacks);
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Nonnull
    public static ItemStack tryModifyTool(NonNullList<ItemStack> input, ItemStack toolStack, boolean removeItems) throws TinkerGuiException {
        ItemStack copy = toolStack.copy();
        NonNullList<ItemStack> stacks = Util.deepCopyFixedNonNullList(input);
        NonNullList<ItemStack> usedStacks = Util.deepCopyFixedNonNullList(input);
        Set<IModifier> appliedModifiers = Sets.newHashSet();
        Iterator var7 = TinkerRegistry.getAllModifiers().iterator();

        while(var7.hasNext()) {
            IModifier modifier = (IModifier)var7.next();

            while(true) {
                Optional<RecipeMatch.Match> matchOptional = modifier.matches(stacks);
                ItemStack backup = copy.copy();
                if (matchOptional.isPresent()) {
                    RecipeMatch.Match match;
                    for(match = (RecipeMatch.Match)matchOptional.get(); match.amount > 0; --match.amount) {
                        TinkerGuiException caughtException = null;
                        boolean canApply = false;

                        try {
                            canApply = modifier.canApply(copy, toolStack);
                        } catch (TinkerGuiException var15) {
                            caughtException = var15;
                        }

                        if (!canApply) {
                            if (caughtException != null && !appliedModifiers.contains(modifier)) {
                                throw caughtException;
                            }

                            copy = backup;
                            RecipeMatch.removeMatch(stacks, match);
                            break;
                        }

                        modifier.apply(copy);
                        appliedModifiers.add(modifier);
                    }

                    if (match.amount == 0) {
                        RecipeMatch.removeMatch(stacks, match);
                        RecipeMatch.removeMatch(usedStacks, match);
                    }
                }

                if (!matchOptional.isPresent()) {
                    break;
                }
            }
        }

        int i;
        for(i = 0; i < input.size(); ++i) {
            if (!((ItemStack)input.get(i)).isEmpty() && ItemStack.areItemStacksEqual((ItemStack)input.get(i), (ItemStack)stacks.get(i))) {
                if (!appliedModifiers.isEmpty()) {
                    String error = I18n.translateToLocalFormatted("gui.error.no_modifier_for_item", new Object[]{((ItemStack)input.get(i)).getDisplayName()});
                    throw new TinkerGuiException(error);
                }

                return ItemStack.EMPTY;
            }
        }

        if (removeItems) {
            for(i = 0; i < input.size(); ++i) {
                if (((ItemStack)usedStacks.get(i)).isEmpty()) {
                    ((ItemStack)input.get(i)).setCount(0);
                } else {
                    ((ItemStack)input.get(i)).setCount(((ItemStack)usedStacks.get(i)).getCount());
                }
            }
        }

        if (!appliedModifiers.isEmpty()) {
            if (copy.getItem() instanceof TinkersMagicItem) {
                NBTTagCompound root = TagUtil.getTagSafe(copy);
                rebuildTool(root, (TinkersMagicItem)copy.getItem());
                copy.setTagCompound(root);
            }

            return copy;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Nonnull
    public static ItemStack tryReplaceToolParts(ItemStack toolStack, NonNullList<ItemStack> toolPartsIn, boolean removeItems) throws TinkerGuiException {
        if (toolStack != null && toolStack.getItem() instanceof TinkersMagicItem) {
            NonNullList<ItemStack> inputItems = ItemStackList.of(Util.deepCopyFixedNonNullList(toolPartsIn));
            if (!TinkerEvent.OnToolPartReplacement.fireEvent(inputItems, toolStack)) {
                return ItemStack.EMPTY;
            } else {
                NonNullList<ItemStack> toolParts = Util.deepCopyFixedNonNullList(inputItems);
                TIntIntMap assigned = new TIntIntHashMap();
                TinkersMagicItem tool = (TinkersMagicItem)toolStack.getItem();
                NBTTagList materialList = TagUtil.getBaseMaterialsTagList(toolStack).copy();

                ItemStack copyToCheck;
                for(int i = 0; i < toolParts.size(); ++i) {
                    copyToCheck = (ItemStack)toolParts.get(i);
                    if (!copyToCheck.isEmpty()) {
                        if (!(copyToCheck.getItem() instanceof IToolPart)) {
                            return ItemStack.EMPTY;
                        }

                        int candidate = -1;
                        List<PartMaterialType> pms = tool.getRequiredComponents();

                        for(int j = 0; j < pms.size(); ++j) {
                            PartMaterialType pmt = (PartMaterialType)pms.get(j);
                            String partMat = ((IToolPart)copyToCheck.getItem()).getMaterial(copyToCheck).getIdentifier();
                            String currentMat = materialList.getStringTagAt(j);
                            if (pmt.isValid(copyToCheck) && !partMat.equals(currentMat) && !assigned.valueCollection().contains(j)) {
                                candidate = j;
                                if (i <= j) {
                                    break;
                                }
                            }
                        }

                        if (candidate < 0) {
                            return ItemStack.EMPTY;
                        }

                        assigned.put(i, candidate);
                    }
                }

                if (assigned.isEmpty()) {
                    return ItemStack.EMPTY;
                } else {
                    assigned.forEachEntry((ix, jx) -> {
                        String mat = ((IToolPart)((ItemStack)toolParts.get(ix)).getItem()).getMaterial((ItemStack)toolParts.get(ix)).getIdentifier();
                        materialList.set(jx, new NBTTagString(mat));
                        if (removeItems && ix < toolPartsIn.size() && !((ItemStack)toolPartsIn.get(ix)).isEmpty()) {
                            ((ItemStack)toolPartsIn.get(ix)).shrink(1);
                        }

                        return true;
                    });
                    TinkersMagicItem tinkersMagicItem = (TinkersMagicItem)toolStack.getItem();
                    copyToCheck = tinkersMagicItem.buildItem(TinkerUtil.getMaterialsFromTagList(materialList));
                    NBTTagList modifiers = TagUtil.getBaseModifiersTagList(toolStack);

                    for(int i = 0; i < modifiers.tagCount(); ++i) {
                        String id = modifiers.getStringTagAt(i);
                        IModifier mod = TinkerRegistry.getModifier(id);
                        boolean canApply = false;

                        try {
                            canApply = mod != null && mod.canApply(copyToCheck, copyToCheck);
                        } catch (TinkerGuiException var18) {
                            if (ToolHelper.getFreeModifiers(copyToCheck) < 3) {
                                ItemStack copyWithModifiers = copyToCheck.copy();
                                NBTTagCompound nbt = TagUtil.getToolTag(copyWithModifiers);
                                nbt.setInteger("FreeModifiers", 3);
                                TagUtil.setToolTag(copyWithModifiers, nbt);
                                canApply = mod.canApply(copyWithModifiers, copyWithModifiers);
                            }
                        }

                        if (!canApply) {
                            throw new TinkerGuiException();
                        }
                    }

                    ItemStack output = toolStack.copy();
                    TagUtil.setBaseMaterialsTagList(output, materialList);
                    NBTTagCompound tag = TagUtil.getTagSafe(output);
                    rebuildTool(tag, (TinkersMagicItem)output.getItem());
                    output.setTagCompound(tag);
                    if (output.getItemDamage() > output.getMaxDamage()) {
                        String error = I18n.translateToLocalFormatted("gui.error.not_enough_durability", new Object[]{output.getItemDamage() - output.getMaxDamage()});
                        throw new TinkerGuiException(error);
                    } else {
                        return output;
                    }
                }
            }
        } else {
            return ItemStack.EMPTY;
        }
    }

    public static NonNullList<ItemStack> tryBuildToolPart(ItemStack pattern, NonNullList<ItemStack> materialItems, boolean removeItems) throws TinkerGuiException {
        Item itemPart = Pattern.getPartFromTag(pattern);
        if (itemPart != null && itemPart instanceof MaterialItem && itemPart instanceof IToolPart) {
            IToolPart part = (IToolPart)itemPart;
            if (!removeItems) {
                materialItems = Util.deepCopyFixedNonNullList(materialItems);
            }

            Optional<RecipeMatch.Match> match = Optional.empty();
            Material foundMaterial = null;
            Iterator var7 = TinkerRegistry.getAllMaterials().iterator();

            while(var7.hasNext()) {
                Material material = (Material)var7.next();
                if (material.isCraftable()) {
                    Optional<RecipeMatch.Match> newMatch = material.matches(materialItems, part.getCost());
                    if (newMatch.isPresent() && !match.isPresent()) {
                        match = newMatch;
                        foundMaterial = material;
                    }
                }
            }

            if (!match.isPresent()) {
                return null;
            } else {
                ItemStack output = ((MaterialItem)itemPart).getItemstackWithMaterial(foundMaterial);
                if (output.isEmpty()) {
                    return null;
                } else if (output.getItem() instanceof IToolPart && !((IToolPart)output.getItem()).canUseMaterial(foundMaterial)) {
                    return null;
                } else {
                    RecipeMatch.removeMatch(materialItems, (RecipeMatch.Match)match.get());
                    ItemStack secondary = ItemStack.EMPTY;
                    int leftover = (((RecipeMatch.Match)match.get()).amount - part.getCost()) / 72;
                    if (leftover > 0) {
                        secondary = TinkerRegistry.getShard(foundMaterial);
                        secondary.setCount(leftover);
                    }

                    return ListUtil.getListFrom(new ItemStack[]{output, secondary});
                }
            }
        } else {
            String error = I18n.translateToLocalFormatted("gui.error.invalid_pattern", new Object[0]);
            throw new TinkerGuiException(error);
        }
    }

    public static void rebuildTool(NBTTagCompound rootNBT, TinkersMagicItem tinkersMagicItem) throws TinkerGuiException {
        boolean broken = TagUtil.getToolTag(rootNBT).getBoolean("Broken");
        NBTTagList materialTag = TagUtil.getBaseMaterialsTagList(rootNBT);
        List<Material> materials = TinkerUtil.getMaterialsFromTagList(materialTag);
        List pms = tinkersMagicItem.getRequiredComponents();

        while(materials.size() < pms.size()) {
            materials.add(Material.UNKNOWN);
        }

        for(int i = 0; i < pms.size(); ++i) {
            if (!((PartMaterialType)pms.get(i)).isValidMaterial((Material)materials.get(i))) {
                materials.set(i, Material.UNKNOWN);
            }
        }

        NBTTagCompound toolTag = tinkersMagicItem.buildTag(materials);
        TagUtil.setToolTag(rootNBT, toolTag);
        rootNBT.setTag("StatsOriginal", toolTag.copy());
        NBTTagList modifiersTagOld = TagUtil.getModifiersTagList(rootNBT);
        rootNBT.removeTag("Modifiers");
        rootNBT.setTag("Modifiers", new NBTTagList());
        rootNBT.removeTag("ench");
        rootNBT.removeTag("EnchantEffect");
        rootNBT.removeTag("Traits");
        tinkersMagicItem.addMaterialTraits(rootNBT, materials);
        TCEvent.OnItemBuilding.fireEvent(rootNBT, ImmutableList.copyOf(materials), tinkersMagicItem);
        NBTTagList modifiers = TagUtil.getBaseModifiersTagList(rootNBT);
        NBTTagList modifiersTag = TagUtil.getModifiersTagList(rootNBT);

        int freeModifiers;
        for(freeModifiers = 0; freeModifiers < modifiers.tagCount(); ++freeModifiers) {
            String identifier = modifiers.getStringTagAt(freeModifiers);
            IModifier modifier = TinkerRegistry.getModifier(identifier);
            if (modifier == null) {
                //TODO:Logging
                //log.debug("Missing modifier: {}", identifier);
            } else {
                int index = TinkerUtil.getIndexInList(modifiersTagOld, modifier.getIdentifier());
                NBTTagCompound tag;
                if (index >= 0) {
                    tag = modifiersTagOld.getCompoundTagAt(index);
                } else {
                    tag = new NBTTagCompound();
                }

                modifier.applyEffect(rootNBT, tag);
                if (!tag.hasNoTags()) {
                    int indexNew = TinkerUtil.getIndexInList(modifiersTag, modifier.getIdentifier());
                    if (indexNew >= 0) {
                        modifiersTag.set(indexNew, tag);
                    } else {
                        modifiersTag.appendTag(tag);
                    }
                }
            }
        }

        toolTag = TagUtil.getToolTag(rootNBT);
        freeModifiers = toolTag.getInteger("FreeModifiers");
        freeModifiers -= TagUtil.getBaseModifiersUsed(rootNBT);
        toolTag.setInteger("FreeModifiers", Math.max(0, freeModifiers));
        if (broken) {
            toolTag.setBoolean("Broken", true);
        }

        TagUtil.setToolTag(rootNBT, toolTag);
        if (freeModifiers < 0) {
            throw new TinkerGuiException(Util.translateFormatted("gui.error.not_enough_modifiers", new Object[]{-freeModifiers}));
        }
    }

    public static short getEnchantmentLevel(NBTTagCompound rootTag, Enchantment enchantment) {
        NBTTagList enchantments = rootTag.getTagList("ench", 10);
        int id = Enchantment.getEnchantmentID(enchantment);

        for(int i = 0; i < enchantments.tagCount(); ++i) {
            if (enchantments.getCompoundTagAt(i).getShort("id") == id) {
                return enchantments.getCompoundTagAt(i).getShort("lvl");
            }
        }

        return 0;
    }

    public static void addEnchantment(NBTTagCompound rootTag, Enchantment enchantment) {
        NBTTagList enchantments = rootTag.getTagList("ench", 10);
        NBTTagCompound enchTag = new NBTTagCompound();
        int enchId = Enchantment.getEnchantmentID(enchantment);
        int id = -1;

        int i;
        for(i = 0; i < enchantments.tagCount(); ++i) {
            if (enchantments.getCompoundTagAt(i).getShort("id") == enchId) {
                enchTag = enchantments.getCompoundTagAt(i);
                id = i;
                break;
            }
        }

        i = enchTag.getShort("lvl") + 1;
        i = Math.min(i, enchantment.getMaxLevel());
        enchTag.setShort("id", (short)enchId);
        enchTag.setShort("lvl", (short)i);
        if (id < 0) {
            enchantments.appendTag(enchTag);
        } else {
            enchantments.set(id, enchTag);
        }

        rootTag.setTag("ench", enchantments);
    }
}
