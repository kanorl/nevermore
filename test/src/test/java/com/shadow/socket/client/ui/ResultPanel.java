package com.shadow.socket.client.ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.ArrayUtils;

import javax.swing.*;
import java.nio.charset.Charset;

public class ResultPanel extends JPanel {

    private static final long serialVersionUID = 6047687818788470206L;

    private final ResponsePanel responsePanel = new ResponsePanel();
    private final ResponsePanel pushPanel = new ResponsePanel();

    /**
     * Create the panel.
     */
    public ResultPanel() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        add(responsePanel);
        add(pushPanel);
    }

    public void onResponseReceived(byte[] valueBytes) {
        if (ArrayUtils.isEmpty(valueBytes)) {
            return;
        }
        String msg = format(new String(valueBytes, Charset.forName("utf-8")));
        responsePanel.receive(msg);
    }

    public static String format(String uglyJSONString) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(uglyJSONString);
        return gson.toJson(je);
    }

    public void onPushReceived(int module, int cmd, byte[] valueBytes) {
        String msg = "Module: " + module + " Cmd: " + cmd + "\n";
        if (ArrayUtils.isNotEmpty(valueBytes)) {
            msg += format(new String(valueBytes, Charset.forName("utf-8")));
        }
        pushPanel.receive(msg);
    }

}
