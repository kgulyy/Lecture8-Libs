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
import retrofit2.Call;
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

    public ListenerHandler<OnGetListener<VkUser>> getUser(final String name, final OnGetListener<VkUser> listener) {
        final ListenerHandler<OnGetListener<VkUser>> handler = new ListenerHandler<>(listener);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final String body = executeCall(userService.getUser(name));
                    invokeSuccess(handler, parseUser(body));
                } catch (IOException e) {
                    invokeError(handler, e);
                }
            }
        });
        return handler;
    }

    public ListenerHandler<OnGetListener<VkGroup>> getGroup(final int id, final OnGetListener<VkGroup> listener) {
        final ListenerHandler<OnGetListener<VkGroup>> handler = new ListenerHandler<>(listener);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final String body = executeCall(groupService.getGroup(id));
                    invokeSuccess(handler, parseGroup(body));
                } catch (IOException e) {
                    invokeError(handler, e);
                }
            }
        });
        return handler;
    }

    private String executeCall(final Call<ResponseBody> call) throws IOException {
        final Response<ResponseBody> response = call.execute();
        try (final ResponseBody responseBody = response.body()) {
            if (response.code() != 200) {
                throw new IOException("HTTP code " + response.code());
            }
            if (responseBody == null) {
                throw new IOException("Cannot get body");
            }
            return responseBody.string();
        }
    }

    private <T> void invokeSuccess(final ListenerHandler<OnGetListener<T>> handler, final T user) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnGetListener<T> listener = handler.getListener();
                if (listener != null) {
                    Log.d("API", "listener NOT null");
                    listener.onSuccess(user);
                } else {
                    Log.d("API", "listener is null");
                }
            }
        });
    }

    private <T> void invokeError(final ListenerHandler<OnGetListener<T>> handler, final Exception error) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnGetListener<T> listener = handler.getListener();
                if (listener != null) {
                    Log.d("API", "listener NOT null");
                    listener.onError(error);
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

    public interface OnGetListener<T> {
        void onSuccess(T res);

        void onError(final Exception error);
    }
}
