package hybolic.meadery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hybolic.meadery.common.blocks.HiveBlock;
import hybolic.meadery.common.blocks.ModBlocks;
import hybolic.meadery.common.entity.BeeEntity;
import hybolic.meadery.common.items.BasicItem;
import hybolic.meadery.common.items.FermentedProduct;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
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
        	//barrel
        	//demijohn
        }
        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> event)
        {
        	IForgeRegistry<Item> r = event.getRegistry();
        	r.register(new BasicItem("honey_comb"));
        	r.register(new BasicItem("honey"));
        	r.register(new FermentedProduct("mead"));
        	r.register(new BasicItem("yeast_culture"));
        	r.register(new BasicItem.ItemBlock("hive", ModBlocks.hiveBlock));
        	r.register(new FermentedProduct("wine"));
        	r.register(new FermentedProduct("cider"));
        	r.register(new FermentedProduct("beer"));
        	r.register(new FermentedProduct("sake"));
        	r.register(new FermentedProduct("dargon_liquor"));
        }
    }
}
