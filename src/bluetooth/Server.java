/*
 * Copyright (c) 2011, Preet Kamal Singh Minhas, http://pksarena.com
 * All rights reserved.
 */
package bluetooth;

import java.io.DataInputStream;
import java.io.IOException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

/**
 * Bluetooth server related functionality.
 * Server plays the role of data receiver.
 * Singleton but must be initialized by calling the
 * static method init(ServerCaller) before an instance can be obtained.
 * @author Preet Kamal Singh Minhas
 */
public class Server implements Runnable {
    //reference to the local bluetooth device

    private LocalDevice mLocalBT;
    //stream connection notifier
    private StreamConnectionNotifier mServerNotifier;
    //flag to track when to end the server
    private volatile boolean endNow = false;
    //flag to indicate server status
    private boolean isServerStarted = false;
    //thread for executing the server
    private Thread myServerThread;
    private static boolean isInitialized = false;
    private static Server myServer = new Server();
    //reference to the serverCaller object which has callbacks defined.
    private static ServerCaller serverCaller;

    public static void init(ServerCaller caller) {
        serverCaller = caller;
        isInitialized = true;
    }

    public static Server getInstance() {
        if (isInitialized == false) {
            throw new RuntimeException("Server not initialized properly!");
        }
        return myServer;
    }

    private Server() {
        //singleton
    }

    /**
     * Starts the server thread.
     */
    public void startServer() {
        if (isServerStarted) {
            return;
        }
        endNow = false;
        // Start server thread
        myServerThread = new Thread(getInstance());
        myServerThread.start();
        isServerStarted = true;
    }

    /**
     * Stops the server
     */
    public void stopServer() {
        endNow = true;
        // Close the notifier
        if (mServerNotifier != null) {
            try {
                mServerNotifier.close();
            } catch (IOException e) {
            } // ignore
        }
        // close server thread
        myServerThread.interrupt();
        isServerStarted = false;

    }

    public void run() {
        try {
            // Get local BT manager
            mLocalBT = LocalDevice.getLocalDevice();
            // Set we are discoverable
            mLocalBT.setDiscoverable(DiscoveryAgent.GIAC);
            String url = "btspp://localhost:" + BTCommon.BLUCHAT_SERVICE_ID.toString()
                    + ";name=BluChat Service;authorize=false";
            // Create notifier now
            mServerNotifier = (StreamConnectionNotifier) Connector.open(
                    url.toString());
        } catch (Exception e) {
            setAppError("Can't initialize bluetooth: " + e.getMessage());
            return;
        }
        StreamConnection conn = null;
        //accept incoming data
        while (!endNow) {
            conn = null;
            try {
                conn = mServerNotifier.acceptAndOpen();
            } catch (IOException e) {
                setAppError(e.getMessage());
                continue;
            }
            if (conn != null) {
                processRequest(conn);
            }
        }
    }

    private void processRequest(StreamConnection conn) {
        DataInputStream dis = null;
        String message = null;
        try {
            dis = conn.openDataInputStream();
            message = dis.readUTF();
            dis.close();
            conn.close();
        } catch (IOException e) {
            setAppError(e.getMessage());
        }
        //we have the message. Display on screen
        serverCaller.setText(message);
    }

    /**
     * Prints the error string to the UI
     * @param errStr
     */
    private void setAppError(String errStr) {
        errStr = "[SErr:" + errStr + "]";
        serverCaller.setText(errStr);
    }
}
