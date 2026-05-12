package com.currencyconverter.controller;

import com.currencyconverter.model.ConversionRecord;
import com.currencyconverter.service.ConversionService;
import com.currencyconverter.util.AppConstants;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainController implements Initializable {

    @FXML private ComboBox<String>  cbFromCurrency;
    @FXML private ComboBox<String>  cbToCurrency;
    @FXML private TextField         tfAmount;
    @FXML private Button            btnConvert;
    @FXML private Button            btnSwap;
    @FXML private Label             lblResult;
    @FXML private Label             lblRate;
    @FXML private Label             lblStatus;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Button                        btnViewHistory;
    @FXML private TableView<ConversionRecord>   tblHistory;
    @FXML private TableColumn<ConversionRecord, String>     colFrom;
    @FXML private TableColumn<ConversionRecord, String>     colTo;
    @FXML private TableColumn<ConversionRecord, BigDecimal> colAmount;
    @FXML private TableColumn<ConversionRecord, BigDecimal> colResult;
    @FXML private TableColumn<ConversionRecord, BigDecimal> colRate;
    @FXML private TableColumn<ConversionRecord, String>     colTimestamp;

    private final ConversionService service = new ConversionService();

    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "converter-worker");
        t.setDaemon(true);
        return t;
    });

    // Flag to stop infinite loop in ComboBox listener
    private boolean updatingCombo = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initCurrencyComboBoxes();
        initHistoryTable();
        setupInputValidation();
        progressIndicator.setVisible(false);
    }

    @FXML
    private void onConvert() {
        String fromCurrency = cbFromCurrency.getValue();
        String toCurrency   = cbToCurrency.getValue();
        String amountText   = tfAmount.getText();

        setWorking(true, "Fetching live rate...");

        executor.submit(() -> {
            try {
                ConversionRecord record = service.convert(fromCurrency, toCurrency, amountText);
                Platform.runLater(() -> {
                    showConversionResult(record);
                    setWorking(false, "Conversion successful  ✓");
                });
            } catch (IllegalArgumentException e) {
                Platform.runLater(() -> {
                    showError("Validation Error", e.getMessage());
                    setWorking(false, "");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Error", "Could not complete conversion:\n" + e.getMessage());
                    setWorking(false, "");
                });
            }
        });
    }

    @FXML
    private void onSwap() {
        String from = cbFromCurrency.getValue();
        String to   = cbToCurrency.getValue();
        cbFromCurrency.setValue(to);
        cbToCurrency.setValue(from);
        clearResults();
    }

    @FXML
    private void onViewHistory() {
        setWorking(true, "Loading history...");
        tblHistory.setVisible(false);

        executor.submit(() -> {
            try {
                List<ConversionRecord> records = service.getRecentHistory();
                Platform.runLater(() -> {
                    ObservableList<ConversionRecord> data =
                            FXCollections.observableArrayList(records);
                    tblHistory.setItems(data);
                    tblHistory.setVisible(true);
                    setWorking(false,
                            records.isEmpty()
                                    ? "No history found."
                                    : "Showing last " + records.size() + " conversions.");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Database Error", "Could not load history:\n" + e.getMessage());
                    setWorking(false, "");
                });
            }
        });
    }

    private void initCurrencyComboBoxes() {
        ObservableList<String> currencies =
                FXCollections.observableArrayList(AppConstants.SUPPORTED_CURRENCIES);
        setupSearchableComboBox(cbFromCurrency, currencies, "USD");
        setupSearchableComboBox(cbToCurrency,   currencies, "EUR");
    }

    private void setupSearchableComboBox(ComboBox<String> comboBox,
                                          ObservableList<String> master,
                                          String defaultValue) {
        comboBox.setEditable(true);
        comboBox.setItems(FXCollections.observableArrayList(master));
        comboBox.setValue(defaultValue);

        // Search as user types — guarded by flag to prevent infinite loop
        comboBox.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (updatingCombo) return;
            if (newText == null) return;

            // If text matches the currently selected value, do nothing
            String currentVal = comboBox.getValue();
            if (currentVal != null && currentVal.equals(newText)) return;

            updatingCombo = true;
            try {
                if (newText.isBlank()) {
                    comboBox.setItems(FXCollections.observableArrayList(master));
                } else {
                    String filter = newText.toUpperCase();
                    ObservableList<String> filtered = master.filtered(
                            c -> c.toUpperCase().contains(filter));
                    comboBox.setItems(filtered);
                    if (!comboBox.isShowing()) {
                        comboBox.show();
                    }
                }
            } finally {
                updatingCombo = false;
            }
        });

        // When user picks a value, restore the full list
        comboBox.setOnAction(e -> {
            if (updatingCombo) return;
            String selected = comboBox.getValue();
            if (selected != null && master.contains(selected)) {
                updatingCombo = true;
                try {
                    comboBox.setItems(FXCollections.observableArrayList(master));
                    comboBox.setValue(selected);
                } finally {
                    updatingCombo = false;
                }
            }
        });
    }

    private void initHistoryTable() {
        colFrom.setCellValueFactory(new PropertyValueFactory<>("fromCurrency"));
        colTo.setCellValueFactory(new PropertyValueFactory<>("toCurrency"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("inputAmount"));
        colResult.setCellValueFactory(new PropertyValueFactory<>("convertedResult"));
        colRate.setCellValueFactory(new PropertyValueFactory<>("exchangeRate"));

        colTimestamp.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getFormattedTimestamp()));

        formatDecimalColumn(colAmount);
        formatDecimalColumn(colResult);

        tblHistory.setVisible(false);
        tblHistory.setPlaceholder(new Label("No history records yet."));
    }

    private void setupInputValidation() {
        tfAmount.textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null) return;
            if (!newText.matches("[0-9]*\\.?[0-9]*")) {
                tfAmount.setText(oldText);
            }
        });
    }

    private void setWorking(boolean working, String statusText) {
        progressIndicator.setVisible(working);
        btnConvert.setDisable(working);
        btnSwap.setDisable(working);
        btnViewHistory.setDisable(working);
        lblStatus.setText(statusText);
    }

    private void showConversionResult(ConversionRecord record) {
        String amount = record.getInputAmount()
                              .setScale(2, RoundingMode.HALF_UP).toPlainString();
        String result = record.getConvertedResult()
                              .setScale(4, RoundingMode.HALF_UP).toPlainString();
        String rate   = record.getExchangeRate()
                              .setScale(6, RoundingMode.HALF_UP).toPlainString();

        lblResult.setText(amount + " " + record.getFromCurrency()
                + "  =  " + result + " " + record.getToCurrency());
        lblRate.setText("1 " + record.getFromCurrency()
                + " = " + rate + " " + record.getToCurrency());
    }

    private void clearResults() {
        lblResult.setText("---");
        lblRate.setText("");
        lblStatus.setText("");
    }

    private void showError(String header, String body) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("CurrencyFX");
        alert.setHeaderText(header);
        alert.setContentText(body);
        alert.showAndWait();
    }

    @SuppressWarnings("unchecked")
    private void formatDecimalColumn(TableColumn<ConversionRecord, ?> column) {
        ((TableColumn<ConversionRecord, BigDecimal>) column).setCellFactory(col ->
                new TableCell<>() {
                    @Override
                    protected void updateItem(BigDecimal item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.setScale(4, RoundingMode.HALF_UP).toPlainString());
                            setStyle("-fx-alignment: CENTER-RIGHT;");
                        }
                    }
                });
    }
}