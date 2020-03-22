package hybolic.meadery;

import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hybolic.meadery.common.blocks.CultureStationBlock;
import hybolic.meadery.common.blocks.DemijohnLargeBlock;
import hybolic.meadery.common.blocks.DemijohnSmallBlock;
import hybolic.meadery.common.blocks.FermentationBarrelBlock;
import hybolic.meadery.common.blocks.HiveBlock;
import hybolic.meadery.common.blocks.ModBlocks;
import hybolic.meadery.common.items.BasicItem;
import hybolic.meadery.common.items.FermentedProduct;
import hybolic.meadery.common.items.HoneyBottleItem;
import hybolic.meadery.common.items.ModItems;
import hybolic.meadery.common.tile.BarrelTileEntity;
import hybolic.meadery.common.tile.CultureStationTileEntity;
import hybolic.meadery.common.tile.DemijohnLargeTileEntity;
import hybolic.meadery.common.tile.DemijohnSmallTileEntity;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.registries.IForgeRegistry;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MeaderyMod.MODID)
public class MeaderyMod
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
	public static final String MODID = "meadery";

    public MeaderyMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event)
    {
    }
    
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

//        @ObjectHolder("meadery:bee")
//    	public static EntityType<BeeEntity> BEE;
        
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> event)
        {
        	IForgeRegistry<Block> r = event.getRegistry();
        	r.register(new HiveBlock("verticle_hive"));
        	r.register(new HiveBlock("wild_hive"));
        	r.register(new FermentationBarrelBlock("barrel"));
        	r.register(new DemijohnSmallBlock("demi_small"));
        	r.register(new DemijohnLargeBlock("demi_large"));
        	r.register(new CultureStationBlock("culture_station"));
        }
        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> event)
        {
        	IForgeRegistry<Item> r = event.getRegistry();
        	r.register(new BasicItem("honey_comb"));
        	r.register(new HoneyBottleItem("honey"));
        	r.register(new BasicItem("yeast_culture"));
        	r.register(new FermentedProduct("mead",ModItems.food_Mead).setIsLiquid());
        	r.register(new FermentedProduct("wine",ModItems.food_Wine).setIsLiquid());
        	r.register(new FermentedProduct("cider",ModItems.food_Cider).setIsLiquid());
        	r.register(new FermentedProduct("beer",ModItems.food_Beer).setIsLiquid());
        	r.register(new FermentedProduct("sake",ModItems.food_Sake).setIsLiquid());
        	r.register(new FermentedProduct("dargon_liquor",ModItems.food_Dargon_Liquor).setIsLiquid());
        	r.register(new BasicItem.ItemBlock("verticle_hive", ModBlocks.hiveBlock));
        	r.register(new BasicItem.ItemBlock("wild_hive", ModBlocks.wildHiveBlock));
        	r.register(new BasicItem.ItemBlock("barrel", ModBlocks.Barrel));
        	r.register(new BasicItem.ItemBlock("demi_small", ModBlocks.Demi_Small));
        	r.register(new BasicItem.ItemBlock("demi_large", ModBlocks.Demi_Large));
        	r.register(new BasicItem.ItemBlock("culture_station", ModBlocks.CultureStation));
        }
        
        @SubscribeEvent
        public static void onTileEntityRegistry(RegistryEvent.Register<TileEntityType<?>> event) {
        	setupTileEntity(event.getRegistry(), BarrelTileEntity::new, ModBlocks.Barrel, "barrel");
        	setupTileEntity(event.getRegistry(), DemijohnSmallTileEntity::new, ModBlocks.Demi_Small, "demi_small");
        	setupTileEntity(event.getRegistry(), DemijohnLargeTileEntity::new, ModBlocks.Demi_Large, "demi_large");
        	setupTileEntity(event.getRegistry(), CultureStationTileEntity::new, ModBlocks.CultureStation, "culture_station");
        }
        
        private static TileEntityType<?> setupTileEntity(IForgeRegistry<TileEntityType<?>> registry, Supplier<? extends TileEntity> tile, Block block,String id)
        {
            TileEntityType<?> type = TileEntityType.Builder.create(tile,block).build(null);
            type.setRegistryName(MODID, id);
            registry.register(type);
            return type;
        }

		//@Mod.EventBusSubscriber(modid = MeaderyMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
		public static class ItemToolTipHandler{
	    	//@OnlyIn(Dist.CLIENT)
	    	//@SubscribeEvent
	    	public static void ItemTooltipEvent(ItemTooltipEvent event)
	    	{
	    		if(event.getFlags().isAdvanced())
	    		{
	    			for(ResourceLocation tag : event.getItemStack().getItem().getTags())
	    			{
	    				event.getToolTip().add(new TranslationTextComponent(tag.toString()));
	    			}
	    			
		    		if(event.getItemStack().hasTag())
		    		{
		    			event.getToolTip().add(new TranslationTextComponent(event.getItemStack().getTag().toString()));
		    		}		    		
	    		}
	    	}
		}
    }
}
