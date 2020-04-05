package hybolic.meadery.common.blocks;

import net.minecraft.block.Block;
import net.minecraftforge.registries.ObjectHolder;

public class ModBlocks {


	@ObjectHolder("meadery:demi_small")
	public static DemijohnSmallBlock Demi_Small;
	@ObjectHolder("meadery:demi_large")
	public static DemijohnLargeBlock Demi_Large;
	@ObjectHolder("meadery:barrel")
	public static BarrelBlock Barrel;
	@ObjectHolder("meadery:vat")
	public static VatBlock VAT;

	@ObjectHolder("meadery:culture")
	public static Block CultureStation;
	@ObjectHolder("meadery:honeycomb_block")
	public static Block honeycomb_block;
	@ObjectHolder("meadery:honey_block")
	public static Block honey_block;

	@ObjectHolder("meadery:blueberry")
	public static BerryBush BLUE_BERRY;
	@ObjectHolder("meadery:blackberry")
	public static BerryBush BLACK_BERRY;
	@ObjectHolder("meadery:raspberry")
	public static BerryBush RASP_BERRY;
	
	@ObjectHolder("meadery:verticle_hive")
	public static HiveBlock VERTICLE_HIVE;
	
	@ObjectHolder("meadery:wild_hive")
	public static Block WILD_HIVE;
}
