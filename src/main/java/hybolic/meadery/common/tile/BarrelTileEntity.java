package hybolic.meadery.common.tile;

import hybolic.meadery.common.blocks.AbstractFermentationBlock;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class BarrelTileEntity extends FermentationTileEntity {

	@ObjectHolder("meadery:barrel")
    public static TileEntityType<?> TILE;
	
	public BarrelTileEntity()
	{
		super(TILE,64);
	}

	@Override
	void pushBubbles() {
		if(getBlockState().get(AbstractFermentationBlock.SEALED))
			world.addParticle(ParticleTypes.BUBBLE_POP, pos.getX() + 0.5, pos.getY() + 0.9375f, pos.getZ() + 0.5, 0, 0.1, 0);
		else if(world.rand.nextInt(3) == 0)
			world.addParticle(ParticleTypes.BUBBLE_POP, pos.getX() + (1f / 16f * (world.rand.nextInt(14)+1)), pos.getY() + 0.875f, pos.getZ() + (1f / 16f * (world.rand.nextInt(14)+1)), 0, 0.1, 0);
	}

}
