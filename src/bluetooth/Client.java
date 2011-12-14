/*
 * Copyright (c) 2011, Preet Kamal Singh Minhas, http://pksarena.com
 * All rights reserved.
 */
package bluetooth;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

/**
 * Bluetooth client functionality.
 * Client plays the role of the data transmitter.
 * Also, the client module has the methods which can search for servers running
 * an instance of this application.
 * Singleton but must be initialized by calling the
 * static method init(ClientCaller) before an instance can be obtained.
 * @author Preet Kamal Singh Minhas
 */
public class Client implements DiscoveryListener {

    private static Client myClient = new Client();
    private static boolean isInitialized = false;
    //flag to indicate whether we are searching for devices
    private boolean isSearching = false;
    //reference to the local discovery agent
    private DiscoveryAgent discAgent;
    //vector to store the discovered devices
    private Vector discoveredDevices;
    //vector to store the service records searched on remote devices
    private Vector searchedServiceRecords;
    //reference to the ClientCaller object. Contains callbacks.
    private static ClientCaller clientCaller;
    //number of bluetooth devices in proximity
    private int totalBtDevicesInProximity = 0;
    //number of devices searched for running an instance of this app.
    private int totalDevicesSearchedForServices = 0;
    //which device are we currently searching for services
    private int deviceCurrentlySearching = -1;
    //list of UUIDs to look for
    private UUID[] uuidList = new UUID[]{BTCommon.BLUCHAT_SERVICE_ID};
    //name of the local user's bt device.
    private String localName = null;

    public static void init(ClientCaller caller) {
        clientCaller = caller;
        isInitialized = true;
    }

    private Client() {
        //singleton
    }

    public static Client getInstance() {
        if (isInitialized == false) {
            throw new RuntimeException("Client not initialized properly!");
        }
        return myClient;
    }

    /**
     * Starts search for other devices running this application
     */
    public void searchDevices() {
        if (isSearching == true) {
            return;
        }
        //initialize values
        discoveredDevices = new Vector();
        searchedServiceRecords = new Vector();
        totalBtDevicesInProximity = 0;
        totalDevicesSearchedForServices = 0;
        deviceCurrentlySearching = -1;

        //get the name of the user's device
        if (localName == null) {
            try {
                localName = LocalDevice.getLocalDevice().getFriendlyName();
                //handle the scenario where the device does not return friendly name
                //occurs in about 30% of devices
                if (localName == null || localName.trim().equals("")) {
                    Random r = new Random();
                    localName = "User" + r.nextInt(99);
                }
            } catch (BluetoothStateException bse) {
                setAppError("SD::" + bse.getMessage());
            }
        }

        try {
            //start inquiry for remote devices
            LocalDevice lDev = LocalDevice.getLocalDevice();
            discAgent = lDev.getDiscoveryAgent();
            discAgent.startInquiry(DiscoveryAgent.GIAC, this);
            //toggle the flag
            isSearching = true;
        } catch (BluetoothStateException bse) {
            setAppError("SD::" + bse.getMessage());
        }

    }

    /**
     * Stops the search for remote devices
     */
    public void stopSearch() {
        if (isSearching == false) {
            return;
        }
        discAgent.cancelInquiry(this);
        isSearching = false;
    }

