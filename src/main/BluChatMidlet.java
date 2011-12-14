/*
 * Copyright (c) 2011, Preet Kamal Singh Minhas, http://pksarena.com
 * All rights reserved.
 */
package main;

import Forms.LoadingForm;
import Forms.MainScreenForm;
import Forms.SplashScreenForm;
import bluetooth.Client;
import bluetooth.Server;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Display;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.plaf.UIManager;
import com.sun.lwuit.util.Resources;
import java.io.IOException;
import javax.microedition.midlet.*;

/**
 * Bluetooth chat application.
 * @author Preet Kamal Singh Minhas
 */
public class BluChatMidlet extends MIDlet {

    public final static String SRC_CODE_URL = "http://pksarena.com/bluChat/source-code";
    public final static String APP_URL = "http://pksarena.com/bluChat/";
    private boolean isInitialized = false;

    public void startApp() {
        if (isInitialized == false) {
            Display.init(this);
            try {
                Resources r = Resources.open("/res/my2.res");
                UIManager.getInstance().setThemeProps(r.getTheme(r.getThemeResourceNames()[0]));
                //set the reversed soft buttons
                UIManager.getInstance().getLookAndFeel().setReverseSoftButtons(true);
                UIManager.getInstance().getLookAndFeel().setDefaultMenuTransitionIn(CommonTransitions.createEmpty());
                UIManager.getInstance().getLookAndFeel().setDefaultMenuTransitionOut(CommonTransitions.createEmpty());
                //set the dialog properties for the app
                Dialog.setAutoAdjustDialogSize(true);
                Dialog.setDefaultDialogPosition(BorderLayout.SOUTH);
                //show splash screen. it will continue after the specified duration
                //Sleeps internally
                SplashScreenForm splashScreen = new SplashScreenForm();
                splashScreen.show();
            } catch (IOException e) {
                //ignore. will never come here in case resources are present in jar.
            }
            beginApp();
            isInitialized = true;
        }
    }

    /**
     * Starts the app logic.
     */
    private void beginApp() {
        //show the mainscreen. Additionally, any initialization code can be
        //executed here.
        MainScreenForm mainForm = new MainScreenForm(this);
        mainForm.init();
        mainForm.show();
    }

    public void pauseApp() {
        //we can temporarily release the bluetooth device here.
        //Would make the code too complex though
    }

    public void destroyApp(boolean unconditional) {
        LoadingForm lf = new LoadingForm("Closing bluetooth connections...",
                null);
        lf.show();
        //close the server
        try {
            Server.getInstance().stopServer();
        } catch (RuntimeException re) {
            //ignore
            //we may get this exception in case user exits the app before starting chat
        }
    }
}
