package hybolic.meadery.common.tile;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class DemijohnSmallTileEntity extends FermentationStationTileEntity {

	@ObjectHolder("meadery:demi_small")
    public static TileEntityType<?> TILE;
	
	public DemijohnSmallTileEntity()
	{
		super(TILE,4);
	}
}
