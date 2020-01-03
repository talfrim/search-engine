import HandleParse.Parse;
import IndexerAndDictionary.CountAndPointerDicValue;
import IndexerAndDictionary.Dictionary;
import IndexerAndDictionary.Indexer;
import OuputFiles.DictionaryFileHandler;
import TermsAndDocs.TermCounterPair;
import TermsAndDocs.Terms.Term;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;


public class GUI extends Application implements EventHandler<ActionEvent> {

    Button startButton;
    Button inputPathBrowseButton;
    Button outputPathBrowseButton;
    Button resetButton;
    Button viewDictionaryButton;
    Button loadDictionaryToMemoryButton;

    TextField inputPathTextField;
    TextField outputPathTextField;

    DirectoryChooser inputPathChooser;
    DirectoryChooser outputPathChooser;

    CheckBox stemCheckBox;

    String inputPath;
    String outputPath;

    Dictionary dictionary;


    @Override
    public void start(Stage primaryStage) throws Exception {
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Search Engine - Part A");
        StackPane layout = new StackPane();
        VBox mainVBox = new VBox();
        HBox extraButtonsHBox;
        HBox inputHBox;
        HBox outputHBox;

        //directory choosers
        inputPathChooser = new DirectoryChooser();
        outputPathChooser = new DirectoryChooser();

        //text fields
        inputPathTextField = new TextField();
        outputPathTextField = new TextField();


        //buttons
        startButton = new Button("Start");
        startButton.setOnAction(this);
        inputPathBrowseButton = new Button("Browse input directory");
        inputPathBrowseButton.setOnAction(e -> {
            File inputLibrary = inputPathChooser.showDialog(primaryStage);
            inputPathTextField.setText(inputLibrary.getAbsolutePath());
        });
        outputPathBrowseButton = new Button("Browse output directory");
        outputPathBrowseButton.setOnAction(e -> {
            File outputLibrary = outputPathChooser.showDialog(primaryStage);
            outputPathTextField.setText(outputLibrary.getAbsolutePath());
        });
        resetButton = new Button("Reset");
        resetButton.setOnAction(this);
        viewDictionaryButton = new Button("View dictionary");
        viewDictionaryButton.setOnAction(this);
        loadDictionaryToMemoryButton = new Button("Load dictionary to memory");
        loadDictionaryToMemoryButton.setOnAction(this);


        //checkBox
        stemCheckBox = new CheckBox("Stemming");


        //build scene
        inputHBox = new HBox(inputPathTextField, inputPathBrowseButton);
        outputHBox = new HBox(outputPathTextField, outputPathBrowseButton);
        mainVBox.getChildren().add(inputHBox);
        mainVBox.getChildren().add(outputHBox);
        mainVBox.getChildren().add(stemCheckBox);
        mainVBox.getChildren().add(startButton);
        extraButtonsHBox = new HBox(resetButton, viewDictionaryButton, loadDictionaryToMemoryButton);
        mainVBox.getChildren().add(extraButtonsHBox);
        layout.getChildren().add(mainVBox);
        primaryStage.setScene(new Scene(layout, 350, 120));
        primaryStage.show();
    }

    /**
     * handles events in the gui
     * @param event
     */
    @Override
    public void handle(ActionEvent event) {
        boolean isWithStemming = stemCheckBox.isSelected();
        inputPath = inputPathTextField.getText();
        outputPath = outputPathTextField.getText();
        if (event.getSource() == startButton) {
            if (inputPath.equals("") || outputPath.equals(""))
                AlertBox.display("Alert", "Please choose paths and try again.");
            else
                startProgram(inputPath, outputPath, isWithStemming);
        }
        if (event.getSource() == resetButton) {
            reset(outputPath);
        }
        if (event.getSource() == viewDictionaryButton) {
            showSortedDictionary();
        }
        if (event.getSource() == loadDictionaryToMemoryButton) {
            loadDictionaryToMemory(outputPath);
        }

    }

    /**
     * loads doctionary from outputPath, according to stemming box
     * @param outputPath
     */
    private void loadDictionaryToMemory(String outputPath) {
        boolean isWithStemming = stemCheckBox.isSelected();
        DictionaryFileHandler dfh = new DictionaryFileHandler(new Dictionary());
        this.dictionary = dfh.readFromFile(outputPath, isWithStemming);
        System.out.println("loaded");
    }

    /**
     * showing sirted dictionary
     */
    private void showSortedDictionary() {
        Stage stage = new Stage();
        TableView tableView = new TableView();
        TableColumn<String, TermCounterPair> termCol = new TableColumn("Term");
        termCol.setCellValueFactory(new PropertyValueFactory<>("termStr"));
        TableColumn<Integer, TermCounterPair> countCol= new TableColumn("Count");
        countCol.setCellValueFactory(new PropertyValueFactory<>("count"));

        tableView.getColumns().add(termCol);
        tableView.getColumns().add(countCol);

        for (Map.Entry<Term, CountAndPointerDicValue> entry : dictionary.dictionaryTable.entrySet())
        {
            tableView.getItems().add(new TermCounterPair(entry.getKey().getData(),entry.getValue().getTotalCount()));
        }




        VBox vbox = new VBox(tableView);
        Scene scene = new Scene(vbox);
        stage.setScene(scene);
        stage.show();
    }


    /**
     * delete info from dictionary
     *
     * @param outputPath
     */
    private void reset(String outputPath) {
        File index = new File(outputPath);
        String[] entries = index.list();
        for (String s : entries) {
            File currentFile = new File(index.getPath(), s);
            currentFile.delete();
        }
        index.delete();
        Parse.deleteStatics();
        Dictionary.deleteMutex();
        Indexer.deleteDictionary();
    }

    /**
     * starts the processing
     *
     * @param inputPath
     * @param outputPath
     * @param toStemm
     */
    private void startProgram(String inputPath, String outputPath, boolean toStemm) {
        ProgramStarter.startProgram(inputPath, outputPath, toStemm);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
