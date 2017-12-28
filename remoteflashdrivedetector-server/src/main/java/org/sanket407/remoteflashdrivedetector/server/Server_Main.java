package org.sanket407.remoteflashdrivedetector.server;

import java.io.InputStream;
import java.util.Properties;

import javax.swing.SwingUtilities;

public class Server_Main
{
    public static void main(String[] args) throws Exception{
        
        Properties properties = new Properties();
        try 
        {
            InputStream is = Server_Main.class.getResourceAsStream("/settings.properties");
            properties.load(is);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        final String operatingSystem = properties.getProperty("operating_system");
        final Server server ;
        
        server = new Server(properties);
        
        startGUI(server);
    }
    
    static void startGUI(final Server server)
    {   
      SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                final Server_Gui gui = new Server_Gui(server);
            }
        });
    }
}