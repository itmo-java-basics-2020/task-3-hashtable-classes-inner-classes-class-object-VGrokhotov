package ru.itmo.java;

import java.util.Arrays;


public class HashTable {

    private static final int DEFAULT_CAPACITY = 1000;

    private static final double DEFAULT_LOAD_FACTOR = 0.5;

    private static final int GAP = 307;

    private final double loadFactor;

    private int threshold;

    private int size = 0;

    private Entry[] array;

    private boolean[] deleted;

    public HashTable() {
        this(DEFAULT_CAPACITY);
    }

    public HashTable(int capacity) {
        this(capacity, DEFAULT_LOAD_FACTOR);
    }

    public HashTable(double loadFactor) {
        this(DEFAULT_CAPACITY, loadFactor);
    }

    public HashTable(int capacity, double loadFactor) {

        if (capacity <= 0){
            capacity = DEFAULT_CAPACITY;
        }

        this.array = new Entry[capacity];
        this.deleted = new boolean[capacity];

        if (loadFactor > 1 || loadFactor <= 0) {
            loadFactor = DEFAULT_LOAD_FACTOR;
        }

        this.loadFactor = loadFactor;
        this.threshold = (int) (this.loadFactor * capacity);
    }

    private int hash(Object key){
        int arrayLength = array.length;
        return (key.hashCode() % arrayLength + arrayLength) % arrayLength;
    }

    private int indexInTable(Object key) {

        int hash = hash(key);

        int firstHash = hash;
        //int i = 1;

        while (deleted[hash] || array[hash] != null && !key.equals(array[hash].key)) {
            //hash = (hash + i*(i++)) % array.length;
            hash = (hash + GAP) % array.length;
            if (hash == firstHash){
                return indexToPut(key);
            }
        }

        return hash;
    }

    private int indexToPut(Object key) {

        int hash = hash(key);

        //int i = 1;

        while (array[hash] != null) {
            //hash = (hash + i*(i++)) % array.length;
            hash = (hash + GAP) % array.length;
        }

        return hash;
    }

    public Object put(Object key, Object value) {

        Entry newElement = new Entry(key, value);

        int index = indexInTable(key);

        if (array[index] == null) {

            index = indexToPut(key);

            if (deleted[index]) {
                deleted[index] = false;
            }

            array[index] = newElement;
            size++;

            if (size >= threshold) {
                this.rehash();
            }

            return null;
        }

        Entry oldElement = array[index];
        array[index] = newElement;

        return oldElement.value;
    }

    public Object get(Object key) {

        int index = indexInTable(key);

        if (array[index] == null) {
            return null;
        }

        return array[index].value;
    }

    public Object remove(Object key) {

        int index = indexInTable(key);

        if (array[index] == null) {
            return null;
        }

        deleted[index] = true;
        Entry deletedElement = array[index];
        size--;
        array[index] = null;

        return deletedElement.value;
    }

    public int size() {
        return this.size;
    }

    private void rehash() {

        var oldArray = array;

        array = new Entry[oldArray.length * 2];
        deleted = new boolean[array.length];
        threshold = (int) (loadFactor * oldArray.length * 2);
        size = 0;

        for (Entry element : oldArray) {
            if (element != null) {
                this.put(element.key, element.value);
            }
        }


    }

    @Override
    public String toString() {
        return Arrays.toString(array);
    }

    private static final class Entry {

        private Object key;

        private Object value;

        public Entry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return "key=" + key +
                    ", value=" + value;
        }
    }

}
