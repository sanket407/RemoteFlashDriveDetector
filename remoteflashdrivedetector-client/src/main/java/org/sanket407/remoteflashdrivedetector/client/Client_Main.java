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

        Properties properties = new Properties();
        try 
        {
            InputStream is = Client_Main.class.getResourceAsStream("/settings.properties");
            properties.load(is);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        final String operatingSystem = properties.getProperty("operating_system");
        final AbstractClient client ;
        
        if(operatingSystem.equals("ubuntu"))
            {
                client = new UbuntuClient(properties);
                startGUI(client);
            }
        else if(operatingSystem.equals("windows"))
            {
                client = new WindowsClient(properties);
                startGUI(client);
            }
        else
            {
                System.out.println("Invalid operating system property.");
                System.exit(1);
            }
    }
    
    static void startGUI(final AbstractClient client)
    {   
      SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                final Client_Gui gui = new Client_Gui(client);
            }
        });
    }
}
