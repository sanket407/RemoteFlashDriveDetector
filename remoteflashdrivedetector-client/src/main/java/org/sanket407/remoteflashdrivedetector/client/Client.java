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

class Client
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

            System.out.println("Found " + serverName);

            serverIp = packet.getAddress();

            System.out.println("IP "+ serverIp);

            serverPort = packet.getPort();

            System.out.println("Port"+ serverPort);

            detectedServerCount++;

            gui.addServer(serverName, serverIp, serverPort);




        } catch (Exception e) {
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
                    {}

                }
            }
        }).start();




    }

    
    
    
    void startService2()
    {
        try
        {
            Runtime.getRuntime().exec("mount");
        }
        catch (IOException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }



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
                    {}

                }
            }
        }).start();




    }


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
    {try{

        this.clientSocket.close();
        this.serverIp=null;
        this.serverName=null;
        this.port=0;
    }
    catch(Exception e){}



    }

}