package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import com.google.gson.*;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class Ingredient implements Predicate<ItemStack> {
    public static final Ingredient EMPTY = new Ingredient(Stream.empty());
    private static final Predicate<? super Ingredient.IItemList> IS_EMPTY = (p_209361_0_) -> {
        return !p_209361_0_.getStacks().stream().allMatch(ItemStack::isEmpty);
    };
    private final Ingredient.IItemList[] acceptedItems;
    private ItemStack[] matchingStacks;
    private IntList matchingStacksPacked;

    private Ingredient(Stream<? extends Ingredient.IItemList> itemLists) {
        this.acceptedItems = itemLists.toArray((p_209360_0_) -> {
            return new Ingredient.IItemList[p_209360_0_];
        });
    }

    private static Ingredient fromItemListStream(Stream<? extends Ingredient.IItemList> stream) {
        Ingredient ingredient = new Ingredient(stream);
        return ingredient.acceptedItems.length == 0 ? EMPTY : ingredient;
    }

    public static Ingredient fromItems(IItemProvider... itemsIn) {
        return fromItemListStream(Arrays.stream(itemsIn).map((p_209353_0_) -> {
            return new Ingredient.SingleItemList(new ItemStack(p_209353_0_));
        }));
    }

    @OnlyIn(Dist.CLIENT)
    public static Ingredient fromStacks(ItemStack... stacks) {
        return fromItemListStream(Arrays.stream(stacks).map((p_209356_0_) -> {
            return new Ingredient.SingleItemList(p_209356_0_);
        }));
    }

    public static Ingredient fromTag(Tag<Item> tagIn) {
        return fromItemListStream(Stream.of(new Ingredient.TagList(tagIn)));
    }

    public static Ingredient read(PacketBuffer buffer) {
        int i = buffer.readVarInt();
        return fromItemListStream(Stream.generate(() -> {
            return new Ingredient.SingleItemList(buffer.readItemStack());
        }).limit((long) i));
    }

    public static Ingredient deserialize(JsonElement json) {
        if (json != null && !json.isJsonNull()) {
            if (json.isJsonObject()) {
                return fromItemListStream(Stream.of(deserializeItemList(json.getAsJsonObject())));
            } else if (json.isJsonArray()) {
                JsonArray jsonarray = json.getAsJsonArray();
                if (jsonarray.size() == 0) {
                    throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
                } else {
                    return fromItemListStream(StreamSupport.stream(jsonarray.spliterator(), false).map((p_209355_0_) -> {
                        return deserializeItemList(JSONUtils.getJsonObject(p_209355_0_, "item"));
                    }));
                }
            } else {
                throw new JsonSyntaxException("Expected item to be object or array of objects");
            }
        } else {
            throw new JsonSyntaxException("Item cannot be null");
        }
    }

    public static Ingredient.IItemList deserializeItemList(JsonObject json) {
        if (json.has("item") && json.has("tag")) {
            throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
        } else if (json.has("item")) {
            ResourceLocation resourcelocation1 = new ResourceLocation(JSONUtils.getString(json, "item"));
            Item item = Registry.ITEM.getValue(resourcelocation1).orElseThrow(() -> {
                return new JsonSyntaxException("Unknown item '" + resourcelocation1 + "'");
            });
            return new Ingredient.SingleItemList(new ItemStack(item));
        } else if (json.has("tag")) {
            ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(json, "tag"));
            Tag<Item> tag = ItemTags.getCollection().get(resourcelocation);
            if (tag == null) {
                throw new JsonSyntaxException("Unknown item tag '" + resourcelocation + "'");
            } else {
                return new Ingredient.TagList(tag);
            }
        } else {
            throw new JsonParseException("An ingredient entry needs either a tag or an item");
        }
    }

    @OnlyIn(Dist.CLIENT)
    public ItemStack[] getMatchingStacks() {
        this.determineMatchingStacks();
        if (this.matchingStacks == null) {
            List<ItemStack> temp = Lists.newArrayList();
            for (Ingredient.IItemList itemList : this.acceptedItems) {
                temp.addAll(itemList.getStacks());
            }
            return temp.toArray(new ItemStack[0]);
        }
        return this.matchingStacks;
    }

    private void determineMatchingStacks() {
        if (this.matchingStacks == null) {
            ItemStack[] resolvedStacks = Arrays.stream(this.acceptedItems).flatMap((p_209359_0_) -> {
                return p_209359_0_.getStacks().stream();
            }).distinct().toArray((p_209358_0_) -> {
                return new ItemStack[p_209358_0_];
            });

            if (resolvedStacks.length > 0 || this.acceptedItems.length == 0) {
                this.matchingStacks = resolvedStacks;
            } else {
                return;
            }
        }

    }

    public boolean test(ItemStack p_test_1_) {
        if (p_test_1_ == null) {
            return false;
        } else if (this.acceptedItems.length == 0) {
            return p_test_1_.isEmpty();
        } else {
            this.determineMatchingStacks();

            if (this.matchingStacks == null) {
                for (Ingredient.IItemList itemList : this.acceptedItems) {
                    for (ItemStack itemstack : itemList.getStacks()) {
                        if (itemstack.getItem() == p_test_1_.getItem()) {
                            return true;
                        }
                    }
                }
                return false;
            }

            for (ItemStack itemstack : this.matchingStacks) {
                if (itemstack.getItem() == p_test_1_.getItem()) {
                    return true;
                }
            }

            return false;
        }
    }

    public IntList getValidItemStacksPacked() {
        if (this.matchingStacksPacked == null) {
            this.determineMatchingStacks();
            if (this.matchingStacks == null) {
                IntList temp = new IntArrayList();
                for (Ingredient.IItemList itemList : this.acceptedItems) {
                    for (ItemStack itemstack : itemList.getStacks()) {
                        int packed = RecipeItemHelper.pack(itemstack);
                        temp.add(packed);
                    }
                }
                temp.sort(IntComparators.NATURAL_COMPARATOR);
                return temp;
            }
            this.matchingStacksPacked = new IntArrayList(this.matchingStacks.length);

            for (ItemStack itemstack : this.matchingStacks) {
                int packed = RecipeItemHelper.pack(itemstack);
                this.matchingStacksPacked.add(packed);
            }

            this.matchingStacksPacked.sort(IntComparators.NATURAL_COMPARATOR);
        }

        return this.matchingStacksPacked;
    }

    public void write(PacketBuffer buffer) {
        this.determineMatchingStacks();
        if (this.matchingStacks == null) {
            List<ItemStack> temp = Lists.newArrayList();
            for (Ingredient.IItemList itemList : this.acceptedItems) {
                temp.addAll(itemList.getStacks());
            }
            buffer.writeVarInt(temp.size());
            for (ItemStack itemstack : temp) {
                buffer.writeItemStack(itemstack);
            }
        } else {
            buffer.writeVarInt(this.matchingStacks.length);

            for (int i = 0; i < this.matchingStacks.length; ++i) {
                buffer.writeItemStack(this.matchingStacks[i]);
            }
        }
    }

    public JsonElement serialize() {
        if (this.acceptedItems.length == 1) {
            return this.acceptedItems[0].serialize();
        } else {
            JsonArray jsonarray = new JsonArray();

            for (Ingredient.IItemList ingredient$iitemlist : this.acceptedItems) {
                jsonarray.add(ingredient$iitemlist.serialize());
            }

            return jsonarray;
        }
    }

    public boolean hasNoMatchingItems() {
        return this.acceptedItems.length == 0 && (this.matchingStacks == null || this.matchingStacks.length == 0) && (this.matchingStacksPacked == null || this.matchingStacksPacked.isEmpty());
    }

    interface IItemList {
        Collection<ItemStack> getStacks();

        JsonObject serialize();
    }

    static class SingleItemList implements Ingredient.IItemList {
        private final ItemStack stack;

        private SingleItemList(ItemStack stackIn) {
            this.stack = stackIn;
        }

        public Collection<ItemStack> getStacks() {
            return Collections.singleton(this.stack);
        }

        public JsonObject serialize() {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("item", Registry.ITEM.getKey(this.stack.getItem()).toString());
            return jsonobject;
        }
    }

    static class TagList implements Ingredient.IItemList {
        private final Tag<Item> tag;

        private TagList(Tag<Item> tagIn) {
            this.tag = tagIn;
        }

        public Collection<ItemStack> getStacks() {
            List<ItemStack> list = Lists.newArrayList();

            for (Item item : this.tag.getAllElements()) {
                list.add(new ItemStack(item));
            }

            if (list.isEmpty()) {
                System.out.println("[CRAFT DEBUG] TagList getStacks for tag " + this.tag.getId() + " is empty! Tag class: " + this.tag.getClass());
            }

            return list;
        }

        public JsonObject serialize() {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("tag", this.tag.getId().toString());
            return jsonobject;
        }
    }
}
