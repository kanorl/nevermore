package com.shadow.socket.client;

import com.google.common.collect.Maps;
import com.shadow.socket.client.model.Param;
import com.shadow.socket.client.socket.SocketClient;
import com.shadow.socket.client.ui.ParamPanel;
import com.shadow.socket.client.ui.ResultPanel;
import com.shadow.socket.client.ui.TopPanel;
import com.shadow.socket.core.domain.Command;
import com.shadow.socket.core.domain.ParameterContainer;
import com.shadow.socket.core.domain.Request;
import com.shadow.util.codec.JsonUtil;
import org.apache.commons.collections4.CollectionUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.Map;

public class Client {
    public static final Client INSTANCE = new Client();

    private SocketClient socketClient;

    private JFrame frame;
    private final TopPanel topPanel = new TopPanel();
    private final ParamPanel paramPanel = new ParamPanel();
    private final ResultPanel resultPanel = new ResultPanel();

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    INSTANCE.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public Client() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 673, 452);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(0, 0));
//        frame.setResizable(false);

        frame.getContentPane().add(topPanel, BorderLayout.NORTH);
        frame.getContentPane().add(paramPanel, BorderLayout.WEST);
        frame.getContentPane().add(resultPanel, BorderLayout.CENTER);
    }

    public void login(String ip, int port, String username, int server) {
//        if (socketClient == null || !socketClient.isConnected()) {
//            connect(ip, port);
//        }
//        String calKey;
//        try {
//            calKey = CryptUtil.md5(username + Constant.KEY).toLowerCase();
//            Map<String, Object> values = new HashMap<String, Object>();
//            values.put("userName", username);
//            values.put("server", server);
//            values.put("loginKey", calKey);
//            values.put("loginWay", 0);
//            values.put("fcmStatus", 0);
//            values.put("adultStatus", 2);
//            Request request = Request.valueOf(Module.PLAYER, 3, values);
//            Response response = socketClient.send(request);
//            if (response != null) {
//                resultPanel.onResponseReceived(response.getValueBytes());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        topPanel.onLogin();
        connect(ip, port);
    }

    private void connect(String ip, int port) {
        socketClient = new SocketClient();
        socketClient.connect(ip, port);
//        socketClient.registerResponseProcessor(new ResponseProcessorAdapter(47, 1000, Map.class));
    }

    public void logout() {
//        if (socketClient != null) {
//            socketClient.close();
//        }
//        topPanel.onLogout();
        socketClient.shutdown();
    }

    public void send() {
        if (socketClient == null) {
            JOptionPane.showMessageDialog(null, "服务器未连接!");
        }
        short module = topPanel.getModule();
        byte cmd = topPanel.getCmd();

        if (module < 0 || cmd < 0) {
            JOptionPane.showMessageDialog(null, String.format("模块/命令错误：module=%d cmd=%d", module, cmd));
        }

        Collection<Param> params = null;
        try {
            params = paramPanel.getParams();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        ParameterContainer pc = toRequestParams(params);
        Command command = Command.valueOf(module, cmd);
        Request request = Request.valueOf(command, pc, null);
//        Response response = socketClient.send(request);
//        if (response != null) {
//            resultPanel.onResponseReceived(response.getValueBytes());
//        }
        socketClient.send(request);
    }

    private ParameterContainer toRequestParams(Collection<Param> params) {
        if (CollectionUtils.isEmpty(params)) {
            return null;
        }
        Map<String, Object> map = Maps.newHashMapWithExpectedSize(params.size());
        for (Param param : params) {
            if (param.getKey() != null) {
                Object value = param.getType() == String.class ? param.getValue() : JsonUtil.toObject(param.getValue(), param.getType());
                map.put(param.getKey(), value);
            }
        }

        return ParameterContainer.valueOf(map);
    }

    public void onPushReceived(int module, int cmd, byte[] valueBytes) {
        resultPanel.onPushReceived(module, cmd, valueBytes);
    }

    public void reconnect() {
        topPanel.login();
    }
}
