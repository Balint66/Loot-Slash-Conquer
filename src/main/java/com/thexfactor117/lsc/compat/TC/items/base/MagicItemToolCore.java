package com.thexfactor117.lsc.compat.TC.items.base;

import com.thexfactor117.lsc.LootSlashConquer;
import com.thexfactor117.lsc.capabilities.implementation.LSCPlayerCapability;
import com.thexfactor117.lsc.compat.TC.items.base.nbt.MagicNBT;
import com.thexfactor117.lsc.config.Configs;
import com.thexfactor117.lsc.entities.projectiles.EntityFireball;
import com.thexfactor117.lsc.entities.projectiles.EntityIcebolt;
import com.thexfactor117.lsc.entities.projectiles.EntityLightning;
import com.thexfactor117.lsc.entities.projectiles.Rune;
import com.thexfactor117.lsc.network.PacketUpdatePlayerStats;
import com.thexfactor117.lsc.util.PlayerUtil;
import com.thexfactor117.lsc.util.misc.NBTHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.ToolHelper;

import java.util.List;

public abstract class MagicItemToolCore extends MagicItemCore
{
    public MagicItemToolCore(String name, int durability, PartMaterialType... components) {
        super(name, durability, components);
    }

    public final NBTTagCompound buildTag(List<Material> materials) {
        return this.buildTagData(materials).get();
    }

    protected abstract MagicNBT buildTagData(List<Material> var1);

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {

        ItemStack currentStack = player.getHeldItem(hand);

        if(!ToolHelper.isBroken(currentStack)){

            LSCPlayerCapability cap = PlayerUtil.getLSCPlayer(player);

            if (cap != null && currentStack != null)
            {

                if (cap.getPlayerLevel() < NBTHelper.loadStackNBT(currentStack).getInteger("Level"))
                {
                    player.sendMessage(new TextComponentString(TextFormatting.RED + "WARNING: You are using a high-leveled item. It will be useless and will take significantly more damage if it is not removed."));
                    return new ActionResult<ItemStack>(EnumActionResult.FAIL, currentStack);
                }

                if ((cap.getMana() - this.getManaPerUse() >= 0 && cap.getPlayerLevel() >= NBTHelper.loadStackNBT(currentStack).getInteger("Level")) || player.isCreative())
                {
                    player.setActiveHand(hand);
                    return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, currentStack);
                }
            }
        }



        return new ActionResult<ItemStack>(EnumActionResult.FAIL, currentStack);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity, int count)
    {
        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            LSCPlayerCapability cap = PlayerUtil.getLSCPlayer(player);
            NBTTagCompound nbt = NBTHelper.loadStackNBT(stack);

            if (cap != null)
            {
                // check to see if we have held it long enough
                double attackSpeed = nbt.getDouble("AttackSpeed") + (Configs.playerCategory.attackSpeedMultiplier * (cap.getTotalAgility()));

                if (count > (this.getMaxItemUseDuration(stack) - ((1 / attackSpeed) * 20)))
                {
                    return;
                }

                // fire projectile because check passed
                world.playSound(player, player.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F);

                if (!world.isRemote)
                {
                    // spawn entity and set position to specified direction
                    Vec3d look = player.getLookVec();

                     fireProjectile(world, player, stack, nbt, look);

                    // update mana and send to client
                    cap.decreaseMana(this.getManaPerUse());
                    LootSlashConquer.network.sendTo(new PacketUpdatePlayerStats(cap), (EntityPlayerMP) player);

                    // damage item
                    stack.damageItem(1, player);
                }
            }
        }
    }

    private void fireProjectile(World world, EntityPlayer player, ItemStack stack, NBTTagCompound nbt, Vec3d look)
    {
        if (Rune.getRune(nbt) == Rune.FIREBALL)
        {
            EntityFireball fireball = new EntityFireball(world, look.x, look.y, look.z, 1F, 0F, player, stack, 2);
            fireball.setPosition(player.posX + look.x, player.posY + look.y + 1.5, player.posZ + look.z);
            world.spawnEntity(fireball);
        }
        else if (Rune.getRune(nbt) == Rune.ICEBOLT)
        {
            EntityIcebolt icebolt = new EntityIcebolt(world, look.x, look.y, look.z, 1F, 0F, player, stack, 2);
            icebolt.setPosition(player.posX + look.x, player.posY + look.y + 1.5, player.posZ + look.z);
            world.spawnEntity(icebolt);
        }
        else if (Rune.getRune(nbt) == Rune.LIGHTNING)
        {
            EntityLightning lightning = new EntityLightning(world, look.x, look.y, look.z, 1F, 0F, player, stack, 2);
            lightning.setPosition(player.posX + look.x, player.posY + look.y + 1.5, player.posZ + look.z);
            world.spawnEntity(lightning);
        }
        else if (Rune.getRune(nbt) == Rune.FIRESTORM)
        {
            for (int i = 0; i < 4; i++)
            {
                EntityFireball fireball = new EntityFireball(world, look.x, look.y, look.z, 1F, 10F, player, stack, 2);
                fireball.setPosition(player.posX + look.x, player.posY + look.y + 1.5, player.posZ + look.z);
                world.spawnEntity(fireball);
            }
        }
        else if (Rune.getRune(nbt) == Rune.BLIZZARD)
        {
            for (int i = 0; i < 4; i++)
            {
                EntityIcebolt icebolt = new EntityIcebolt(world, look.x, look.y, look.z, 1F, 10F, player, stack, 2);
                icebolt.setPosition(player.posX + look.x, player.posY + look.y + 1.5, player.posZ + look.z);
                world.spawnEntity(icebolt);
            }
        }
        else if (Rune.getRune(nbt) == Rune.DISCHARGE)
        {
            for (int i = 0; i < 4; i++)
            {
                EntityLightning lightning = new EntityLightning(world, look.x, look.y, look.z, 1F, 10F, player, stack, 2);
                lightning.setPosition(player.posX + look.x, player.posY + look.y + 1.5, player.posZ + look.z);
                world.spawnEntity(lightning);
            }
        }
    }

}
