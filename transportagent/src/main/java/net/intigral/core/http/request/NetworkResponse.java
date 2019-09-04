package net.intigral.core.http.request;

/**
 * Created by Farhan_a on 12/01/2016.
 */
public class NetworkResponse extends RequestResponseBase {

    private int httpCode;
    private String httpStatusLine;
    private String originalRequestURL;

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public String getHttpStatusLine() {
        return httpStatusLine;
    }

    public void setHttpStatusLine(String httpStatusLine) {
        this.httpStatusLine = httpStatusLine;
    }

    public String getOriginalRequestURL() {
        return originalRequestURL;
    }

    public void setOriginalRequestURL(String originalRequestURL) {
        this.originalRequestURL = originalRequestURL;
    }
}
