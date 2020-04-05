package hybolic.meadery.common.blocks;

import hybolic.meadery.MeaderyMod;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.common.ToolType;

public class BasicBlock extends Block {

	public BasicBlock(String id, Material mat, MaterialColor matcolor, float hr, ToolType tool, SoundType sound)
	{
		super(Block.Properties.create(mat, matcolor).hardnessAndResistance(hr).harvestTool(tool).sound(sound));
		this.setRegistryName(MeaderyMod.MODID, id);
	}
	public BasicBlock(String id, Material mat, MaterialColor matcolor, float hr, ToolType tool)
	{
		super(Block.Properties.create(mat, matcolor).hardnessAndResistance(hr).harvestTool(tool));
		this.setRegistryName(MeaderyMod.MODID, id);
	}
	public BasicBlock(String id, Material mat, MaterialColor matcolor, float hr, SoundType sound)
	{
		super(Block.Properties.create(mat, matcolor).hardnessAndResistance(hr).sound(sound));
		this.setRegistryName(MeaderyMod.MODID, id);
	}
	public BasicBlock(String id, Material mat, MaterialColor matcolor, float hr)
	{
		super(Block.Properties.create(mat, matcolor).hardnessAndResistance(hr));
		this.setRegistryName(MeaderyMod.MODID, id);
	}
}
