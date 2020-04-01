package hybolic.meadery;

import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hybolic.meadery.client.tesr.WaterRenderer;
import hybolic.meadery.common.blocks.BarrelBlock;
import hybolic.meadery.common.blocks.BerryBush;
import hybolic.meadery.common.blocks.CultureStationBlock;
import hybolic.meadery.common.blocks.DemijohnLargeBlock;
import hybolic.meadery.common.blocks.DemijohnSmallBlock;
import hybolic.meadery.common.blocks.HiveBlock;
import hybolic.meadery.common.blocks.ModBlocks;
import hybolic.meadery.common.blocks.VatBlock;
import hybolic.meadery.common.items.BasicItem;
import hybolic.meadery.common.items.Berry;
import hybolic.meadery.common.items.FermentedProduct;
import hybolic.meadery.common.items.Group;
import hybolic.meadery.common.items.HoneyBottleItem;
import hybolic.meadery.common.items.ModItems;
import hybolic.meadery.common.recipe.BrewSerializer;
import hybolic.meadery.common.recipe.FermentSerializer;
import hybolic.meadery.common.tile.BarrelTileEntity;
import hybolic.meadery.common.tile.CultureStationTileEntity;
import hybolic.meadery.common.tile.DemijohnLargeTileEntity;
import hybolic.meadery.common.tile.DemijohnSmallTileEntity;
import hybolic.meadery.common.tile.VatTileEntity;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.registries.IForgeRegistry;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MeaderyMod.MODID)
public class MeaderyMod
{
    // Directly reference a log4j logger.
	@Deprecated
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
    	
		@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    	public static class ClientRegistryEvents
    	{

            @SubscribeEvent
            public static void onTileEntityRegistry(RegistryEvent.Register<TileEntityType<?>> event) {
            	ClientRegistry.bindTileEntitySpecialRenderer(DemijohnSmallTileEntity.class, new WaterRenderer(0.001f,0.3745f,6f,6f,4f,4f));
            	ClientRegistry.bindTileEntitySpecialRenderer(DemijohnLargeTileEntity.class, new WaterRenderer(0.001f,0.745f,0f,0f,14f,14f));
            	ClientRegistry.bindTileEntitySpecialRenderer(BarrelTileEntity.class, new WaterRenderer(0.125f,0.875f,2f,2f,12f,12f));
            	ClientRegistry.bindTileEntitySpecialRenderer(VatTileEntity.class, new WaterRenderer(0.1876f,1.8125f,1f,1f,14f,14f));
            }
    	}
    	
