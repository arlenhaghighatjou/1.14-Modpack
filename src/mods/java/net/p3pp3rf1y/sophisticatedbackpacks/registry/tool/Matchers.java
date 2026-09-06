package net.p3pp3rf1y.sophisticatedbackpacks.registry.tool;

import net.minecraft.util.registry.Registry;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.item.Item;
import net.minecraft.tags.Tag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.util.WorldHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Matchers {
	private Matchers() {}

	private static final List<ItemMatcherFactory> ITEM_MATCHER_FACTORIES = new ArrayList<>();
	private static final List<IMatcherFactory<BlockContext>> BLOCK_MATCHER_FACTORIES = new ArrayList<>();
	private static final List<IMatcherFactory<Entity>> ENTITY_MATCHER_FACTORIES = new ArrayList<>();

	static {
		addItemMatcherFactory(new ItemMatcherFactory("tag") {
			@Override
			protected Optional<CacheableStackPredicate> getPredicateFromObject(JsonObject jsonObject) {
				String tagName = JSONUtils.getString(jsonObject, "tag");
				Tag<Item> tag = ItemTags.getCollection().get(new ResourceLocation(tagName));
				return tag == null ? Optional.empty() : Optional.of(new ItemTagMatcher(tag));
			}
		});

		addItemMatcherFactory(new ItemMatcherFactory("emptynbt") {
			@Override
			protected Optional<CacheableStackPredicate> getPredicateFromObject(JsonObject jsonObject) {
				ResourceLocation itemName = new ResourceLocation(JSONUtils.getString(jsonObject, "item"));
				if (!Registry.ITEM.keySet().contains(itemName)) {
					SophisticatedBackpacks.LOGGER.debug("{} isn't loaded in item registry, skipping ...", itemName);
				}
				Item item = Registry.ITEM.getOrDefault(itemName);
				return Optional.of(st -> st.getItem() == item && (st.getTag() == null || st.getTag().isEmpty()));
			}
		});

		BLOCK_MATCHER_FACTORIES.add(new IMatcherFactory<BlockContext>() {
			@Override
			public boolean appliesTo(JsonElement jsonElement) {
				return jsonElement.isJsonPrimitive();
			}

			@Override
			public Optional<Predicate<BlockContext>> getPredicate(JsonElement jsonElement) {
				String modId = jsonElement.getString();
				if (!modpack.ModLoader.isLoaded(modId)) {
					SophisticatedBackpacks.LOGGER.debug("{} mod isn't loaded, skipping ...", modId);
					return Optional.empty();
				}

				return Optional.of(new ModMatcher<>(modId, BlockContext::getBlock));
			}
		});
		BLOCK_MATCHER_FACTORIES.add(new TypedMatcherFactory<BlockContext>("all") {
			@Override
			protected Optional<Predicate<BlockContext>> getPredicateFromObject(JsonObject jsonObject) {
				return Optional.of(block -> true);
			}
		});
		BLOCK_MATCHER_FACTORIES.add(new TypedMatcherFactory<BlockContext>("rail") {
			@Override
			protected Optional<Predicate<BlockContext>> getPredicateFromObject(JsonObject jsonObject) {
				return Optional.of(blockContext -> blockContext.getBlock() instanceof AbstractRailBlock);
			}
		});
		BLOCK_MATCHER_FACTORIES.add(new TypedMatcherFactory<BlockContext>("item_handler") {
			@Override
			protected Optional<Predicate<BlockContext>> getPredicateFromObject(JsonObject jsonObject) {
				return Optional.of(blockContext -> WorldHelper.getTile(blockContext.getWorld(),
						blockContext.getPos()).map(te -> ItemHandlerLookup.get(te, null).isPresent()).orElse(false));
			}
		});
		ENTITY_MATCHER_FACTORIES.add(new TypedMatcherFactory<Entity>("animal") {
			@Override
			protected Optional<Predicate<Entity>> getPredicateFromObject(JsonObject jsonObject) {
				return Optional.of(entity -> entity instanceof AnimalEntity);
			}
		});
		ENTITY_MATCHER_FACTORIES.add(new TypedMatcherFactory<Entity>("living") {
			@Override
			protected Optional<Predicate<Entity>> getPredicateFromObject(JsonObject jsonObject) {
				return Optional.of(entity -> entity instanceof LivingEntity);
			}
		});
		ENTITY_MATCHER_FACTORIES.add(new TypedMatcherFactory<Entity>("bee") {
			@Override
			protected Optional<Predicate<Entity>> getPredicateFromObject(JsonObject jsonObject) {
				return Optional.empty();
			}
		});
		ENTITY_MATCHER_FACTORIES.add(new TypedMatcherFactory<Entity>("tameable") {
			@Override
			protected Optional<Predicate<Entity>> getPredicateFromObject(JsonObject jsonObject) {
				return Optional.of(entity -> entity instanceof TameableEntity);
			}
		});
	}

	static void addItemMatcherFactory(ItemMatcherFactory matcherFactory) {
		ITEM_MATCHER_FACTORIES.add(matcherFactory);
	}

	public static Optional<CacheableStackPredicate> getItemMatcher(JsonElement jsonElement) {
		for (ItemMatcherFactory itemMatcherFactory : Matchers.ITEM_MATCHER_FACTORIES) {
			if (itemMatcherFactory.appliesTo(jsonElement)) {
				return itemMatcherFactory.getPredicate(jsonElement);
			}
		}
		return Optional.empty();
	}

	public static List<IMatcherFactory<BlockContext>> getBlockMatcherFactories() {
		return BLOCK_MATCHER_FACTORIES;
	}

	public static List<IMatcherFactory<Entity>> getEntityMatcherFactories() {
		return ENTITY_MATCHER_FACTORIES;
	}
}
