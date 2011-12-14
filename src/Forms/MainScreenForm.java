/*
 * Copyright (c) 2011, Preet Kamal Singh Minhas, http://pksarena.com
 * All rights reserved.
 */
package Forms;

import bluetooth.Client;
import bluetooth.Server;
import com.sun.lwuit.Button;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Display;
import com.sun.lwuit.Form;
import com.sun.lwuit.Image;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.GridLayout;
import java.io.IOException;
import javax.bluetooth.LocalDevice;
import main.BluChatMidlet;

/**
 *
 * @author Preet Kamal Singh Minhas
 */
public class MainScreenForm extends Form implements ActionListener {

    private Button btnChat, btnHelp, btnAbout, btnExit, btnGetSrcCode, btnMore;
    BluChatMidlet m;

    public MainScreenForm(BluChatMidlet m) {
        super("PKSArena BluChat");
        this.m = m;
    }

    public void init() {
        initializeComponents();
        int iTotalButtons = 6;
        int iWidth = Display.getInstance().getDisplayWidth();
        int elementWidth = 0;
        elementWidth = Math.max(elementWidth, btnHelp.getPreferredW());
        elementWidth = Math.max(elementWidth, btnChat.getPreferredW());
        elementWidth = Math.max(elementWidth, btnMore.getPreferredW());
        elementWidth = Math.max(elementWidth, btnAbout.getPreferredW());
        elementWidth = Math.max(elementWidth, btnExit.getPreferredW());
        elementWidth = Math.max(elementWidth, btnGetSrcCode.getPreferredW());

        int cols = iWidth / elementWidth;
        int rows = iTotalButtons / cols;

        this.setLayout(new GridLayout(rows, cols));
        addComponent(btnChat);
        addComponent(btnHelp);
        addComponent(btnAbout);
        addComponent(btnMore);
        addComponent(btnGetSrcCode);
        addComponent(btnExit);

        this.setFocused(btnChat);
    }

    protected void initializeComponents() {
        Image imgButtons[] = new Image[6];
        try {
            imgButtons[0] = Image.createImage("/images/main/chat.png");
            imgButtons[1] = Image.createImage("/images/main/help.png");
            imgButtons[2] = Image.createImage("/images/main/more.png");
            imgButtons[3] = Image.createImage("/images/main/about.png");
            imgButtons[4] = Image.createImage("/images/main/source.png");
            imgButtons[5] = Image.createImage("/images/main/exit.png");
        } catch (IOException e) {
        }

        btnChat = new Button("Chat", imgButtons[0]);
        btnChat.addActionListener(this);
        btnChat.setAlignment(Button.CENTER);
        btnChat.setTextPosition(Button.BOTTOM);

        btnHelp = new Button("Help", imgButtons[1]);
        btnHelp.addActionListener(this);
        btnHelp.setAlignment(Button.CENTER);
        btnHelp.setTextPosition(Button.BOTTOM);


        btnMore = new Button("More softwares", imgButtons[2]);
        btnMore.addActionListener(this);
        btnMore.setAlignment(Button.CENTER);
        btnMore.setTextPosition(Button.BOTTOM);

        btnAbout = new Button("About", imgButtons[3]);
        btnAbout.addActionListener(this);
        btnAbout.setAlignment(Button.CENTER);
        btnAbout.setTextPosition(Button.BOTTOM);

        btnGetSrcCode = new Button("Source code", imgButtons[4]);
        btnGetSrcCode.addActionListener(this);
        btnGetSrcCode.setAlignment(Button.CENTER);
        btnGetSrcCode.setTextPosition(Button.BOTTOM);


        btnExit = new Button("Exit", imgButtons[5]);
        btnExit.addActionListener(this);
        btnExit.setAlignment(Button.CENTER);
        btnExit.setTextPosition(Button.BOTTOM);
    }

    public void actionPerformed(ActionEvent ae) {
        Button selectedButton = (Button) ae.getSource();
        if (selectedButton == btnExit) {
            boolean ret = Dialog.show("Confirm!", "Do you want to exit?", "Yes", "No");
            if (ret == false) {
                return;
            }
            m.destroyApp(true);
            m.notifyDestroyed();
        } else if (selectedButton == btnChat) {
            //TODO: comment the code marked between "COMMENT_FOLLOWING" in case
            //you are unable to test on emulator. Some emulators don't expose 
            //Bluetooth Connection Center.(especially on Mac OSX)

            // <editor-fold defaultstate="collapsed" desc="COMMENT_FOLLOWING">

            /* COMMENT_FOLLOWING. But, do ensure that this code is part of production release.*/
            //check if bluetooth device is on
            if (!LocalDevice.isPowerOn()) {
                Dialog.show("Error!", "Bluetooth switched off!\n"
                        + "Please switch on bluetooth and restart application", "OK", null);
                return;
            }
            
            /* END OF COMMENT_FOLLOWING */
            // </editor-fold>

            //start the bluetooth controls
            BluetoothController.getInstance(m).startOperations();
            return;
        } else if (selectedButton == btnAbout) {
            AboutForm af = new AboutForm(this);
            af.init();
            af.show();
            return;
        } else if (selectedButton == btnHelp) {
            HelpForm hf = new HelpForm(this);
            hf.init();
            hf.show();
            return;
        } else if (selectedButton == btnGetSrcCode) {
            GetSrcForm gsf = new GetSrcForm(this);
            gsf.init();
            gsf.show();
            return;
        } else if (selectedButton == btnMore) {
            ShowcaseForm gsf = new ShowcaseForm(this);
            gsf.init();
            gsf.show();
            return;
        }
    }
}

/**
 * Controls the bluetooth initialization
 * @author Preet Kamal Singh Minhas
 */
class BluetoothController {

    BluChatMidlet m;
    private static BluetoothController me;

    private BluetoothController(BluChatMidlet m) {
        this.m = m;
    }

    public static BluetoothController getInstance(BluChatMidlet m) {
        if (me == null) {
            me = new BluetoothController(m);
        }
        return me;
    }

    /**
     * starts the bt ops and then shows the form
     */
    public void startOperations() {
        //show loading form
        LoadingForm lf = new LoadingForm("Searching devices...", null);
        lf.show();
        ChatForm cf = new ChatForm(m);
        cf.init();
        //init the client and server
        Client.init(cf);
        Server.init(cf);
        //start the server
        Server.getInstance().startServer();

        //doing the client search in separate thread to stop the search after x secs.
        //if sleep is on EDT, whole display is unresponsive
        Runnable r = new Runnable() {

            public void run() {
                Client client = Client.getInstance();
                //start search, sleep current thread for 30 sec and stop search
                //TODO: this value can be changed or made user configurable
                //30 secs is a good time to search for devices
                try {
                    client.searchDevices();
                    Thread.sleep(30000l);
                } catch (InterruptedException ie) {
                    //do nothing
                } finally {
                    client.stopSearch();
                }
                //client will take care of displaying the chat form once search is complete
            }
        };
        //start the thread
        Thread t = new Thread(r);
        t.start();
    }
}
