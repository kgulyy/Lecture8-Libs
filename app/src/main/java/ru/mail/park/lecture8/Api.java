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
import ru.mail.park.lecture8.task.BooleanTypeAdapter;
import ru.mail.park.lecture8.task.Gender;
import ru.mail.park.lecture8.task.GenderAdapter;
import ru.mail.park.lecture8.task.GroupService;
import ru.mail.park.lecture8.task.VkGroup;

public class Api {
    private static final Api INSTANCE = new Api();

    private static final Gson GSON_USER = new GsonBuilder()
            .registerTypeAdapter(Sex.class, new SexAdapter())
            .create();

    private static final Gson GSON_GROUP = new GsonBuilder()
            .registerTypeAdapter(Gender.class, new GenderAdapter())
            .registerTypeAdapter(boolean.class, new BooleanTypeAdapter())
            .create();

    private final Executor executor = Executors.newSingleThreadExecutor();

    private final UserService userService;
    private final GroupService groupService;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private Api() {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://json-storage.herokuapp.com/")
                .build();
        userService = retrofit.create(UserService.class);
        groupService = retrofit.create(GroupService.class);
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
                    final Response<ResponseBody> response = userService.getUser(name).execute();
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

    public ListenerHandler<OnGroupGetListener> getGroup(final int id, final OnGroupGetListener listener) {
        final ListenerHandler<OnGroupGetListener> handler = new ListenerHandler<>(listener);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Response<ResponseBody> response = groupService.getGroup(id).execute();
                    if (response.code() != 200) {
                        throw new IOException("HTTP code " + response.code());
                    }
                    try (final ResponseBody responseBody = response.body()) {
                        if (responseBody == null) {
                            throw new IOException("Cannot get body");
                        }
                        final String body = responseBody.string();
                        invokeSuccess(handler, parseGroup(body));
                    }
                } catch (IOException e) {
                    invokeError(handler, e, 0);
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

    private void invokeSuccess(final ListenerHandler<OnGroupGetListener> handler, final VkGroup group) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnGroupGetListener listener = handler.getListener();
                if (listener != null) {
                    Log.d("API", "listener NOT null");
                    listener.onGroupSuccess(group);
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

    private void invokeError(final ListenerHandler<OnGroupGetListener> handler, final Exception error, int trash) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnGroupGetListener listener = handler.getListener();
                if (listener != null) {
                    Log.d("API", "listener NOT null");
                    listener.onGroupError(error);
                } else {
                    Log.d("API", "listener is null");
                }
            }
        });
    }

    private VkUser parseUser(final String body) throws IOException {
        try {
            return GSON_USER.fromJson(body, VkUser.class);
        } catch (JsonSyntaxException e) {
            throw new IOException(e);
        }
    }

    private VkGroup parseGroup(String body) throws IOException {
        try {
            return GSON_GROUP.fromJson(body, VkGroup.class);
        } catch (JsonSyntaxException e) {
            throw new IOException(e);
        }
    }

    public interface OnUserGetListener {
        void onUserSuccess(final VkUser user);

        void onUserError(final Exception error);
    }

    public interface OnGroupGetListener {
        void onGroupSuccess(final VkGroup group);

        void onGroupError(final Exception error);
    }
}
