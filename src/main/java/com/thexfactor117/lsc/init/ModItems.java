package com.thexfactor117.lsc.init;

import com.thexfactor117.lsc.compat.TC.TCRegistry;
import com.thexfactor117.lsc.compat.TC.items.base.MagicItemCore;
import com.thexfactor117.lsc.compat.TC.items.weapons.Staff;
import com.thexfactor117.lsc.compat.TC.parts.StaffHead;
import com.thexfactor117.lsc.items.base.ItemBase;
import com.thexfactor117.lsc.items.base.ItemBauble;
import com.thexfactor117.lsc.items.scrolls.ItemBlizzardScroll;
import com.thexfactor117.lsc.items.scrolls.ItemDischargeScroll;
import com.thexfactor117.lsc.items.scrolls.ItemFireballScroll;
import com.thexfactor117.lsc.items.scrolls.ItemFirestormScroll;
import com.thexfactor117.lsc.items.scrolls.ItemFrostbiteScroll;
import com.thexfactor117.lsc.items.scrolls.ItemInvisibilityScroll;
import com.thexfactor117.lsc.items.scrolls.ItemMajorEtherealScroll;
import com.thexfactor117.lsc.items.scrolls.ItemMinorEtherealScroll;
import com.thexfactor117.lsc.items.scrolls.ItemVoidScroll;
import com.thexfactor117.lsc.loot.Rarity;

import baubles.api.BaubleType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.tools.Pattern;
import slimeknights.tconstruct.library.tools.ToolPart;
import slimeknights.tconstruct.tools.TinkerTools;

/**
 * 
 * @author TheXFactor117
 *
 */
@Mod.EventBusSubscriber
public class ModItems
{
	// jewelry
	public static final Item GOLDEN_RING = new ItemBauble("golden_ring", BaubleType.RING);
	public static final Item DIAMOND_RING = new ItemBauble("diamond_ring", BaubleType.RING);
	public static final Item GOLDEN_AMULET = new ItemBauble("golden_amulet", BaubleType.AMULET);
	public static final Item DIAMOND_AMULET = new ItemBauble("diamond_amulet", BaubleType.AMULET);
	public static final Item LEATHER_SASH = new ItemBauble("leather_sash", BaubleType.BELT);
	
	// scrolls
	public static final Item FIREBALL_SCROLL = new ItemFireballScroll("fireball_scroll", Rarity.UNCOMMON);
	public static final Item FROSTBITE_SCROLL = new ItemFrostbiteScroll("frostbite_scroll", Rarity.UNCOMMON);
	public static final Item MINOR_ETHEREAL_SCROLL = new ItemMinorEtherealScroll("minor_ethereal_scroll", Rarity.RARE);
	public static final Item FIRESTORM_SCROLL = new ItemFirestormScroll("firestorm_scroll", Rarity.RARE);
	public static final Item BLIZZARD_SCROLL = new ItemBlizzardScroll("blizzard_scroll", Rarity.RARE);
	public static final Item DISCHARGE_SCROLL = new ItemDischargeScroll("discharge_scroll", Rarity.RARE);
	public static final Item INVISIBILITY_SCROLL = new ItemInvisibilityScroll("invisibility_scroll", Rarity.EPIC);
	public static final Item MAJOR_ETHEREAL_SCROLL = new ItemMajorEtherealScroll("major_ethereal_scroll", Rarity.EPIC);
	public static final Item VOID_SCROLL = new ItemVoidScroll("void_scroll", Rarity.LEGENDARY);
	
	// miscellaneous
	public static final Item CORRUPTED_TOWER_KEY = new ItemBase("corrupted_tower_key", ModTabs.lscTab);

	//Tinker
	public static final Item TINKER_STAFF = new Staff();
		
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		// jewelry
		event.getRegistry().register(GOLDEN_RING);
		event.getRegistry().register(DIAMOND_RING);
		event.getRegistry().register(GOLDEN_AMULET);
		event.getRegistry().register(DIAMOND_AMULET);
		event.getRegistry().register(LEATHER_SASH);
		
		// scrolls
		event.getRegistry().register(FIREBALL_SCROLL);
		event.getRegistry().register(FROSTBITE_SCROLL);
		event.getRegistry().register(MINOR_ETHEREAL_SCROLL);
		event.getRegistry().register(FIRESTORM_SCROLL);
		event.getRegistry().register(BLIZZARD_SCROLL);
		event.getRegistry().register(DISCHARGE_SCROLL);
		event.getRegistry().register(INVISIBILITY_SCROLL);
		event.getRegistry().register(MAJOR_ETHEREAL_SCROLL);
		event.getRegistry().register(VOID_SCROLL);
		
		// miscellaneous
		event.getRegistry().register(CORRUPTED_TOWER_KEY);

		//Tinkers
		//event.getRegistry().register();
		TCRegistry.registerToolPart(event.getRegistry(),StaffHead.toolPartStaffHead,"staff_head");
		ItemStack stack = new ItemStack(TinkerTools.pattern);
		Pattern.setTagForPart( stack , StaffHead.toolPartStaffHead);
		TinkerRegistry.registerStencilTableCrafting(stack);
		event.getRegistry().register(TINKER_STAFF);

	}
}
