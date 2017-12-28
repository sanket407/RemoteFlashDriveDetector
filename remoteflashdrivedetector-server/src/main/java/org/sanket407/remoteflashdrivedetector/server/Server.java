package org.sanket407.remoteflashdrivedetector.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.GroupLayout.SequentialGroup;

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
    
    Server(Properties properties)
    {
        String inetAdd = properties.getProperty("ip");
        InetAddress ip = null;
        try
        {   
            System.out.println(inetAdd);
            ip = InetAddress.getByName(inetAdd);
        }
        catch (UnknownHostException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        setIp(ip);
        
        int port = Integer.parseInt(properties.getProperty("port"));
        setPort(port);
    }
    
  

    void initializeServer(String name) 
    {
        setName(name);
        try
        {
            setGroup("237.0.0.1");
        }
        catch (UnknownHostException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        setclientSocketMap(new HashMap<Socket,String>());
    }
    
   

    void startServer()throws IOException
    {
        serverSocket = new ServerSocket(this.port);

        final byte[] bt = this.name.getBytes();
        new Thread(new Runnable() {
            public void run() {
                try {
                    InetAddress bind = ip;
                    MulticastSocket socket = new MulticastSocket(new InetSocketAddress(bind,port));
                    socket.joinGroup(group);

                    while(true) 
                    {
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
                        PrintWriter out = new PrintWriter (clientSocket.getOutputStream(),true);
                        out.println("accepted");
                        gui.addClient(clientSocket,name,clientSocket.getInetAddress(),clientSocket.getPort());
                        clientSocketMap.put(clientSocket,name);
                        startDetecting(clientSocket);
                        Thread.sleep(1000);
                    }
                   }
                    catch (Exception e)
                    {
                        System.out.println(e.toString());
                    }
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
                            gui.update(clientSocket,1);
                        }
                        else
                        {
                            gui.update(clientSocket,-1);
                        }
                        Thread.sleep(1000);}

                    catch(Exception e){
                        System.out.println(e.toString());
                    }
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
    
    private void setIp(InetAddress ip2)
    {
       ip = ip2;        
    }

    private void setPort(int port)
    {
        this.port = port;        
    }
    
    private void setGroup(String string) throws UnknownHostException
    {
        group = InetAddress.getByName(string);        
    }

    private void setName(String name)
    {
        this.name = name;        
    }

    private void setclientSocketMap(HashMap<Socket, String> hashMap)
    {
        this.clientSocketMap = hashMap;
        
    }
}