package ru.mail.park.lecture8;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TechnoparkService {
    @GET("user.get/{name}")
    Call<ResponseBody> getUser(@Path("name") String name);
}
