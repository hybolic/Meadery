package hybolic.meadery.common.blocks;

import javax.annotation.Nullable;

import hybolic.meadery.MeaderyMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public abstract class AbstractFermnetationBlock extends Block
{

	public AbstractFermnetationBlock(Material mat, MaterialColor color, String id, float hr) {
		super(Block.Properties.create(mat, color).tickRandomly().hardnessAndResistance(hr));
		this.setRegistryName(MeaderyMod.MODID, id);
	}
	
	public abstract boolean hasTileEntity_(BlockState state);
	public abstract TileEntity createTileEntity_(BlockState state, IBlockReader world);
	
    public boolean hasTileEntity(BlockState state)
    {
        return hasTileEntity_(state);
    }
    
    @Nullable
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
    	return createTileEntity_(state,world);
    }
}
