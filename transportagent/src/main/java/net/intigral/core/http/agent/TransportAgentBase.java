package net.intigral.core.http.agent;

import net.intigral.core.http.request.APIRequestID;
import net.intigral.core.http.request.NetworkRequest;
import net.intigral.core.http.request.ResponseObserver;

import java.io.File;

import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by Farhan_a on 12/01/2016.
 */
public interface TransportAgentBase {

    void init(File cacheDirectory, HTTPTransportAgentLogger logger);
    void send(NetworkRequest request, final ResponseObserver observer);
    void cancelRequest(APIRequestID requestID);

    interface HTTPTransportAgentLogger extends HttpLoggingInterceptor.Logger {

    }

}
