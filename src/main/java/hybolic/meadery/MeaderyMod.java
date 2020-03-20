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
import hybolic.meadery.common.entity.BeeEntity;
import hybolic.meadery.common.items.BasicItem;
import hybolic.meadery.common.items.FermentedProduct;
import hybolic.meadery.common.items.ModItems;
import hybolic.meadery.common.tile.CultureStationTileEntity;
import hybolic.meadery.common.tile.FermentationStationTileEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

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

        @ObjectHolder("meadery:bee")
    	public static EntityType<BeeEntity> BEE;
        
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> event)
        {
        	IForgeRegistry<Block> r = event.getRegistry();
        	r.register(new HiveBlock());
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
        	r.register(new BasicItem("honey"));
        	r.register(new BasicItem("yeast_culture"));
        	r.register(new BasicItem.ItemBlock("hive", ModBlocks.hiveBlock));
        	r.register(new BasicItem.ItemBlock("barrel", ModBlocks.Barrel));
        	r.register(new BasicItem.ItemBlock("demi_small", ModBlocks.Demi_Small));
        	r.register(new BasicItem.ItemBlock("demi_large", ModBlocks.Demi_Large));
        	r.register(new BasicItem.ItemBlock("culture_station", ModBlocks.CultureStation));
        	r.register(new FermentedProduct("mead",ModItems.food_mead).setIsLiquid());
        	r.register(new FermentedProduct("wine",ModItems.food_Wine).setIsLiquid());
        	r.register(new FermentedProduct("cider",ModItems.food_Cider).setIsLiquid());
        	r.register(new FermentedProduct("beer",ModItems.food_Beer).setIsLiquid());
        	r.register(new FermentedProduct("sake",ModItems.food_Sake).setIsLiquid());
        	r.register(new FermentedProduct("dargon_liquor",ModItems.food_Dargon_Liquor).setIsLiquid());
        }
        
        @SubscribeEvent
        public static void onTileEntityRegistry(RegistryEvent.Register<TileEntityType<?>> event) {
        	setupTileEntity(event.getRegistry(), FermentationStationTileEntity::new, ModBlocks.Barrel, "fermentation_station");
        	setupTileEntity(event.getRegistry(), FermentationStationTileEntity::new, ModBlocks.Demi_Small, "demi_small");
        	setupTileEntity(event.getRegistry(), FermentationStationTileEntity::new, ModBlocks.Demi_Large, "demi_large");
        	setupTileEntity(event.getRegistry(), CultureStationTileEntity::new, ModBlocks.CultureStation, "culture_station");
        }
        
        
        
        
        private static TileEntityType<?> setupTileEntity(IForgeRegistry<TileEntityType<?>> registry, Supplier<? extends TileEntity> tile, Block block,String id)
        {
            TileEntityType<?> type = TileEntityType.Builder.create(tile,block).build(null);
            type.setRegistryName(MODID, id);
            registry.register(type);
            return type;
        }
    }
}
