package hybolic.meadery.common.blocks;

import net.minecraft.block.Block;
import net.minecraftforge.registries.ObjectHolder;

public class ModBlocks {


	@ObjectHolder("meadery:demi_small")
	public static final DemijohnSmallBlock Demi_Small = null;
	@ObjectHolder("meadery:demi_large")
	public static final DemijohnLargeBlock Demi_Large = null;
	@ObjectHolder("meadery:barrel")
	public static final FermentationBarrelBlock Barrel = null;
	@ObjectHolder("meadery:vat")
	public static final FermentationVatBlock VAT = null;
	
	@ObjectHolder("meadery:culture_station")
	public static final Block CultureStation = null;
	
	@ObjectHolder("meadery:verticle_hive")
	public static HiveBlock hiveBlock;
	
	@ObjectHolder("meadery:wild_hive")
	public static Block wildHiveBlock;
}
