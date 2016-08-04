package com.chair49.sentimentkeyboard;

import java.util.HashMap;

public class DefaultHashMap<K, V> extends HashMap<K, V> {

    private static final long serialVersionUID = 3382559836710528625L;

    private V defaultValue;

    public DefaultHashMap(V defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public V get(Object arg0) {

        if (this.containsKey(arg0))
            return super.get(arg0);
        return defaultValue;
    }
}
