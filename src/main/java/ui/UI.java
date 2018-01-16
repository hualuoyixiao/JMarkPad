package ui;

import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextArea;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;


public class UI extends Application implements Initializable {

    private Stage stage;
    @FXML
    private TabPane tabPane;

    //private ArrayList<MyTab> tabs = new ArrayList();

    static String receivedPath="";
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/JPadUI.fxml"));
            fxmlLoader.setController(this);

            Parent root = (Region) fxmlLoader.load();
            JFXDecorator decorator = new JFXDecorator(stage, root);

            decorator.setCustomMaximize(true);
            Scene scene = new Scene(decorator, 800, 600);


            scene.getStylesheets().add("/css/JPadUI.css");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setResizable(true);

            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.setScene(scene);

            stage.show();

            tabPane.setTabClosingPolicy(JFXTabPane.TabClosingPolicy.ALL_TABS);
            MyTab tab;
            if(!receivedPath.equals("")){
                 tab = new MyTab(receivedPath, tabs);
            }else{
                tab = new MyTab("New file", tabs);
            }


            tabPane.getTabs().add(tab);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void newClicked(ActionEvent ae) {
        MyTab tab = new MyTab("New file", tabs);

        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }


    @FXML
    public void openClicked(ActionEvent ae) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Markdown files (*.md)", "*.md"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {

            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String text;

                JFXTextArea textArea = new JFXTextArea("");
                while ((text = bufferedReader.readLine()) != null) {
                    textArea.appendText(text + "\n");
                }
                bufferedReader.close();
                MyTab tab = new MyTab(file.getName(), tabs);
                tab.setTextArea(textArea);
                tabPane.getTabs().add(tab);
                tabPane.getSelectionModel().select(tab);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    @FXML
    public void saveClicked(ActionEvent ae) {
        tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex()).saveFile();
    }

    @FXML
    public void closeClicked(ActionEvent ae) {
        if (!tabs.get(tabPane.getSelectionModel().getSelectedIndex()).isSaved) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Save");
            alert.setContentText("Save file " + tabs.get(tabPane.getSelectionModel().getSelectedIndex()).getText() + "?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                tabs.get(tabPane.getSelectionModel().getSelectedIndex()).saveFile();
            }

        }
        tabPane.getTabs().remove(tabPane.getSelectionModel().getSelectedIndex());
    }

    @Override
    public void stop() {
        for (MyTab tab : tabs) {
            if (!tab.isSaved) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Save");
                alert.setContentText("Save file " + tab.getText() + "?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    tab.saveFile();
                }

            }
        }
        //TODO Check if everything has been saved
        System.exit(0);
    }


    public static void main(String[] args) {
        if (args.length > 0) {
            receivedPath=args[0];
        }
        launch(args);
    }

}