package hybolic.meadery.common.blocks;

import javax.annotation.Nullable;

import hybolic.meadery.MeaderyMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public abstract class AbstractFermentationBlock extends Block implements IWaterLoggable
{
	public static final BooleanProperty   WATER_LOGGED = BlockStateProperties.WATERLOGGED;
	public static BooleanProperty SEALED     = BooleanProperty.create("sealed");
	public static BooleanProperty DECORATIVE = BooleanProperty.create("decorative");
	
	public AbstractFermentationBlock(Material mat, MaterialColor color, String id, float hr) {
		super(Block.Properties.create(mat, color).tickRandomly().hardnessAndResistance(hr));
		this.setRegistryName(MeaderyMod.MODID, id);
	}
	
    public boolean hasTileEntity(BlockState state)
    {
        return hasTileEntity_(state);
    }
    
    @Nullable
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
    	return createTileEntity_(state,world);
    }

    public IFluidState getFluidState(BlockState state) {
       return state.get(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }
	
	public abstract boolean hasTileEntity_(BlockState state);
	
	public abstract TileEntity createTileEntity_(BlockState state, IBlockReader world);
	
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(SEALED);
		builder.add(DECORATIVE);
		builder.add(WATER_LOGGED);
	}
}
