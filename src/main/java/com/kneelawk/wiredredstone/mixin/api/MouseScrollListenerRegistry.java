package com.kneelawk.wiredredstone.mixin.api;

import java.util.ArrayList;
import java.util.List;

public class MouseScrollListenerRegistry {
    private static final List<MouseScrollListener> listeners = new ArrayList<>();

    public static void registerListener(MouseScrollListener listener) {
        listeners.add(listener);
    }

    public static void notifyListeners(long window, double scrollDeltaX, double scrollDeltaY) {
        for (MouseScrollListener listener : listeners) {
            listener.onMouseScroll(window, scrollDeltaX, scrollDeltaY);
        }
    }
}
