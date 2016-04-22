package yamato.vtracking.network;

import okhttp3.OkHttpClient;

/**
 * A singleton class for
 * Created by Gison on 8/4/16.
 */
public class HttpClient {
    private static HttpClient mInstance = null;

    private OkHttpClient client;

    private HttpClient (){
        this.client = new OkHttpClient();
    }

    private OkHttpClient getHttpClient() {
        return this.client;
    }

    public static OkHttpClient getInstance(){
        if(mInstance == null) {
            mInstance = new HttpClient();
        }
        return mInstance.getHttpClient();
    }
}
