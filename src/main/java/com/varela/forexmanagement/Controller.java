package com.varela.forexmanagement;

import com.varela.forexmanagement.api.ApiRequests;
import com.varela.forexmanagement.api.models.CurrenciesListModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private ObservableList<String> itemsAvailable = FXCollections.observableArrayList();
    private ObservableList<String> itemsSelected = FXCollections.observableArrayList();

    @FXML
    private ListView<String> listViewCurrenciesAvailable;

    @FXML
    private ListView<String> listViewCurrenciesActive;

    public void initialize(URL location, ResourceBundle resources) {

        listViewCurrenciesAvailable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                ArrayList<CurrenciesListModel> currenciesListModels = ApiRequests.getListOfCurrencies();

                if (currenciesListModels != null){
                    updateCurrenciesAvailableList(currenciesListModels);
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

        listViewCurrenciesActive.setItems(itemsSelected);
    }



    private void updateCurrenciesAvailableList(ArrayList<CurrenciesListModel> arrayList){
        for (int i = 0; i < arrayList.size(); i++){
            itemsAvailable.add(arrayList.get(i).getSymbol());

            if (i == arrayList.size() - 1){
                listViewCurrenciesAvailable.setItems(itemsAvailable);
            }
        }
    }


    @FXML
    private Button btnAddToActiveList;

    @FXML
    void onBtnAddToActiveListClick(ActionEvent event) {
        ObservableList<Integer> selectedIndices = listViewCurrenciesAvailable.getSelectionModel().getSelectedIndices();
        ObservableList<String> selectedCurrencies = listViewCurrenciesAvailable.getSelectionModel().getSelectedItems();

        for (int i = 0; i < selectedIndices.size(); i++){
            itemsSelected.addAll(selectedCurrencies);
            itemsAvailable.removeAll(selectedCurrencies);

            listViewCurrenciesAvailable.getSelectionModel().clearSelection();
        }

        /*
        for (int i = 0; i < currencies.size(); i++) {
            itemsSelected.add(currencies.get(i));
            itemsAvailable.remove(currencies.get(i));

            if (i == currencies.size() - 1){
                listViewCurrenciesActive.setItems(itemsSelected);
                //for (int selected: listViewCurrenciesAvailable.getSelectionModel().getSelectedIndices()){
                //    listViewCurrenciesAvailable.getSelectionModel().clearSelection(selected);
                //}

            }
        }*/


    }
}
