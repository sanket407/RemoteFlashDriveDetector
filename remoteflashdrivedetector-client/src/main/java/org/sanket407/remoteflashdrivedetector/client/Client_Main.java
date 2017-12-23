package org.sanket407.remoteflashdrivedetector.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.SwingUtilities;

public class Client_Main
{
    public static void main(String[] args) throws Exception {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Client_Gui gui = new Client_Gui("ubuntu");
            }
        });
    }
}
