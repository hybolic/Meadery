package hybolic.meadery;

import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hybolic.meadery.client.tesr.WaterRenderer;
import hybolic.meadery.common.blocks.BarrelBlock;
import hybolic.meadery.common.blocks.BasicBlock;
import hybolic.meadery.common.blocks.BerryBush;
import hybolic.meadery.common.blocks.CultureStationBlock;
import hybolic.meadery.common.blocks.DemijohnLargeBlock;
import hybolic.meadery.common.blocks.DemijohnSmallBlock;
import hybolic.meadery.common.blocks.HiveBlock;
import hybolic.meadery.common.blocks.HoneyBlock;
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
import hybolic.meadery.common.util.LootTables;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.BasicTrade;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;

@Mod(MeaderyMod.MODID)
//@Mod.EventBusSubscriber(modid = MeaderyMod.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class MeaderyMod
{
	@Deprecated
    public static final Logger LOGGER = LogManager.getLogger();
	public static final String MODID = "meadery";
	public MeaderyMod()
	{
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonStarting);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientStarting);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onServerStarting);
        MinecraftForge.EVENT_BUS.register(this);
    }
	
    @SubscribeEvent
    public void onCommonStarting(final FMLCommonSetupEvent event)
    {
        LootTables.addToTrades(VillagerProfession.FARMER, 2, new BasicTrade(16, new ItemStack(ModBlocks.WILD_HIVE), 1, 10),
        		new BasicTrade(26, new ItemStack(ModBlocks.VERTICLE_HIVE), 1, 10),
        		new BasicTrade(4, new ItemStack(ModItems.blackberry), 30, 5),
        		new BasicTrade(4, new ItemStack(ModItems.blueberry), 30, 5),
        		new BasicTrade(4, new ItemStack(ModItems.raspberry), 30, 5));
        LootTables.addToTrades(VillagerProfession.FARMER, 1, new BasicTrade(new ItemStack(ModItems.Honey, 10), new ItemStack(Items.EMERALD), 4, 5, 1f),
        		new BasicTrade(new ItemStack(ModItems.HoneyComb, 22), new ItemStack(Items.EMERALD), 20, 5, 1f),
        		new BasicTrade(new ItemStack(ModItems.blackberry, 24), new ItemStack(Items.EMERALD), 4, 5, 1f),
        		new BasicTrade(new ItemStack(ModItems.blueberry, 24), new ItemStack(Items.EMERALD), 4, 5, 1f),
        		new BasicTrade(new ItemStack(ModItems.raspberry, 24), new ItemStack(Items.EMERALD), 4, 5, 1f));
        LootTables.registerTrades();
        DispenserBlock.registerDispenseBehavior(Items.GLASS_BOTTLE, new IDispenseItemBehavior()
        		{
        			private final DefaultDispenseItemBehavior def = new DefaultDispenseItemBehavior();

					@Override
					public ItemStack dispense(IBlockSource source, ItemStack stack)
					{
						World world = source.getWorld();
						BlockPos pos_offset = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));
						BlockState state = world.getBlockState(pos_offset);
						DispenserTileEntity tile = source.<DispenserTileEntity>getBlockTileEntity();
						if(state.getBlock() instanceof HiveBlock)
							if(state.get(HiveBlock.HONEY_AMOUNT) > 0)
							{
								state = state.with(HiveBlock.HONEY_AMOUNT, state.get(HiveBlock.HONEY_AMOUNT) - 1);
								world.setBlockState(pos_offset, state);
								ItemStack honey_stack = new ItemStack(ModItems.Honey);
								if(addItemStack(tile, honey_stack) == -1)
								{
									def.dispense(source, honey_stack);
								}
								stack.shrink(1);
							}
						if(stack.getCount() > 0)
							return stack;
						return ItemStack.EMPTY;
					}

					public int addItemStack(DispenserTileEntity tile, ItemStack stack) {
						for (int i = 0; i < tile.getSizeInventory(); ++i) {
							if (tile.getStackInSlot(i).isEmpty()) {
								tile.setInventorySlotContents(i, stack);
								return i;
							} else if (ItemStack.areItemStacksEqual(tile.getStackInSlot(i), stack)) {
								if (tile.getStackInSlot(i).getCount() + stack.getCount() > tile.getStackInSlot(i)
										.getMaxStackSize()) {
									int temp = tile.getStackInSlot(i).getMaxStackSize() - tile.getStackInSlot(i).getCount();
									tile.getStackInSlot(i).setCount(tile.getStackInSlot(i).getMaxStackSize());
									stack.shrink(temp);
									continue;
								} else {
									tile.getStackInSlot(i).grow(stack.getCount());
									return i;
								}
							}
						}
		
						return -1;
					}
        		});
        DispenserBlock.registerDispenseBehavior(Items.SHEARS, new IDispenseItemBehavior()
		{
			private final DefaultDispenseItemBehavior def = new DefaultDispenseItemBehavior();

			@Override
			public ItemStack dispense(IBlockSource source, ItemStack stack)
			{
				World world = source.getWorld();
				BlockPos pos_offset = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));
				BlockState state = world.getBlockState(pos_offset);
				DispenserTileEntity tile = source.<DispenserTileEntity>getBlockTileEntity();
				if(state.getBlock() instanceof HiveBlock)
					if(state.get(HiveBlock.HONEY_AMOUNT) > 0)
					{
						state = state.with(HiveBlock.HONEY_AMOUNT, state.get(HiveBlock.HONEY_AMOUNT) - 1);
						world.setBlockState(pos_offset, state);
						ItemStack honey_comb_stack = new ItemStack(ModItems.HoneyComb, 3);
						if(addItemStack(tile, honey_comb_stack) == -1)
						{
							for(int i = 0; i < honey_comb_stack.getCount(); i++)
								def.dispense(source, honey_comb_stack);	
						}
						if(stack.attemptDamageItem(1, world.rand, (ServerPlayerEntity)null))
						{
							stack.setCount(0);
						}
					}
				if(stack.getCount() > 0)
					return stack;
				return ItemStack.EMPTY;
			}

			public int addItemStack(DispenserTileEntity tile, ItemStack stack) {
				for (int i = 0; i < tile.getSizeInventory(); ++i) {
					if (tile.getStackInSlot(i).isEmpty()) {
						tile.setInventorySlotContents(i, stack);
						return i;
					} else if (ItemStack.areItemStacksEqual(tile.getStackInSlot(i), stack)) {
						if (tile.getStackInSlot(i).getCount() + stack.getCount() > tile.getStackInSlot(i)
								.getMaxStackSize()) {
							int temp = tile.getStackInSlot(i).getMaxStackSize() - tile.getStackInSlot(i).getCount();
							tile.getStackInSlot(i).setCount(tile.getStackInSlot(i).getMaxStackSize());
							stack.shrink(temp);
							continue;
						} else {
							tile.getStackInSlot(i).grow(stack.getCount());
							return i;
						}
					}
				}

				return -1;
			}
		});
    }

    @SubscribeEvent
    public void onClientStarting(final FMLClientSetupEvent event)
    {
    }
    
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event)
    {
    }
    
    @Mod.EventBusSubscriber(modid = MeaderyMod.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        
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
        	r.register(new BasicBlock("honeycomb_block", Material.CLAY, MaterialColor.ADOBE, 0.6F, SoundType.CORAL));
        	r.register(new HoneyBlock());
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
//        	r.register(new FermentedProduct("rice",ModItems.food_Rice,        ItemGroup.FOOD));
        	r.register(new Berry(ModBlocks.BLUE_BERRY, "blueberry",ItemGroup.FOOD,  ModItems.food_Other));
        	r.register(new Berry(ModBlocks.BLACK_BERRY,"blackberry",ItemGroup.FOOD, ModItems.food_Other));
        	r.register(new Berry(ModBlocks.RASP_BERRY,"raspberry",ItemGroup.FOOD,  ModItems.food_Other));
        	r.register(new BasicItem.ItemBlock("verticle_hive", ModBlocks.VERTICLE_HIVE, Group.BEES));
        	r.register(new BasicItem.ItemBlock("wild_hive", ModBlocks.WILD_HIVE, Group.BEES));
        	r.register(new BasicItem.TickingItemBlock("barrel", ModBlocks.Barrel, Group.MEADERY));
        	r.register(new BasicItem.TickingItemBlock("demi_small", ModBlocks.Demi_Small, Group.MEADERY));
        	r.register(new BasicItem.TickingItemBlock("demi_large", ModBlocks.Demi_Large, Group.MEADERY));
        	r.register(new BasicItem.ItemBlock("vat", ModBlocks.VAT, Group.MEADERY));
        	r.register(new BasicItem.ItemBlock("culture", ModBlocks.CultureStation, Group.MEADERY));
        	r.register(new BasicItem.ItemBlock("honey_block", ModBlocks.honey_block, Group.BEES));
        	r.register(new BasicItem.ItemBlock("honeycomb_block", ModBlocks.honeycomb_block, Group.BEES));
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

    	@OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        public static void onClientTileEntityRegistry(RegistryEvent.Register<TileEntityType<?>> event) {
        	ClientRegistry.bindTileEntitySpecialRenderer(DemijohnSmallTileEntity.class, new WaterRenderer(0.001f,0.3745f,6f,6f,4f,4f));
        	ClientRegistry.bindTileEntitySpecialRenderer(DemijohnLargeTileEntity.class, new WaterRenderer(0.001f,0.745f,0f,0f,14f,14f));
        	ClientRegistry.bindTileEntitySpecialRenderer(BarrelTileEntity.class, new WaterRenderer(0.125f,0.875f,2f,2f,12f,12f));
        	ClientRegistry.bindTileEntitySpecialRenderer(VatTileEntity.class, new WaterRenderer(0.1876f,1.8125f,1f,1f,14f,14f));
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
    }
}
