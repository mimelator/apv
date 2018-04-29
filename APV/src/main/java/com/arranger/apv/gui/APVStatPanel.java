package com.arranger.apv.gui;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.arranger.apv.Main;
import com.arranger.apv.util.CounterMap;

public class APVStatPanel extends APVFrame {

	CounterMapTableModel model;
	
	JPanel panel;
	
	public APVStatPanel(Main parent, CounterMap map) {
		super(parent);
		model = new CounterMapTableModel(map);
		
		panel = new JPanel();
		
		JTable table = new JTable(model);
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table.getModel());
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
		sorter.setSortKeys(sortKeys);
		table.setRowSorter(sorter);
		
		panel.add(new JScrollPane(table));
		
		
		panel.addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent e) {
				update();
			}

			public void componentHidden(ComponentEvent e) {
				//System.out.println("Component hidden");
			}
		});
	}
	
	public void update() {
		model.reload();
		model.fireTableDataChanged();
	}
	
	@SuppressWarnings("serial")
	private class CounterMapTableModel extends AbstractTableModel {

		protected CounterMap map;
        private String[] keys;

        public CounterMapTableModel(CounterMap map) {
        	this.map = map;
            reload();
        }
        
        @Override
		public Class<?> getColumnClass(int col) {
        	if (col == 0) {
        		return String.class;
        	} else {
        		return Integer.class;
        	}
		}

		public void reload() {
        	keys = map.getMap().keySet().toArray(new String[map.getMap().size()]);
        }

        @Override
        public String getColumnName(int col) {
            if (col == 0) {
                return "Key";
            } else {
                return "Value";
            }
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public int getRowCount() {
            return map.getMap().size();
        }

        @Override
        public Object getValueAt(int row, int col) {
            if (col == 0) {
                return keys[row];
            } else {
                return map.get(keys[row]);
            }
        }
    }
	
	public JPanel getPanel() {
		return panel;
	}
}
