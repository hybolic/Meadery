package hybolic.meadery.client.tesr;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import hybolic.meadery.common.blocks.AbstractDemijohnBlock;
import hybolic.meadery.common.blocks.AbstractFermentationBlock;
import hybolic.meadery.common.blocks.ModBlocks;
import hybolic.meadery.common.tile.BarrelTileEntity;
import hybolic.meadery.common.tile.FermentationTileEntity;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;

@SuppressWarnings("incomplete-switch")
public class WaterRenderer extends TileEntityRenderer<FermentationTileEntity> {
	public float min_waterLevel;
	public float max_waterLevel;
	public float waterLevel;
	public float x_off;
	public float y_off;
	public float x2_off;
	public float y2_off;
	public float scale;

	public WaterRenderer(float min, float max, float x, float y, float x2, float y2) {
		min_waterLevel = min;
		max_waterLevel = max;
		waterLevel = max - min;
		setSize(x, y, x2, y2);

	}

	public void setSize(float x, float y, float x2, float y2) {
		x_off = ((1f / 16f) * x) + .001f;
		y_off = ((1f / 16f) * y) + .001f;
		x2_off = ((1f / 16f) * x2) - 0.002f;
		y2_off = ((1f / 16f) * y2) - 0.002f;
	}

	public void render(FermentationTileEntity te, double d0, double d1, double d2, float partialTicks,
			int destroyStage) {
		super.render(te, d0, d1, d2, partialTicks, destroyStage);
		if ((te.getFluidAmount() / (float)te.getCapacity()) > 0.0) {
			@SuppressWarnings("deprecation")
			TextureAtlasSprite water_texture = Minecraft.getInstance().getModelManager().getBlockModelShapes().getModel(Blocks.WATER.getDefaultState()).getParticleTexture();
			GlStateManager.pushMatrix();
			
			//hard offsets
			if(te.getBlockState().getBlock() == ModBlocks.Demi_Large)
			{
				switch(te.getBlockState().get(AbstractDemijohnBlock.FACING))
				{
				case NORTH:
					GlStateManager.translated(0.0625, 0, 0);
					break;
				case EAST:
					GlStateManager.translated(0.125, 0, 0.0625);
					break;
				case SOUTH:
					GlStateManager.translated(0.0625, 0, 0.125);
					break;
				case WEST:
					GlStateManager.translated(0, 0, 0.0625);
					break;
				
				}
			}
			if(te.getBlockState().getBlock() == ModBlocks.Barrel || te.getBlockState().getBlock() == ModBlocks.VAT)
			{
				if(te instanceof BarrelTileEntity)
				{
					min_waterLevel = 0.125f;
					max_waterLevel = 0.875f;
					waterLevel = max_waterLevel - min_waterLevel;
				}
				if(te.getBlockState().get(AbstractFermentationBlock.SEALED))
				{
					GlStateManager.popMatrix();
					return;
				}
			}
			GlStateManager.translated(d0, d1 + min_waterLevel, d2 );

			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GlStateManager.disableAlphaTest();
			GlStateManager.enableCull();
			GlStateManager.color3f(0.3f, 0.3f, 1f);
			Minecraft.getInstance().textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
			GlStateManager.disableCull();
			GlStateManager.disableLighting();
			float value = waterLevel * ((float)te.getFluidAmount() / (float)te.getCapacity());
			if(te.getStackInSlot(5).isEmpty() == false)
			{
				value = waterLevel;
				GlStateManager.color3f(0.5f, 0.3f, 1f);
			}
			renderIcon(x_off, y_off, water_texture, x2_off, y2_off, value, 240);
			GlStateManager.enableCull();
			GlStateManager.enableAlphaTest();
			GlStateManager.disableBlend();
			GlStateManager.enableLighting();
			GlStateManager.color4f(1F, 1F, 1F, 1F);
			GlStateManager.popMatrix();

		}
	}

	public static VertexFormat POSITION_TEX_LMAP;

