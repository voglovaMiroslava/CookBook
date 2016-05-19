/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.cookbook.gui;

import eu.dominiktousek.pv168.cookbook.IngredientAmount;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

/**
 *
 * @author Dominik
 */
public class IngredientComboboxModel extends AbstractListModel implements ComboBoxModel{
    private final List<IngredientAmount> items = new LinkedList<>();
    private IngredientAmount selected = null;
    
    @Override
    public int getSize() {
        return items.size();
    }

    @Override
    public String getElementAt(int index) {
        IngredientAmount item = items.get(index);
        return item.getIngredient().getName() + " " + item.getAmount();
    }
    
    public void addItem(IngredientAmount item){
        items.add(item);
    }
    
    public void clear(){
        items.clear();
    }
    
    public List<IngredientAmount> getAllItems(){
        return Collections.unmodifiableList(items);
    }
    
    public IngredientAmount getValueByIndex(int idx){
        return items.get(idx);
    }

    @Override
    public void setSelectedItem(Object anItem) {
        selected = (IngredientAmount) anItem;
    }

    @Override
    public Object getSelectedItem() {
        return selected;
    }
    
}
