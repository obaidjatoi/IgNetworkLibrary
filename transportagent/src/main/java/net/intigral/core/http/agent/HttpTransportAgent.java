package net.intigral.core.http.agent;


import net.intigral.core.http.request.APIRequestID;
import net.intigral.core.http.request.NetworkError;
import net.intigral.core.http.request.NetworkRequest;
import net.intigral.core.http.request.NetworkResponse;
import net.intigral.core.http.request.ResponseObserver;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CertificatePinner;
import okhttp3.Dispatcher;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;


/**
 * Created by Farhan_a on 12/01/2016.
 * <p/>
 * Class that represents the core if the Library
 * It uses OKHttp Library to send HTTP requests
 */
class HttpTransportAgent implements TransportAgentBase {

    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("application/json; charset=utf-8");
    public static final int CONNECTION_TIME_OUT = 30;
//    public static final int READ_TIME_OUT = 30;
//    public static final int WRITE_TIME_OUT = 30;

    private HashMap<APIRequestID, Call> requestToCallMap;
    private HttpLoggingInterceptor loggingInterceptor;

    private static class Holder {
        private static final HttpTransportAgent INSTANCE = new HttpTransportAgent();
    }

    private OkHttpClient okHttpClient, secureOkHttpClient, okHttpClientSeq;

    private Dispatcher dispatcher;

    private CertificatePinner certPinner = new CertificatePinner.Builder()
            .add("*.intigral-ott.net",
                    "sha256/63HoyXjKDUJjmP8uCqaSj4bpOKd6lndnJiHss9O+A2o=")
            .add("*.intigral-ott.net",
                    "sha256/IQBnNBEiFuhj+8x6X8XLgh01V9Ic5/V3IRQLNFFc7v4=")
            .add("*.intigral-ott.net",
                    "sha256/K87oWBWM9UZfyddvDfoxL+8lpNyoUB2ptGtn0fv6G2Q=")
            .add("*.intigral-ott.net",
                    "sha256/4hw5tz+scE+TW+mlai5YipDfFWn1dqvfLG+nU7tq1V8=")
            .add("*.intigral-ott.net",
                    "sha256/Vjs8r4z+80wjNcr1YKepWQboSIRi63WsWXhIMN+eWys=")
            .add("*.intigral-ott.net",
                    "sha256/YLh1dUR9y6Kja30RrAn7JKnbQG/uEtLMkBgFF2Fuihg=")
            .add("*.intigral-ott.net",
                    "sha256/RAoCHD5MBfF6scGuUvYczfUsz8z8x0Wvmel0/3p5WWM=")
            .add("*.intigral-i6.net", "sha256/3WFmCh7lviK+v2ak7eptYFpAVASdihqArpNgA2TGWzE=")
            .add("*.intigral-i6.net", "sha256/IQBnNBEiFuhj+8x6X8XLgh01V9Ic5/V3IRQLNFFc7v4=")
            .add("*.intigral-i6.net", "sha256/K87oWBWM9UZfyddvDfoxL+8lpNyoUB2ptGtn0fv6G2Q=")
            .build();

    private HttpTransportAgent() {

        requestToCallMap = new HashMap<>();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        dispatcher = new Dispatcher();
        dispatcher.setMaxRequestsPerHost(1);
        dispatcher.setMaxRequests(1);

        okHttpClient = builder.connectTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS)
//                .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
//                .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                .build();
        secureOkHttpClient = builder.certificatePinner(certPinner)
                .connectTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS).build();

        okHttpClientSeq = builder.dispatcher(dispatcher)
                .connectTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS).build();
    }

    @Override
    public void init(File cacheDirectory, HTTPTransportAgentLogger logger) {

        OkHttpClient.Builder newBuilder = okHttpClient.newBuilder();
        OkHttpClient.Builder secureNewBuilder = secureOkHttpClient.newBuilder();
        OkHttpClient.Builder seqNewBuilder = okHttpClientSeq.newBuilder();
        boolean builderChanged = false;

        if (loggingInterceptor == null && logger != null) {
            loggingInterceptor = new HttpLoggingInterceptor(logger);
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            newBuilder.addInterceptor(loggingInterceptor);
            newBuilder
//                    .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
//                    .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                    .connectTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS);
            secureNewBuilder.addInterceptor(loggingInterceptor);
            secureNewBuilder.connectTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS);

            seqNewBuilder.addInterceptor(loggingInterceptor);
            seqNewBuilder.connectTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS);

            builderChanged = true;
        }

        if (okHttpClient.cache() == null || secureOkHttpClient.cache() == null) {
            int cacheSize = 10 * 1024 * 1024; // 10 MiB
            Cache cache = new Cache(cacheDirectory, cacheSize);
            if (okHttpClient.cache() == null) {
                newBuilder.cache(cache);
            } else {
                secureNewBuilder.cache(cache);
            }
            builderChanged = true;
        }

        if (builderChanged) {
            okHttpClient = newBuilder.connectTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS)
