package org.sanket407.remoteflashdrivedetector.client;

import java.io.File;

public class UbuntuClient extends AbstractClient
{
    void startService()
    {
       final File root = new File("/media/sanket/");
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
