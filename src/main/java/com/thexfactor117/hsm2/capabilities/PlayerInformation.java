package com.thexfactor117.hsm2.capabilities;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;

/**
 * 
 * @author TheXFactor117
 *
 */
public class PlayerInformation implements IPlayerInformation
{
	private String playerClass;
	private int level;
	private int experience;
	private int skillPoints;
	
	// stats
	private int strengthStat;
	private int agilityStat;
	private int dexterityStat;
	private int intelligenceStat;
	private int wisdomStat;
	private int fortitudeStat;
	
	@SuppressWarnings("unused")
	private final EntityLivingBase entity;
	
	public PlayerInformation(@Nullable EntityLivingBase entity)
	{
		this.entity = entity;
	}
	
	@Override
	public String getPlayerClass() 
	{
		return playerClass;
	}

	@Override
	public void setPlayerClass(String playerClass) 
	{
		this.playerClass = playerClass;
	}

	@Override
	public int getPlayerLevel() 
	{
		return level;
	}

	@Override
	public void setPlayerLevel(int level) 
	{
		this.level = level;
	}

	@Override
	public int getPlayerExperience() 
	{
		return experience;
	}

	@Override
	public void setPlayerExperience(int experience) 
	{
		this.experience = experience;
	}

	@Override
	public int getLevelUpExperience(int currentLevel) 
	{
		return 0; // implement later!
	}

	@Override
	public int getSkillPoints() 
	{
		return skillPoints;
	}

	@Override
	public void setSkillPoints(int skillPoints) 
	{
		this.skillPoints = skillPoints;
	}

	/*
	 * STATS
	 */
	
	@Override
	public int getStrengthStat() 
	{
		return strengthStat;
	}

	@Override
	public void setStrengthStat(int stat) 
	{
		this.strengthStat = stat;
	}

	@Override
	public int getAgilityStat() 
	{
		return agilityStat;
	}

	@Override
	public void setAgilityStat(int stat) 
	{
		this.agilityStat = stat;
	}

	@Override
	public int getDexterityStat() 
	{
		return dexterityStat;
	}

	@Override
	public void setDexterityStat(int stat) 
	{
		this.dexterityStat = stat;
	}

	@Override
	public int getIntelligenceStat() 
	{
		return intelligenceStat;
	}

	@Override
	public void setIntelligenceStat(int stat) 
	{
		this.intelligenceStat = stat;
	}

	@Override
	public int getWisdomStat() 
	{
		return wisdomStat;
	}

	@Override
	public void setWisdomStat(int stat) 
	{
		this.wisdomStat = stat;
	}

	@Override
	public int getFortitudeStat() 
	{
		return fortitudeStat;
	}

	@Override
	public void setFortitudeStat(int stat) 
	{
		this.fortitudeStat = stat;
	}
}
