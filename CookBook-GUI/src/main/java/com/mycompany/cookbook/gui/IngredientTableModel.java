package com.mycompany.cookbook.gui;

import eu.dominiktousek.pv168.cookbook.Ingredient;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Dominik
 */
public class IngredientTableModel extends AbstractTableModel{
    
    private final List<Ingredient> data = new LinkedList<>();
    private final ResourceBundle bundle = ResourceBundle.getBundle("com/mycompany/cookbook/gui/Bundle");
    
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
    
    public void clear(){
        int lastIdx = data.size()-1;
        if(lastIdx<0){
            return;
        }
        data.clear();
        fireTableRowsDeleted(0, lastIdx);
    }
    
    public Ingredient getValueByRow(int rowIndex){
        return data.get(rowIndex);
    }
    
    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return bundle.getString("name");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
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
