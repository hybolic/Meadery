package hybolic.meadery.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import hybolic.meadery.MeaderyMod;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.entity.merchant.villager.VillagerTrades.ITrade;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootPool.Builder;
import net.minecraft.world.storage.loot.TableLootEntry;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LootTables
{
	static HashMap<ResourceLocation, List<ResourceLocation>> loot;
	static HashMap<VillagerProfession, HashMap<Integer, List<ITrade>>> trades;

	public static void addToLoot(ResourceLocation original, ResourceLocation additions)
	{
		if(loot == null)
			loot = new HashMap<ResourceLocation, List<ResourceLocation>>();
		if(!loot.containsKey(original))
		{
			loot.put(original, new ArrayList<ResourceLocation>());
		}
		loot.get(original).add(additions);
	}
	
	public static void addToTrades(VillagerProfession villager, int level, ITrade... loot_table)
	{
		if(trades == null)
			trades = new HashMap<VillagerProfession, HashMap<Integer, List<ITrade>>>();
		if(!trades.containsKey(villager))
		{
			trades.put(villager, new HashMap<Integer, List<ITrade>>());
		}
		if(trades.get(villager).get(level) == null)
		{
			trades.get(villager).put(level, new ArrayList<ITrade>());
		}
		trades.get(villager).get(level).addAll(Arrays.asList(loot_table));
	}
	
    @SubscribeEvent
    public static void onLootLoad(LootTableLoadEvent event) {
    	ResourceLocation current = event.getName();
    	if(loot!=null)
    	if(loot.containsKey(current))
    	{
        	MeaderyMod.LOGGER.info("injecting Loot into \"" + current.getPath() + "\"!");
    		Builder loot = LootPool.builder();
    		for(ResourceLocation value : LootTables.loot.get(current))
    		{
    			loot.addEntry(TableLootEntry.builder(value));
    		}
            event.getTable().addPool(loot.build());
    	}
    }
    
    public static void registerTrades() {
    	MeaderyMod.LOGGER.info("Injecting trades!");
    	for(VillagerProfession prof : trades.keySet())
    	{
    		registerTrade(prof);
    	}
    }
    
    private static void registerTrade(VillagerProfession prof)
    {
    	if(trades != null)
    		addToVillagerLootTable(prof, trades.get(prof));
    }
    
    private static void addToVillagerLootTable(VillagerProfession prof, HashMap<Integer, List<ITrade>> hashMap) {
    	for(int level : hashMap.keySet())
    	{
    		addToLeveledVillagerLootTable(prof,level,hashMap.get(level).toArray(new ITrade[0]));
    	}
	}

	private static void addToLeveledVillagerLootTable(VillagerProfession prof, int level, ITrade... newTrades)
    {
		Int2ObjectMap<VillagerTrades.ITrade[]> tradeMap = VillagerTrades.field_221239_a.get(prof);
		ITrade[] oldtrades = tradeMap.get(level);
		ArrayList<ITrade> list = new ArrayList<ITrade>(Arrays.asList(oldtrades));
		list.addAll(Arrays.asList(newTrades));
		ITrade[] trades = new ITrade[list.size()];
		trades = list.toArray(trades);
		tradeMap.put(level, trades);
    }
}