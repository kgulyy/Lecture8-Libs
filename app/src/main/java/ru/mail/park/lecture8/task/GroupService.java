package ru.mail.park.lecture8.task;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GroupService {
    @GET("community/{id}")
    Call<ResponseBody> getGroup(@Path("id") int id);
}
