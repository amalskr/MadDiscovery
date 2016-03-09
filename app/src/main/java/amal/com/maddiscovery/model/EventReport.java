package amal.com.maddiscovery.model;

/**
 * Created by Shazeen-PC on 3/5/2016.
 */
public class EventReport {

    private int eventId;
    private String message;
    private String reportDateTime;


    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReportDateTime() {
        return reportDateTime;
    }

    public void setReportDateTime(String reportDateTime) {
        this.reportDateTime = reportDateTime;
    }


}
