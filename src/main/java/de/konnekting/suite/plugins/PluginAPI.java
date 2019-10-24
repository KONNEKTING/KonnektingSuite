/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.konnekting.suite.plugins;

/**
 *
 * @author achristian
 */
public interface PluginAPI {
    
    // program: all, ...
    // update firmware ??? --> More logic into plugin, otherwise someone will just create an own plugin?
    // listen to telegrams, incl. filter
    // send telegrams (firmware-update not part of deviceconnfug lib, and therefore complete logic in plugin?)
    // read knx ets project data
    // get project folder
    
    // access to devices in suite: bulk-changes like re-assign other/new IAs
    // read out all params/data from arduino --> reconstruction
    
    
    
}
