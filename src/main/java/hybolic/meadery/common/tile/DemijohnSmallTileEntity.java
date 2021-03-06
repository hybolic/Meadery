package hybolic.meadery.common.tile;

import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class DemijohnSmallTileEntity extends FermentationTileEntity {

	@ObjectHolder("meadery:demi_small")
    public static TileEntityType<?> TILE;
	
	public DemijohnSmallTileEntity()
	{
		super(TILE,4);
	}

	@Override
	void pushBubbles() {
		world.addParticle(ParticleTypes.BUBBLE_POP, pos.getX()+.5, pos.getY()+.5, pos.getZ()+.5, 0, 0.1, 0);
	}
}
