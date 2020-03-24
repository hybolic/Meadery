package hybolic.meadery.common.tile;

import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class VatTileEntity extends FermentationTileEntity {

	@ObjectHolder("meadery:vat")
    public static TileEntityType<?> TILE;
	
	public VatTileEntity()
	{
		super(TILE,256);
	}

	@Override
	void pushBubbles() {
		world.addParticle(ParticleTypes.BUBBLE_POP, pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0);
	}
}
