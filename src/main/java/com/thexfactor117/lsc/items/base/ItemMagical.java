package com.thexfactor117.lsc.items.base;

import javax.annotation.Nullable;

import com.thexfactor117.lsc.LootSlashConquer;
import com.thexfactor117.lsc.capabilities.cap.CapabilityPlayerInformation;
import com.thexfactor117.lsc.capabilities.cap.CapabilityPlayerStats;
import com.thexfactor117.lsc.capabilities.implementation.PlayerInformation;
import com.thexfactor117.lsc.capabilities.implementation.PlayerStats;
import com.thexfactor117.lsc.config.Configs;
import com.thexfactor117.lsc.entities.projectiles.EntityFireball;
import com.thexfactor117.lsc.entities.projectiles.EntityIcebolt;
import com.thexfactor117.lsc.entities.projectiles.EntityLightning;
import com.thexfactor117.lsc.entities.projectiles.Rune;
import com.thexfactor117.lsc.init.ModTabs;
import com.thexfactor117.lsc.network.PacketUpdatePlayerStats;
import com.thexfactor117.lsc.util.NBTHelper;
import com.thexfactor117.lsc.util.Reference;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * 
 * @author TheXFactor117
 *
 */
public class ItemMagical extends Item
{
	private double baseDamage;
	private double baseAttackSpeed;
	private int manaPerUse;
		
	public ItemMagical(String name, double baseDamage, double attackSpeed, int manaPerUse, int durability)
	{
		super();
		this.setRegistryName(Reference.MODID, name);
		this.setUnlocalizedName(name);
		this.setCreativeTab(ModTabs.lscTab);
		this.setMaxStackSize(1);
		this.setNoRepair();
		this.setMaxDamage(durability);
		this.baseDamage = baseDamage;
		this.baseAttackSpeed = attackSpeed;
		this.manaPerUse = manaPerUse;
		
		// adds a property override so we can render the right texture based on how long we have been using the item.
		this.addPropertyOverride(new ResourceLocation("charged"), new IItemPropertyGetter()
		{
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity)
			{
				if (entity instanceof EntityPlayer)
				{
					EntityPlayer player = (EntityPlayer) entity;
					NBTTagCompound nbt = NBTHelper.loadStackNBT(stack);
					PlayerInformation playerInfo = (PlayerInformation) player.getCapability(CapabilityPlayerInformation.PLAYER_INFORMATION, null);
					
					if (playerInfo != null)
					{
						double attackSpeed = nbt.getDouble("AttackSpeed") + (Configs.playerCategory.attackSpeedMultiplier * (playerInfo.getTotalAgility()));
						
						return player.isHandActive() && player.getActiveItemStack() == stack && player.getItemInUseCount() < (stack.getMaxItemUseDuration() - (1 / attackSpeed) * 20) ? 1 : 0;
					}
				}
				
				return 0;
			}
		});
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		PlayerStats statsCap = (PlayerStats) player.getCapability(CapabilityPlayerStats.PLAYER_STATS, null);
		PlayerInformation playerInfo = (PlayerInformation) player.getCapability(CapabilityPlayerInformation.PLAYER_INFORMATION, null);
		ItemStack currentStack = player.getHeldItem(hand);
		
		if (statsCap != null && playerInfo != null && currentStack != null)
		{
			if ((statsCap.getMana() - this.manaPerUse >= 0 && playerInfo.getPlayerLevel() >= NBTHelper.loadStackNBT(currentStack).getInteger("Level")) || player.isCreative())
			{	
				player.setActiveHand(hand);
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, currentStack);
			}
		}
		
		if (playerInfo.getPlayerLevel() < NBTHelper.loadStackNBT(currentStack).getInteger("Level"))
		{
			player.sendMessage(new TextComponentString(TextFormatting.RED + "WARNING: You are using a high-leveled item. It will be useless and will take significantly more damage if it is not removed."));
		}
		
		return new ActionResult<ItemStack>(EnumActionResult.FAIL, currentStack);
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity, int count)
	{	
		if (entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) entity;
			PlayerStats statsCap = (PlayerStats) player.getCapability(CapabilityPlayerStats.PLAYER_STATS, null);	
			PlayerInformation info = (PlayerInformation) player.getCapability(CapabilityPlayerInformation.PLAYER_INFORMATION, null);
			NBTTagCompound nbt = NBTHelper.loadStackNBT(stack);
			
			if (info != null)
			{
				// check to see if we have held it long enough
				double attackSpeed = nbt.getDouble("AttackSpeed") + (Configs.playerCategory.attackSpeedMultiplier * (info.getTotalAgility()));
				
				if (count > (this.getMaxItemUseDuration(stack) - ((1 / attackSpeed) * 20))) 
				{
					return;
				}
				
				// fire projectile because check passed
				if (statsCap != null)
				{
					world.playSound(player, player.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F);
					
					if (!world.isRemote)
					{
						// spawn entity and set position to specified direction
						Vec3d look = player.getLookVec();

						fireProjectile(world, player, stack, nbt, look);
						
						// update mana and send to client
						statsCap.decreaseMana(this.manaPerUse);
						LootSlashConquer.network.sendTo(new PacketUpdatePlayerStats(statsCap), (EntityPlayerMP) player);
						
						// damage item
						stack.damageItem(1, player);
					}
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
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack)
	{
		return 300;
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack)
	{
		return EnumAction.NONE;
	}
	
	public double getBaseDamage()
	{
		return baseDamage;
	}
	
	public double getBaseAttackSpeed()
	{
		return baseAttackSpeed;
	}
	
	public int getManaPerUse()
	{
		return manaPerUse;
	}
}
