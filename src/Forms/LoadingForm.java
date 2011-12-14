/*
 * Copyright (c) 2011, Preet Kamal Singh Minhas, http://pksarena.com
 * All rights reserved.
 */
package Forms;

import com.sun.lwuit.Display;
import com.sun.lwuit.Form;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.layouts.BorderLayout;
import java.io.IOException;

/**
 * This form would be used to display the loading screen
 * @author Preet Kamal Singh Minhas
 */
public class LoadingForm extends Form {

    Label lblLoading;
    Form f;
    boolean isAnimating = false;
    //performance optimization at cost of few KBs of RAM
    private static Image imgLoading = null;

    static {
        try {
            imgLoading = Image.createImage("/images/loading.png");
        } catch (IOException e) {
        }
    }

    public LoadingForm(String title, Form f) {
        super(title);
        lblLoading = new Label(imgLoading);
        lblLoading.setAlignment(Label.CENTER);
        lblLoading.setTextPosition(Label.BOTTOM);
        BorderLayout layout = new BorderLayout();
        this.setLayout(layout);
        this.f = f;
        this.addComponent(BorderLayout.CENTER, lblLoading);
        
    }

    private void startAnimatingIcon() {
        final Form me = this;
        Runnable r = new Runnable() {

            public void run() {
                while (me.isVisible()) {
                    //run the animation thread only in case form is visible
                    Display.getInstance().callSerially(new Runnable() {

                        public void run() {
                            lblLoading.setText(lblLoading.getText() + "*");
                        }
                    });
                    try {
                        Thread.sleep(1000l);
                    } catch (InterruptedException ioe) {
                        //do nothing
                    }
                }
                //out of animation loop. toggle flag
                lblLoading.setText("");
                isAnimating = false;                
            }
        };
        Thread thrdRotateImg = new Thread(r);
        thrdRotateImg.start();
    }

    public void disappear() {
        f.show();
    }

    public void show(){
        //call the real show
        super.show();
        //now start the animation loop if not already started
        if(!isAnimating){
            startAnimatingIcon();
            isAnimating = true;
        }
    }
}
