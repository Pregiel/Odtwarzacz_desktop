/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package odtwarzacz.Utils;

import odtwarzacz.Odtwarzacz;

import java.util.Locale;

/**
 *
 * @author Pregiel
 */
public final class MyLocale {

    static public final Locale ENGLISH = new Locale("en");

    static public final Locale POLISH = new Locale("pl");

    static public Locale getLocale() {
        
        switch (Odtwarzacz.getConfig().getProperty("language")) {
            case "pl":
                return POLISH;
                
            default:
                return ENGLISH;

        }
    }

}
