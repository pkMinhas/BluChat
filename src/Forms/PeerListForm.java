/*
 * Copyright (c) 2011, Preet Kamal Singh Minhas, http://pksarena.com
 * All rights reserved.
 */
package Forms;

import bluetooth.Client;
import bluetooth.ServerDetails;
import com.sun.lwuit.Button;
import com.sun.lwuit.Command;
import com.sun.lwuit.Display;
import com.sun.lwuit.Form;
import com.sun.lwuit.List;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.table.TableLayout;
import java.io.IOException;
import java.util.Vector;

/**
 *
 * @author Preet Kamal Singh Minhas
 */
public class PeerListForm extends Form implements ActionListener {

    List lstPeer;
    Button btnSend, btnBack;
    Command cmdSend, cmdBack;
    Vector serverDetails;
    ChatForm cf;

    public PeerListForm(Vector serverDetails, ChatForm cf) {
        super("Select recipient");
        this.serverDetails = serverDetails;
        this.cf = cf;
    }

    public void init() {
        initializeComponents();
        TableLayout.setDefaultColumnWidth(50);
        TableLayout layout = new TableLayout(2, 2);
        this.setLayout(layout);
        TableLayout.Constraint constraint;


        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(2);
        addComponent(constraint, lstPeer);

        addComponent(btnSend);
        addComponent(btnBack);

        addCommand(cmdBack);
        addCommand(cmdSend);

        this.addCommandListener(this);

    }

    protected void initializeComponents() {
        cmdSend = new Command("Send");
        btnSend = new Button(cmdSend);
        btnSend.setAlignment(Button.CENTER);

        cmdBack = new Command("Back");
        btnBack = new Button(cmdBack);
        btnBack.setAlignment(Button.CENTER);

        lstPeer = new List(serverDetails);

        this.setBackCommand(cmdBack);
    }

    public void actionPerformed(ActionEvent evt) {
        Command selCmd = evt.getCommand();
        if (selCmd == cmdBack) {
            cf.show();
        } else {
            //cmdsend

            final String msg = cf.getMessage();
            final ServerDetails selPeer = (ServerDetails) lstPeer.getSelectedItem();

            Display.getInstance().invokeAndBlock(new Runnable() {

                public void run() {
                    try {
                        LoadingForm lf = new LoadingForm("Sending message...", null);
                        lf.show();
                        Client.getInstance().sendMessage(msg, selPeer.getConnUrl());
                        cf.setText("Me->" + selPeer.getName() + ": " + msg);
                        cf.show();
                    } catch (Exception ioe) {
                        cf.setText("**Me->"
                                + selPeer.getName() + ": Unable to deliver [ "
                                + ioe.getMessage() +" ]**");
                        cf.show();
                    }
                }
            });




        }

    }
}
