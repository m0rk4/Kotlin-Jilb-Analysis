package by.mark;

import by.mark.table.ConditionalOperatorTableContent;
import by.mark.table.OperatorTableContent;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.net.URL;
import java.util.*;

public class AppController implements Initializable {

    @FXML
    private MenuItem about;
    @FXML
    private TableView<ConditionalOperatorTableContent> condOperatorsTable;
    @FXML
    private MenuItem close;
    @FXML
    private GridPane grid;
    @FXML
    private TableView<OperatorTableContent> operatorsTable;
    @FXML
    private Label linesText;
    @FXML
    private Label symbText;
    @FXML
    private TextArea codeArea;
    @FXML
    private Button loadFileButton;
    @FXML
    private Button analyzeTextButton;
    @FXML
    private TextField absComplexity;
    @FXML
    private TextField relComplexity;
    @FXML
    private TextField maxNestLevel;

    private final Model model;

    private final Stage currStage;
    private final Rectangle2D screen = Screen.getPrimary().getBounds();

    public AppController(Model model, Stage stage) {
        this.model = model;
        this.currStage = stage;
    }

    TableColumn<OperatorTableContent, String> operatorCol =
            new TableColumn<>("Operator");
    TableColumn<OperatorTableContent, Integer> operatorCountCol =
            new TableColumn<>("Count");
    TableColumn<ConditionalOperatorTableContent, String> condOperatorCol =
            new TableColumn<>("Operator");
    TableColumn<ConditionalOperatorTableContent, Integer> countOperatorCountCol =
            new TableColumn<>("Count");
    TableColumn<ConditionalOperatorTableContent, Integer> ifCol =
            new TableColumn<>("IF-ELSE Equivalent");


    private int fontSize = 10;

    private void updateFont() {
        double perc = (currStage.getHeight() * currStage.getWidth() - 800 * 600)
                / (screen.getWidth() * screen.getHeight() - 800 * 600);
        fontSize =  (int) (perc * 10 + 10);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        about.setOnAction(e -> showAlert(Alert.AlertType.INFORMATION, "About", "Jilb Kotlin Metrics.\nBy Mark & Artem!"));
        close.setOnAction(e -> currStage.close());
        loadFileButton.setOnAction(this::loadFile);
        analyzeTextButton.setOnAction(this::analyzeText);
        codeArea.textProperty().addListener(this::updateInfo);

        styleColumns(20, condOperatorCol, countOperatorCountCol, ifCol, operatorCol, operatorCountCol);


        currStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            updateFont();
            styleColumns(fontSize, condOperatorCol, countOperatorCountCol, ifCol, operatorCol, operatorCountCol );
            operatorsTable.refresh();
        });

        currStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            updateFont();
            styleColumns(fontSize, condOperatorCol, countOperatorCountCol, ifCol, operatorCol, operatorCountCol );
            condOperatorsTable.refresh();
        });

        operatorCol.setCellValueFactory(new PropertyValueFactory<>("operator"));
        operatorCountCol.setCellValueFactory(new PropertyValueFactory<>("operatorCount"));

        operatorsTable.getColumns().clear();
        operatorsTable.getColumns().addAll(operatorCol, operatorCountCol);

        condOperatorCol.setCellValueFactory(new PropertyValueFactory<>("condOperator"));
        countOperatorCountCol.setCellValueFactory(new PropertyValueFactory<>("condOperCount"));
        ifCol.setCellValueFactory(new PropertyValueFactory<>("ifEquivalent"));

        condOperatorsTable.getColumns().clear();
        condOperatorsTable.getColumns().addAll(condOperatorCol, countOperatorCountCol, ifCol);
    }

    private void updateInfo(ObservableValue<? extends String> obs, String o, String n) {
        linesText.setText(linesText.getText().replaceAll("\\d+",
                Integer.toString(codeArea.getText().trim().length() == 0 ? 0 :
                        codeArea.getText().split("\n").length)));
        symbText.setText(symbText.getText().replaceAll("\\d+",
                Integer.toString(codeArea.getText().length())));
    }

    private void analyzeText(ActionEvent e) {
        ResultSet resultSet = model.analyze(Arrays.asList(codeArea.getText().split("\n")));

        int condOperators = resultSet.getNumberOfConditionalOperators();
        int genOperators = resultSet.getNumberOfOperators();
        int maxNestedLevel = resultSet.getMaxNestedLevel();

        absComplexity.setText(String.valueOf(condOperators));
        relComplexity.setText(String.valueOf(Math.round((double) 10000 * condOperators / genOperators) / 10000.0));
        maxNestLevel.setText(String.valueOf(maxNestedLevel));

        // tables update
        updateTables(resultSet.getOperators(), resultSet.getCondOperators());
    }

    private void updateTables(Map<String, Integer> operators,
                              Map<String, Pair<Integer, Integer>> condOperators) {
        ObservableList<OperatorTableContent> operatorTableContents
                = FXCollections.observableArrayList();
        ObservableList<ConditionalOperatorTableContent> conditionalOperatorTableContents
                = FXCollections.observableArrayList();

        for (Map.Entry<String, Integer> entry : operators.entrySet()) {
            operatorTableContents.add(new OperatorTableContent(entry.getKey(), entry.getValue()));
        }

        int totalOps = 0;
        for (Integer value : operators.values()) {
            totalOps += value;
        }

        operatorTableContents.add(new OperatorTableContent("Total:", totalOps));

        for (Map.Entry<String, Pair<Integer, Integer>> entry : condOperators.entrySet()) {
            conditionalOperatorTableContents.add(new ConditionalOperatorTableContent(entry.getKey(),
                    entry.getValue().getKey(), entry.getValue().getValue()));
        }

        int totalCOps = 0, totalIf = 0;
        for (Pair<Integer, Integer> pair : condOperators.values()) {
            totalCOps += pair.getKey();
            totalIf += pair.getValue();
        }

        conditionalOperatorTableContents.add(new ConditionalOperatorTableContent("Total:", totalCOps, totalIf));

        operatorsTable.setItems(operatorTableContents);
        condOperatorsTable.setItems(conditionalOperatorTableContents);
    }

    private void styleColumns(int size, TableColumn<?, ?>... columns) {
        for (TableColumn<?, ?> column : columns) {
            column.setStyle("-fx-alignment: center;\n" +
                    "    -fx-font-family: \"Courier New\", Courier, monospace;\n" +
                    "    -fx-font-weight: bold;\n" +
                    "    -fx-font-size: " + size + "px;\n");
        }
    }

    private void loadFile(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./"));
        fileChooser.setTitle("Choose File");
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            String filename = file.getName();
            Optional<String> ext = Optional.of(filename)
                    .filter(f -> f.contains("."))
                    .map(f -> f.substring(filename.lastIndexOf(".") + 1));
            String extName;
            if (ext.isPresent() && ((extName = ext.get()).equals("kt")
                    || extName.equals("txt")
                    || extName.equals("java"))) {
                Optional<List<String>> res = model.load(file.toPath());
                if (res.isPresent()) {
                    codeArea.clear();
                    res.get().forEach(s -> codeArea.appendText(s + "\n"));
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Loading error.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Please, choose .kt or .txt file.");
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String info) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(info);
        alert.show();
    }


}


