package odtwarzacz;

import java.util.Locale;
import java.util.ResourceBundle;

public class Theme {
    private ResourceBundle resourceBundle;

    public static final String DARK_THEME = "Dark";
    public static final String LIGHT_THEME = "Light";

    public static final int MAIN_FXML = 1;
    public static final int MEDIA_FXML = 2;

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
                return getStyles(getStyle("-color-background", "menu.background.color"),
                        getStyle("-color-background-highlight", "menu.background_highlight.color"),
                        getStyle("-color-text", "menu.text.color"),
                        getStyle("-color-text-highlight", "menu.text_highlight.color"));

            case MEDIA_FXML:
                return getStyles(getStyle("-color-background", "player.background.color"),
                        getStyle("-color-text", "player.text.color"),
                        getStyle("-color-icon", "player.icon.color"),
                        getStyle("-color-icon-off", "player.icon.off.color"),
                        getStyle("-color-volume-box-background", "player.volume.box.background.color"),
                        getStyle("-color-volume-box-border", "player.volume.box.border.color"),
                        getStyle("-color-volume-box-text", "player.volume.box.text.color"),
                        getStyle("-color-volume-slider-background", "player.volume.slider.background.color"),
                        getStyle("-color-volume-slider-foreground", "player.volume.slider.foreground.color"),
                        getStyle("-color-icon-background", "player.icon.background.color"),
                        getStyle("-color-icon-background-hover", "player.icon.background.hover.color"));
        }
        return "";
    }
}
