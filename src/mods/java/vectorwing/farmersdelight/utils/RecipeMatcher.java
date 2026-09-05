package vectorwing.farmersdelight.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.List;

public class RecipeMatcher
{
	public static boolean matches(List<ItemStack> inputs, List<Ingredient> ingredients)
	{
		if (inputs.size() != ingredients.size()) {
			return false;
		}
		return match(inputs, ingredients, new boolean[inputs.size()], 0);
	}

	private static boolean match(List<ItemStack> inputs, List<Ingredient> ingredients, boolean[] used, int index)
	{
		if (index == ingredients.size()) {
			return true;
		}

		Ingredient ingredient = ingredients.get(index);

		for (int i = 0; i < inputs.size(); ++i) {
			if (!used[i] && ingredient.test(inputs.get(i))) {
				used[i] = true;
				if (match(inputs, ingredients, used, index + 1)) {
					return true;
				}
				used[i] = false;
			}
		}

		return false;
	}
}
