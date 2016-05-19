package com.mycompany.cookbook.gui;


import eu.dominiktousek.pv168.cookbook.Ingredient;
import eu.dominiktousek.pv168.cookbook.IngredientAmount;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Dominik
 */
public class IngredientAmountTableModel extends AbstractTableModel{
    
    private final List<IngredientAmount> data = new LinkedList<>();
    private final ResourceBundle bundle = ResourceBundle.getBundle("com/mycompany/cookbook/gui/Bundle");
    
    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        IngredientAmount item = data.get(rowIndex);
        
        switch(columnIndex){
            case 0 : 
                return item.getIngredient().getName();
            case 1 : 
                return item.getAmount();
            default : 
                throw new IllegalArgumentException("columnIndex");
        }
    }
    
    public void addItem(IngredientAmount item){
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
    
    public List<IngredientAmount> getAllData(){
        return Collections.unmodifiableList(data);
    }
    
    public IngredientAmount getValueByRow(int rowIndex){
        return data.get(rowIndex);
    }
    
    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return bundle.getString("name");
            case 1:
                return bundle.getString("amount");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
            case 1:
                return String.class;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
    
}
