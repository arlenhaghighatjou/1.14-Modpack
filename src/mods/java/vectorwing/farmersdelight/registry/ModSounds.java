package vectorwing.farmersdelight.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.Registry;
import vectorwing.farmersdelight.FarmersDelight;

public class ModSounds
{
	public static SoundEvent BLOCK_COOKING_POT_BOIL;
	public static SoundEvent BLOCK_CUTTING_BOARD_KNIFE;
	public static SoundEvent BLOCK_STOVE_CRACKLE;

	public static void registerSounds()
	{
		BLOCK_COOKING_POT_BOIL = Registry.register(Registry.SOUND_EVENT, new ResourceLocation(FarmersDelight.MODID, "block.cooking_pot.boil"), new SoundEvent(new ResourceLocation(FarmersDelight.MODID, "block.cooking_pot.boil")));
		BLOCK_CUTTING_BOARD_KNIFE = Registry.register(Registry.SOUND_EVENT, new ResourceLocation(FarmersDelight.MODID, "block.cutting_board.knife"), new SoundEvent(new ResourceLocation(FarmersDelight.MODID, "block.cutting_board.knife")));
		BLOCK_STOVE_CRACKLE = Registry.register(Registry.SOUND_EVENT, new ResourceLocation(FarmersDelight.MODID, "block.stove.crackle"), new SoundEvent(new ResourceLocation(FarmersDelight.MODID, "block.stove.crackle")));
	}
}
