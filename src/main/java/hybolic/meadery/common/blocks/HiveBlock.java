package hybolic.meadery.common.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import hybolic.meadery.MeaderyMod;
import hybolic.meadery.common.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;

public class HiveBlock extends Block {

	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	public static final BooleanProperty HIBERNATING = BooleanProperty.create("hibernating");
	public static final IntegerProperty HONEY_AMOUNT = IntegerProperty.create("amount", 0, 15);
	// public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 30);
	
	public HiveBlock(String id)
	{
		super(Block.Properties.create(Material.WOOD, MaterialColor.GOLD).tickRandomly().hardnessAndResistance(0.4F).harvestTool(ToolType.AXE));
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(HIBERNATING, false).with(HONEY_AMOUNT, 0));
		this.setRegistryName(MeaderyMod.MODID, id);
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
				if(random.nextInt(99) != 0)
				{
					BlockPos leafpos = getRandomBlock(worldIn,pos);
					if(leafpos != pos)
					{
						if(nearbyHives(worldIn,pos) < 3)
						{
							worldIn.setBlockState(leafpos, ModBlocks.WILD_HIVE.getDefaultState().with(HONEY_AMOUNT, 15 - state.get(HONEY_AMOUNT)).with(HIBERNATING, true).rotate(Rotation.values()[random.nextInt(3)]));
	
							for (int j = 0; j < (int)leafpos.distanceSq(pos); ++j) {
								double d0 = random.nextDouble();
								float f = (random.nextFloat() - 0.5F) * 0.2F;
								float f1 = (random.nextFloat() - 0.5F) * 0.2F;
								float f2 = (random.nextFloat() - 0.5F) * 0.2F;
								double d1 = MathHelper.lerp(d0, (double)pos.getX(), (double)leafpos.getX()) + (random.nextDouble() - 0.5D) + 0.5D;
								double d2 = MathHelper.lerp(d0, (double)pos.getY(), (double)leafpos.getY()) + random.nextDouble() - 0.5D;
								double d3 = MathHelper.lerp(d0, (double)pos.getZ(), (double)leafpos.getZ()) + (random.nextDouble() - 0.5D) + 0.5D;
								((ServerWorld) worldIn).spawnParticle(ParticleTypes.HEART, d1, d2, d3, 0, (double)f, (double)f1, (double)f2, 0);
							}
						}
					}
				}
			}
		}
		worldIn.setBlockState(pos, state);
	}

	private int nearbyHives(World worldIn, BlockPos pos)
	{
		int count = 0;
		for(int x = -7; x < 8; x++)
		{
			for(int z = -7; z < 8; z++)
			{
				for(int y = -7; y < 8; y++)
				{
					BlockPos newpos = pos.add(x, y, z);
					if(x == 0 && y == 0 && z == 0)
						continue;
					else if(worldIn.getBlockState(newpos).getBlock() instanceof HiveBlock)
						count++;
				}
			}
		}
		return count;
	}

	private BlockPos getRandomBlock(World world, BlockPos pos)
	{
		for(int i = 16; i > 0; i--)
		{
			int x = -7 + world.getRandom().nextInt(15);
			int y = -7 + world.getRandom().nextInt(15);
			int z = -7 + world.getRandom().nextInt(15);
			BlockPos new_pos = pos.add(x,y,z);
			while(world.getBlockState(new_pos).getBlock() instanceof LeavesBlock)
			{
				if(world.getBlockState(new_pos.down()).isAir(world, new_pos.down()))
					return new_pos.down();
				else if(world.getBlockState(new_pos.down()).getBlock() instanceof LeavesBlock)
				{
					new_pos = new_pos.down();
				}
				else
					break;
			}
		}
		return pos;
	}

	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (player.getHeldItem(hand).getItem() == Items.SHEARS && state.get(HONEY_AMOUNT) > 0) {
			if (player.getHeldItem(hand).getDamage() < player.getHeldItem(hand).getMaxDamage()) {
				checkIfSmoked(state, world, pos);
				player.getHeldItem(hand).damageItem(1, player, e -> e.sendBreakAnimation(hand));
				if (!player.inventory.addItemStackToInventory(new ItemStack(ModItems.HoneyComb, 3))) {
					player.dropItem(new ItemStack(ModItems.Honey), false);
				}
				state = state.with(HONEY_AMOUNT, state.get(HONEY_AMOUNT).intValue() - 1);
				world.setBlockState(pos, state);
				return true;
			}
		} else if (ItemStack.areItemsEqualIgnoreDurability(player.getHeldItem(hand),
				Items.GLASS_BOTTLE.getDefaultInstance()) && state.get(HONEY_AMOUNT) > 0) {
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
		return state.getBlock().getRegistryName().getNamespace().equalsIgnoreCase("verticle_hive");
	}
	
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
		MeaderyMod.LOGGER.info("COMP");
		return blockState.get(HONEY_AMOUNT);
	}

	private void checkIfSmoked(BlockState state, World world, BlockPos pos) {
		BlockState LowerBlock = world.getBlockState(pos.down());
		if (LowerBlock.getBlock() == Blocks.CAMPFIRE)
			if (LowerBlock.get(CampfireBlock.LIT))
				return;
		// spawn bee swarm!!
	}

	public static List<ItemStack> getDrops(BlockState state, ServerWorld worldIn, BlockPos pos, @Nullable TileEntity tileEntityIn) {
		//Entity entity = builder.get(LootParameters.THIS_ENTITY);
		List<ItemStack> items = new ArrayList<ItemStack>();
		ItemStack hive = new ItemStack(state.getBlock());
		CompoundNBT DETAILS = new CompoundNBT();
		DETAILS.putInt("amount", state.get(HONEY_AMOUNT));
		DETAILS.putBoolean("hibernating", state.get(HIBERNATING));
		hive.setTag(DETAILS);
		items.add(hive);
		return items;
	}
	
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
    {
    	if(player.isSneaking())
    	{
			ItemStack hive = new ItemStack(state.getBlock());
			CompoundNBT DETAILS = new CompoundNBT();
			DETAILS.putInt("amount", state.get(HONEY_AMOUNT));
			DETAILS.putBoolean("hibernating", state.get(HIBERNATING));
			hive.setTag(DETAILS);
	        return hive;
    	}
    	return super.getPickBlock(state, target, world, pos, player);
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