		@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.DEDICATED_SERVER)
    	public static class ServerRegistryEvents
    	{
    	}
        
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> event)
        {
        	IForgeRegistry<Block> r = event.getRegistry();
        	r.register(new HiveBlock("verticle_hive"));
        	r.register(new HiveBlock("wild_hive"));
        	r.register(new BarrelBlock("barrel"));
        	r.register(new DemijohnSmallBlock("demi_small"));
        	r.register(new DemijohnLargeBlock("demi_large"));
        	r.register(new CultureStationBlock("culture"));
        	r.register(new VatBlock("vat"));
        	r.register(new BerryBush("blueberry"));
        	r.register(new BerryBush("blackberry"));
        	r.register(new BerryBush("raspberry"));
        }
        
        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> event)
        {
        	IForgeRegistry<Item> r = event.getRegistry();
        	r.register(new BasicItem("honey_comb", Group.BEES));
        	r.register(new HoneyBottleItem("honey", Group.BEES));
        	r.register(new BasicItem("yeast_culture", Group.DRINK));
        	r.register(new FermentedProduct("mead",ModItems.food_Mead).setIsLiquid());
        	r.register(new FermentedProduct("wine",ModItems.food_Wine).setIsLiquid());
        	r.register(new FermentedProduct("cider",ModItems.food_Cider).setIsLiquid());
        	r.register(new FermentedProduct("beer",ModItems.food_Beer).setIsLiquid());
        	r.register(new FermentedProduct("sake",ModItems.food_Sake).setIsLiquid());
        	r.register(new FermentedProduct("dragon_liquor",ModItems.food_Dragon_Liquor).setIsLiquid());
//        	/r.register(new FermentedProduct("rice",ModItems.food_Rice,        ItemGroup.FOOD));
        	r.register(new Berry(ModBlocks.BLUE_BERRY, "blueberry",ItemGroup.FOOD,  ModItems.food_Other));
        	r.register(new Berry(ModBlocks.BLACK_BERRY,"blackberry",ItemGroup.FOOD, ModItems.food_Other));
        	r.register(new Berry(ModBlocks.RASP_BERRY,"raspberry",ItemGroup.FOOD,  ModItems.food_Other));
        	r.register(new BasicItem.ItemBlock("verticle_hive", ModBlocks.hiveBlock, Group.BEES));
        	r.register(new BasicItem.ItemBlock("wild_hive", ModBlocks.wildHiveBlock, Group.BEES));
        	r.register(new BasicItem.TickingItemBlock("barrel", ModBlocks.Barrel, Group.MEADERY));
        	r.register(new BasicItem.TickingItemBlock("demi_small", ModBlocks.Demi_Small, Group.MEADERY));
        	r.register(new BasicItem.TickingItemBlock("demi_large", ModBlocks.Demi_Large, Group.MEADERY));
        	r.register(new BasicItem.ItemBlock("vat", ModBlocks.VAT, Group.MEADERY));
        	r.register(new BasicItem.ItemBlock("culture", ModBlocks.CultureStation, Group.MEADERY));
        }
        
        @SubscribeEvent
        public static void onBlockColorRegistry(final ColorHandlerEvent.Block event)
        {
            event.getBlockColors().register(ModBlocks.VAT, ModBlocks.VAT);
        }
        
        @SubscribeEvent
        public static void onItemColorRegistry(final ColorHandlerEvent.Item event)
        {
        	event.getItemColors().register(ModItems.mead, ModItems.mead);
        	event.getItemColors().register(ModItems.wine, ModItems.wine);
        	event.getItemColors().register(ModItems.cider, ModItems.cider);
        	event.getItemColors().register(ModItems.beer, ModItems.beer);
        	event.getItemColors().register(ModItems.sake, ModItems.sake);
        	event.getItemColors().register(ModItems.dragon_liquor, ModItems.dragon_liquor);
        }
        
        @SubscribeEvent
        public static void onTileEntityRegistry(RegistryEvent.Register<TileEntityType<?>> event) {
        	setupTileEntity(event.getRegistry(), BarrelTileEntity::new, ModBlocks.Barrel, "barrel");
        	setupTileEntity(event.getRegistry(), DemijohnSmallTileEntity::new, ModBlocks.Demi_Small, "demi_small");
        	setupTileEntity(event.getRegistry(), DemijohnLargeTileEntity::new, ModBlocks.Demi_Large, "demi_large");
        	setupTileEntity(event.getRegistry(), VatTileEntity::new, ModBlocks.VAT, "vat");
        	setupTileEntity(event.getRegistry(), CultureStationTileEntity::new, ModBlocks.CultureStation, "culture");
        }
        
        private static TileEntityType<?> setupTileEntity(IForgeRegistry<TileEntityType<?>> registry, Supplier<? extends TileEntity> tile, Block block,String id)
        {
            TileEntityType<?> type = TileEntityType.Builder.create(tile,block).build(null);
            type.setRegistryName(MODID, id);
            registry.register(type);
            return type;
        }
        
        @SubscribeEvent
        public static void onRegisterRecipeTypes (Register<IRecipeSerializer<?>> event)
        {
        	BrewSerializer.INSTANCE = new BrewSerializer();
        	FermentSerializer.INSTANCE = new FermentSerializer();
        	event.getRegistry().register(Registry.register(Registry.RECIPE_TYPE, BrewSerializer.getRegistry(),    BrewSerializer.INSTANCE));
        	event.getRegistry().register(Registry.register(Registry.RECIPE_TYPE, FermentSerializer.getRegistry(), FermentSerializer.INSTANCE));
        }

        public static <T extends IRecipe<?>> IRecipeType<T> registerRecipeType (String typeId) {
            
            final IRecipeType<T> type = new IRecipeType<T>() {
                
                @Override
                public String toString () {
                    
                    return MeaderyMod.MODID + ":" + typeId;
                }
            };
            
            return type;
        }
        
		@Mod.EventBusSubscriber(modid = MeaderyMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
		public static class ItemToolTipHandler{
	    	@OnlyIn(Dist.CLIENT)
	    	@SubscribeEvent
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
