package hybolic.meadery.client;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ItemToolTipHandler {
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void ItemTooltipEvent(ItemTooltipEvent event) {
		if(Blocks.DIRT.getClass().getCanonicalName().equalsIgnoreCase("net.minecraft.block.Block"))
			if (event.getFlags().isAdvanced()) {
				for (ResourceLocation tag : event.getItemStack().getItem().getTags()) {
					event.getToolTip().add(new TranslationTextComponent(tag.toString()));
				}
				if (event.getItemStack().hasTag()) {
					event.getToolTip().add(new TranslationTextComponent(event.getItemStack().getTag().toString()));
				}
			}
	}
}