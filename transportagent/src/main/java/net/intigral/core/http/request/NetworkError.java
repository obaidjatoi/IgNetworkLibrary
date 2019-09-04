package net.intigral.core.http.request;

import java.io.Serializable;

/**
 * Created by Simon Gerges on 3/13/16.
 * <p>
 */
public class NetworkError implements Serializable {

    public static final String PARSING_ERROR_CODE = "PARSING_ERROR_CODE";
    public static final String NETWORK_ERROR_CODE = "NETWORK_ERROR_CODE";
    public static final String TIMEOUT_ERROR_CODE = "900";
    public static final String PUBNUB_ERROR_CODE = "PUBNUB_ERROR_CODE";
    public static final String UNKNOWN_ERROR_CODE = "UNKNOWN_ERROR_CODE";
    public static final String USER_DOESNOT_EXIST = "9932";

    private Throwable cause;
    private String errorCode;
    private String errorMessage;
    private String serverRequestID;
    private long timeStamp;

    public NetworkError() {
        this.timeStamp = System.currentTimeMillis();
    }

    public NetworkError(String errorCode) {
        this.errorCode = errorCode;
    }

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getServerRequestID() {
        return serverRequestID;
    }

    public void setServerRequestID(String serverRequestID) {
        this.serverRequestID = serverRequestID;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public static NetworkError buildNetworkError(String errorCode, String errorMessage) {
        NetworkError error = new NetworkError();
        error.setErrorCode(errorCode);
        error.setErrorMessage(errorMessage);
        return error;
    }
}
