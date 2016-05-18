package com.mycompany.cookbook.gui;

import eu.dominiktousek.pv168.cookbook.Recipe;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Dominik
 */
public class RecipeTableModel extends AbstractTableModel {
    private final List<Recipe> data = new LinkedList<>();
    
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
