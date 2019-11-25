package forexmanagement;

import forexmanagement.api.ApiRequests;
import forexmanagement.api.models.CurrenciesListModel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Controller implements Initializable {

    private ObservableList<String> itemsAvailable = FXCollections.observableArrayList();
    private ObservableList<String> itemsSelected = FXCollections.observableArrayList();
    private ObservableList<ListViewStatusModel> statusList = FXCollections.observableArrayList();

    @FXML
    private TableColumn<ListViewStatusModel, String> strategyColumn;

    @FXML
    private TableColumn<ListViewStatusModel, String> currencyColumn;

    @FXML
    private TableView<ListViewStatusModel> tableViewStatus;

    @FXML
    private TableColumn<ListViewStatusModel, String> statusColumn;

    @FXML
    private TableColumn<ListViewStatusModel, String> trendColumn;


    @FXML
    private ListView<String> listViewCurrenciesAvailable;

    @FXML
    private ListView<String> listViewCurrenciesActive;

    public void initialize(URL location, ResourceBundle resources) {

        currencyColumn.setCellValueFactory(
                new PropertyValueFactory<>("symbol"));
        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>("status"));
        trendColumn.setCellValueFactory(
                new PropertyValueFactory<>("trend"));
        strategyColumn.setCellValueFactory(
                new PropertyValueFactory<>("strategy"));

        tableViewStatus.setItems(statusList);

        listViewCurrenciesAvailable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                ArrayList<CurrenciesListModel> currenciesListModels = ApiRequests.getListOfCurrencies();

                if (currenciesListModels != null){
                    for (int i = 0; i < currenciesListModels.size(); i++){
                        itemsAvailable.add(currenciesListModels.get(i).getSymbol());

                        if (i == currenciesListModels.size() - 1){
                            listViewCurrenciesAvailable.setItems(itemsAvailable);
                        }
                    }
                }

                return null;
            }
        };

        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();

        listViewCurrenciesAvailable.addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
            Node node = evt.getPickResult().getIntersectedNode();

            // go up from the target node until a list cell is found or it's clear
            // it was not a cell that was clicked
            while (node != null && node != listViewCurrenciesAvailable && !(node instanceof ListCell)) {
                node = node.getParent();
            }

            // if is part of a cell or the cell,
            // handle event instead of using standard handling
            if (node instanceof ListCell) {
                // prevent further handling
                evt.consume();

                ListCell cell = (ListCell) node;
                ListView lv = cell.getListView();

                // focus the listview
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

        listViewCurrenciesActive.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        listViewCurrenciesActive.addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
            Node node = evt.getPickResult().getIntersectedNode();

            // go up from the target node until a list cell is found or it's clear
            // it was not a cell that was clicked
            while (node != null && node != listViewCurrenciesActive && !(node instanceof ListCell)) {
                node = node.getParent();
            }

            // if is part of a cell or the cell,
            // handle event instead of using standard handling
            if (node instanceof ListCell) {
                // prevent further handling
                evt.consume();

                ListCell cell = (ListCell) node;
                ListView lv = cell.getListView();

                // focus the listview
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


        listViewCurrenciesActive.setItems(itemsSelected);
    }


    private void updateCurrenciesListView(){
        Config.activeCurrencies.clear();
        itemsSelected.clear();
        itemsAvailable.clear();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
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
        });
    }


    @FXML
    private Button btnAddToActiveList;

    @FXML
    void onBtnUpdateCurrencies(ActionEvent event) {
        updateCurrenciesListView();
    }

    @FXML
    void onBtnAddToActiveListClick(ActionEvent event) {
        ObservableList<Integer> selectedIndices = listViewCurrenciesAvailable.getSelectionModel().getSelectedIndices();
        ObservableList<String> selectedCurrencies = listViewCurrenciesAvailable.getSelectionModel().getSelectedItems();

        Config.activeCurrencies.addAll(selectedCurrencies);
        itemsSelected.addAll(selectedCurrencies);
        itemsAvailable.removeAll(selectedCurrencies);

        listViewCurrenciesAvailable.getSelectionModel().clearSelection();
    }

    @FXML
    void onBtnRemoveFromActiveList(ActionEvent event) {
        itemsAvailable.addAll(listViewCurrenciesActive.getSelectionModel().getSelectedItems());
        Config.activeCurrencies.removeAll(listViewCurrenciesActive.getSelectionModel().getSelectedItems());
        itemsSelected.removeAll(listViewCurrenciesActive.getSelectionModel().getSelectedItems());
    }

    @FXML
    void onBtnCheckStatus(ActionEvent event) {
        statusList.clear();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < Config.activeCurrencies.size(); i++){
                    try {
                        if (!addItemToStatusList(i)){
                            Thread.sleep(60000);
                            addItemToStatusList(i);
                        }
                        Thread.sleep(7000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

    }


    @FXML
    void clearTableViewStatus(ActionEvent event) {
        statusList.clear();
    }

    private boolean addItemToStatusList(int i){
        Strategies.Strategy1.Model m = ApiRequests.getMa(Config.activeCurrencies.get(i));
        if (m != null){
            Strategies.Strategy1 s = new Strategies.Strategy1(m);
            statusList.add(new ListViewStatusModel(Config.activeCurrencies.get(i), s.checkStatus(), s.checkTrend(), "MA1"));
            return true;
        }else {
            return false;
        }

    }

}
