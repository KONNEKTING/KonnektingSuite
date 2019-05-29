/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.konnekting.suite;

import java.net.NetworkInterface;
import java.util.Objects;


/**
 *
 * @author achristian
 */
public class NetworkInterfaceItem {

    private final NetworkInterface ni;

    public NetworkInterfaceItem(NetworkInterface ni) {
        this.ni = ni;
    }

    @Override
    public String toString() {
        return ni.getDisplayName()+" ("+ni.getName()+")";
    }

    public NetworkInterface getNetworkInterface() {
        return ni;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.ni);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NetworkInterfaceItem other = (NetworkInterfaceItem) obj;
        if (!Objects.equals(this.ni, other.ni)) {
            return false;
        }
        return true;
    }
    
    
    
}
