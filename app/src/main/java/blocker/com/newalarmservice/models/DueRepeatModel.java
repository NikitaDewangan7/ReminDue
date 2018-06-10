package blocker.com.newalarmservice.models;

import java.util.Arrays;


public class DueRepeatModel  {
    int id;
    long[]repeatTimesArray;
    int[] paidUnpaidArray;

    public DueRepeatModel()
    {

    }
    public DueRepeatModel(int id, long[] repeatTimesArray, int[] paidUnpaidArray) {
        this.id = id;
        this.repeatTimesArray = repeatTimesArray;
        this.paidUnpaidArray = paidUnpaidArray;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long[] getRepeatTimesArray() {
        return repeatTimesArray;
    }

    public void setRepeatTimesArray(long[] repeatTimesArray) {
        this.repeatTimesArray = repeatTimesArray;
    }

    public int[] getPaidUnpaidArray() {
        return paidUnpaidArray;
    }

    public void setPaidUnpaidArray(int[] paidUnpaidArray) {
        this.paidUnpaidArray = paidUnpaidArray;
    }

    @Override
    public String toString() {
        return "DueRepeatModel{" +
                "id=" + id +
                ", repeatTimesArray=" + Arrays.toString(repeatTimesArray) +
                ", paidUnpaidArray=" + Arrays.toString(paidUnpaidArray) +
                '}';
    }
}
