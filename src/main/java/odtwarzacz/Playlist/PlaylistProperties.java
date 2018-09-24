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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
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

        for (int i = 0; (value = getProperty(PROPERTY_NAME + "." + i)) != null; i++) {
            result.add(value);
        }

        return result;
    }


    public void setArray(List<String> list) {
        clearArray();
        int i = 0;
        for (String s : list) {
            setProperty(PROPERTY_NAME + "." + i++, s);
        }
    }

    private void clearArray() {
        for (int i = 0; getProperty(PROPERTY_NAME + "." + i) != null; i++) {
            remove(PROPERTY_NAME + "." + i);
        }
        save();
    }

}
