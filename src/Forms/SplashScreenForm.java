/*
 * Copyright (c) 2011, Preet Kamal Singh Minhas, http://pksarena.com
 * All rights reserved.
 */
package Forms;


import com.sun.lwuit.Form;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.layouts.BorderLayout;
import java.io.IOException;

/**
 * Splash screen for the application
 * @author Preet Kamal Singh Minhas
 */
public class SplashScreenForm extends Form {

   
    private final int DISPLAY_DURATION = 2;

    public SplashScreenForm() {
        super("");
       
        Image logo = null;
        try {
            logo = Image.createImage("/images/logo.png");
        } catch (IOException e) {
        }
        Label lblLogo = new Label(logo);
        lblLogo.setAlignment(Label.CENTER);
        BorderLayout layout = new BorderLayout();
        this.setLayout(layout);
        this.addComponent(BorderLayout.CENTER, lblLogo);

    }
    
    /**
     * overloaded show. will return back after DISPLAY_DURATION seconds
     */
    public void show() {
        super.show();
        try{
            Thread.sleep(DISPLAY_DURATION*1000);
        } catch(Exception e){
        }

    }
}
