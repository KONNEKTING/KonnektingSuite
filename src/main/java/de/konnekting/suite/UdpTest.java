/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.konnekting.suite;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.DatagramSocketImpl;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 *
 * @author achristian
 */
public class UdpTest {
    
    public static void main(String[] args) throws IOException {
        InetSocketAddress localEP = new InetSocketAddress(InetAddress.getLocalHost(), 0);
        System.out.println("localEP: "+localEP);
        DatagramSocket socket = new DatagramSocket(localEP);
        
        InetSocketAddress addr = new InetSocketAddress("192.168.200.71", 3671);
        byte[] buf = {6,16,2,5,0,26,8,1,127,0,1,1,-45,-17,8,1,127,0,1,1,-45,-17,4,4,2,0};
        final DatagramPacket p = new DatagramPacket(buf, buf.length, addr.getAddress(),
					addr.getPort());
        socket.send(p);
    }
    
}
