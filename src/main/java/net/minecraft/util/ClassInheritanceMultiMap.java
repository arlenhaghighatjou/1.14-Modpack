package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.*;

public class ClassInheritanceMultiMap<T> extends AbstractCollection<T> {
    private Map<Class<?>, List<T>> map;
    private Map<Class<?>, Collection<T>> mapViews;
    private final Class<T> baseClass;
    private final List<T> values = Lists.newArrayList();
    private final Collection<T> valuesView = Collections.unmodifiableList(this.values);

    public ClassInheritanceMultiMap(Class<T> baseClassIn) {
        this.baseClass = baseClassIn;
    }

    public boolean add(T p_add_1_) {
        boolean added = this.values.add(p_add_1_);
        if (this.map != null) {
            for (Map.Entry<Class<?>, List<T>> entry : this.map.entrySet()) {
                if (entry.getKey().isInstance(p_add_1_)) {
                    entry.getValue().add(p_add_1_);
                }
            }
        }
        return added;
    }

    public boolean remove(Object p_remove_1_) {
        boolean removed = this.values.remove(p_remove_1_);
        if (this.map != null) {
            for (Map.Entry<Class<?>, List<T>> entry : this.map.entrySet()) {
                if (entry.getKey().isInstance(p_remove_1_)) {
                    entry.getValue().remove(p_remove_1_);
                }
            }
        }
        return removed;
    }

    public boolean contains(Object p_contains_1_) {
        return this.values.contains(p_contains_1_);
    }

    public <S> Collection<S> func_219790_a(Class<S> p_219790_1_) {
        if (p_219790_1_ == this.baseClass) {
            return (Collection<S>) this.valuesView;
        }
        if (this.map == null) {
            this.map = new HashMap<>(4);
            this.mapViews = new HashMap<>(4);
        }
        List<T> list = this.map.get(p_219790_1_);
        if (list == null) {
            if (!this.baseClass.isAssignableFrom(p_219790_1_)) {
                throw new IllegalArgumentException("Don't know how to search for " + p_219790_1_);
            }
            list = new ArrayList<>();
            for (int i = 0, len = this.values.size(); i < len; ++i) {
                T value = this.values.get(i);
                if (p_219790_1_.isInstance(value)) {
                    list.add(value);
                }
            }
            this.map.put(p_219790_1_, list);
            this.mapViews.put(p_219790_1_, Collections.unmodifiableList(list));
        }
        return (Collection<S>) this.mapViews.get(p_219790_1_);
    }

    public Iterator<T> iterator() {
        return this.valuesView.iterator();
    }

    public int size() {
        return this.values.size();
    }
}
