package forexmanagement.ui;

import forexmanagement.Config;
import forexmanagement.Strategies;
import forexmanagement.api.ApiRequests;
import forexmanagement.models.ActivePairsModel;
import forexmanagement.models.CurrenciesListModel;
import forexmanagement.models.ListViewStatusModel;
import forexmanagement.models.TableViewWatchListModel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Handler;

public class MainController implements Initializable {

    static private String BUY = "Buy";
    static private String SELL = "Sell";
    static private String UNDEFINED = "Undefined";

    // save all the currencies available to make search
    private ObservableList<String> itemsAvailable = FXCollections.observableArrayList();

    // save the currencies selected to get status
    private ObservableList<String> itemsSelected = FXCollections.observableArrayList();

    // list with the result of search of all the selected currencies
    private ObservableList<ListViewStatusModel> statusList = FXCollections.observableArrayList();

    // list with watch list models
    private ObservableList<TableViewWatchListModel> tableViewWatchListModels = FXCollections.observableArrayList();

    // list with options of operations
    private ObservableList<String> nextEntryList =  FXCollections.observableArrayList();

    // save the active pairs
    private ObservableList<ActivePairsModel> activePairs = FXCollections.observableArrayList();

    @FXML
    private Text textViewProgress;

    // Table View current status
    @FXML
    private TableColumn<ListViewStatusModel, String> strategyColumn;

    @FXML
    private TableColumn<ListViewStatusModel, String> currencyColumn;

    @FXML
    private TableColumn<ListViewStatusModel, String> statusColumn;

    @FXML
    private TableColumn<ListViewStatusModel, String> trendColumn;

    @FXML
    private TableView<ListViewStatusModel> tableViewStatus;

    // Table view watch list
    @FXML
    private TableView<TableViewWatchListModel> tableViewWatchList;

    @FXML
    private TableColumn<TableViewWatchListModel, String> watchCurrencyColumn;

    @FXML
    private TableColumn<TableViewWatchListModel, String> watchNotesColumn;

    @FXML
    private TableColumn<TableViewWatchListModel, String> watchNextEntryColumn;

    @FXML
    private TableColumn<TableViewWatchListModel, String> watchStatusColumn;

    // List view to pick active currencies
    @FXML
    private ListView<String> listViewCurrenciesAvailable;

    @FXML
    private ListView<String> listViewCurrenciesActive;

    // Table view active pairs
    @FXML
    private TableView<ActivePairsModel> tableViewActivePairs;

    @FXML
    private TableColumn<ActivePairsModel, String> activePairsLastUpdate;

    @FXML
    private TableColumn<ActivePairsModel, String> activePairsCurrency;

    public void initialize(URL location, ResourceBundle resources) {
        setupTableViewStatus();
        setupTableViewWatchList();
        setupAvailableCurrenciesList();
        setupActiveCurrenciesList();
        setupTableViewActivePairs();
    }

    @FXML
    void onBtnRemoveFromWatchList() {
        removeItemFromWatchList();
    }

    @FXML
    void addToWatchList() {
        addCurrencyToWatchList();
    }

    @FXML
    void onBtnActiveAll() {
        itemsSelected.addAll(itemsAvailable);
        itemsAvailable.clear();
    }

    @FXML
    void onBtnUpdateCurrencies() {
        Platform.runLater(this::updateCurrenciesLists);
    }

    @FXML
    void onBtnAddToActiveListClick() {
        addToActiveList();
    }

    @FXML
    void onBtnRemoveFromActiveList() {
        removeFromActiveList();
    }

    @FXML
    void onBtnCheckStatus() {
        checkCurrenciesStatus();
    }

    @FXML
    void clearTableViewStatus() {
        statusList.clear();
        textViewProgress.setText("");
    }

    @FXML
    void onBtnRemoveActivePair() {
        removeFromActivePairs();
    }

    /**
     * To update the progress of getting status
     * */
    private void updateProgressOfStatusSearching(int progress, int total){
        textViewProgress.setText("Progress: " + progress + "/" + total);
    }

    /**
     * Remove the selected item from watch list
     * */
    private void removeItemFromWatchList(){
        tableViewWatchListModels.remove(tableViewWatchList.getSelectionModel().getSelectedItem());
    }

