/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Playlist;

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
 *
 * @author Pregiel
 */
public final class PlaylistProperties extends Properties {

    private static final String PROPERTY_NAME = "playlist";
    private File playlistFile;

    public PlaylistProperties(String playlistFileName) {
        super();
        playlistFile = new File(playlistFileName);
        try {
            if (playlistFile.exists()) {
                load(playlistFile);
                save();
            }
        } catch (IOException ex) {
            Logger.getLogger(PlaylistProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        clear();
        int i = 0;
        for (String s : list) {
            setProperty(PROPERTY_NAME + "." + i++, s);
        }
    }

}
