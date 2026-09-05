package net.minecraft.village;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.lax1dude.eaglercraft.Random;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GossipManager {
    private final Map<EaglercraftUUID, GossipManager.Gossips> field_220928_a = Maps.newHashMap();

    public void func_223538_b() {
        Iterator<GossipManager.Gossips> iterator = this.field_220928_a.values().iterator();

        while (iterator.hasNext()) {
            GossipManager.Gossips gossipmanager$gossips = iterator.next();
            gossipmanager$gossips.func_223529_a();
            if (gossipmanager$gossips.func_223530_b()) {
                iterator.remove();
            }
        }

    }

    private Stream<GossipManager.GossipEntry> func_220911_b() {
        return this.field_220928_a.entrySet().stream().flatMap((p_220917_0_) -> {
            return p_220917_0_.getValue().func_220895_a(p_220917_0_.getKey());
        });
    }

    private Collection<GossipManager.GossipEntry> func_220920_a(Random p_220920_1_, int p_220920_2_) {
        List<GossipManager.GossipEntry> list = this.func_220911_b().collect(Collectors.toList());
        if (list.isEmpty()) {
            return Collections.emptyList();
        } else {
            int[] aint = new int[list.size()];
            int i = 0;

            for (int j = 0; j < list.size(); ++j) {
                GossipManager.GossipEntry gossipmanager$gossipentry = list.get(j);
                i += Math.abs(gossipmanager$gossipentry.func_220904_a());
                aint[j] = i - 1;
            }

            Set<GossipManager.GossipEntry> set = Sets.newIdentityHashSet();

            for (int i1 = 0; i1 < p_220920_2_; ++i1) {
                int k = p_220920_1_.nextInt(i);
                int l = Arrays.binarySearch(aint, k);
                set.add(list.get(l < 0 ? -l - 1 : l));
            }

            return set;
        }
    }

    private GossipManager.Gossips func_220926_a(EaglercraftUUID p_220926_1_) {
        return this.field_220928_a.computeIfAbsent(p_220926_1_, (p_220922_0_) -> {
            return new GossipManager.Gossips();
        });
    }

    public void func_220912_a(GossipManager p_220912_1_, Random p_220912_2_, int p_220912_3_) {
        Collection<GossipManager.GossipEntry> collection = p_220912_1_.func_220920_a(p_220912_2_, p_220912_3_);
        collection.forEach((p_220923_1_) -> {
            int i = p_220923_1_.value - p_220923_1_.type.field_220935_k;
            if (i >= 2) {
            }

        });
    }

    public int func_220921_a(EaglercraftUUID p_220921_1_, Predicate<GossipType> p_220921_2_) {
        GossipManager.Gossips gossipmanager$gossips = this.field_220928_a.get(p_220921_1_);
        return gossipmanager$gossips != null ? gossipmanager$gossips.func_220896_a(p_220921_2_) : 0;
    }

    public void func_220916_a(EaglercraftUUID p_220916_1_, GossipType p_220916_2_, int p_220916_3_) {

    }

    public <T> Dynamic<T> func_220914_a(DynamicOps<T> p_220914_1_) {
        return new Dynamic<>(p_220914_1_, p_220914_1_.createList(this.func_220911_b().map((p_220919_1_) -> {
            return p_220919_1_.serialize(p_220914_1_);
        }).map(Dynamic::getValue)));
    }

    public void func_220918_a(Dynamic<?> p_220918_1_) {

    }

    private static int func_220924_a(int p_220924_0_, int p_220924_1_) {
        return Math.max(p_220924_0_, p_220924_1_);
    }

    private int func_220925_a(GossipType p_220925_1_, int p_220925_2_, int p_220925_3_) {
        int i = p_220925_2_ + p_220925_3_;
        return i > p_220925_1_.field_220933_i ? Math.max(p_220925_1_.field_220933_i, p_220925_2_) : i;
    }

    static class GossipEntry {
        public final EaglercraftUUID target;
        public final GossipType type;
        public final int value;

        public GossipEntry(EaglercraftUUID target, GossipType type, int value) {
            this.target = target;
            this.type = type;
            this.value = value;
        }

        public int func_220904_a() {
            return this.value * this.type.field_220932_h;
        }

        public String toString() {
            return "GossipEntry{target=" + this.target + ", type=" + this.type + ", value=" + this.value + '}';
        }

        public <T> Dynamic<T> serialize(DynamicOps<T> p_220905_1_) {
            return null;
        }

        public static Optional<GossipManager.GossipEntry> deserialize(Dynamic<?> p_220902_0_) {
            return Optional.empty();
        }
    }

    static class Gossips {
        private final Object2IntMap<GossipType> field_220900_a = new Object2IntOpenHashMap<>();

        private Gossips() {
        }

        public int func_220896_a(Predicate<GossipType> p_220896_1_) {
            return this.field_220900_a.object2IntEntrySet().stream().filter((p_220898_1_) -> {
                return p_220896_1_.test(p_220898_1_.getKey());
            }).mapToInt((p_220894_0_) -> {
                return p_220894_0_.getIntValue() * (p_220894_0_.getKey()).field_220932_h;
            }).sum();
        }

        public Stream<GossipManager.GossipEntry> func_220895_a(EaglercraftUUID p_220895_1_) {
            return this.field_220900_a.object2IntEntrySet().stream().map((p_220897_1_) -> {
                return new GossipManager.GossipEntry(p_220895_1_, p_220897_1_.getKey(), p_220897_1_.getIntValue());
            });
        }

        public void func_223529_a() {
            ObjectIterator<Entry<GossipType>> objectiterator = this.field_220900_a.object2IntEntrySet().iterator();

            while (objectiterator.hasNext()) {
                Entry<GossipType> entry = objectiterator.next();
                int i = entry.getIntValue() - (entry.getKey()).field_220934_j;
                if (i < 2) {
                    objectiterator.remove();
                } else {
                    entry.setValue(i);
                }
            }

        }

        public boolean func_223530_b() {
            return this.field_220900_a.isEmpty();
        }

        public void func_223531_a(GossipType p_223531_1_) {
            int i = this.field_220900_a.getInt(p_223531_1_);
            if (i > p_223531_1_.field_220933_i) {
                this.field_220900_a.put(p_223531_1_, p_223531_1_.field_220933_i);
            }

            if (i < 2) {
                this.func_223528_b(p_223531_1_);
            }

        }

        public void func_223528_b(GossipType p_223528_1_) {
            this.field_220900_a.removeInt(p_223528_1_);
        }
    }
}
