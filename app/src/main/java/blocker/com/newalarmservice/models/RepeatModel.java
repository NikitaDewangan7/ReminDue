package blocker.com.newalarmservice.models;

import java.io.Serializable;


public class RepeatModel implements Serializable {
   private long dueTime;
    private int paymentStatus;
    private int notificationStatus;

    public int getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(int notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    public RepeatModel()
    {

    }
    public RepeatModel(long dueTime, int paymentStatus) {
        this.dueTime = dueTime;
        this.paymentStatus = paymentStatus;
    }

    @Override
    public String toString() {
        return "RepeatModel{" +
                "dueTime=" + dueTime +
                ", paymentStatus=" + paymentStatus +
                '}';
    }

    public long getDueTime() {
        return dueTime;
    }

    public void setDueTime(long dueTime) {
        this.dueTime = dueTime;
    }

    public int getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(int paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
