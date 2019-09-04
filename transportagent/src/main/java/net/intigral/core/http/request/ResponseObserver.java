package net.intigral.core.http.request;

/**
 * Created by Farhan_a on 12/01/2016.
 */
public interface ResponseObserver {

    void onResponse(APIRequestID reqId, NetworkResponse responseMsg);
    void onFail(APIRequestID reqId, NetworkError networkError);
}
