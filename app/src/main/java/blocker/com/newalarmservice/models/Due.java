package blocker.com.newalarmservice.models;


public class Due {
    private String payee, category, payentType, repeatEveryCatgory;
    private long dueDate, repeatUpto,amount;
    private int  reminderNotification, repeatEvery, repeatFlag,id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Due() {

    }

    public int getRepeatFlag() {
        return repeatFlag;
    }

    public void setRepeatFlag(int repeatFlag) {
        this.repeatFlag = repeatFlag;
    }

    @Override
    public String toString() {
        return "Due{" +
                "payee='" + payee + '\'' +
                ", category='" + category + '\'' +
                ", payentType='" + payentType + '\'' +
                ", repeatEveryCatgory='" + repeatEveryCatgory + '\'' +
                ", dueDate=" + dueDate +
                ", repeatUpto=" + repeatUpto +
                ", amount=" + amount +
                ", reminderNotification=" + reminderNotification +
                ", repeatEvery=" + repeatEvery +
                '}';
    }

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPayentType() {
        return payentType;
    }

    public void setPayentType(String payentType) {
        this.payentType = payentType;
    }

    public String getRepeatEveryCatgory() {
        return repeatEveryCatgory;
    }

    public void setRepeatEveryCatgory(String repeatEveryCatgory) {
        this.repeatEveryCatgory = repeatEveryCatgory;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public long getRepeatUpto() {
        return repeatUpto;
    }

    public void setRepeatUpto(long repeatUpto) {
        this.repeatUpto = repeatUpto;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public int getReminderNotification() {
        return reminderNotification;
    }

    public void setReminderNotification(int reminderNotification) {
        this.reminderNotification = reminderNotification;
    }

    public int getRepeatEvery() {
        return repeatEvery;
    }

    public void setRepeatEvery(int repeatEvery) {
        this.repeatEvery = repeatEvery;
    }
}
