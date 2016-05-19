package com.mycompany.cookbook.gui;

import eu.dominiktousek.pv168.cookbook.Recipe;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Dominik
 */
public class RecipeTableModel extends AbstractTableModel {
    private final List<Recipe> data = new LinkedList<>();
    private final ResourceBundle bundle = ResourceBundle.getBundle("com/mycompany/cookbook/gui/Bundle");
    
    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Recipe item = data.get(rowIndex);
        
        switch(columnIndex){
            case 0 : 
                return item.getName();
            case 1 : 
                return item.getInstructions();
            case 2 : 
                return item.getDuration();
            default : 
                throw new IllegalArgumentException("columnIndex");
        }
    }
    
    public void addItem(Recipe item){
        data.add(item);
        int changedRow = data.size()-1;
        fireTableRowsInserted(changedRow, changedRow);
    }
    
    public void clear(){
        int oldSize = data.size();
        data.clear();
        fireTableRowsDeleted(0, oldSize-1);
    }
    
    public Recipe getValueByRow(int rowIndex){
        return data.get(rowIndex);
    }
    
    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return bundle.getString("name");
            case 1:
                return bundle.getString("description");
            case 2:
                return bundle.getString("duration");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return String.class;
            case 2:
                return Duration.class;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
    
}
