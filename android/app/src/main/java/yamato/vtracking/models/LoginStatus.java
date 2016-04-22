package yamato.vtracking.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

/**
 * Created by Gison on 8/4/16.
 */
@Generated("org.jsonschema2pojo")
public class LoginStatus {
    @SerializedName("handset_user_id")
    @Expose
    private String handsetUserId;
    @SerializedName("work_id")
    @Expose
    private String workId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("status")
    @Expose
    private String status;

    /**
     * @return The handsetUserId
     */
    public String getHandsetUserId() {
        return handsetUserId;
    }

    /**
     * @param handsetUserId The handset_user_id
     */
    public void setHandsetUserId(String handsetUserId) {
        this.handsetUserId = handsetUserId;
    }

    /**
     * @return The workId
     */
    public String getWorkId() {
        return workId;
    }

    /**
     * @param workId The work_id
     */
    public void setWorkId(String workId) {
        this.workId = workId;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
