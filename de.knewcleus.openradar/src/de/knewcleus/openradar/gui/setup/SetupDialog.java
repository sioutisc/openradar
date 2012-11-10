package de.knewcleus.openradar.gui.setup;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 * The setup dialog...
 * 
 * @author Wolfram Wagner
 */
public class SetupDialog extends JFrame {

    private static final long serialVersionUID = -3703867188892313521L;
    private SetupController setupManager;

    private JTextField tfSearchBox;
    private JList<SectorBean> liSearchResults;
    private JTextField tfFgComExec;
    private JTextField tfFgComServer;
    private JTextField tfFgComHost;
    private JTextField tfFgComPorts;
    private JTextField tfMpServer;
    private JTextField tfMpPort;
    private JTextField tfMpLocalPort;
    private JTextField tfMetarUrl;
    private JLabel lbfgComExec;
    private JLabel lbFgComServer;
    private JLabel lbFgComHost;
    private JLabel lbFgComPorts;
    private JPanel jPnlMultiplayer;
    private JLabel lbMpServer;
    private JLabel lbMpPort;
    private JLabel lbMpLocalPort;
    private JLabel lbMetarUrl;
    private JLabel lbMessage;
    private JButton btStart;
    private JButton btCheckSettings;
    private JPanel jPnlSettings;
    private JButton btCreateSector;
    private JComboBox<String> cbStatusMessages;
    private JProgressBar jProgressBar;
    private StatusMessageComboboxModel cbStatusModel;

    public SetupDialog(SetupController setupManager) {
        this.setupManager = setupManager;

        initComponents();

        this.loadProperties();
    }

    private void initComponents() {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("OpenRadar - Welcome!");
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle maxBounds = env.getMaximumWindowBounds();

        this.setLocation((int) maxBounds.getWidth() / 2 - 200, (int) maxBounds.getHeight() / 2 - 200);
        this.setSize(400, 550);

        JPanel jPnlContentPane = new JPanel();
        jPnlContentPane.setLayout(new GridBagLayout());
        setContentPane(jPnlContentPane);
        
        JTabbedPane jtpMain = new JTabbedPane();
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx=1;
        gridBagConstraints.weighty=1;
        gridBagConstraints.fill=GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0,0, 0, 0);
        jPnlContentPane.add(jtpMain, gridBagConstraints);
        
