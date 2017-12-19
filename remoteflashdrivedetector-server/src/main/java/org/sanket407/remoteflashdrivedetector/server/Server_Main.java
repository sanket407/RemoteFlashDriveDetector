package org.sanket407.remoteflashdrivedetector.server;

import javax.swing.SwingUtilities;

public class Server_Main
{
    public static void main(String[] args) throws Exception{
        
        SwingUtilities.invokeLater(new Runnable() {
             
            public void run() {
                final Server_Gui gui = new Server_Gui();
            }
        });
    }
}