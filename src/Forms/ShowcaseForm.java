/*
 * Copyright (c) 2011, Preet Kamal Singh Minhas, http://pksarena.com
 * All rights reserved.
 */
package Forms;


import com.sun.lwuit.Button;
import com.sun.lwuit.Command;
import com.sun.lwuit.Form;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import java.io.IOException;

/**
 *
 * @author Preet Kamal Singh Minhas
 */
public class ShowcaseForm extends Form implements ActionListener {

    Form prevForm;
    TextArea txtImportant;
    Command cmdBack;
    Button btnBack;
    private final String sImportant =
            "Expense Manager:\n"
            + "Complete expense management and budgeting solution for your mobile device.\n"
            + "Visit:\n"
            + "http://pksarena.com/emanager\n\n"
            + "Crypt:\n"
            + "Encrypt and store your sensitive information and keep it safe from prying eyes.\n"
            + "Visit:\n"
            + "http://pksarena.com/crypt";
            

    public ShowcaseForm(Form prevForm) {
        super("PKSARENA: More softwares");
        this.prevForm = prevForm;
    }

    public void init() {
        initializeComponents();
        this.setLayout(new BorderLayout());
        addComponent(BorderLayout.CENTER, txtImportant);
        addComponent(BorderLayout.SOUTH, btnBack);
        addCommand(cmdBack);
        setBackCommand(cmdBack);
    }

    private void initializeComponents() {
        txtImportant = new TextArea();
        txtImportant.setEditable(false);
        txtImportant.setGrowByContent(true);
        txtImportant.setText(sImportant);

        cmdBack = new Command("Back");
        this.addCommandListener(this);
        this.setBackCommand(cmdBack);

        btnBack = new Button(cmdBack);
        btnBack.setAlignment(Button.CENTER);

        this.setCyclicFocus(false);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getCommand() == cmdBack) {
            prevForm.show();
        }
    }
}