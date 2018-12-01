/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Playlist;

import odtwarzacz.MainFXMLController;
import odtwarzacz.Odtwarzacz;
import odtwarzacz.Utils.Utils;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Key;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Pregiel
 */
public final class PlaylistProperties extends Properties {
    public static final String ENABLE = "enable";

    private static final String PROPERTY_NAME = "playlist";
    private File playlistFile;

    public PlaylistProperties(String playlistFileName, String playlistName) {
        super();
        playlistFile = new File(playlistFileName);
        try {
            if (playlistFile.exists()) {
                if (!playlistName.equals("")) {
                    long i = 1;
                    while (playlistFile.exists() && i < Long.MAX_VALUE) {
                        playlistFileName = Playlist.PLAYLIST_FOLDER + "/" + playlistName + "_" + i + ".playlist";
                        playlistFile = new File(playlistFileName);
                        i++;
                    }
                    setPlaylistName(playlistName);
                }
                load(playlistFile);
            } else {
                if (playlistName.equals("")) {
                    setPlaylistName(Utils.getTranslationsBundle().getString("player.playlist"));
                } else {
                    setPlaylistName(playlistName);
                }
            }

            if (getProperty("playlist.name") == null) {
                setProperty("playlist.name", Utils.getTranslationsBundle().getString("player.playlist"));
            }

            if (getProperty("playlist.id") == null) {
                setProperty("playlist.id", String.valueOf(Playlist.getPlaylistLastId()));
            }

            save();
        } catch (IOException ex) {
            Logger.getLogger(PlaylistProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public PlaylistProperties(String playlistFileName) {
        this(playlistFileName, "");
    }

    public String getPlaylistName() {
        return getProperty("playlist.name", Utils.getTranslationsBundle().getString("player.playlist"));
    }

    public void setProperty(int index, String key, Object value) {
        setProperty(PROPERTY_NAME + "." + index + "." + key, value.toString());
    }

    public String getProperty(int index, String key) {
        return getProperty(PROPERTY_NAME + "." + index + "." + key);
    }


    public int getPlaylistId() {
        String id = getProperty("playlist.id");
        if (id == null) {
            return -1;
        }
        return Integer.parseInt(id);
    }

    public void setPlaylistName(String name) {
        setProperty("playlist.name", name);
        save();
    }

    public void load(File file) throws IOException {
        FileReader reader = new FileReader(file);
        this.load(reader);
        reader.close();
    }

    public void save() {
        try {
            FileWriter writer = new FileWriter(playlistFile);
            this.store(writer, "Playlist");
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(PlaylistProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<String> getArray() {
        List<String> result = new ArrayList<>();

        String value;

        for (int i = 1; (value = getProperty(PROPERTY_NAME + "." + i + ".path")) != null; i++) {
            result.add(value);
        }

        return result;
    }


    public void setArray(List<String> list) {
        clearArray();
        int i = 0;
        for (String s : list) {
            setProperty(i, "path", s);
        }
    }

    public void addToArray(String path, int index) {
        setProperty(index, "path", path);
        save();
    }

    public void removeFromArray(int index) {
        Enumeration<String> enumeration = (Enumeration<String>) propertyNames();

        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            if (name.startsWith(PROPERTY_NAME + "." + index + ".")) {
                remove(name);
            }
        }
        save();
    }

    public void changeIndexInArray(int from, int to) {
        Enumeration<String> enumeration = (Enumeration<String>) propertyNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            if (name.startsWith(PROPERTY_NAME + "." + from + ".")) {
                String value = getProperty(name);

                setProperty(to, name.substring((PROPERTY_NAME + "." + from).length() + 1), value);
                remove(name);
            }
        }
        save();
    }

    private class KeyValue {
        private String key, value;
        private int index;

        KeyValue(String key, String value) {
            this.key = key;
            this.value = value;
        }

        KeyValue(int index, String key, String value) {
            this.key = key;
            this.value = value;
            this.index = index;
        }

        String getKey() {
            return key;
        }

        String getValue() {
            return value;
        }

        int getIndex() {
            return index;
        }
    }

    public void swapIndexes(int index_1, int index_2) {
        Enumeration<String> enumeration = (Enumeration<String>) propertyNames();
        List<KeyValue> firstElement = new ArrayList<>();
        List<KeyValue> secondElement = new ArrayList<>();

        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            if (name.startsWith(PROPERTY_NAME + "." + index_1 + ".")) {
                firstElement.add(new KeyValue(name.substring((PROPERTY_NAME + "." + index_1).length() + 1),
                        getProperty(name)));

                remove(name);
            } else if (name.startsWith(PROPERTY_NAME + "." + index_2 + ".")) {
                secondElement.add(new KeyValue(name.substring((PROPERTY_NAME + "." + index_2).length() + 1),
                        getProperty(name)));

                remove(name);
            }
        }

        for (KeyValue keyValue : firstElement) {
            setProperty(index_2, keyValue.getKey(), keyValue.getValue());
        }

        for (KeyValue keyValue : secondElement) {
            setProperty(index_1, keyValue.getKey(), keyValue.getValue());
        }
        save();
    }

    public void moveToIndex(int from, int to) {
        Enumeration<String> enumeration = (Enumeration<String>) propertyNames();
        List<KeyValue> fromElement = new ArrayList<>();
        List<List<KeyValue>> restElements = new ArrayList<>();

        for (int i = 0; i < Math.max(from, to) - Math.min(from, to); i++) {
            restElements.add(new ArrayList<>());
        }

        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            if (name.matches("^(" + PROPERTY_NAME + ".\\d.).*")) {
                int index = Integer.parseInt(name.substring(name.indexOf(".") + 1, name.indexOf(".", name.indexOf(".") + 1)));
                if (index >= Math.min(from, to) && index <= Math.max(from, to)) {
                    if (index == from) {
                        fromElement.add(new KeyValue(to, name.substring((PROPERTY_NAME + "." + from).length() + 1),
                                getProperty(name)));
                    } else {
                        int newIndex, listIndex;
                        if (from > to) {
                            listIndex = index - Math.min(from, to);
                            newIndex = index + 1;
                        } else {
                            listIndex = index - Math.min(from, to) - 1;
                            newIndex = index - 1;
                        }
                        restElements.get(listIndex).add(new KeyValue(newIndex, name.substring((PROPERTY_NAME + "." + index).length() + 1),
                                getProperty(name)));
                    }
                    remove(name);
                }
            }
        }

        for (KeyValue keyValue : fromElement) {
            setProperty(keyValue.getIndex(), keyValue.getKey(), keyValue.getValue());
        }

        for (List<KeyValue> restElement : restElements) {
            for (KeyValue keyValue : restElement) {
                setProperty(keyValue.getIndex(), keyValue.getKey(), keyValue.getValue());
            }
        }
//        save();
    }

    private void clearArray() {
        for (int i = 1; getProperty(PROPERTY_NAME + "." + i + ".path") != null; i++) {
            remove(PROPERTY_NAME + "." + i);
        }
        save();
    }

}
