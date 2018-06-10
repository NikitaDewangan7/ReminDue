package blocker.com.newalarmservice.models;

import java.util.Comparator;


public class DueUpcomingModelDateComparator implements Comparator<DueUpcomingModel> {
    @Override
    public int compare(DueUpcomingModel lhs, DueUpcomingModel rhs) {
        if (lhs.getDuedate() > rhs.getDuedate()) {
            return 1;
        } else if (lhs.getDuedate() < rhs.getDuedate()) {
            return -1;
        } else
            return 0;

    }
}
