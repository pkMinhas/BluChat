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
public class AboutForm extends Form implements ActionListener {

    Form f;
    TextArea txtAbout;
    Command cmdBack;
    Button btnBack;
    private final String sAbout =
            "BluChat by PKSArena\u00A9\nVersion 1.0\n" +
            "\nwww.pksarena.com\n\n" +
            "We look forward to receiving your feedback.\n" +
            "Please visit\n"+ BluChatMidlet.APP_URL+"\nto log your feedback, " +
            "comments or suggestions for further improvement of this product.\n" +
            "Alternatively, you can contact us via email:\nmobile@pksarena.com\n\n" +
            "Credits:\nConcept & development:\nPK Minhas\n\n" +
            "Special thanks to:\n" +
            "Nandini Duggal\n" ;

    public AboutForm(Form f) {
        super("About");
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
        txtAbout.setText(sAbout);
        
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
