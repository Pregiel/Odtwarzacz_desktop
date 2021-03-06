package odtwarzacz;

import javafx.scene.Node;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class Theme {
    private ResourceBundle resourceBundle;

    public static final String DARK_THEME = "Dark";
    public static final String LIGHT_THEME = "Light";

    private static final String DARK_THEME_FULL = "Theme_Dark.properties";
    private static final String LIGHT_THEME_FULL = "Theme_Light.properties";

    public static final int MAIN_FXML = 1, MEDIA_FXML = 2,
            PLAYLIST_FXML = 3, PLAYLISTELEMENT_FXML = 4,
            FIRSTVIEW_FXML = 5, WINDOW_FXML = 6, ALERT_FXML = 7;

    private static Theme instance;

    private Node mainNode, mediaNode, playListNode, firstViewNode, playListUndockedNode;
    private List<Node> playListElementNode;

    private ClassLoader loader;

    public Theme(String theme) {
        new File("Themes").mkdir();

        try {
            if (!new File("Themes\\" + DARK_THEME_FULL).exists())
                Files.copy(Theme.class.getResourceAsStream("/Themes/" + DARK_THEME_FULL), Paths.get("Themes\\" + DARK_THEME_FULL), StandardCopyOption.REPLACE_EXISTING);
            if (!new File("Themes\\" + LIGHT_THEME_FULL).exists())
                Files.copy(Theme.class.getResourceAsStream("/Themes/" + LIGHT_THEME_FULL), Paths.get("Themes\\" + LIGHT_THEME_FULL), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }


        File file = new File("Themes");
        URL[] urls = new URL[0];
        try {
            urls = new URL[]{file.toURI().toURL()};
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        loader = new URLClassLoader(urls);

        resourceBundle = ResourceBundle.getBundle("Theme", new Locale(theme), loader);
        instance = this;
        playListElementNode = new ArrayList<>();
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public void changeTheme(String theme) {
        Odtwarzacz.getConfig().setProperty("theme", theme);
        Odtwarzacz.getConfig().save();

        resourceBundle = ResourceBundle.getBundle("Theme", new Locale(theme), loader);
        if (mainNode != null)
            mainNode.setStyle(getStyleConst(MAIN_FXML));
        if (mediaNode != null)
            mediaNode.setStyle(getStyleConst(MEDIA_FXML));
        if (playListNode != null)
            playListNode.setStyle(getStyleConst(PLAYLIST_FXML));
        if (playListElementNode.size() > 0) {
            for (Node node : playListElementNode) {
                node.setStyle(getStyleConst(PLAYLISTELEMENT_FXML));
            }
        }
        if (firstViewNode != null)
            firstViewNode.setStyle(getStyleConst(FIRSTVIEW_FXML));
        if (playListUndockedNode != null)
            playListUndockedNode.setStyle(getStyleConst(WINDOW_FXML));

    }

    public void setMainNode(Node mainNode) {
        this.mainNode = mainNode;
    }

    public void setMediaNode(Node mediaNode) {
        this.mediaNode = mediaNode;
    }

    public void setPlayListNode(Node playListNode) {
        this.playListNode = playListNode;
    }

    public void setFirstViewNode(Node firstViewNode) {
        this.firstViewNode = firstViewNode;
    }

    public void setPlayListUndockedNode(Node playListUndockedNode) {
        this.playListUndockedNode = playListUndockedNode;
    }

    public void clearPlayListElementNode() {
        this.playListElementNode.clear();
    }

    public void addPlayListElementNode(Node playListElementNode) {
        this.playListElementNode.add(playListElementNode);
    }

    public void removePlayListElementNode(Node playListElementNode) {
        this.playListElementNode.remove(playListElementNode);
    }

    public static Theme getInstance() {
        return instance;
    }

    public static String getStyle(String key, String value) {
        return key + ": " + instance.getResourceBundle().getString(value) + ";";
    }


    public static String getStyle(String key) {
        return instance.getResourceBundle().getString(key);
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
                return getStyles(
                        getStyle("-background-color", "background.color"),
                        getStyle("-background-hover-color", "background.hover.color"),
                        getStyle("-background-pressed-color", "background.pressed.color"),
                        getStyle("-text-color", "text.color"),
                        getStyle("-text-pressed-color", "text.pressed.color"),
                        getStyle("-menu-exit-button-hover-color", "menu.exit.button.hover.color"),
                        getStyle("-menu-exit-button-pressed-color", "menu.exit.button.pressed.color"),
                        getStyle("-player-text-color", "player.text.color"),
                        getStyle("-box-color-primary", "box.color.primary"),
                        getStyle("-box-color-secondary", "box.color.secondary"));

            case MEDIA_FXML:
                return getStyles(
                        getStyle("-player-background-color", "player.background.color"),

                        getStyle("-player-time-slider-background-color", "time.slider.background.color"),
                        getStyle("-player-time-slider-foreground-color", "time.slider.foreground.color"),

                        getStyle("-player-volume-box-border-color", "background.hover.color"),
                        getStyle("-player-volume-slider-background-color", "volume.slider.background.color"),
                        getStyle("-player-volume-slider-foreground-color", "volume.slider.foreground.color"),

                        getStyle("-player-icon-color", "player.icon.color"),
                        getStyle("-player-icon-off-color", "player.icon.off.color"),

                        getStyle("-player-icon-background-color", "player.icon.background.color"),
                        getStyle("-player-icon-background-hover-color", "player.icon.background.hover.color"),
                        getStyle("-player-icon-background-pressed-color", "player.icon.background.pressed.color"));

            case PLAYLIST_FXML:
                return getStyles(
                        getStyle("-playlist-search-background-color", "background.hover.color"));

            case PLAYLISTELEMENT_FXML:
                return getStyles(
                        getStyle("-box-hover-color-primary", "box.hover.color.primary"),
                        getStyle("-box-hover-color-secondary", "box.hover.color.secondary"),

                        getStyle("-background-selected-color", "background.selected.color"),
                        getStyle("-background-selected-hover-color", "background.selected.hover.color"),

                        getStyle("-text-selected-color", "text.selected.color"),
                        getStyle("-text-playing-color", "text.playing.color"),
                        getStyle("-text-notfound-color", "text.notfound.color"),

                        getStyle("-playlist-element-checkbox-background-color", "background.color"),
                        getStyle("-playlist-element-checkbox-mark-color", "text.pressed.color"));
            case FIRSTVIEW_FXML:
                return getStyles(
                        getStyle("-first-box-border-color", "background.color"));
            case WINDOW_FXML:
                return getStyles(
                        getStyle("-background-color", "background.color"),
                        getStyle("-background-hover-color", "background.hover.color"),
                        getStyle("-background-pressed-color", "background.pressed.color"),
                        getStyle("-text-color", "text.color"),
                        getStyle("-text-pressed-color", "text.pressed.color"),
                        getStyle("-text-selected-color", "text.selected.color"),
                        getStyle("-exit-button-hover-color", "menu.exit.button.hover.color"),
                        getStyle("-exit-button-pressed-color", "menu.exit.button.pressed.color"),
                        getStyle("-box-color-primary", "box.color.primary"),
                        getStyle("-box-color-secondary", "box.color.secondary"),
                        getStyle("-box-hover-color-primary", "box.hover.color.primary"),
                        getStyle("-box-hover-color-secondary", "box.hover.color.secondary"),
                        getStyle("-background-selected-color", "background.selected.color"),
                        getStyle("-background-selected-hover-color", "background.selected.hover.color"));

            case ALERT_FXML:
                return getStyles(
                        getStyle("-background-color", "background.color"),
                        getStyle("-background-hover-color", "background.hover.color"),
                        getStyle("-background-pressed-color", "background.pressed.color"),
                        getStyle("-text-color", "text.color"),
                        getStyle("-text-pressed-color", "text.pressed.color")
                );


        }
        return "";
    }
}
