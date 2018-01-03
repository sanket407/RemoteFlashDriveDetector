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

    private String name;                 //general information
    private InetAddress ip;
    protected int  detected;
    private InetAddress group; 
    private int port ;

    private int detectedServerCount;
    private String serverName;           //server information 
    private InetAddress serverIp;
    private int serverPort;

    protected Socket clientSocket;          //socket information for tcp-ip
    protected PrintWriter sendToServer;
    protected static File[] oldListRoot;
    private Client_Gui gui;
    protected static int cnt;
    private Properties properties;

    public AbstractClient(Properties properties)
    {   
        setProperties(properties);
    }

    void initializeClient(String name) 
    {   
        setIpAddress(getProperty("ip"));
        setPort(getProperty("port"));
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
            MulticastSocket socket = new MulticastSocket(new InetSocketAddress(port));
            socket.joinGroup(group);

            DatagramPacket packet = new DatagramPacket(new byte[100], 100);

            socket.receive(packet);

            setServerName(new String( packet.getData(), 0,
                                      packet.getLength() ));

            setServerIp(packet.getAddress());

            setServerPort(packet.getPort());

            setDetectedServerCount(getDetectedServerCount() + 1);

            getGui().addServer(getServerName(), serverIp, serverPort);

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
                        getGui().serverStoppedAlert();
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
            setServerName(null);
            port=0;

        }
        catch(Exception e){
            System.out.println(e.toString());
        }
    }

    private void setIpAddress(String _ip) 
    {
        try
        {
            this.ip = InetAddress.getByName(_ip);
        }
        catch (UnknownHostException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void setPort(String _port)
    {
        this.port = Integer.parseInt(_port);
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

    private void setProperties(Properties prop)
    {
        properties = prop;
    }

    protected String getProperty(String name)
    {
        return properties.getProperty(name);
    }

    public Client_Gui getGui()
    {
        return gui;
    }

    public void setGui(Client_Gui gui)
    {
        this.gui = gui;
    }

    public int getDetectedServerCount()
    {
        return detectedServerCount;
    }

    public void setDetectedServerCount(int detectedServerCount)
    {
        this.detectedServerCount = detectedServerCount;
    }

    public String getServerName()
    {
        return serverName;
    }
    
    abstract void startService();
}