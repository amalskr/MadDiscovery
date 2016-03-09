package amal.com.maddiscovery.model;

/**
 * Created by Shazeen-PC on 3/5/2016.
 */
public class MadEvent {

    private int id;
    private String eventName;
    private String eventDate;
    private String eventTime;
    private String eventOrganizer;
    private String eventLocation;
    private String eventPicturePath;
    private String isEventEnd;

    // Empty constructor
    public MadEvent() {
    }

    // constructor
    public MadEvent(String event_Name, String event_Date, String event_Time, String event_Organizer, String event_Location) {
        this.eventName = event_Name;
        this.eventDate = event_Date;
        this.eventTime = event_Time;
        this.eventOrganizer = event_Organizer;
        this.eventLocation = event_Location;
    }


    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventOrganizer() {
        return eventOrganizer;
    }

    public void setEventOrganizer(String eventOrganizer) {
        this.eventOrganizer = eventOrganizer;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEventPicturePath() {
        return eventPicturePath;
    }

    public void setEventPicturePath(String eventPicturePath) {
        this.eventPicturePath = eventPicturePath;
    }

    public String getIsEventEnd() {
        return isEventEnd;
    }

    public void setIsEventEnd(String isEventEnd) {
        this.isEventEnd = isEventEnd;
    }

}
