package net.intigral.core.http.request;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Farhan_a on 12/01/2016.
 */
class RequestResponseBase {

    private Map<String, String> httpHeaders = new HashMap<>();
    private byte[] payLoad;
    private long timeStamp;

    public RequestResponseBase() {
        this.timeStamp = System.currentTimeMillis();
    }

    public Map<String, String> getHttpHeaders() {
        return httpHeaders;
    }

    public void setHttpHeaders(Map<String, String> httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public void addHeader(String key, String value) {
        httpHeaders.put(key, value);
    }

    public String getHeader(String key, String value) {
        return httpHeaders.get(key);
    }

    public byte[] getPayLoad() {
        return payLoad;
    }

    public void setPayLoad(byte[] payLoad) {
        this.payLoad = payLoad;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
