package daedeoksw.dimigo.xtream;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by JunHyeok on 2016. 11. 19..
 */

public interface SendFile {
    @Multipart
    @POST("upload")
    Call<ResponseBody> upload(@Part("description") RequestBody description,
                              @Part MultipartBody.Part file);
}
