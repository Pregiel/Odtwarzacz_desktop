/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Metadata;


import javafx.util.Duration;

import java.io.File;

/**
 *
 * @author Pregiel
 */
public abstract class Metadata {
    private Duration duration;
    private String title;

    public Metadata() {
    }


    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public abstract void generate(File file);

}
