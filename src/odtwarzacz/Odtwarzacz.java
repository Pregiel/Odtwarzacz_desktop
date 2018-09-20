/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import odtwarzacz.Playlist.Playlist;
import odtwarzacz.Utils.ConfigProperties;
import odtwarzacz.Utils.Utils;

/**
 * @author Pregiel
 */
public class Odtwarzacz extends Application {

    private static final String CONFIGFILENAME = "config.properties";
    private static final String DEFAULTCONFIGFILENAME = "src/Resources/DefaultConfigFile.properties";

    public static final int MEDIA_MIN_WIDTH = 420;
    public static final int PLAYLIST_MIN_WIDTH = 300;
    public static final int PLAYER_MIN_HEIGHT = 300;
    public static final int PLAYER_MIN_WIDTH = MEDIA_MIN_WIDTH + PLAYLIST_MIN_WIDTH;


    private static ConfigProperties configProp;



    @Override
    public void start(Stage stage) throws Exception {

        configProp = new ConfigProperties(CONFIGFILENAME, DEFAULTCONFIGFILENAME);

        new Theme(configProp.getProperty("theme"));

        Utils.initResourceBundle();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Layouts/MainFXML.fxml"), Utils.getResourceBundle());
        Parent root = loader.load();

        root.setStyle(Theme.getStyleConst(Theme.MAIN_FXML));
        Theme.getInstance().setMainNode(root);

        Scene scene = new Scene(root);


        stage.setScene(scene);
        stage.setWidth(Integer.valueOf(getConfig().getProperty("window.width")));
        stage.setHeight(Integer.valueOf(getConfig().getProperty("window.height")));

        stage.widthProperty().addListener((observable, oldValue, newValue) -> {
            getConfig().setProperty("window.width", String.valueOf(newValue.intValue()));
            getConfig().save();
        });
        stage.heightProperty().addListener((observable, oldValue, newValue) -> {
            getConfig().setProperty("window.height", String.valueOf(newValue.intValue()));
            getConfig().save();
        });

        if (Boolean.valueOf(getConfig().getProperty("playlist.visible", "false"))) {
            stage.setMinWidth(PLAYER_MIN_WIDTH);
        } else {
            stage.setMinWidth(MEDIA_MIN_WIDTH);
        }

        stage.setFullScreenExitHint("");

        stage.setMinHeight(PLAYER_MIN_HEIGHT);

        stage.setOnCloseRequest(event -> {
            try {
                MainFXMLController.getPlaylist().getPlaylistWindow().close();
            } catch (Exception ignored) {

            }
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