    /**
     * First, will clear the active and available lists.
     * Then, will make an API request to get a list of all the currencies available
     * Finally, will populate available list
     * */
    private void updateCurrenciesLists(){
        Config.activeCurrencies.clear();
        itemsSelected.clear();
        itemsAvailable.clear();

        ArrayList<CurrenciesListModel> currenciesListModels = ApiRequests.getListOfCurrencies();

        if (currenciesListModels != null){
            for (int i = 0; i < currenciesListModels.size(); i++){
                itemsAvailable.add(currenciesListModels.get(i).getSymbol());

                if (i == currenciesListModels.size() - 1){
                    listViewCurrenciesAvailable.setItems(itemsAvailable);
                }
            }
        }
    }

    /**
     * Will get a model from status list, and will create a new item in watch list
     * */
    private void addCurrencyToWatchList(){
        ListViewStatusModel l = tableViewStatus.getSelectionModel().getSelectedItem();
        String nextEntry;
        if (l.getTrend().equals(Strategies.TREND_DOWN)){
            nextEntry = SELL;
        }else if (l.getTrend().equals(Strategies.TREND_UP)){
            nextEntry = BUY;
        }else {
            nextEntry = UNDEFINED;
        }

        TableViewWatchListModel t = new TableViewWatchListModel(l.getSymbol(), nextEntry, l.getStatus(), "");

        tableViewWatchListModels.add(t);
    }

    /**
     * Will get the selected items in available list and will put them in active list
     * */
    private void addToActiveList(){
        ObservableList<String> selectedCurrencies = listViewCurrenciesAvailable.getSelectionModel().getSelectedItems();

        Config.activeCurrencies.addAll(selectedCurrencies);
        itemsSelected.addAll(selectedCurrencies);
        itemsAvailable.removeAll(selectedCurrencies);

        listViewCurrenciesAvailable.getSelectionModel().clearSelection();
    }


    /**
     * Will make a request for each selected currency, and display result in status list
     * Interval of 7 seconds between each request to avoid overload the server
     * */
    private void checkCurrenciesStatus(){
        statusList.clear();

        Runnable runnable = () -> {
            for (int i = 0; i < Config.activeCurrencies.size(); i++){
                try {
                    if (!addItemToStatusList(i)){
                        Thread.sleep(60000);
                        addItemToStatusList(i);
                    }
                    Thread.sleep(7000);

                    if (i == Config.activeCurrencies.size() - 1){
                        checkAndAddToActivePairs();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    /**
     * Will remove item from active list and will add it again to available list
     * */
    private void removeFromActiveList(){
        itemsAvailable.addAll(listViewCurrenciesActive.getSelectionModel().getSelectedItems());
        Config.activeCurrencies.removeAll(listViewCurrenciesActive.getSelectionModel().getSelectedItems());
        itemsSelected.removeAll(listViewCurrenciesActive.getSelectionModel().getSelectedItems());
    }

    /**
     * Will add the item to the status list
     * */
    private boolean addItemToStatusList(int i){
        Strategies.Strategy1.Model m = ApiRequests.getMa(Config.activeCurrencies.get(i));
        if (m != null){
            Strategies.Strategy1 s = new Strategies.Strategy1(m);
            statusList.add(new ListViewStatusModel(Config.activeCurrencies.get(i), s.checkStatus(), s.checkTrend(), "MA1"));
            updateProgressOfStatusSearching(i + 1, Config.activeCurrencies.size());
            return true;
        }else {
            return false;
        }

    }

    /**
     * Function to allow select multiple items in a list view
     * */
    private static void allowMultipleSelection(ListView listView){
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        listView.addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
            Node node = evt.getPickResult().getIntersectedNode();

            // go up from the target node until a list cell is found or it's clear
            // it was not a cell that was clicked
            while (node != null && node != listView && !(node instanceof ListCell)) {
                node = node.getParent();
            }

            // if is part of a cell or the cell,
            // handle event instead of using standard handling
            if (node instanceof ListCell) {
                // prevent further handling
                evt.consume();

                ListCell cell = (ListCell) node;
                ListView lv = cell.getListView();

                // focus the list view
                lv.requestFocus();

                if (!cell.isEmpty()) {
                    // handle selection for non-empty cells
                    int index = cell.getIndex();
                    if (cell.isSelected()) {
                        lv.getSelectionModel().clearSelection(index);
                    } else {
                        lv.getSelectionModel().select(index);
                    }
                }
            }
        });

    }

    /**
     * Will setup the status table view
     * */
    private void setupTableViewStatus(){
        currencyColumn.setCellValueFactory(new PropertyValueFactory<>("symbol"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        trendColumn.setCellValueFactory(new PropertyValueFactory<>("trend"));
        strategyColumn.setCellValueFactory(new PropertyValueFactory<>("strategy"));

        tableViewStatus.setItems(statusList);
    }

    /**
     * Will setup the watch list table view
     * */
    private void setupTableViewWatchList(){
        nextEntryList.add(BUY);
        nextEntryList.add(SELL);
        nextEntryList.add(UNDEFINED);

        watchCurrencyColumn.setCellValueFactory(new PropertyValueFactory<>("currency"));
        watchNextEntryColumn.setCellValueFactory(new PropertyValueFactory<>("nextEntry"));
        watchStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        watchNotesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));

        tableViewWatchList.setItems(tableViewWatchListModels);
        tableViewWatchList.setEditable(true);
        watchNotesColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        watchNextEntryColumn.setCellFactory(ComboBoxTableCell.forTableColumn(nextEntryList));

        watchNotesColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<TableViewWatchListModel, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<TableViewWatchListModel, String> event) {
                String currency = tableViewWatchListModels.get(event.getTablePosition().getRow()).getCurrency();
                String nextEntry = tableViewWatchListModels.get(event.getTablePosition().getRow()).getNextEntry();
                String status = tableViewWatchListModels.get(event.getTablePosition().getRow()).getStatus();
                String notes = event.getNewValue();

                System.out.println(currency);
                System.out.println(nextEntry);
                System.out.println(status);
                System.out.println(notes);
                System.out.println(event.getTablePosition().getRow());

                TableViewWatchListModel t = new TableViewWatchListModel(currency, nextEntry, status, notes);
                tableViewWatchListModels.set(event.getTablePosition().getRow(), t);

            }
        });
    }