        cbStatusMessages = new JComboBox<String>();
        cbStatusModel = new StatusMessageComboboxModel();
        cbStatusMessages.setEditable(false);
        //cbStatusMessages.setEnabled(false);
        cbStatusMessages.setFont(cbStatusMessages.getFont().deriveFont(7));
        cbStatusMessages.setModel(cbStatusModel);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx=1;
        gridBagConstraints.weighty=0;
        gridBagConstraints.fill=GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0,0, 0, 0);
        jPnlContentPane.add(cbStatusMessages, gridBagConstraints);        
        
        jProgressBar = new JProgressBar();
        jProgressBar.setMinimum(0);
        jProgressBar.setMaximum(100);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx=1;
        gridBagConstraints.weighty=0;
        gridBagConstraints.fill=GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0,0, 0, 0);
        jPnlContentPane.add(jProgressBar, gridBagConstraints);

        // TAB SELECT/CREATE AIRPORT

        JPanel jPnlSelectAirport = new JPanel();
        jtpMain.add("Select Airport", jPnlSelectAirport);
        jPnlSelectAirport.setLayout(new GridBagLayout());

        JLabel lbWelcome = new JLabel();
        lbWelcome.setText("Welcome to OpenRadar!");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(12, 4, 0, 2);
        jPnlSelectAirport.add(lbWelcome, gridBagConstraints);

        JLabel lbPleaseSelect = new JLabel();
        lbPleaseSelect.setText("Please select your airport!");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlSelectAirport.add(lbPleaseSelect, gridBagConstraints);

        tfSearchBox = new JTextField();
        tfSearchBox.setName("SearchBox");
        tfSearchBox.setToolTipText("Please enter code or name of airport! (Wildcard *)");
        tfSearchBox.addActionListener(setupManager.getActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.insets = new java.awt.Insets(12, 8, 0, 4);
        jPnlSelectAirport.add(tfSearchBox, gridBagConstraints);

        JButton btSearch = new JButton();
        btSearch.setText("search");
        btSearch.setName("SearchButton");
        btSearch.addActionListener(setupManager.getActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 8);
        jPnlSelectAirport.add(btSearch, gridBagConstraints);

        JButton btShowExisting = new JButton();
        btShowExisting.setText("existing");
        btShowExisting.setToolTipText("Reset search results to existing sectors");
        btShowExisting.setName("ShowExistingButton");
        btShowExisting.addActionListener(setupManager.getActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 8);
        jPnlSelectAirport.add(btShowExisting, gridBagConstraints);

        JScrollPane jsPane = new JScrollPane();
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 0, 8);
        jPnlSelectAirport.add(jsPane, gridBagConstraints);
        
        liSearchResults = new JList<SectorBean>();
        liSearchResults.setModel(setupManager.getSearchResultsModel());
        liSearchResults.setName("SearchResultList");
        liSearchResults.setVisibleRowCount(10);
        liSearchResults.setCellRenderer(new SectorBeanRenderer());
        liSearchResults.addListSelectionListener(setupManager.getSectorListSelectionListener());
        liSearchResults.addMouseListener(setupManager.getSectorListMouseListener());
        jsPane.getViewport().add(liSearchResults);
        
        lbMessage = new JLabel();
        lbMessage.setForeground(Color.red);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlSelectAirport.add(lbMessage, gridBagConstraints);

        JPanel jPnlButtons = new JPanel();
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPnlSelectAirport.add(jPnlButtons, gridBagConstraints);

        jPnlButtons.setLayout(new GridBagLayout());

        btCreateSector = new JButton();
        btCreateSector.setText("Download Scenery");
        btCreateSector.setName("DownloadButton");
        btCreateSector.setEnabled(false);
        btCreateSector.addActionListener(setupManager.getActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 2);
        jPnlButtons.add(btCreateSector, gridBagConstraints);

        btStart = new JButton();
        btStart.setText("Start");
        btStart.setName("StartButton");
        btStart.setEnabled(false);
        btStart.addActionListener(setupManager.getActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 4);
        jPnlButtons.add(btStart, gridBagConstraints);

        // TAB SETTINGS

        jPnlSettings = new JPanel();
        jtpMain.add("Settings", jPnlSettings);
        jPnlSettings.setLayout(new GridBagLayout());

        // FGCom

        JPanel jPnlFgCom = new JPanel();
        jPnlFgCom.setLayout(new GridBagLayout());
        jPnlFgCom.setBorder(new TitledBorder("FGCom"));
        jPnlFgCom.setToolTipText("OpenRadar will start and control FGCom in the background. It must be installed separately!");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlSettings.add(jPnlFgCom, gridBagConstraints);

        // executable

        lbfgComExec = new JLabel();
        lbfgComExec.setText("Executable:");
        lbfgComExec.setToolTipText("Executable of FGCom if it should be started loaclly in background");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFgCom.add(lbfgComExec, gridBagConstraints);

        tfFgComExec = new JTextField();
        tfFgComExec.setName("FgComExec");
        tfFgComExec.setText("");
        tfFgComExec.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFgCom.add(tfFgComExec, gridBagConstraints);

        JButton btBrowseFgComServer = new JButton();
        btBrowseFgComServer.setText("Search...");
        btBrowseFgComServer.setName("SearchFgCom");
        btBrowseFgComServer.addActionListener(setupManager.getActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFgCom.add(btBrowseFgComServer, gridBagConstraints);

        // server

        lbFgComServer = new JLabel();
        lbFgComServer.setText("Server");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        jPnlFgCom.add(lbFgComServer, gridBagConstraints);

        tfFgComServer = new JTextField();
        tfFgComServer.setName("FgComServer");
        tfFgComServer.setToolTipText("Please enter the server address!");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFgCom.add(tfFgComServer, gridBagConstraints);

        // host

        lbFgComHost = new JLabel();
        lbFgComHost.setText("Client");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFgCom.add(lbFgComHost, gridBagConstraints);

        tfFgComHost = new JTextField();
        tfFgComHost.setName("FgComHost");
        tfFgComHost.setToolTipText("The machine that runs fgcom, if set to 'localhost', OpenRadar will start it automatically..");
        tfFgComHost.setText("localhost");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFgCom.add(tfFgComHost, gridBagConstraints);

        // ports

        lbFgComPorts = new JLabel();
        lbFgComPorts.setText("FgCom Ports");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFgCom.add(lbFgComPorts, gridBagConstraints);

        tfFgComPorts = new JTextField();
        tfFgComPorts.setName("FgComPorts");
        tfFgComPorts.setToolTipText("Comma separated list of FgCom Ports, two are enough, four maximum, e.g. '16661,16662'");
        tfFgComPorts.setText("16661");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 2);
        jPnlFgCom.add(tfFgComPorts, gridBagConstraints);

        // Multiplayer

        jPnlMultiplayer = new JPanel();
        jPnlMultiplayer.setLayout(new GridBagLayout());
        jPnlMultiplayer.setBorder(new TitledBorder("FG Multiplayer"));
        jPnlMultiplayer.setToolTipText("These settings should be correct already!");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 4, 0, 2);
        jPnlSettings.add(jPnlMultiplayer, gridBagConstraints);

        lbMpServer = new JLabel();
        lbMpServer.setText("Server");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlMultiplayer.add(lbMpServer, gridBagConstraints);

        tfMpServer = new JTextField();
        tfMpServer.setName("MpServer");
        tfMpServer.setToolTipText("Address of FlightGear multiplayer server");
        tfMpServer.setText("mpserver01.flightgear.com");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlMultiplayer.add(tfMpServer, gridBagConstraints);

        lbMpPort = new JLabel();
        lbMpPort.setText("Server Port");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlMultiplayer.add(lbMpPort, gridBagConstraints);

        tfMpPort = new JTextField();
        tfMpPort.setName("MpServer");
        tfMpPort.setToolTipText("Port of FlightGear multiplayer server");
        tfMpPort.setText("5000");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlMultiplayer.add(tfMpPort, gridBagConstraints);

        lbMpLocalPort = new JLabel();
        lbMpLocalPort.setText("Client port");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlMultiplayer.add(lbMpLocalPort, gridBagConstraints);

        tfMpLocalPort = new JTextField();
        tfMpLocalPort.setName("MpPort");
        tfMpLocalPort.setToolTipText("Port of FlightGear multiplayer client on this machine");
        tfMpLocalPort.setText("5001");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlMultiplayer.add(tfMpLocalPort, gridBagConstraints);

        // METAR

        JPanel jPnlMetar = new JPanel();
        jPnlMetar.setLayout(new GridBagLayout());
        jPnlMetar.setBorder(new TitledBorder("METAR"));
        jPnlMetar.setToolTipText("These settings should be correct already!");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 4, 0, 2);
        jPnlSettings.add(jPnlMetar, gridBagConstraints);

        lbMetarUrl = new JLabel();
        lbMetarUrl.setText("METAR URL");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlMetar.add(lbMetarUrl, gridBagConstraints);

        tfMetarUrl = new JTextField();
        tfMetarUrl.setName("MetarUrl");
        tfMetarUrl.setToolTipText("URL of weather data provider");
        tfMetarUrl.setText("http://weather.noaa.gov/pub/data/observations/metar/stations/");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 2);
        jPnlMetar.add(tfMetarUrl, gridBagConstraints);

        btCheckSettings = new JButton();
        btCheckSettings.setText("Check Settings");
        btCheckSettings.setName("CheckButton");
        btCheckSettings.addActionListener(setupManager.getActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.insets = new java.awt.Insets(12, 2, 2, 2);
        jPnlSettings.add(btCheckSettings, gridBagConstraints);

        doLayout();
    }

    public String getSearchTerm() {
        return tfSearchBox.getText();
    }

    public SectorBean getSelectedSector() {
        return liSearchResults.getSelectedValue();
    }

    public boolean readInputs(AirportData data) {
        btCheckSettings.setText("Checking...");
        btCheckSettings.setEnabled(false);
        btCheckSettings.setForeground(Color.gray);
        
        boolean dataOk = true;

        List<Integer> list = null;
        if(!tfFgComExec.getText().isEmpty()) {
            if (checkHost(tfFgComHost.getText())) {
                lbFgComHost.setForeground(Color.black);
                data.setFgComHost(tfFgComHost.getText());
            } else {
                lbFgComHost.setForeground(Color.red);
                dataOk = false;
            }
            if (checkPath(tfFgComExec.getText())) {
                lbfgComExec.setForeground(Color.black);
                data.setFgComPath(tfFgComExec.getText());
            } else {
                lbfgComExec.setForeground(Color.red);
                dataOk = false;
            }
            if ((list = checkPorts(tfFgComPorts.getText())).size() > 0 &&
                    (list = checkPorts(tfFgComPorts.getText())).size() < 5) {
                lbFgComPorts.setForeground(Color.black);
                data.setFgComPorts(list);
            } else {
                lbFgComPorts.setForeground(Color.red);
                dataOk = false;
            }
            if (checkHost(tfFgComServer.getText())) {
                lbFgComServer.setForeground(Color.black);
                data.setFgComServer(tfFgComServer.getText());
            } else {
                lbFgComServer.setForeground(Color.red);
                dataOk = false;
            }
        }
        if (checkUrl(tfMetarUrl.getText())) {
            lbMetarUrl.setForeground(Color.black);
            data.setMetarUrl(tfMetarUrl.getText());
        } else {
            lbMetarUrl.setForeground(Color.red);
            dataOk = false;
        }
        if((list = checkPorts(tfMpLocalPort.getText())).size() > 0) {
            lbMpLocalPort.setForeground(Color.black);
            data.setMpLocalPort(list.get(0));
        } else {
            lbMpLocalPort.setForeground(Color.red);
            dataOk = false;
        }
        if (checkHost(tfMpServer.getText())) {
            lbMpServer.setForeground(Color.black);
            data.setMpServer(tfMpServer.getText());
        } else {
            lbMpServer.setForeground(Color.red);
            dataOk = false;
        }
        if ((list = checkPorts(tfMpPort.getText())).size() == 1) {
            lbMpPort.setForeground(Color.black);
            data.setMpServerPort(list.get(0));
        } else {
            lbMpPort.setForeground(Color.red);
            dataOk = false;
        }
        if ((list = checkPorts(tfMpLocalPort.getText())).size() == 1) {
            lbMpLocalPort.setForeground(Color.black);
            data.setMpLocalPort(list.get(0));
        } else {
            lbMpLocalPort.setForeground(Color.red);
            dataOk = false;
        }

        btCheckSettings.setText("Check Settings");
        btCheckSettings.setEnabled(true);
        btCheckSettings.setForeground(Color.black);

        if (!dataOk) {
            lbMessage.setText("Please verify your settings!");
            return false;
        } else {
            lbMessage.setText(null);
            saveProperties();
            return true;
        }
    }

    private boolean checkUrl(String sUrl) {
        if(sUrl.isEmpty()) return false;
        try {
            URL url = new URL(sUrl);
            return checkHost(url.getHost());
        } catch (MalformedURLException e) {
            return false;
        }
    }

    private List<Integer> checkPorts(String text) {
        try {
            List<Integer> result = new ArrayList<Integer>();
            StringTokenizer st = new StringTokenizer(text, ",");
            while (st.hasMoreElements()) {
                int port = Integer.parseInt(st.nextToken());
                if (port < 1025 || port > 65535)
                    return new ArrayList<Integer>();
                result.add(port);
            }
            return result;
        } catch (Exception e) {
            return new ArrayList<Integer>();
        }
    }

    private boolean checkPath(String text) {
        if(text.isEmpty()) return false;
        return true;
    }

    private boolean checkHost(String hostname) {
        if(hostname.isEmpty()) return false;
        try {
            InetAddress address = Inet4Address.getByName(hostname);
            return address != null;
        } catch (UnknownHostException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void loadProperties() {
        File defaultsFile = new File("sectors" + File.separator + "defaults.properties");
        File userFile = new File("sectors" + File.separator + "user.properties");
        FileReader defReader = null;
        FileReader userReader = null;
        if (defaultsFile.exists()) {
            Properties p = new Properties();
            try {
                defReader = new FileReader(defaultsFile);
                p.load(defReader);
                defReader.close();
                if (userFile.exists()) {
                    userReader = new FileReader(userFile);
                    p.load(userReader);

                }

            } catch (IOException e) {

            } finally {
                if (defReader != null) {
                    try {
                        defReader.close();
                    } catch (IOException e) {}
                }
                if (userReader != null) {
                    try {
                        userReader.close();
                    } catch (IOException e) {}
                }
                tfFgComExec.setText(p.getProperty("fgCom.exec", ""));
                tfFgComServer.setText(p.getProperty("fgCom.server", ""));
                ;
                tfFgComHost.setText(p.getProperty("fgCom.host", "localhost"));
                ;
                tfFgComPorts.setText(p.getProperty("fgCom.clientPorts", "16661,16662"));
                ;
                tfMpServer.setText(p.getProperty("mp.server", "mpserver01.flightgear.org"));
                ;
                tfMpPort.setText(p.getProperty("mp.serverPort", "5000"));
                ;
                tfMpLocalPort.setText(p.getProperty("mp.clientPort", "5001"));
                ;
                tfMetarUrl.setText(p.getProperty("metar.url", "http://weather.noaa.gov/pub/data/observations/metar/stations/"));
                ;
            }

        }
    }

    private void saveProperties() {
        File userFile = new File("sectors" + File.separator + "user.properties");
        Properties p = new Properties();
        p.put("fgCom.exec", tfFgComExec.getText());
        p.put("fgCom.server", tfFgComServer.getText());
        p.put("fgCom.host", tfFgComHost.getText());
        p.put("fgCom.clientPorts", tfFgComPorts.getText());
        p.put("mp.server", tfMpServer.getText());
        p.put("mp.serverPort", tfMpPort.getText());
        p.put("mp.clientPort", tfMpLocalPort.getText());
        p.put("metar.url", tfMetarUrl.getText());

        FileWriter userWriter = null;
        try {
            if (userFile.exists())
                userFile.delete();
            userWriter = new FileWriter(userFile);

            p.store(userWriter, "Open Radar user settings... This file will be overwritten!");
        } catch (IOException e) {

        } finally {
            if (userWriter != null) {
                try {
                    userWriter.close();
                } catch (IOException e) {}
            }
        }
    }

    public void sectorSelected(AirportData data) {
        SectorBean sb = liSearchResults.getSelectedValue(); 
        if(sb!=null) {
            if(sb.isSectorDownloaded()) {
                data.setAirportCode(sb.getAirportCode());
                data.setAirportPosition(sb.getPosition());
                data.setAirportName(sb.getAirportName());
                btStart.setEnabled(readInputs(data));
                btCreateSector.setEnabled(false);
            } else {
                data.setAirportCode(sb.getAirportCode());
                data.setAirportName(sb.getAirportName());
                btStart.setEnabled(false);
                btCreateSector.setEnabled(true);
            }
        } else {
            data.setAirportCode(null);
            btStart.setEnabled(false);
            btCreateSector.setEnabled(false);
        }
    }

    public void setStatus(int progress, String message) {
        jProgressBar.setValue(progress);
        jProgressBar.setString(""+progress+"%");
        Rectangle rect = jProgressBar.getBounds();  
        rect.x = 0;  
        rect.y = 0;  
        jProgressBar.paintImmediately( rect );
        cbStatusModel.addNewStatusMessage(message);
        cbStatusMessages.setSelectedItem(message);
        rect = cbStatusMessages.getBounds();  
        rect.x = 0;  
        rect.y = 0;  
        cbStatusMessages.paintImmediately( rect );  

    }
}
