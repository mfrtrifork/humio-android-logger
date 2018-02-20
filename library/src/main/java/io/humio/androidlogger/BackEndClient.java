package io.humio.androidlogger;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class BackEndClient {
    private static BackEndClient instance;

    BackEndService getService() {
        return mService;
    }

    final private BackEndService mService;

    static BackEndClient getInstance() {
        return instance;
    }

    private BackEndClient(final String url, final String token, final boolean enableRequestLogging) {
        Interceptor httpHeaderInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("Authorization", "Bearer " + token)
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        };
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.addInterceptor(httpHeaderInterceptor);
        if (enableRequestLogging) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }
        OkHttpClient httpClient = builder.build();

        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return false;
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(url)
                .client(httpClient)
                .build();
        mService = retrofit.create(BackEndService.class);
    }

    static void setupInstance(String url, String token, final boolean enableRequestLogging) {
        instance = new BackEndClient(url, token, enableRequestLogging);
    }
}