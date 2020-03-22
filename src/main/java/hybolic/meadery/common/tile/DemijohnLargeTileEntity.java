package hybolic.meadery.common.tile;

import hybolic.meadery.common.tile.FermentationStationTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class DemijohnLargeTileEntity extends FermentationStationTileEntity {

	@ObjectHolder("meadery:demi_large")
    public static TileEntityType<?> TILE;
	
	public DemijohnLargeTileEntity()
	{
		super(TILE,8);
	}
}
