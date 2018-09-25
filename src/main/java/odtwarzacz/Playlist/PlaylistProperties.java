/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Playlist;

import odtwarzacz.MainFXMLController;
import odtwarzacz.Odtwarzacz;
import odtwarzacz.Utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Pregiel
 */
public final class PlaylistProperties extends Properties {

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

    private void clearArray() {
        for (int i = 1; getProperty(PROPERTY_NAME + "." + i + ".path") != null; i++) {
            remove(PROPERTY_NAME + "." + i);
        }
        save();
    }

}
