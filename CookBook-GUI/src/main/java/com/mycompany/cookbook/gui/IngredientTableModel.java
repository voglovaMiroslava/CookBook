package com.mycompany.cookbook.gui;

import eu.dominiktousek.pv168.cookbook.Ingredient;
import java.util.LinkedList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Dominik
 */
public class IngredientTableModel extends AbstractTableModel{
    
    private final List<Ingredient> data = new LinkedList<>();
    
    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Ingredient item = data.get(rowIndex);
        
        switch(columnIndex){
            case 0 : 
                return item.getName();
            default : 
                throw new IllegalArgumentException("columnIndex");
        }
    }
    
    public void addItem(Ingredient item){
        data.add(item);
        int changedRow = data.size()-1;
        fireTableRowsInserted(changedRow, changedRow);
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
}
