package com.shadow.socket.client.ui;


import com.shadow.socket.client.Client;
import com.shadow.socket.client.config.CMD;
import com.shadow.socket.client.model.Module;
import com.shadow.socket.client.model.Option;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;

public class TopPanel extends JPanel {
    private static final long serialVersionUID = -4482146844228777923L;

    private final JTextField ipField;
    private final JTextField portField;
    private final JTextField usernameField;
    private final JTextField serverField;
    private final JComboBox moduleField;
    private final JComboBox cmdField;

    private final JButton btnSend;
    private final JButton btnLogin;
    private final JButton btnLogout;

    /**
     * Create the panel.
     */
    public TopPanel() {
        setBorder(new LineBorder(new Color(0, 0, 0)));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel panel = new JPanel();
        add(panel);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

        JLabel lblIp = new JLabel("ip");
        panel.add(lblIp);

        ipField = new JTextField();
        ipField.setText("127.0.0.1");
        panel.add(ipField);
        ipField.setColumns(8);

        JLabel lblPort = new JLabel("port");
        panel.add(lblPort);

        portField = new JTextField();
        portField.setText("8888");
        panel.add(portField);
        portField.setColumns(5);

        JLabel lblUsername = new JLabel("username");
        panel.add(lblUsername);

        usernameField = new JTextField();
        usernameField.setText("wwsilver");
        panel.add(usernameField);
        usernameField.setColumns(10);

        JLabel lblServer = new JLabel("server");
        panel.add(lblServer);

        serverField = new JTextField();
        serverField.setText("1");
        panel.add(serverField);
        serverField.setColumns(5);

        btnLogin = new JButton("login");
        panel.add(btnLogin);

        btnLogout = new JButton("logout");
        panel.add(btnLogout);

        JPanel panel_1 = new JPanel();
        add(panel_1);
        panel_1.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

        JLabel lblModule = new JLabel("module");
        panel_1.add(lblModule);

        moduleField = new JComboBox();
        panel_1.add(moduleField);

        JLabel lblCmd = new JLabel("cmd");
        panel_1.add(lblCmd);

        cmdField = new JComboBox();
        panel_1.add(cmdField);

        btnSend = new JButton("send");
        panel_1.add(btnSend);

        addActionListener();

        initModuleAndCmd();
    }

    private void initModuleAndCmd() {
        try {

            for (Field field : Module.class.getDeclaredFields()) {
                String name = field.getName();
                int value = (Integer) field.get(null);
                moduleField.addItem(Option.valueOf(name, value));
            }
            for (CMD cmd : CMD.values()) {
                String name = cmd.name();
                int value = cmd.ordinal();
                cmdField.addItem(Option.valueOf(name, value));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void addActionListener() {

        btnLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                login();
            }
        });
        btnLogout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                logout();
            }
        });

        btnSend.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                send();
            }
        });
    }

    private void send() {
        Client.INSTANCE.send();
    }

    private void logout() {
        Client.INSTANCE.logout();
    }

    public void login() {
        String ip = ipField.getText().trim();
        int port = Integer.parseInt(portField.getText().trim());
        String username = usernameField.getText().trim();
        int server = Integer.parseInt(serverField.getText().trim());

        Client.INSTANCE.login(ip, port, username, server);

        save(ip, port, username);
    }

    private void save(String ip, int port, String username) {
//        Properties p = new Properties();
//        p.setProperty("ip", ip);
//        p.setProperty("port", String.valueOf(port));
//        p.setProperty("username", username);
//        try {
//            p.store(new FileOutputStream("user.txt"), null);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public int getModule() {
        Option option = (Option) moduleField.getSelectedItem();
        if (option == null) {
            return -1;
        }
        return (Integer) option.getValue();
    }

    public int getCmd() {
        Option option = (Option) cmdField.getSelectedItem();
        if (option == null) {
            return -1;
        }
        return (Integer) option.getValue();
    }

    public void onLogin() {
        btnLogin.setEnabled(false);
    }

    public void onLogout() {
        btnLogin.setEnabled(true);
    }
}
