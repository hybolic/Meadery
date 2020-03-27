package hybolic.meadery.common.recipe;

import net.minecraft.util.IStringSerializable;

public enum FermentationType implements IStringSerializable
{
	FRUIT("fruit"),
	GRAIN("grain"),
	SUGAR("sugar"),
	HONEY("honey"),
	STRANGE1("strange"),
	STRANGE2("strange_dragon"),
	NULL("null");

	
	
	FermentationType(String s)
	{
		name = s;
	}
	
	private String name;
	@Override
	public String getName() {
		return name;
	}
	
	public static FermentationType valueOf_(String s)
	{
		for(FermentationType ferm : values())
		{
			if(ferm.getName().equalsIgnoreCase(s))
				return ferm;
		}
		return NULL;
	}
}