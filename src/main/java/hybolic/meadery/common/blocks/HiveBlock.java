package hybolic.meadery.common.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import hybolic.meadery.MeaderyMod;
import hybolic.meadery.common.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;

public class HiveBlock extends Block {

	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	public static final BooleanProperty HIBERNATING = BooleanProperty.create("hibernating");
	public static final IntegerProperty HONEY_AMOUNT = IntegerProperty.create("amount", 0, 15);
	// public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 30);

	public HiveBlock() {
		super(Block.Properties.create(Material.WOOD, MaterialColor.GOLD).tickRandomly().hardnessAndResistance(0.4F));
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(HIBERNATING, false));
		this.setRegistryName(MeaderyMod.MODID, "hive");
	}

	public void tick(BlockState state, World worldIn, BlockPos pos, Random random) {
		if (state.get(HIBERNATING)) {
			if(random.nextInt(10) == 0) {
				if (state.get(HONEY_AMOUNT) > 0)
					state = state.with(HONEY_AMOUNT, state.get(HONEY_AMOUNT) - 1);
				else
					state = state.with(HIBERNATING, false);
			}
		} else if(worldIn.isDaytime() && !worldIn.isRaining()){
			if (state.get(HONEY_AMOUNT) < 15)
				state = state.with(HONEY_AMOUNT, state.get(HONEY_AMOUNT) + 1);
			else {
				state = state.with(HONEY_AMOUNT, 12).with(HIBERNATING, true);
				//spawn queen
			}
		}
		worldIn.setBlockState(pos, state);
	}

	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockRayTraceResult hit) {
		if (ItemStack.areItemsEqualIgnoreDurability(player.getHeldItem(hand), Items.SHEARS.getDefaultInstance())) {
			if (player.getHeldItem(hand).getDamage() > 1) {
				checkIfSmoked(state, world, pos);
				player.getHeldItem(hand).damageItem(1, player, e -> e.sendBreakAnimation(hand));
				spawnAsEntity(world, pos, new ItemStack(ModItems.HoneyComb, 3));
				return true;
			}
		} else if (ItemStack.areItemsEqualIgnoreDurability(player.getHeldItem(hand),
				Items.GLASS_BOTTLE.getDefaultInstance()) && state.get(HONEY_AMOUNT).intValue() > 0) {
			state = state.with(HONEY_AMOUNT, state.get(HONEY_AMOUNT).intValue() - 1);
			world.setBlockState(pos, state);
			checkIfSmoked(state, world, pos);
			player.getHeldItem(hand).shrink(1);
			world.playSound(player, player.posX, player.posY, player.posZ, SoundEvents.ITEM_BOTTLE_FILL,
					SoundCategory.NEUTRAL, 1.0F, 1.0F);
			if (player.getHeldItem(hand).isEmpty()) {
				player.setHeldItem(hand, new ItemStack(ModItems.Honey));
			} else if (!player.inventory.addItemStackToInventory(new ItemStack(ModItems.Honey))) {
				player.dropItem(new ItemStack(ModItems.Honey), false);
			}
			return true;
		}
		return false;
	}

	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}
	
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
		return blockState.get(HONEY_AMOUNT);
	}

	private void checkIfSmoked(BlockState state, World world, BlockPos pos) {
		BlockState LowerBlock = world.getBlockState(pos.down());
		if (LowerBlock.getBlock() == Blocks.CAMPFIRE)
			if (LowerBlock.get(CampfireBlock.LIT))
				return;
		// spawn angry bees!!
	}

	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		Entity entity = builder.get(LootParameters.THIS_ENTITY);
		List<ItemStack> items = new ArrayList<ItemStack>();
		ItemStack hive = new ItemStack(this);
		CompoundNBT DETAILS = new CompoundNBT();
		DETAILS.putInt("amount", state.get(HONEY_AMOUNT));
		DETAILS.putBoolean("hibernating", state.get(HIBERNATING));
		hive.setTag(DETAILS);
		items.add(hive);
		return items;
	}

	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
		builder.add(HIBERNATING);
		builder.add(HONEY_AMOUNT);
	}

	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.with(FACING, mirrorIn.mirror(state.get(FACING)));
	}

	public BlockState getStateForPlacement(BlockItemUseContext context) {
		ItemStack item = context.getItem();
		BlockState state = this.getDefaultState();
		if (item.hasTag()) {
			state = state.with(HONEY_AMOUNT, item.getTag().getInt("amount"));
			state = state.with(HIBERNATING, item.getTag().getBoolean("hibernating"));

		}
		return state.with(FACING, context.getPlacementHorizontalFacing());
	}
}
