package mobcom.iacademy.thesis.model;

import com.prolificinteractive.materialcalendarview.CalendarDay;

/**
 * Created by user on 28/01/2016.
 */
public class DayBean
{
    String event;
    int year;
    int dayNow;
    int month;
    private boolean checked = false;
    String userId;

    public DayBean(String objId, String event, int year, int month, int dayNow) {
        this.userId = objId;
        this.event = event;
        this.year = year;
        this.dayNow = dayNow;
        this.month = month;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getEvent() {
        return event;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getDayNow() {
        return dayNow;
    }

    public void setDayNow(int dayNow) {
        this.dayNow = dayNow;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }
}
