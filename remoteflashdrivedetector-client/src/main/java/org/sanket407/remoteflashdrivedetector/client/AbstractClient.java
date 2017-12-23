package org.sanket407.remoteflashdrivedetector.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.Socket;

abstract class AbstractClient
{

    String name;                 //general information
    InetAddress ip;
    int  detected;
    InetAddress group; 
    int port ;

    int detectedServerCount;
    String serverName;           //server information 
    InetAddress serverIp;
    int serverPort;

    Socket clientSocket;          //socket information for tcp-ip
    PrintWriter sendToServer;
    static File[] oldListRoot;
    Client_Gui gui;
    static int cnt;
    void initializeClient(String name) 
    {
        try{
            this.name = name;
            this.ip = InetAddress.getLocalHost();
            this.detected = 0;
            this.group = InetAddress.getByName("237.0.0.1");
            this.port = 9000;
            System.out.println(this.name);
        }
        catch(Exception e){}
    }

    void searchServer()
    {
        try
        {
            InetAddress bind = InetAddress.getByName("192.168.1.105");
            MulticastSocket socket = new MulticastSocket(new InetSocketAddress(port));
            socket.joinGroup(group);

            DatagramPacket packet = new DatagramPacket(new byte[100], 100);

            socket.receive(packet);

            serverName = new String( packet.getData(), 0,
                                     packet.getLength() );

            serverIp = packet.getAddress();

            serverPort = packet.getPort();

            detectedServerCount++;

            gui.addServer(serverName, serverIp, serverPort);

        } 
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    void connectToServer()throws Exception
    {
        clientSocket = new Socket(serverIp,serverPort);
        sendToServer = new PrintWriter(clientSocket.getOutputStream(), true);
        sendToServer.println(this.name);
        BufferedReader readFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        return ;
    }
/*
    void startService1()
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

    void startService2()
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
*/
    void getServerSignal()
    {
        new Thread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                try{
                    BufferedReader readFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                    String signal = readFromServer.readLine();

                    if(signal.equals("close"))
                    {
                        gui.serverStoppedAlert();
                    }
                }
                catch(Exception e){}
            }
        }).start();
    }

    void disconnectClient()
    {
        try{

            this.clientSocket.close();
            this.serverIp=null;
            this.serverName=null;
            this.port=0;
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
    }
    
    abstract void startService();
    
}