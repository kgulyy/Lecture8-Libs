package ru.mail.park.lecture8;

import android.support.annotation.Nullable;

public class ListenerHandler<T> {
    private T listener;

    public ListenerHandler(final T listener) {
        this.listener = listener;
    }

    @Nullable
    public T getListener() {
        return listener;
    }

    public void unregister() {
        listener = null;
    }
}
