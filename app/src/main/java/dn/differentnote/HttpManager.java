package dn.differentnote;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpManager {
    public static void sendOkHttpRequest(String address, RequestBody requestBody, Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }
    public static JSONArray forGetJSONArray(String address, RequestBody requestBody) throws IOException, JSONException {
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();//得到Response 对象
        JSONObject result=new JSONObject(response.body().string());
        return result.getJSONArray("data");
    }

}
