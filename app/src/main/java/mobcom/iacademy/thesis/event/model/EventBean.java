package mobcom.iacademy.thesis.event.model;

public class EventBean {
    private String id;
    private String event;
    private String description;
    private String timeStart;
    private String timeEnd;
    private String location;
    private Boolean isCompleted;
    private String dateStart;
    private String dateEnd;
    private String username;
    private boolean checked = false;
    private boolean isAllDay = false;
    private int year;
    private int day;
    private int month;

    public EventBean(String userId, String userEvent, String userContent, String userTimeStart, String userTimeEnd, String userLocation, String userDayStart, String userDayEnd, String username) {
        this.id = userId;
        this.event = userEvent;
        this.description = userContent;
        this.timeStart = userTimeStart;
        this.timeEnd = userTimeEnd;
        this.location = userLocation;
        this.dateStart = userDayStart;
        this.dateEnd = userDayEnd;
        this.username = username;
    }

    public EventBean(String userId, String userEvent, String userContent, String userTimeStart, String userTimeEnd, String userLocation, String userDayStart, String userDayEnd, String username, Boolean isAllDay, int year, int month, int day) {
        this.id = userId;
        this.event = userEvent;
        this.description = userContent;
        this.timeStart = userTimeStart;
        this.timeEnd = userTimeEnd;
        this.location = userLocation;
        this.dateStart = userDayStart;
        this.dateEnd = userDayEnd;
        this.username = username;
        this.isAllDay = isAllDay;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public boolean isAllDay() {
        return isAllDay;
    }

    public void setIsAllDay(boolean isAllDay) {
        this.isAllDay = isAllDay;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void toggleChecked() {
        checked = !checked;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }


    @Override
    public String toString() {
        return this.getEvent();
    }
}
