package org.sanket407.remoteflashdrivedetector;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.SwingUtilities;

public class Client_Main
{
    public static void main(String[] args) throws Exception {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final Client_Gui gui = new Client_Gui();

            }
        });



    }}
