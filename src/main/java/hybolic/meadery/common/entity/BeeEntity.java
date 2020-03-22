package hybolic.meadery.common.entity;

import java.util.UUID;

import javax.annotation.Nullable;

import hybolic.meadery.common.entity.Movement.FlyingMovement;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BushBlock;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.TallFlowerBlock;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BeeEntity extends AnimalEntity implements IFlyingAnimal {

	public static final ResourceLocation pollinateSFX = new ResourceLocation("meadery", "entity.bee.pollinate");
	public static final ResourceLocation buzzingSFX = new ResourceLocation("meadery", "entity.bee.buzz");

	private class FindFlowersGoal extends MoveToBlockGoal {

		protected int ticked;

		public FindFlowersGoal(double speed, int length, int other) {
			super(BeeEntity.this, speed, length, other);
		}

		public double getTargetDistanceSq() {
			return 2.0D;
		}

		public boolean shouldMove() {
			return this.timeoutCounter % 100 == 0;
		}
		
		protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
			BlockState blockstate = worldIn.getBlockState(pos);
			return isPollinatable(blockstate);
		}

		private boolean isPollinatable(BlockState blockstate) {
			return blockstate.getBlock() instanceof FlowerBlock || blockstate.getBlock() instanceof CropsBlock
					|| blockstate.getBlock() instanceof TallFlowerBlock || blockstate.getBlock() instanceof BushBlock;
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		public void tick() {
			if (this.getIsAboveDestination()) {
				if (this.ticked >= 40) {
					this.pollinate();
				} else {
					++this.ticked;
				}
			} else if (!this.getIsAboveDestination() && BeeEntity.this.rand.nextFloat() < 0.05F) {
				// BeeEntity.this.playSound(BUZZING, 1.0F, 1.0F);
			}

			super.tick();
		}

		protected void pollinate() {
			BlockState blockstate = BeeEntity.this.world.getBlockState(this.destinationBlock);
			if (isPollinatable(blockstate)) {
				ItemStack itemstack = BeeEntity.this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
				if (itemstack.isEmpty()) {
					BeeEntity.this.setPollenType(blockstate.getBlock());
					BeeEntity.this.shouldReturnToHive(true);
				}
				BeeEntity.this.playSound(new SoundEvent(BeeEntity.pollinateSFX), 1.0F, 1.0F);
			}
		}

		public boolean shouldExecute() {
			return !BeeEntity.this.isSleeping() && super.shouldExecute();
		}

		public void startExecuting() {
			this.ticked = 0;
			BeeEntity.this.shouldReturnToHive(false);
			super.startExecuting();
		}
	}

	private UUID hurtByUUID;
	private boolean hasStinger = true;

	@Nullable
	private BlockPos flowerPos = null;
	@Nullable
	private BlockPos hivePos = null;

	private PollenType heldPollen;
	private boolean shouldReturnToHive = false;

	public BeeEntity(EntityType<? extends BeeEntity> type, World world) {
		super(type, world);
		this.moveController = new FlyingMovement(this, 20, true);
		this.setPathPriority(PathNodeType.WATER, -1.0F);
		this.setPathPriority(PathNodeType.FENCE, -1.0F);
	}

	public void shouldReturnToHive(boolean b) {
		shouldReturnToHive = b;
	}

	public enum PollenType {
		FLORAL, EARTHY, FRUITY, SPICY, STRANGE
	}

	public void setPollenType(Block block) {
		if (block instanceof FlowerBlock || block instanceof TallFlowerBlock)
			this.heldPollen = PollenType.FLORAL;
		else if (block == Blocks.NETHER_WART)
			this.heldPollen = PollenType.STRANGE;
		else if (block instanceof CropsBlock)
			this.heldPollen = PollenType.EARTHY;
		else if (block instanceof BushBlock)
			this.heldPollen = PollenType.FRUITY;
		else
			this.heldPollen = PollenType.STRANGE;
	}

	@Override
	public AgeableEntity createChild(AgeableEntity ageable) {
		return null;
	}

	public boolean isBreedingItem(ItemStack stack) {
		return stack.getItem().isIn(ItemTags.SMALL_FLOWERS);
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(1, new BeeEntity.FindFlowersGoal(1.2f, 12, 2));
	}
}