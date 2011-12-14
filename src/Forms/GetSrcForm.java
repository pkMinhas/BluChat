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
public class GetSrcForm extends Form implements ActionListener {

    Form f;
    TextArea txtAbout;
    Command cmdBack;
    Button btnBack;
    private final String sMsg =
            "BluChat is written in J2ME and uses "
            + "JSR82 API for bluetooth communication and "
            + "the LWUIT library for UI.\n"
            + "Get the complete source code for BluChat from:\n"
            + BluChatMidlet.SRC_CODE_URL;

    public GetSrcForm(Form f) {
        super("BluChat source code");
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
        txtAbout.setText(sMsg);
        
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
