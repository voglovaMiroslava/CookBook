package com.mycompany.cookbook.gui;


import eu.dominiktousek.pv168.cookbook.Ingredient;
import eu.dominiktousek.pv168.cookbook.IngredientAmount;
import java.util.LinkedList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Dominik
 */
public class IngredientAmountTableModel extends AbstractTableModel{
    
    private List<IngredientAmount> data = new LinkedList<>();
    
    public IngredientAmountTableModel(){
        super();
        IngredientAmount i = new IngredientAmount();
        Ingredient in = new Ingredient();
        in.setId(1l);
        in.setName("Cibule");
        i.setId(10l);
        i.setIngredient(in);
        i.setAmount("10 dkg");
        data.add(i);
    }
    
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