    /**
     *
     * @return The local device's bluetooth name
     */
    public String getLocalName() {
        return localName;
    }

    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
        //add the device to vector of discovered devices
        discoveredDevices.addElement(btDevice);
    }

    /**
     * Algorithm: this method would be called by the Bluetooth APIs once the search for devices
     * is complete. We start searching for the device running our service by
     * iterating through the vector containing the details for BT devices in proximity.
     */
    public void inquiryCompleted(int discType) {
        totalBtDevicesInProximity = discoveredDevices.size();
        //if there are no devices in proximity, signal the ui
        if (totalBtDevicesInProximity == 0) {
            //no bt devices to search for
            clientCaller.signalSearchComplete(null);
            //toggle the searching flag
            isSearching = false;
            return;
        } else {
            try {
                //start service search.
                //element 0 is searched for service here. rest is handled in the 
                //serviceSearchComplete method.
                deviceCurrentlySearching = 0;
                RemoteDevice remDev = (RemoteDevice) discoveredDevices.elementAt(deviceCurrentlySearching);
                discAgent.searchServices(null, uuidList, remDev, getInstance());
            } catch (BluetoothStateException bse) {
                setAppError("BSE:: " + bse.getMessage());
            } catch (Exception e) {
                setAppError("E::" + e.getMessage());
            }
        }
    }

    public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
        //a device running matching serviceß discovered.
        //add the first service record
        searchedServiceRecords.addElement(servRecord[0]);
    }

    //this method is called everytime the service search is completed on a device
    public void serviceSearchCompleted(int transID, int respCode) {

        //increment number of devices searched for services
        totalDevicesSearchedForServices++;

        //we have searched all the discovered devices for service
        if (totalDevicesSearchedForServices == totalBtDevicesInProximity) {
            int totalDevices = searchedServiceRecords.size();
            //create a vector to store the details of every device discovered
            //running the required service.
            Vector serverDetailsVector = new Vector(totalDevices);

            //Get the remote device name and connection url
            for (int x = 0; x < totalDevices; x++) {
                ServiceRecord presentRec = (ServiceRecord) searchedServiceRecords.elementAt(x);
                String presentRemDevName, presentConnUrl;
                try {
                    presentRemDevName = presentRec.getHostDevice().getFriendlyName(false);
                } catch (Exception e) {
                    setAppError(e.getMessage());
                    //give default name in case unable to retrieve the name
                    presentRemDevName = "Peer " + x;
                }
                //get the connection url for the current remote device
                presentConnUrl = presentRec.getConnectionURL(
                        ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                //store these details as a ServerDetails object
                ServerDetails details = new ServerDetails();
                details.setConnUrl(presentConnUrl);
                details.setName(presentRemDevName);
                serverDetailsVector.addElement(details);
            }
            //empty the vector storing the searched service records since it is
            //no longer required. This step ensures that GC can reclaim the memory
            //occupied by these records now.
            searchedServiceRecords.removeAllElements();
            searchedServiceRecords = null;
            //signal the ui that the search is complete
            clientCaller.signalSearchComplete(serverDetailsVector);
        } else {
            //increment the counter
            deviceCurrentlySearching++;
            if (deviceCurrentlySearching != totalBtDevicesInProximity) {
                // search the next device for service
                try {
                    RemoteDevice remDev = (RemoteDevice) discoveredDevices.elementAt(deviceCurrentlySearching);
                    discAgent.searchServices(null, uuidList, remDev, getInstance());
                } catch (BluetoothStateException bse) {
                    setAppError("BSE:: " + bse.getMessage());
                } catch (Exception e) {
                    setAppError("E::" + e.getMessage());
                }
            } else {
                //empty the discoveredDevices vector. its not reqd anymore.
                discoveredDevices.removeAllElements();
                discoveredDevices = null;
                //device search complete. signal
                isSearching = false;
                return;
            }
        }
    }

    /**
     * Blocking method to send a message
     * Should be called in a new thread than the display thread (EDT)
     * @param message The message
     * @param url The bt connection url for remote device
     */
    public synchronized void sendMessage(String message, String url) throws Exception {
        StreamConnection conn = null;
        DataOutputStream dos = null;
        message = localName + ": " + message;
        try {
            conn = (StreamConnection) Connector.open(url);
            dos = conn.openDataOutputStream();
            dos.writeUTF(message);
        } finally {
            dos.flush();
            dos.close();
            conn.close();
        }
    }

    /**
     * Prints the error string to the UI
     * @param errStr
     */
    private void setAppError(String errStr){
        errStr = "[CErr:" + errStr + "]";
        clientCaller.setText(errStr);
    }
}
