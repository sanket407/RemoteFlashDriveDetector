package org.sanket407.remoteflashdrivedetector.client;

import java.io.File;

public class WindowsClient extends AbstractClient
{
    
    
    void startService()
    {
        oldListRoot = File.listRoots();
        new Thread(new Runnable(){

            public void run(){
                while (true) {
                    try{
                        if (File.listRoots().length > oldListRoot.length) {
                            sendToServer.println("inserted");
                            oldListRoot = File.listRoots();
                            detected++;

                        } else if (File.listRoots().length < oldListRoot.length) {
                            sendToServer.println("removed");

                            oldListRoot = File.listRoots();
                            detected--;
                        }
                        else
                        {    
                            Thread.sleep(1000);
                            if(clientSocket.isClosed())
                                break;
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