//                    .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
//                    .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                    .build();
            secureOkHttpClient = secureNewBuilder.certificatePinner(certPinner)
                    .connectTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS).build();

            okHttpClientSeq = seqNewBuilder.dispatcher(dispatcher)
                    .connectTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS).build();
        }
    }

    @Override
    public void send(final NetworkRequest networkRequest, final ResponseObserver observer) {

        if (getOkHttpClient(networkRequest.getRequestType()) == null)
            throw new IllegalStateException("Transport Agent not initialized yet, please call TransportAgentBase.init(File) first");

        try {

            //add query params
            HttpUrl baseURL = HttpUrl.parse(networkRequest.getRequestUrl());
            HttpUrl.Builder urlBuilder = baseURL.newBuilder();
            Set<String> params = networkRequest.getQueryParams().keySet();
            for (String paramKey : params) {
                urlBuilder.addQueryParameter(paramKey, networkRequest.getQueryParams().get(paramKey));
            }

            Request.Builder requestBuilder = new Request.Builder().url(urlBuilder.build());

            //add headers
            Set<String> headers = networkRequest.getHttpHeaders().keySet();
            for (String headerKey : headers) {
                requestBuilder.addHeader(headerKey, networkRequest.getHttpHeaders().get(headerKey));
            }

            //Add post only if there is a payload
            if (networkRequest.getHttpMethod() == NetworkRequest.HttpMethod.POST && networkRequest.getPayLoad() != null) {
                requestBuilder.post(RequestBody.create(MEDIA_TYPE_MARKDOWN, new String(networkRequest.getPayLoad())));
            }

            if (networkRequest.getHttpMethod() == NetworkRequest.HttpMethod.PUT && networkRequest.getPayLoad() != null) {
                requestBuilder.put(RequestBody.create(MEDIA_TYPE_MARKDOWN, new String(networkRequest.getPayLoad())));
            }

            if (networkRequest.getHttpMethod() == NetworkRequest.HttpMethod.DELETE) {
                requestBuilder.delete();
            }
            //cache
            int cacheMaxAge = networkRequest.getCacheMaxAge();
            if (cacheMaxAge != -1) {
                requestBuilder.cacheControl(CacheControl.FORCE_CACHE);
                requestBuilder.cacheControl(new CacheControl.Builder().maxAge(cacheMaxAge, TimeUnit.SECONDS).build());
            } else {
                requestBuilder.cacheControl(CacheControl.FORCE_NETWORK);
            }

            Call call = getOkHttpClient(networkRequest.getRequestType()).newCall(requestBuilder.build());
            requestToCallMap.put(networkRequest.getAPIRequestId(), call);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                    NetworkError networkError = new NetworkError();

                    if (e instanceof SocketTimeoutException) {
                        networkError.setErrorCode(NetworkError.TIMEOUT_ERROR_CODE);
                    } else {
                        networkError.setErrorCode(NetworkError.NETWORK_ERROR_CODE);
                    }
                    networkError.setCause(e);

                    observer.onFail(networkRequest.getAPIRequestId(), networkError);
                    requestToCallMap.remove(networkRequest.getAPIRequestId());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    NetworkResponse responseMsg = toNetworkResponse(response);
                    observer.onResponse(networkRequest.getAPIRequestId(), responseMsg);
                    requestToCallMap.remove(networkRequest.getAPIRequestId());
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            NetworkError networkError = new NetworkError();
            networkError.setErrorCode(NetworkError.UNKNOWN_ERROR_CODE);
            networkError.setCause(throwable);

            observer.onFail(networkRequest.getAPIRequestId(), networkError);
            requestToCallMap.remove(networkRequest.getAPIRequestId());
        }

    }

    @Override
    public void cancelRequest(APIRequestID requestID) {
        Call call = requestToCallMap.get(requestID);
        if (call != null) {
            call.cancel();
        }
    }

    private OkHttpClient getOkHttpClient(NetworkRequest.RequestType requestType) {
        if (requestType == NetworkRequest.RequestType.SECURE) {
            return secureOkHttpClient;
        } else if (requestType == NetworkRequest.RequestType.NORMAL_SEQ) {
            return okHttpClientSeq;
        }
        return okHttpClient;
    }

    /**
     * Method to fill the response Headers, Payload, and the HTTP status code
     *
     * @param response native response object
     * @return our Network Response Object
     */

    private NetworkResponse toNetworkResponse(Response response) {

        NetworkResponse responseMsg = new NetworkResponse();
        try {
            Headers responseHeader = response.headers();
            for (String headerName : responseHeader.names()) {
                String headerValue = responseHeader.get(headerName);
                responseMsg.addHeader(headerName, headerValue);
            }
            responseMsg.setOriginalRequestURL(response.request().url().toString());
            responseMsg.setHttpCode(response.code());
            responseMsg.setPayLoad(response.body().bytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseMsg;
    }

    public static TransportAgentBase instance() {
        return Holder.INSTANCE;
    }

}
