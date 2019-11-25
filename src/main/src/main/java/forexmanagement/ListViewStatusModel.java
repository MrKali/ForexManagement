package forexmanagement;

import javafx.beans.property.SimpleStringProperty;

public class ListViewStatusModel {
    private String  symbol;
    private String  status;
    private String trend;
    private String  strategy;

    public ListViewStatusModel(String symbol, String status, String trend, String strategy) {
        this.symbol = symbol;
        this.status = status;
        this.trend = trend;
        this.strategy = strategy;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getStatus() {
        return status;
    }

    public String getTrend() {
        return trend;
    }

    public String getStrategy() {
        return strategy;
    }
}
