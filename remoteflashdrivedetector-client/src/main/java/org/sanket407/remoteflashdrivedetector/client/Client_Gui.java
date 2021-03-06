package org.sanket407.remoteflashdrivedetector.client;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.net.InetAddress;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

class Client_Gui {

    JFrame startupFrame, searchResultsFrame ; 
    JPanel searchResultsPanel ;
    AbstractClient client ;
    JButton enterButton;

    public Client_Gui(AbstractClient client)
    {           
        this.client = client;
        client.gui = this;
        startupWindow();
    }
    
    void startupWindow()
    {
        startupFrame = new JFrame ("Client");
        final JPanel startupPanel = new JPanel();
        final  JLabel nameLabel = new JLabel("Enter name of this machine");
        final JTextField nameField = new JTextField("",20);
        final JButton searchButton = new JButton("SEARCH SERVER");
        final JLabel connectingLabel = new JLabel("searching for server"); 
        enterButton = new JButton("Enter");

        startupPanel.add(nameLabel);
        startupPanel.add(nameField);
        startupPanel.add(enterButton);
        startupFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        startupFrame.add(startupPanel);
        startupFrame.pack();
        startupFrame.setLocation(200,200);
        startupFrame.setVisible(true);
       
        enterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                // TODO Auto-generated method stub
                String name = nameField.getText();
                client.initializeClient(name);
                client.detectedServerCount=0;
                startupPanel.removeAll();
                startupPanel.add(searchButton);
                startupFrame.revalidate();
                startupFrame.repaint();
                searchResultsFrame = new JFrame("Client");

            }
        });

        searchButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                // TODO Auto-generated method stub

                startupPanel.removeAll();

                startupPanel.add(connectingLabel);
                //startupFrame.pack();
                startupFrame.revalidate();

                startupFrame.repaint();
                initializeSearchPanel();

                @SuppressWarnings("rawtypes")
                SwingWorker aWorker = new SwingWorker() 
                {
                    @Override
                    protected Object doInBackground() throws Exception {
                        // TODO Auto-generated method stub

                        client.searchServer();
                        return null;
                    }

                    @Override
                    protected void done(){

                        startupFrame.setVisible(false);
                        startupFrame.dispose();
                        searchResultsFrame.setVisible(true);
                    }
                };
                aWorker.execute();
            }
        });
        startupFrame.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent x) {
                System.exit(1);
            }
        });
    }
    void initializeSearchPanel()
    {
        searchResultsPanel = new JPanel();
        searchResultsPanel.setLayout(new GridBagLayout());

        int gridwidth = 1;
        int gridheight = 1;
        Insets insets = new Insets(10,10,0,10);
        int anchor = GridBagConstraints.LINE_START;
        int fill = GridBagConstraints.HORIZONTAL;

        JLabel name = new JLabel("Name");
        JLabel add = new JLabel("Address");
        JLabel port = new JLabel("Port");

        GridBagConstraints gbc = new GridBagConstraints(0,0,
                                                        1, 1, 1.0D, 1.0D, anchor , fill ,insets , 0, 0);
        searchResultsPanel.add(name,gbc);
        gbc = new GridBagConstraints(1, 0,
                                     1, 1, 1.0D, 1.0D, anchor , fill ,insets , 0, 0);
        searchResultsPanel.add(add,gbc);
        gbc = new GridBagConstraints(2, 0,
                                     1, 1, 1.0D, 1.0D, anchor , fill ,insets , 0, 0);
        searchResultsPanel.add(port,gbc);

        searchResultsFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        searchResultsFrame.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent x) {
                try{
                    PrintWriter out = new PrintWriter(client.clientSocket.getOutputStream(),true);
                    out.println("close");
                }catch(Exception e){}
                client.disconnectClient();
                System.exit(1);
            }
        });

        searchResultsFrame.revalidate();
        searchResultsFrame.repaint();
    }


    void addServer(String name , InetAddress ip , int port)
    {
        JLabel  namefield = new JLabel(name );
        JLabel ipfield = new JLabel(ip.toString().substring(1));
        JLabel portfield = new JLabel(String.valueOf(port));
        JButton connectButton = new JButton("Connect");

        int gridwidth = 1;
        int gridheight = 1;
        Insets insets = new Insets(10,10,0,10);
        int anchor = GridBagConstraints.LINE_START;
        int fill = GridBagConstraints.HORIZONTAL;

        GridBagConstraints gbc = new GridBagConstraints(0, client.detectedServerCount,
                                                        1, 1, 1.0D, 1.0D, anchor , fill ,insets , 0, 0);
        searchResultsPanel.add(namefield,gbc);

        gbc = new GridBagConstraints(1, client.detectedServerCount,
                                     gridwidth, gridheight, 1.0D, 1.0D, anchor, fill, insets, 0, 0);
        searchResultsPanel.add(ipfield,gbc);

        gbc = new GridBagConstraints(2, client.detectedServerCount,
                                     gridwidth, gridheight, 1.0D, 1.0D, anchor, fill, insets, 0, 0);
        searchResultsPanel.add(portfield,gbc);

        gbc = new GridBagConstraints(3, client.detectedServerCount,
                                     gridwidth, gridheight, 1.0D, 1.0D, anchor, fill, insets, 0, 0);
        searchResultsPanel.add(connectButton,gbc);
        searchResultsFrame.add(searchResultsPanel);
        
        searchResultsFrame.revalidate();
        searchResultsFrame.repaint();
        searchResultsFrame.pack();
        searchResultsFrame.setLocation(200,200);

        connectButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                // TODO Auto-generated method stub

                searchResultsPanel.removeAll();
                JLabel connecting = new JLabel("Connecting");

                searchResultsPanel.add(connecting);
                searchResultsFrame.revalidate();
                searchResultsFrame.repaint();
                searchResultsFrame.pack();
                searchResultsFrame.setLocation(200,200);
                try{    
                    client.connectToServer();
                }
                catch(Exception e){}
                client.gui.startService();
            }
        });
    }          
    void startService()
    {
        searchResultsPanel.removeAll();
        searchResultsPanel.setLayout(new BoxLayout(searchResultsPanel,BoxLayout.Y_AXIS));
        JLabel connected = new JLabel("connected to "+client.serverName);

        searchResultsPanel.add(connected);
        JButton disconnectButton = new JButton("Disconnect");
        searchResultsPanel.add(disconnectButton);
        searchResultsFrame.revalidate();
        searchResultsFrame.repaint();
        searchResultsFrame.pack();
        searchResultsFrame.setLocation(200,200);
        client.startService();
        client.getServerSignal();

        disconnectButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                // TODO Auto-generated method stub
                try{

                    PrintWriter out = new PrintWriter(client.clientSocket.getOutputStream(),true);
                    out.println("close");
                    client.disconnectClient();
                    searchResultsPanel.removeAll();
                    searchResultsFrame.setVisible(false);
                    searchResultsFrame.dispose();
                    startupFrame.setVisible(true);    
                    client.gui.enterButton.doClick();
                }
                catch(Exception e){}
            }
        });
    }

    void serverStoppedAlert()
    {

        client.disconnectClient();

        final JFrame alertFrame = new JFrame("Alert");

        JPanel alertPanel = new JPanel();

        JLabel  alertText= new JLabel("Server has been stopped");

        JButton alertButton = new JButton("OK !");

        alertPanel.setLayout(new BoxLayout(alertPanel,BoxLayout.Y_AXIS));

        alertPanel.add(alertText);
        alertPanel.add(alertButton);
        alertFrame.add(alertPanel);
        alertFrame.setVisible(true);
        alertFrame.pack();
        alertFrame.setLocation(200,200);
        alertFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        alertFrame.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent x) {

                searchResultsPanel.removeAll();
                searchResultsFrame.setVisible(false);
                searchResultsFrame.dispose();
                startupFrame.setVisible(true);  
                alertFrame.setVisible(false);
                alertFrame.dispose();
                client.gui.enterButton.doClick();
            }

        });

        alertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                searchResultsPanel.removeAll();
                searchResultsFrame.setVisible(false);
                searchResultsFrame.dispose();
                startupFrame.setVisible(true);    
                alertFrame.setVisible(false);
                alertFrame.dispose();
                client.gui.enterButton.doClick();
            }
        });
    }
}