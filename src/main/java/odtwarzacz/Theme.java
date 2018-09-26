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
            FIRSTVIEW_FXML = 5, PLAYLISTUNDOCKED_FXML = 6;

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
            playListUndockedNode.setStyle(getStyleConst(PLAYLISTUNDOCKED_FXML));

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
                        getStyle("-menu-background-color", "menu.background.color"),
                        getStyle("-menu-background-highlight-color", "menu.background.highlight.color"),
                        getStyle("-menu-text-color", "menu.text.color"),
                        getStyle("-menu-text-highlight-color", "menu.text.highlight.color"),
                        getStyle("-playlist-background-color", "playlist.background.color"),
                        getStyle("-menu-button-pressed-color", "menu.button.pressed.color"),
                        getStyle("-menu-exit-button-hover-color", "menu.exit.button.hover.color"),
                        getStyle("-menu-exit-button-pressed-color", "menu.exit.button.pressed.color"));

            case MEDIA_FXML:
                return getStyles(
                        getStyle("-player-background-color", "player.background.color"),
                        getStyle("-player-text-color", "player.text.color"),

                        getStyle("-player-time-slider-background-color", "player.time.slider.background.color"),
                        getStyle("-player-time-slider-foreground-color", "player.time.slider.foreground.color"),

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
                return getStyles(
                        getStyle("-playlist-background-color", "playlist.background.color"),
                        getStyle("-playlist-text-color", "playlist.text.color"),
                        getStyle("-playlist-text-hover-color", "playlist.text.hover.color"),
                        getStyle("-playlist-text-pressed-color", "playlist.text.pressed.color"),
                        getStyle("-playlist-search-background-color", "playlist.search.background.color"),
                        getStyle("-playlist-button-color", "playlist.button.color"),
                        getStyle("-playlist-button-background-color", "playlist.button.background.color"),
                        getStyle("-playlist-button-background-hover-color", "playlist.button.background.hover.color"),
                        getStyle("-playlist-button-background-pressed-color", "playlist.button.background.pressed.color"),
                        getStyle("-playlist-box-background-color", "playlist.box.background.color"));

            case PLAYLISTELEMENT_FXML:
                return getStyles(
                        getStyle("-playlist-element-background-color-1", "playlist.element.background.color.1"),
                        getStyle("-playlist-element-background-color-2", "playlist.element.background.color.2"),

                        getStyle("-playlist-element-background-hover-color-1", "playlist.element.background.hover.color.1"),
                        getStyle("-playlist-element-background-hover-color-2", "playlist.element.background.hover.color.2"),

                        getStyle("-playlist-element-background-selected-color", "playlist.element.background.selected.color"),
                        getStyle("-playlist-element-background-selected-hover-color", "playlist.element.background.selected.hover.color"),

                        getStyle("-playlist-element-text-color", "playlist.element.text.color"),
                        getStyle("-playlist-element-selected-text-color", "playlist.element.selected.text.color"),
                        getStyle("-playlist-element-playing-text-color", "playlist.element.playing.text.color"),
                        getStyle("-playlist-element-notfound-text-color", "playlist.element.notfound.text.color"),

                        getStyle("-playlist-element-button-background-color", "playlist.element.button.background.color"),
                        getStyle("-playlist-element-button-background-hover-color", "playlist.element.button.background.hover.color"),
                        getStyle("-playlist-element-button-background-pressed-color", "playlist.element.button.background.pressed.color"),

                        getStyle("-playlist-element-checkbox-background-color", "playlist.element.checkbox.background.color"),
                        getStyle("-playlist-element-checkbox-mark-color", "playlist.element.checkbox.mark.color"));
            case FIRSTVIEW_FXML:
                return getStyles(
                        getStyle("-first-background-color", "first.background.color"),
                        getStyle("-first-box-background-color", "first.box.background.color"),
                        getStyle("-first-box-border-color", "first.box.border.color"),
                        getStyle("-first-text-color", "first.text.color"),
                        getStyle("-first-text-hover-color", "first.text.hover.color"));
            case PLAYLISTUNDOCKED_FXML:
                return getStyles(
                        getStyle("-playlist-undocked-background-color", "menu.background.color"),
                        getStyle("-playlist-undocked-background-highlight-color", "menu.background.highlight.color"),
                        getStyle("-playlist-undocked-text-color", "menu.text.color"),
                        getStyle("-playlist-undocked-text-highlight-color", "menu.text.highlight.color"),
                        getStyle("-playlist-undocked-button-pressed-color", "menu.button.pressed.color"),
                        getStyle("-playlist-undocked-exit-button-hover-color", "menu.exit.button.hover.color"),
                        getStyle("-playlist-undocked-exit-button-pressed-color", "menu.exit.button.pressed.color"));


        }
        return "";
    }
}
