package org.sanket407.remoteflashdrivedetector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

class Server
{

    InetAddress ip;
    String name;
    int clientCount;
    InetAddress group;
    int port;

    ServerSocket serverSocket;
    HashMap  <Socket,String> clientSocketMap;

    Server_Gui gui;

    void initializeServer(String name) 
    {


        try{
            this.ip = InetAddress.getLocalHost(); 
            this.name = name;



            this.clientCount = 0;
            this.group = InetAddress.getByName("237.0.0.1");
            this.port = 9000;
            this.clientSocketMap = new HashMap<Socket,String>();
        }
        catch(Exception e){}

    }

    void startServer()throws IOException
    {

        serverSocket = new ServerSocket(this.port);

        final byte[] bt = this.name.getBytes();
        new Thread(new Runnable() {
            public void run() {
                try {
                    MulticastSocket socket = new MulticastSocket(port);
                    socket.setInterface(InetAddress.getLocalHost());
                    socket.joinGroup(group);


                    while(true) {

                        socket.send(new DatagramPacket(bt, bt.length, group, port));

                        Thread.sleep(1*1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }


    void acceptConnections() {

        new Thread(new Runnable(){

            public void run(){


                try
                {

                    while(true)
                    {
                        Socket clientSocket =  serverSocket.accept();

                        clientCount++;
                        BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                        String name = br.readLine();
                        System.out.println(name+" connected");

                        PrintWriter out = new PrintWriter (clientSocket.getOutputStream(),true);
                        out.println("accepted");
                        gui.addClient(clientSocket,name,clientSocket.getInetAddress(),clientSocket.getPort());
                        clientSocketMap.put(clientSocket,name);
                        startDetecting(clientSocket);

                        Thread.sleep(1000);

                    }}
                catch (Exception e)
                {}

            } 
        }).start();





    }

    void startDetecting(final Socket clientSocket)
    {

        new Thread(new Runnable(){

            public void run()
            {

                while (true)
                {
                    try{
                        BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                        String status = br.readLine();
                        if(status.equals("close"))
                        {//System.out.println(clientSocketMap.get(clientSocket)+" disconnected");
                            clientSocketMap.remove(clientSocket);
                            gui.removeClient(clientSocket);
                            clientCount--;
                            clientSocket.close(); 

                            break;
                        }
                        else if(status.equals("inserted"))
                        {
                            //System.out.println(clientSocketMap.get(clientSocket)+" "+status);
                            gui.update(clientSocket,1);
                        }
                        else
                        {
                            //  System.out.println(clientSocketMap.get(clientSocket)+" "+status);
                            gui.update(clientSocket,-1);
                        }


                        Thread.sleep(1000);}

                    catch(Exception e){}

                }




            }



        }).start();




    }

    void stopServer()
    {
        for(Map.Entry<Socket, String> mapEntry : clientSocketMap.entrySet())
        {
            Socket clientSocket = mapEntry.getKey();
            try{
                PrintWriter sendToClient = new PrintWriter(clientSocket.getOutputStream(),true);



                sendToClient.println("close");
            }

            catch(Exception e){}
        }


    }

}