package forexmanagement.models;

public class ActivePairsModel {
    private String currency;
    private String maAlreadyCrossed;

    public ActivePairsModel(String currency, String maAlreadyCrossed) {
        this.currency = currency;
        this.maAlreadyCrossed = maAlreadyCrossed;
    }

    public String getCurrency() {
        return currency;
    }

    public String getMaAlreadyCrossed() {
        return maAlreadyCrossed;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setMaAlreadyCrossed(String maAlreadyCrossed) {
        this.maAlreadyCrossed = maAlreadyCrossed;
    }
}
