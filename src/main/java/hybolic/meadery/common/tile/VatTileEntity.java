package hybolic.meadery.common.tile;

import hybolic.meadery.common.blocks.AbstractFermentationBlock;
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
		if(getBlockState().get(AbstractFermentationBlock.SEALED) == false)
		{
			float calY = 1.9375f * this.getFluidAmount() / (float)this.getCapacity();
			world.addParticle(ParticleTypes.BUBBLE_POP, pos.getX()+0.0625+(0.9375 * random.nextFloat()), pos.getY()+calY, pos.getZ()+0.0625+(0.9375 * random.nextFloat()), 0, 0, 0);
		}
	}
}
