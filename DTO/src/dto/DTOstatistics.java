package dto;
import statistics.StatisticRecord;
import problem.Problem;

import java.util.List;

public class DTOstatistics extends DTOstatus {
    private List<StatisticRecord> stats;


    public DTOstatistics(boolean isSucceeded, Problem details, List<StatisticRecord> stats) {
        super(isSucceeded, details);
        this.stats = stats;
    }

    public List<StatisticRecord> getStats() {
        return stats;
    }

    @Override
    public String toString() {
        return "DTOstatistics{" +
                "stats=" + stats +
                '}';
    }
}
