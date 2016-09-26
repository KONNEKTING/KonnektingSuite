/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.konnekting.suite.utils;

/**
 *
 * @author achristian
 */
public class SelectionItem {
    
    private String name;
    private Object object;

    public SelectionItem(String name, Object o) {
        this.name = name;
        this.object = o;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object o) {
        this.object = o;
    }

    @Override
    public String toString() {
        return name;
    }
    
}
