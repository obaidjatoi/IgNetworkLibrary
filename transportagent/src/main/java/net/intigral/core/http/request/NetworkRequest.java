package net.intigral.core.http.request;

import java.util.HashMap;
import java.util.Map;



/**
 * Created by Farhan_a on 12/01/2016.
 * <p/>
 * A Class will hold the data content of any server request need to be sent
 */
public class NetworkRequest extends RequestResponseBase {

    public enum HttpMethod {
        GET,
        PUT,
        POST,
        DELETE
    }

    public enum RequestType {
        SECURE,
        NORMAL,
        NORMAL_SEQ
    }

    private int cacheMaxAge;
    private String requestUrl;
    private HttpMethod httpMethod;
    private APIRequestID apiRequestId;
    private Map<String, String> queryParams = new HashMap<>();
    private Object tag;
    private RequestType requestType;

    public NetworkRequest(APIRequestID apiRequestId) {
        this.apiRequestId = apiRequestId;
        cacheMaxAge = -1;
        httpMethod = HttpMethod.GET;
        requestType = RequestType.NORMAL;
    }

    public APIRequestID getAPIRequestId() {
        return apiRequestId;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
        setRequestType(requestUrl);
    }

    public void setRequestUrl(String requestUrl,RequestType requestType) {
        this.requestUrl = requestUrl;
        setRequestType(requestType);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public void addQueryParam(String key, String value) {
        queryParams.put(key, value);
    }

    public void addQueryParams(Map<String, String> params) {
        queryParams.putAll(params);
    }

    public int getCacheMaxAge() {
        return cacheMaxAge;
    }

    public void setCacheMaxAge(int cacheMaxAge) {
        this.cacheMaxAge = cacheMaxAge;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public void forceNetwork() {
        cacheMaxAge = -1;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    private void setRequestType(String requestUrl) {
        if (requestUrl.contains(".intigral-ott.net") || requestUrl.contains(".intigral-i6.net"))
            requestType = RequestType.SECURE;
        else
            requestType = RequestType.NORMAL;
    }
}
