package com.invaders99.service;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.invaders99.util.AppConfig;

public class FirebaseService {
    private static FirebaseService instance;

    public interface FirebaseCallback {
        void onSuccess(String response);
        void onFailure(String error);
    }

    private final String baseUrl;

    private FirebaseService(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public static void init() {
        instance = new FirebaseService(AppConfig.get().firebaseBaseUrl);
    }

    public static FirebaseService getInstance() {
        return instance;
    }

    public void testConnection(final FirebaseCallback callback) {
        HttpRequestBuilder builder = new HttpRequestBuilder();
        Net.HttpRequest request = builder
            .newRequest()
            .method(Net.HttpMethods.GET)
            .url(baseUrl + "/helloWorld")
            .build();

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                final String result = httpResponse.getResultAsString();
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(result);
                    }
                });
            }

            @Override
            public void failed(Throwable t) {
                final String msg = t.getMessage();
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure("Request failed: " + msg);
                    }
                });
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure("Request cancelled");
                    }
                });
            }
        });
    }
}
