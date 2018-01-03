package org.sanket407.remoteflashdrivedetector.client;

import java.io.File;
import java.util.Properties;

public class UbuntuClient extends AbstractClient
{
    public UbuntuClient(Properties properties)
    {
        super(properties);
    }

    void startService()
    {  
        String userName = getProperty("username");
        final File root = new File("/media/" + userName + "/");
        cnt = root.listFiles().length;

        new Thread(new Runnable(){
            public void run(){
                while (true) {
                    try{
                        if (root.listFiles().length > cnt) {
                            sendToServer.println("inserted");
                            cnt = root.listFiles().length;
                            detected++;
                        } else if (root.listFiles().length < cnt) {
                            sendToServer.println("removed");

                            cnt = root.listFiles().length;
                            detected--;
                        }
                        else
                        {    
                            Thread.sleep(1000);
                            if(clientSocket.isClosed())
                                break;
                            System.out.println(cnt);
                        }
                    }
                    catch(Exception e)
                    {
                        System.out.println(e.toString());
                    }
                }
            }
        }).start();
    }

}
