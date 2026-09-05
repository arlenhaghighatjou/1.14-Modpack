package it.unimi.dsi.fastutil.objects;

import java.util.Iterator;
import java.util.TreeSet;

public class ObjectAVLTreeSet<K> extends TreeSet<K> implements ObjectSortedSet<K> {
    public ObjectAVLTreeSet() {
        super();
    }

    @Override
    public ObjectBidirectionalIterator<K> iterator(K fromElement) {
        Iterator<K> it = tailSet(fromElement).iterator();
        return new ObjectBidirectionalIterator<K>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public K next() {
                return it.next();
            }

            @Override
            public void remove() {
                it.remove();
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public K previous() {
                throw new UnsupportedOperationException();
            }

            @Override
            public int skip(int n) {
                int i = 0;
                while (i < n && hasNext()) {
                    next();
                    i++;
                }
                return i;
            }

            @Override
            public int back(int n) {
                return 0;
            }
        };
    }

    @Override
    public ObjectBidirectionalIterator<K> objectIterator() {
        return iterator();
    }

    @Override
    public ObjectBidirectionalIterator<K> iterator() {
        Iterator<K> it = super.iterator();
        return new ObjectBidirectionalIterator<K>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public K next() {
                return it.next();
            }

            @Override
            public void remove() {
                it.remove();
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public K previous() {
                throw new UnsupportedOperationException();
            }

            @Override
            public int skip(int n) {
                int i = 0;
                while (i < n && hasNext()) {
                    next();
                    i++;
                }
                return i;
            }

            @Override
            public int back(int n) {
                return 0;
            }
        };
    }

    @Override
    public ObjectSortedSet<K> subSet(K fromElement, K toElement) {
        return (ObjectSortedSet<K>) super.subSet(fromElement, toElement);
    }

    @Override
    public ObjectSortedSet<K> headSet(K toElement) {
        return (ObjectSortedSet<K>) super.headSet(toElement);
    }

    @Override
    public ObjectSortedSet<K> tailSet(K fromElement) {
        return (ObjectSortedSet<K>) super.tailSet(fromElement);
    }
} 