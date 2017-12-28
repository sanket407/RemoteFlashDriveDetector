package org.sanket407.remoteflashdrivedetector.client;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.swing.SwingUtilities;

public class Client_Main
{
    public static void main(String[] args) throws Exception {

        Properties prop = new Properties();
        try 
        {
            InputStream is = Client_Main.class.getResourceAsStream("/settings.properties");
            prop.load(is);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        final String operatingSystem = prop.getProperty("operating_system");
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Client_Gui gui = new Client_Gui(operatingSystem);
            }
        });
    }
}
