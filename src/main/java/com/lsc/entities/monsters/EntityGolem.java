package com.lsc.entities.monsters;

import com.lsc.entities.EntityMonster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * 
 * @author TheXFactor117
 *
 */
public class EntityGolem extends EntityMonster
{
	public EntityGolem(World world)
	{
		super(world);
		this.setSize(1.0F, 2.0F);
	}
	
	@Override
	protected void initEntityAI()
	{
		super.initEntityAI();
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.0D, false));
		this.tasks.addTask(2, new EntityAIWanderAvoidWater(this, 1.0D));
		this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(4, new EntityAILookIdle(this));
		this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, false));
	}
	
	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(75);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(20);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.5);
	}
	
	@Override
	public boolean attackEntityAsMob(Entity entity)
    {
		if (super.attackEntityAsMob(entity) && !entity.world.isRemote)
		{
			if (entity instanceof EntityLivingBase)
			{
				EntityLivingBase enemy = (EntityLivingBase) entity;
				
				enemy.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20 * 3, 10, false, false));
				enemy.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 20 * 7, 10, false, false));
				
				// knockback
				((EntityLivingBase) enemy).knockBack(this, (float) 3 * 0.5F, (double) MathHelper.sin(this.rotationYaw * 0.017453292F), (double) (-MathHelper.cos(this.rotationYaw * 0.017453292F)));
	            this.motionX *= 0.6D;
	            this.motionZ *= 0.6D;
			}
			
			return true;
		}
		
		return false;
    }
}
