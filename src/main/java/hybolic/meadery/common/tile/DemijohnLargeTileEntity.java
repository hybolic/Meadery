package hybolic.meadery.common.tile;

import hybolic.meadery.common.blocks.DemijohnBlock;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class DemijohnLargeTileEntity extends FermentationTileEntity {

	@ObjectHolder("meadery:demi_large")
    public static TileEntityType<?> TILE;
	
	public DemijohnLargeTileEntity()
	{
		super(TILE,8);
	}

	@Override
	void pushBubbles() {
		float _1_16th = 1f / 16f;
		float _8_16th = 1f / 16f * 8f;
		switch(world.getBlockState(getPos()).get(DemijohnBlock.FACING))
		{
		case EAST:
			world.addParticle(ParticleTypes.BUBBLE_POP, pos.getX() + _1_16th + _8_16th, pos.getY()+1, pos.getZ()+_8_16th, 0, .1, 0);
			break;
		case NORTH:
			world.addParticle(ParticleTypes.BUBBLE_POP, pos.getX() + _8_16th, pos.getY()+1, pos.getZ() - _1_16th + _8_16th, 0, .1, 0);
			break;
		case SOUTH:
			world.addParticle(ParticleTypes.BUBBLE_POP, pos.getX() + _8_16th, pos.getY()+1, pos.getZ() + _1_16th + _8_16th, 0, .1, 0);
			break;
		case WEST:
			world.addParticle(ParticleTypes.BUBBLE_POP, pos.getX() - _1_16th + _8_16th, pos.getY()+1, pos.getZ()+_8_16th, 0, .1, 0);
			break;
		default:
			break;
		
		}
	}
}
