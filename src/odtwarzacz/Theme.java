package odtwarzacz;

import java.util.Locale;
import java.util.ResourceBundle;

public class Theme {
    private ResourceBundle resourceBundle;

    public static final String DARK_THEME = "Dark";
    public static final String LIGHT_THEME = "Light";

    public static final int MAIN_FXML = 1;
    public static final int MEDIA_FXML = 2;
    public static final int PLAYLIST_FXML = 3;

    private static Theme instance;

    public Theme(String theme) {
        resourceBundle = ResourceBundle.getBundle("Resources.Theme", new Locale(theme));
        instance = this;
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public static Theme getInstance() {
        return instance;
    }

    public static String getStyle(String key, String value) {
        return key + ": " + instance.getResourceBundle().getString(value) + ";";
    }

    public static String getStyles(String... style) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : style) {
            stringBuilder.append(s);
        }
        return stringBuilder.toString();
    }

    public static String getStyleConst(int fxmlStyle) {
        switch (fxmlStyle) {
            case MAIN_FXML:
                return getStyles(getStyle("-menu-background-color", "menu.background.color"),
                        getStyle("-menu-background-highlight-color", "menu.background.highlight.color"),
                        getStyle("-menu-text-color", "menu.text.color"),
                        getStyle("-menu-text-highlight-color", "menu.text.highlight.color"),
                        getStyle("-playlist-background-color", "playlist.background.color"));

            case MEDIA_FXML:
                return getStyles(getStyle("-player-background-color", "player.background.color"),
                        getStyle("-player-text-color", "player.text.color"),
                        getStyle("-player-volume-box-background-color", "player.volume.box.background.color"),
                        getStyle("-player-volume-box-border-color", "player.volume.box.border.color"),
                        getStyle("-player-volume-box-text-color", "player.volume.box.text.color"),
                        getStyle("-player-volume-slider-background-color", "player.volume.slider.background.color"),
                        getStyle("-player-volume-slider-foreground-color", "player.volume.slider.foreground.color"),
                        getStyle("-player-icon-color", "player.icon.color"),
                        getStyle("-player-icon-off-color", "player.icon.off.color"),
                        getStyle("-player-icon-background-color", "player.icon.background.color"),
                        getStyle("-player-icon-background-hover-color", "player.icon.background.hover.color"),
                        getStyle("-player-icon-background-pressed-color", "player.icon.background.pressed.color"));


            case PLAYLIST_FXML:
                return getStyles(getStyle("-playlist-background-color", "playlist.background.color"),
                        getStyle("-playlist-text-color", "playlist.text.color"),
                        getStyle("-playlist-search-background-color", "playlist.search.background.color"),
                        getStyle("-playlist-button-color", "playlist.button.color"),
                        getStyle("-playlist-button-background-color", "playlist.button.background.color"),
                        getStyle("-playlist-button-background-hover-color", "playlist.button.background.hover.color"),
                        getStyle("-playlist-button-background-pressed-color", "playlist.button.background.pressed.color"));
        }
        return "";
    }
}