	public void renderIcon(float x1, float y1, TextureAtlasSprite atlas, float x2, float y2, float height, int brightness) {
		if (POSITION_TEX_LMAP == null)
			POSITION_TEX_LMAP = new VertexFormat().addElement(DefaultVertexFormats.POSITION_3F).addElement(DefaultVertexFormats.TEX_2F).addElement(DefaultVertexFormats.TEX_2S);
		
		Tessellator tessellator = Tessellator.getInstance();
		tessellator.getBuffer().begin(GL11.GL_QUADS, POSITION_TEX_LMAP);
		tessellator.getBuffer().pos(x1 + 0, 0, y1 + y2).tex(atlas.getMinU(), atlas.getMaxV()).lightmap(brightness, brightness).endVertex();
		tessellator.getBuffer().pos(x1 + x2, 0, y1 + y2).tex(atlas.getMaxU(), atlas.getMaxV()).lightmap(brightness, brightness).endVertex();
		tessellator.getBuffer().pos(x1 + x2, 0, y1 + 0).tex(atlas.getMaxU(), atlas.getMinV()).lightmap(brightness, brightness).endVertex();
		tessellator.getBuffer().pos(x1 + 0, 0, y1 + 0).tex(atlas.getMinU(), atlas.getMinV()).lightmap(brightness, brightness).endVertex();

		tessellator.getBuffer().pos(x1 + x2, height, y1 + 0).tex(atlas.getMaxU(), atlas.getMaxV()).lightmap(brightness, brightness).endVertex();
		tessellator.getBuffer().pos(x1 + 0, height, y1 + 0).tex(atlas.getMaxU(), atlas.getMinV()).lightmap(brightness, brightness).endVertex();
		tessellator.getBuffer().pos(x1 + 0, 0, y1 + 0).tex(atlas.getMinU(), atlas.getMinV()).lightmap(brightness, brightness).endVertex();
		tessellator.getBuffer().pos(x1 + x2, 0, y1 + 0).tex(atlas.getMinU(), atlas.getMaxV()).lightmap(brightness, brightness).endVertex();

		tessellator.getBuffer().pos(x1 + x2, height, y1 + y2).tex(atlas.getMaxU(), atlas.getMaxV()).lightmap(brightness, brightness).endVertex();
		tessellator.getBuffer().pos(x1 + 0, height, y1 + y2).tex(atlas.getMaxU(), atlas.getMinV()).lightmap(brightness, brightness).endVertex();
		tessellator.getBuffer().pos(x1 + 0, 0, y1 + y2).tex(atlas.getMinU(), atlas.getMinV()).lightmap(brightness, brightness).endVertex();
		tessellator.getBuffer().pos(x1 + x2, 0, y1 + y2).tex(atlas.getMinU(), atlas.getMaxV()).lightmap(brightness, brightness).endVertex();

		tessellator.getBuffer().pos(x1 + x2, 0, y1 + y2).tex(atlas.getMinU(), atlas.getMaxV()).lightmap(brightness, brightness).endVertex();
		tessellator.getBuffer().pos(x1 + x2, height, y1 + y2).tex(atlas.getMaxU(), atlas.getMaxV()).lightmap(brightness, brightness).endVertex();
		tessellator.getBuffer().pos(x1 + x2, height, y1 + 0).tex(atlas.getMaxU(), atlas.getMinV()).lightmap(brightness, brightness).endVertex();
		tessellator.getBuffer().pos(x1 + x2, 0, y1 + 0).tex(atlas.getMinU(), atlas.getMinV()).lightmap(brightness, brightness).endVertex();

		tessellator.getBuffer().pos(x1 + 0, 0, y1 + y2).tex(atlas.getMinU(), atlas.getMaxV()).lightmap(brightness, brightness).endVertex();
		tessellator.getBuffer().pos(x1 + 0, height, y1 + y2).tex(atlas.getMaxU(), atlas.getMaxV()).lightmap(brightness, brightness).endVertex();
		tessellator.getBuffer().pos(x1 + 0, height, y1 + 0).tex(atlas.getMaxU(), atlas.getMinV()).lightmap(brightness, brightness).endVertex();
		tessellator.getBuffer().pos(x1 + 0, 0, y1 + 0).tex(atlas.getMinU(), atlas.getMinV()).lightmap(brightness, brightness).endVertex();

		tessellator.getBuffer().pos(x1 + 0, height, y1 + y2).tex(atlas.getMinU(), atlas.getMaxV()).lightmap(brightness, brightness).endVertex();
		tessellator.getBuffer().pos(x1 + x2, height, y1 + y2).tex(atlas.getMaxU(), atlas.getMaxV()).lightmap(brightness, brightness).endVertex();
		tessellator.getBuffer().pos(x1 + x2, height, y1 + 0).tex(atlas.getMaxU(), atlas.getMinV()).lightmap(brightness, brightness).endVertex();
		tessellator.getBuffer().pos(x1 + 0, height, y1 + 0).tex(atlas.getMinU(), atlas.getMinV()).lightmap(brightness, brightness).endVertex();
		tessellator.draw();
	}
	
	public static class RenderPlane extends Model {

		public static RenderPlane PLANE = new RenderPlane();
		private RendererModel plane;

		public RenderPlane() {
			this.plane = new RendererModel(this, 0, 0);
			this.plane.setTextureSize(1, 16);
			this.plane.addBox(0, 0, 0, 1, 1, 1);
			this.plane.setRotationPoint(0.5f, 0f, 0.5f);
		}

		public void render(float offset) {
			plane.render(1f);
		}
	}

}
