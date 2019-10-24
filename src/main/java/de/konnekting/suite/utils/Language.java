package de.konnekting.suite.utils;

import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author achristian
 */
public class Language {

    private static final Logger log = LoggerFactory.getLogger(Language.class);

    private static final ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/konnekting/suite/i18n/language");

    public static String getText(String key) {
        try {
            return bundle.getString(key);
        } catch (Exception ex) {
            log.error("Problem reading/using key '" + key + "'", ex);
            return "<" + key + ">";
        }

    }

    public static String getText(Object o, String... keyAndformat) {

        String completeKey = o.getClass().getSimpleName() + "." + keyAndformat[0];

        try {
            String s = bundle.getString(completeKey);
            return String.format(s, keyAndformat);
        } catch (Exception ex) {
            log.error("Problem reading/using key '" + completeKey + "'", ex);

            return "<" + completeKey + ">";
        }
    }
}
