package hybolic.meadery.common.blocks;

import javax.annotation.Nullable;

import hybolic.meadery.MeaderyMod;
import hybolic.meadery.common.tile.CultureStationTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class CultureStationBlock extends Block {

	public CultureStationBlock(String id) {
		super(Block.Properties.create(Material.WOOD, MaterialColor.GOLD).tickRandomly().hardnessAndResistance(0.3f));
		this.setRegistryName(MeaderyMod.MODID, id);
	}

    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }
    
    @Nullable
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
    	return new CultureStationTileEntity();
    }

}