    /**
     * Will setup available currencies list
     * */
    private void setupAvailableCurrenciesList(){
        allowMultipleSelection(listViewCurrenciesAvailable);

        Task task = new Task() {
            @Override
            protected Object call() {
                updateCurrenciesLists();
                return null;
            }
        };

        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    /**
     * Will setup active currencies list
     * */
    private void setupActiveCurrenciesList(){
        allowMultipleSelection(listViewCurrenciesActive);
        listViewCurrenciesActive.setItems(itemsSelected);
    }

    /**
     * Will setup the table view of active pairs
     * */
    private void setupTableViewActivePairs(){
        activePairsCurrency.setCellValueFactory(new PropertyValueFactory<>("currency"));
        activePairsLastUpdate.setCellValueFactory(new PropertyValueFactory<>("maAlreadyCrossed"));

        tableViewActivePairs.setItems(activePairs);
    }

    /**
     * Will remove item from active pairs
     * */
    private void removeFromActivePairs(){
        activePairs.remove(tableViewActivePairs.getSelectionModel().getSelectedItem());
    }

    /**
     * Check if currency is already in active list.
     * If not and MA isn't crossed yet, add to the active list
     * If is already in the list, check if status is still MA not crossed yet
     * */
    private void checkAndAddToActivePairs(){
        for (ActivePairsModel activePair : activePairs) {
            boolean foundInNewList = false;

            for (int z = 0; z < statusList.size(); z++) {
                if (activePair.getCurrency().equals(statusList.get(z).getSymbol())) {
                    foundInNewList = true;
                    activePair.setMaAlreadyCrossed("False");
                }

                if (z == statusList.size() - 1) {
                    if (!foundInNewList) activePair.setMaAlreadyCrossed("Yes");
                }
            }
        }

        for (ListViewStatusModel listViewStatusModel : statusList) {
            boolean foundInTheList = false;

            if (activePairs.size() > 0) {
                for (int z = 0; z < activePairs.size(); z++) {
                    if (listViewStatusModel.getSymbol().equals(activePairs.get(z).getCurrency())) {
                        foundInTheList = true;
                    }

                    if (z == activePairs.size() - 1) {
                        if (!foundInTheList && listViewStatusModel.getStatus().equals(Strategies.Strategy1.MA_NOT_CROSS_YET)) {
                            ActivePairsModel l = new ActivePairsModel(
                                    listViewStatusModel.getSymbol(),
                                    "False");

                            activePairs.add(l);
                        }
                    }
                }
            } else {
                if (listViewStatusModel.getStatus().equals(Strategies.Strategy1.MA_NOT_CROSS_YET)) {
                    ActivePairsModel l = new ActivePairsModel(
                            listViewStatusModel.getSymbol(),
                            "False");

                    activePairs.add(l);
                }
            }
        }
    }
}
