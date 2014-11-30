package com.shadow.socket.client.ui;


import com.shadow.socket.client.config.Constant;

import javax.swing.*;
import java.awt.*;

public class ResponsePanel extends JPanel {
    private static final long serialVersionUID = 217159548513400995L;

    private JTextArea textArea;

    /**
     * Create the panel.
     */
    public ResponsePanel() {
        setLayout(new GridLayout(1, 0));

        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane
                .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setAutoscrolls(true);

        add(scrollPane);

    }

    public void receive(String response) {
        textArea.append(Constant.MSG_DELIMITER);
        textArea.append(response);
    }
}
