package hybolic.meadery.common.blocks;

import hybolic.meadery.MeaderyMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class BerryBush extends SweetBerryBushBlock {

	public Item item;
	
	public BerryBush(String id) {
		super(Block.Properties.create(Material.WOOD, MaterialColor.GOLD).hardnessAndResistance(0.4F).tickRandomly().harvestTool(ToolType.get("hoe")));
		this.setRegistryName(MeaderyMod.MODID, id);
	}


	public ItemStack getItem(IBlockReader world, BlockPos pos, BlockState state) {
		if(item == null)
			return ItemStack.EMPTY;
		return new ItemStack(item);
	}

	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn,
			BlockRayTraceResult hit) {
		int i = state.get(AGE);
		boolean flag = i == 3;
		if (!flag && player.getHeldItem(handIn).getItem() == Items.BONE_MEAL) {
			return false;
		} else if (i > 1) {
			int j = 1 + worldIn.rand.nextInt(2);
			spawnAsEntity(worldIn, pos, new ItemStack(item, j + (flag ? 1 : 0)));
			worldIn.playSound((PlayerEntity) null, pos, SoundEvents.ITEM_SWEET_BERRIES_PICK_FROM_BUSH,
					SoundCategory.BLOCKS, 1.0F, 0.8F + worldIn.rand.nextFloat() * 0.4F);
			worldIn.setBlockState(pos, state.with(AGE, Integer.valueOf(1)), 2);
			return true;
		} else {
			return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
		}
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
	    return VoxelShapes.empty();
	}

}
