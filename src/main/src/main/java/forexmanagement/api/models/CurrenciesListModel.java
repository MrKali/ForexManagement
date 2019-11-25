package forexmanagement.api.models;

public class CurrenciesListModel {
    private String id;
    private String name;
    private String decimal;
    private String symbol;

    public CurrenciesListModel(String id, String name, String decimal, String symbol) {
        this.id = id;
        this.name = name;
        this.decimal = decimal;
        this.symbol = symbol;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDecimal() {
        return decimal;
    }

    public String getSymbol() {
        return symbol;
    }
}
