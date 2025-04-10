package TCP;

import Enums.RequestType;

import java.io.Serializable;
import java.lang.foreign.SegmentAllocator;

public class Request implements Serializable {
    private RequestType requestType;
    private String requestMessage;

    public Request(RequestType requestType, String requestMessage) {
        this.requestType = requestType;
        this.requestMessage = requestMessage;
    }
    public Request(){

    }
    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }
}
