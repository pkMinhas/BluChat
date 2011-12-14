/*
 * Copyright (c) 2011, Preet Kamal Singh Minhas, http://pksarena.com
 * All rights reserved.
 */
package Forms;

import bluetooth.Client;
import bluetooth.ClientCaller;
import bluetooth.Server;
import bluetooth.ServerCaller;
import bluetooth.ServerDetails;
import com.sun.lwuit.Button;
import com.sun.lwuit.Command;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Display;
import com.sun.lwuit.Form;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.table.TableLayout;
import java.util.Vector;
import javax.bluetooth.LocalDevice;
import main.BluChatMidlet;

/**
 *
 * @author Preet Kamal Singh Minhas
 */
public class ChatForm extends Form
        implements ActionListener, ServerCaller, ClientCaller {

    
    private TextArea txtRecdMsg;
    private TextArea txtSendMsg;
    private Button btnSend, btnBack;
    private Command cmdSend, cmdBack;
    //making this static so that the data persists between going to main screen
    //and coming back
    private static String bodyText="System started...";
    private PeerListForm plf = null;
    private BluChatMidlet m;
    final int MAX_TEXT_CHAR = 15000;

    public ChatForm(BluChatMidlet m) {
        super("BluChat");
        this.m = m;
    }

    public void init() {
        initializeComponents();
        TableLayout.setDefaultColumnWidth(50);
        TableLayout layout = new TableLayout(3, 2);
        this.setLayout(layout);
        TableLayout.Constraint constraint;

        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(2);
        constraint.setHeightPercentage(70);
        addComponent(constraint, txtRecdMsg);

        constraint = layout.createConstraint();
        constraint.setHorizontalSpan(2);
        addComponent(constraint, txtSendMsg);

        addComponent(btnSend);
        addComponent(btnBack);

        addCommand(cmdBack);
        addCommand(cmdSend);

        addCommandListener(this);

        this.setScrollable(false);

    }

    protected void initializeComponents() {


        txtRecdMsg = new TextArea(bodyText);
        txtRecdMsg.setEditable(false);
        txtRecdMsg.setGrowByContent(true);
        
        
        txtSendMsg = new TextArea();
        txtSendMsg.setHint("Your message");

        cmdSend = new Command("Send");
        btnSend = new Button(cmdSend);
        btnSend.setAlignment(Button.CENTER);

        cmdBack = new Command("Back");
        btnBack = new Button(cmdBack);
        btnBack.setAlignment(Button.CENTER);

        this.setBackCommand(cmdBack);
    }

    public void actionPerformed(ActionEvent evt) {
        Command selectedCmd = evt.getCommand();
        if (selectedCmd == cmdBack) {
            //show the mainscreen
            MainScreenForm mainForm = new MainScreenForm(m);
            mainForm.init();
            mainForm.show();
        } else {
            //cmd send
            //show the peer (server) list
            plf.show();
        }
    }


    /**
     * Sets the text on the UI component
     * @param text
     */
    public synchronized void setText(String text) {        
        StringBuffer buffer = new StringBuffer(text);
        buffer.append("\n");
        buffer.append(bodyText);
        if(buffer.length() > MAX_TEXT_CHAR){
            //purge old data if chat text num of char is > MAX_TEXT_CHAR
            buffer = new StringBuffer(text);
        }
        bodyText =  new String(buffer);
        //calling serially since text modification needs to happen on edt
        Display.getInstance().callSeriallyAndWait(new Runnable() {

            public void run() {
                txtRecdMsg.setText(bodyText);
                
            }
        });


    }

    public void signalSearchComplete(Vector serverDetailsRecords) {

        //display self
        this.show();
        StringBuffer buffer = new StringBuffer();
        buffer.append("**Hi ");
        buffer.append(Client.getInstance().getLocalName() );
        buffer.append(", ");
        
        if (serverDetailsRecords == null) {
            buffer.append("0 peers found!**");
            setText(buffer.toString());
            //disable the send button and command
            btnSend.setEnabled(false);
            this.removeCommand(cmdSend);
            return;
        }
        buffer.append(serverDetailsRecords.size());
        buffer.append(" peers found!**");
        setText(buffer.toString());
        //initialize the peerlist form
        plf = new PeerListForm(serverDetailsRecords, this);
        plf.init();

        

    }

    public String getMessage() {
        return txtSendMsg.getText();
    }
}
