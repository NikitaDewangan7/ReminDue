package blocker.com.newalarmservice.models;

import java.io.Serializable;
import java.util.ArrayList;


public class DueUpcomingModel implements Serializable {
    private String payeeName, dueCategory, dueType, dueRepeatEveryCategory;
    private long duedate, repeatupto, dueAmount;
    private int dueReminderNotification, dueRepeatEvery, repeatFlag, id, paymentStatus;
    private ArrayList<RepeatModel> repeatModelArrayList;
    private long paymentDate;
    private int notificationFlag;


    public int getNotificationFlag() {
        return notificationFlag;
    }

    public void setNotificationFlag(int notificationFlag) {
        this.notificationFlag = notificationFlag;
    }

    public long getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(long paymentDate) {
        this.paymentDate = paymentDate;
    }

    public long getDueAmount() {
        return dueAmount;
    }

    public void setDueAmount(long dueAmount) {
        this.dueAmount = dueAmount;
    }

    public ArrayList<RepeatModel> getRepeatModelArrayList() {
        return repeatModelArrayList;
    }

    public void setRepeatModelArrayList(ArrayList<RepeatModel> repeatModelArrayList) {
        this.repeatModelArrayList = repeatModelArrayList;
    }

    public DueUpcomingModel() {

    }

    public int getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(int paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public DueUpcomingModel(String payeeName, String dueCategory, String dueType, String dueRepeatEveryCategory, long duedate, long repeatupto, int dueReminderNotification, int dueRepeatEvery, int repeatFlag, int id) {
        this.payeeName = payeeName;
        this.dueCategory = dueCategory;
        this.dueType = dueType;
        this.dueRepeatEveryCategory = dueRepeatEveryCategory;
        this.duedate = duedate;
        this.repeatupto = repeatupto;
        this.dueReminderNotification = dueReminderNotification;
        this.dueRepeatEvery = dueRepeatEvery;
        this.repeatFlag = repeatFlag;
        this.id = id;
    }

    @Override
    public String toString() {
        return "DueUpcomingModel{" +
                "payeeName='" + payeeName + '\'' +
                ", dueCategory='" + dueCategory + '\'' +
                ", dueType='" + dueType + '\'' +
                ", dueRepeatEveryCategory='" + dueRepeatEveryCategory + '\'' +
                ", duedate=" + duedate +
                ", repeatupto=" + repeatupto +
                ", dueReminderNotification=" + dueReminderNotification +
                ", dueRepeatEvery=" + dueRepeatEvery +
                ", repeatFlag=" + repeatFlag +
                ", id=" + id +
                ", paymentStatus=" + paymentStatus +
                ", dueAmount=" + dueAmount +
                ", repeatModelArrayList=" + repeatModelArrayList +
                '}';
    }

    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    public String getDueCategory() {
        return dueCategory;
    }

    public void setDueCategory(String dueCategory) {
        this.dueCategory = dueCategory;
    }

    public String getDueType() {
        return dueType;
    }

    public void setDueType(String dueType) {
        this.dueType = dueType;
    }

    public String getDueRepeatEveryCategory() {
        return dueRepeatEveryCategory;
    }

    public void setDueRepeatEveryCategory(String dueRepeatEveryCategory) {
        this.dueRepeatEveryCategory = dueRepeatEveryCategory;
    }

    public long getDuedate() {
        return duedate;
    }

    public void setDuedate(long duedate) {
        this.duedate = duedate;
    }

    public long getRepeatupto() {
        return repeatupto;
    }

    public void setRepeatupto(long repeatupto) {
        this.repeatupto = repeatupto;
    }

    public int getDueReminderNotification() {
        return dueReminderNotification;
    }

    public void setDueReminderNotification(int dueReminderNotification) {
        this.dueReminderNotification = dueReminderNotification;
    }

    public int getDueRepeatEvery() {
        return dueRepeatEvery;
    }

    public void setDueRepeatEvery(int dueRepeatEvery) {
        this.dueRepeatEvery = dueRepeatEvery;
    }

    public int getRepeatFlag() {
        return repeatFlag;
    }

    public void setRepeatFlag(int repeatFlag) {
        this.repeatFlag = repeatFlag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
