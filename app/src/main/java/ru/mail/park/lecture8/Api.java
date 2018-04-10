package ru.mail.park.lecture8;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Api {
    private static final Api INSTANCE = new Api();

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Sex.class, new SexAdapter())
            .create();

    private final Executor executor = Executors.newSingleThreadExecutor();

    private final TechnoparkService service;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private Api() {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://json-storage.herokuapp.com/")
                .build();
        service = retrofit.create(TechnoparkService.class);
    }

    public static Api getInstance() {
        return INSTANCE;
    }

    public ListenerHandler<OnUserGetListener> getUser(final String name, final OnUserGetListener listener) {
        final ListenerHandler<OnUserGetListener> handler = new ListenerHandler<>(listener);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Response<ResponseBody> response = service.getUser(name).execute();
                    if (response.code() != 200) {
                        throw new IOException("HTTP code " + response.code());
                    }
                    try (final ResponseBody responseBody = response.body()) {
                        if (responseBody == null) {
                            throw new IOException("Cannot get body");
                        }
                        final String body = responseBody.string();
                        invokeSuccess(handler, parseUser(body));
                    }
                } catch (IOException e) {
                    invokeError(handler, e);
                }
            }
        });
        return handler;
    }

    private void invokeSuccess(final ListenerHandler<OnUserGetListener> handler, final VkUser user) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnUserGetListener listener = handler.getListener();
                if (listener != null) {
                    Log.d("API", "listener NOT null");
                    listener.onUserSuccess(user);
                } else {
                    Log.d("API", "listener is null");
                }
            }
        });
    }

    private void invokeError(final ListenerHandler<OnUserGetListener> handler, final Exception error) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnUserGetListener listener = handler.getListener();
                if (listener != null) {
                    Log.d("API", "listener NOT null");
                    listener.onUserError(error);
                } else {
                    Log.d("API", "listener is null");
                }
            }
        });
    }

    private VkUser parseUser(final String body) throws IOException {
        try {
            return GSON.fromJson(body, VkUser.class);
        } catch (JsonSyntaxException e) {
            throw new IOException(e);
        }
    }

    public interface OnUserGetListener {
        void onUserSuccess(final VkUser user);

        void onUserError(final Exception error);
    }
}
