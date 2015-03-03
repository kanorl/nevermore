package com.shadow.socket.client.ui;


import com.shadow.socket.client.model.Option;
import com.shadow.socket.client.model.Param;
import com.shadow.test.module.player.model.Country;
import com.shadow.test.module.player.model.Gender;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public class ParamPanel extends JPanel {
    private static final long serialVersionUID = 5113480619726942485L;

    private final Class<?>[] types = {Integer.class, String.class, Long.class, Gender.class, Country.class};

    private final JTable table;

    /**
     * Create the panel.
     */
    public ParamPanel() {
        setLayout(new GridLayout(1, 0));

        final String[] columnNames = {"key", "value", "type"};
        table = new JTable(new DefaultTableModel(columnNames, 5));
        table.setBorder(new LineBorder(new Color(0, 0, 0)));
        table.setPreferredScrollableViewportSize(new Dimension(200, 70));
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane);

        setUpSportColumn();
    }

    private void setUpSportColumn() {
        JComboBox comboBox = new JComboBox();
        for (Class<?> type : types) {
            comboBox.addItem(Option.valueOf(type.getSimpleName(), type.getName()));
        }

        TableColumn column = table.getColumnModel().getColumn(2);
        column.setCellEditor(new DefaultCellEditor(comboBox));
    }

    public Collection<Param> getParams() throws ClassNotFoundException {
        Collection<Param> params = new ArrayList<Param>();
        for (int i = 0; i < table.getRowCount(); i++) {
            Object value = table.getValueAt(i, 1);
            if (value == null) {
                continue;
            }
            Object key = table.getValueAt(i, 0);
            Option option = (Option) table.getValueAt(i, 2);
            Class<?> type = Class.forName(option.getValue().toString());

            params.add(Param.valueOf(key, value, type));
        }
        return params;
    }
}
