/*
 * Copyright (c) 2011, Preet Kamal Singh Minhas, http://pksarena.com
 * All rights reserved.
 */
package Forms;

import com.sun.lwuit.Button;
import com.sun.lwuit.Command;
import com.sun.lwuit.Form;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import main.BluChatMidlet;
/**
 *
 * @author Preet Kamal Singh Minhas
 */
public class HelpForm extends Form implements ActionListener {

    Form f;
    TextArea txtAbout;
    Command cmdBack;
    Button btnBack;
    private final String sHelp =
            "BluChat help"
            + "\n\nDownload the source code for this application at:\n"
            + BluChatMidlet.SRC_CODE_URL
            + "\n\nBluChat is a bluetooth based proximity chat application. In order to use BluChat:"
            + "\n1. Switch on your device's bluetooth and then press the application's 'Chat' button."
            + "\n2. The application will search for devices running BluChat in your bluetooth range."
            + "\n3. Once the device search is complete, you will be presented with the chat screen."
            + "\n4. To send a message, type your message in the message box and then select 'Send'."
            + " You will be presented with a list of devices present in your vicinity running BluChat."
            + " Select the recipient and select 'Send' to send your message."
            + "\n\n#To search for devices again, go to the mainscreen by pressing the 'Back' button and"
            + " then press the 'Chat' button to search again for devices."
            + "\n\n*Note: The application will try to use your device's bluetooth name as your chat id."
            + " In case the application is unable to read your device's bluetooth name, it will set your chat id"
            + " to a default value with the format User<Num> e.g. User19, User7 etc.";
            

    public HelpForm(Form f) {
        super("Help");
        this.f = f;
    }

    public void init() {
        initializeComponents();
        this.setLayout(new BorderLayout());
        addComponent(BorderLayout.CENTER, txtAbout);
        addComponent(BorderLayout.SOUTH, btnBack);
        addCommand(cmdBack);

    }

    private void initializeComponents() {
        txtAbout = new TextArea();
        txtAbout.setEditable(false);
        txtAbout.setGrowByContent(true);
        txtAbout.setText(sHelp);
        
        cmdBack = new Command("Back");
        this.addCommandListener(this);
        this.setBackCommand(cmdBack);

        btnBack = new Button(cmdBack);
        btnBack.setAlignment(Button.CENTER);

        this.setCyclicFocus(false);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getCommand() == cmdBack) {
            f.show();
        }
    }
}
