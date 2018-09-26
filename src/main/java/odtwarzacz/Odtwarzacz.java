package odtwarzacz;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import odtwarzacz.Utils.ConfigProperties;
import odtwarzacz.Utils.CustomStage;
import odtwarzacz.Utils.ResizeHelper;
import odtwarzacz.Utils.Utils;

/**
 * @author Pregiel
 */
public class Odtwarzacz extends Application {

    private static final String CONFIGFILENAME = "config.properties";
    private static final String DEFAULTCONFIGFILENAME = "/Resources/DefaultConfigFile.properties";

    public static final int MEDIA_MIN_WIDTH = 420;
    public static final int PLAYLIST_MIN_WIDTH = 300;
    public static final int PLAYER_MIN_HEIGHT = 300;
    public static final int PLAYER_MIN_WIDTH = MEDIA_MIN_WIDTH + PLAYLIST_MIN_WIDTH;


    private static ConfigProperties configProp;


    @Override
    public void start(Stage primaryStage) throws Exception {
        configProp = new ConfigProperties(CONFIGFILENAME, DEFAULTCONFIGFILENAME);

        new Theme(configProp.getProperty("theme"));

        Utils.initTranslationsBundle();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Layouts/MainFXML.fxml"), Utils.getTranslationsBundle());
        Pane root = loader.load();

        root.setStyle(Theme.getStyleConst(Theme.MAIN_FXML));
        Theme.getInstance().setMainNode(root);

        CustomStage stage = new CustomStage();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setWidth(Integer.valueOf(getConfig().getProperty("window.width")));
        stage.setHeight(Integer.valueOf(getConfig().getProperty("window.height")));

        stage.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (!stage.isWindowMaximized() && !stage.isFullScreen() && !stage.isIconified()) {
                getConfig().setProperty("window.width", String.valueOf(newValue.intValue()));
                getConfig().save();
            }
        });

        stage.heightProperty().addListener((observable, oldValue, newValue) -> {
            if (!stage.isWindowMaximized() && !stage.isFullScreen() && !stage.isIconified()) {
                getConfig().setProperty("window.height", String.valueOf(newValue.intValue()));
                getConfig().save();
            }
        });


//        if (Boolean.valueOf(getConfig().getProperty("playlist.visible", "false"))) {
//            stage.setMinWidth(PLAYER_MIN_WIDTH-30);
//        } else {
//            stage.setMinWidth(MEDIA_MIN_WIDTH-30);
//        }

        stage.setFullScreenExitHint("");

//        stage.setMinHeight(PLAYER_MIN_HEIGHT);

        stage.setOnCloseRequest(event -> {
            try {
                MainFXMLController.getPlaylist().getPlaylistWindow().close();
            } catch (Exception ignored) {

            }
        });

        stage.initStyle(StageStyle.UNDECORATED);
        ResizeHelper.addResizeListener(stage, PLAYER_MIN_WIDTH+20, PLAYER_MIN_HEIGHT+30, 1.7976931348623157E308D, 1.7976931348623157E308D);


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
