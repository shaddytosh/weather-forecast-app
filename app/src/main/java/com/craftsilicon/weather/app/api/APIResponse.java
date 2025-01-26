package com.craftsilicon.weather.app.api;

import java.io.IOException;

import okhttp3.Headers;
import retrofit2.Response;

public class APIResponse<T> {
    private final T body;
    private final Headers headers;
    private String errorBody;
    private final int code;
    private final boolean successful;

    public APIResponse(Response<T> response) {
        if (response != null) {
            this.body = response.body();
            this.code = response.code();
            this.successful = response.isSuccessful();
            this.headers = response.headers();
            try {
                //noinspection ConstantConditions
                this.errorBody = response.errorBody().string();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
                this.errorBody = null;
            }
        } else {
            this.body = null;
            this.code = 0;
            this.successful = false;
            this.headers = null;
            this.errorBody = "No connection to our server";
        }
    }

    public boolean isSuccessful() {
        return successful;
    }

    public Headers headers() {
        return this.headers;
    }

    public T body() {
        return body;
    }

    public int code() {
        return code;
    }

    public String errorBody() {
        return errorBody;
    }

    public boolean serverError() {
        return code >= 500;
    }

    public boolean badRequest() {
        return code == 400;
    }

    public boolean authenticationError() {
        return code == 401;
    }

    public boolean authorizationError() {
        return code == 403;
    }


}
