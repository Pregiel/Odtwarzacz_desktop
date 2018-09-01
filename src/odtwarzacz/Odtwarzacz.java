/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Pregiel
 */
public class Odtwarzacz extends Application {

    private static final String CONFIGFILENAME = "config.properties";
    private static final String DEFAULTCONFIGFILENAME = "src/Resources/DefaultConfigFile.properties";


    private static ConfigProperties configProp;

    @Override
    public void start(Stage stage) throws Exception {

        configProp = new ConfigProperties(CONFIGFILENAME, DEFAULTCONFIGFILENAME);
//            Locale.setDefault(MyLocale.ENGLISH);
        Utils.initResourceBundle();
        Parent root = FXMLLoader.load(getClass().getResource("MainFXML.fxml"), Utils.getResourceBundle());

        Scene scene = new Scene(root);
        scene.getStylesheets().add("odtwarzacz/styleSheet.css");
        

        stage.setScene(scene);
        stage.setWidth(Integer.valueOf(getConfig().getProperty("window.width")));
        stage.setHeight(Integer.valueOf(getConfig().getProperty("window.height")));
        stage.widthProperty().addListener((observable, oldValue, newValue) -> {
            getConfig().setProperty("window.width", String.valueOf(newValue.intValue()));
            getConfig().save();
        });
        stage.heightProperty().addListener((observable, oldValue, newValue) -> {
            getConfig().setProperty("window.height", String.valueOf(newValue.intValue()));
        });
        stage.show();

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    
    public static ConfigProperties getConfig() {
        return configProp;
    }
}
