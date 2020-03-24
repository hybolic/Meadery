package hybolic.meadery.common.recipe;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.network.PacketBuffer;

public class FermentationIngredient
{
	public FermentationType fermentation_type;
	public int count = 1;
	
	public FermentationIngredient(FermentationType fermentation_type, int count)
	{
		this.fermentation_type = fermentation_type;
		this.count = count;
	}
	

	public static FermentationIngredient[] deserializer(JsonObject json) {
		if(json.isJsonArray())
		{
			List<FermentationIngredient> list = new ArrayList<FermentationIngredient>();
			for(JsonElement element : json.getAsJsonArray())
			{
				JsonObject item = element.getAsJsonObject();
				final FermentationType fermentation_type = FermentationType.valueOf(item.get("fermentation_type").getAsString());
				final int count =  item.get("count").getAsInt();
				list.add(new FermentationIngredient(fermentation_type, count));
			}
			return (FermentationIngredient[]) list.toArray();
	    }
		return new FermentationIngredient[] {};
	}

	public static FermentationIngredient[] deserializer(PacketBuffer buffer)
	{
		int loop = buffer.readInt();
		List<FermentationIngredient> list = new ArrayList<FermentationIngredient>();
		for(int i = 0; i < loop; i++)
		{
			FermentationIngredient in = deserializer_single(buffer);
			list.add(in);
		}
		return (FermentationIngredient[]) list.toArray();
	}

	public static FermentationIngredient deserializer_single(PacketBuffer buffer)
	{
		final FermentationType fermentation_type = FermentationType.valueOf(buffer.readString());
		final int count =  buffer.readInt();
		return new FermentationIngredient(fermentation_type, count);
	}
	
	public static void serialize(PacketBuffer buffer, FermentationIngredient ingredient)
	{
		buffer.writeString(ingredient.fermentation_type.getName());
		buffer.writeInt(ingredient.count);
	}
}
