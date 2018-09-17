/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pregiel
 */
public final class ConfigProperties extends Properties {
    private File configFile;

    public ConfigProperties(String configFileName, String defaultConfigFileName) {
        super();
        configFile = new File(configFileName);
        try {
            if (configFile.exists()) {
                load(configFile);
                verify(defaultConfigFileName);
                save();
            } else {
                load(new File(defaultConfigFileName));
                save();
            }
        } catch (IOException ex) {
            Logger.getLogger(ConfigProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void load(File file) throws IOException {
        FileReader reader = new FileReader(file);
        this.load(reader);
        reader.close();
    }

    public void save() {
        try {
            FileWriter writer = new FileWriter(configFile);
            this.store(writer, "Config");
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(ConfigProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void verify(String defaultConfigFileName) throws IOException {
        Properties defaultConfig = new Properties();

        FileReader reader = new FileReader(defaultConfigFileName);
        defaultConfig.load(reader);
        defaultConfig.forEach((key, value) -> {
            if (this.get(key) == null) {
                this.setProperty(key.toString(), value.toString());
            }
        });
    }
    
    public List<String> getArrayProperty(String key) {
        List<String> result = new ArrayList<>();
        
        String value;
        
        for (int i = 0; (value = getProperty(key + "." + i)) != null; i++) {
            result.add(value);
        }
        
        return result;
    }

}
