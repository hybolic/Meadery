package hybolic.meadery.common.tile;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class BarrelTileEntity extends FermentationStationTileEntity {

	@ObjectHolder("meadery:barrel")
    public static TileEntityType<?> TILE;
	
	public BarrelTileEntity()
	{
		super(TILE,64);
	}

}
