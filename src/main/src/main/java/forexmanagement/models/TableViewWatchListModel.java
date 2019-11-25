package forexmanagement.models;

public class TableViewWatchListModel {
    private String currency;
    private String nextEntry;
    private String status;
    private String notes;

    public TableViewWatchListModel(String currency, String nextEntry, String status, String notes) {
        this.currency = currency;
        this.nextEntry = nextEntry;
        this.status = status;
        this.notes = notes;
    }

    public String getCurrency() {
        return currency;
    }

    public String getNextEntry() {
        return nextEntry;
    }

    public String getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }
}
