package oracle.alpha.cloud;

import java.io.Serializable;

public class EMIaaSMessage implements Serializable {
    private String method;
    private String imageName;
    private String sQueueId;
    private String sBusinessServiceId;
    
    public EMIaaSMessage() {
        super();
    }

    public EMIaaSMessage(String sQueueId) {
        super();
        this.sQueueId = sQueueId;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setSQueueId(String sQueueId) {
        this.sQueueId = sQueueId;
    }

    public String getSQueueId() {
        return sQueueId;
    }

    public void setSBusinessServiceId(String sBusinessServiceId) {
        this.sBusinessServiceId = sBusinessServiceId;
    }

    public String getSBusinessServiceId() {
        return sBusinessServiceId;
    }

    @Override
    public String toString () {
        return ("Method: " + this.getMethod() + "  ImageName: " + this.getImageName() + "  sQueueId: " + this.getSQueueId() + "sBusinessServiceId: " + this.getSBusinessServiceId());
    }
}
