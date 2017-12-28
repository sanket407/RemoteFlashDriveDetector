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
import java.net.UnknownHostException;
import java.util.Properties;

import javax.swing.SwingUtilities;



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
    
    public AbstractClient(Properties properties)
    {
        InetAddress ip = null;
        try
        {
            ip = InetAddress.getByName(properties.getProperty("ip"));
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
        setIpAddress(ip);
        int port = Integer.parseInt(properties.getProperty("port"));
        setPort(port);
        
    }
    
    void initializeClient(String name) 
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
    } 

    

    void searchServer()
    {
        try
        {
            //InetAddress bind = InetAddress.getByName("192.168.1.105");
            MulticastSocket socket = new MulticastSocket(new InetSocketAddress(port));
            socket.joinGroup(group);

            DatagramPacket packet = new DatagramPacket(new byte[100], 100);

            socket.receive(packet);

            setServerName(new String( packet.getData(), 0,
                                     packet.getLength() ));

            setServerIp(packet.getAddress());

            setServerPort(packet.getPort());

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

            clientSocket.close();
            serverIp=null;
            serverName=null;
            port=0;
            
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
    }
    
    private void setIpAddress(InetAddress ip)
    {
        this.ip = ip;
    }

    private void setPort(int port)
    {
        this.port = port;
    }
    
    private void setName(String name1)
    {
        name = name1;
        
    }

    private void setGroup(String string) throws UnknownHostException
    {
        group = InetAddress.getByName(string);        
    }
    
    private void setServerName(String name)
    {
        serverName = name;
    }

    private void setServerIp(InetAddress address)
    {
        serverIp = address;
    }

    private void setServerPort(int port)
    {
        serverPort = port;
    }
    
       
    abstract void startService();
    
    
}