package hybolic.meadery.common.blocks;

import hybolic.meadery.MeaderyMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BreakableBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class HoneyBlock extends BreakableBlock {

	public HoneyBlock() {
		super(Block.Properties.create(Material.CLAY, MaterialColor.ADOBE).hardnessAndResistance(0.4F,0.5F).sound(SoundType.SLIME));
		this.setRegistryName(new ResourceLocation(MeaderyMod.MODID, "honey_block"));
	}

	protected static final VoxelShape shape = Block.makeCuboidShape(1.0D, 1.0D, 1.0D, 15.0D, 15.0D, 15.0D);

	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return shape;
	}

	public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
		entityIn.playSound(SoundEvents.BLOCK_SLIME_BLOCK_FALL, 1.0F, 1.0F);

		if (entityIn.isSneaking()) {
			super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
		} else {
			entityIn.fall(fallDistance, 0.0F);
		}

	}

	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}
	
	public void onLanded(IBlockReader worldIn, Entity entityIn) {
		if (entityIn.isSneaking()) {
			super.onLanded(worldIn, entityIn);
		} else {
			Vec3d vec3d = entityIn.getMotion();
			if (vec3d.y < 0.0D) {
				double d0 = entityIn instanceof LivingEntity ? 1.0D : 0.2D;
				entityIn.setMotion(vec3d.x, -vec3d.y * d0, vec3d.z);
			}
		}
	}

	public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
		double d0 = Math.abs(entityIn.getMotion().y);
		if (d0 < 0.1D && !entityIn.isSneaking()) {
			double d1 = 0.4D + d0 * 0.2D;
			entityIn.setMotion(entityIn.getMotion().mul(d1, 1.0D, d1));
		}

		super.onEntityWalk(worldIn, pos, entityIn);
	}

}
