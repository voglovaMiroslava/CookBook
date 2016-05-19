/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.cookbook.gui;

import eu.dominiktousek.pv168.cookbook.Ingredient;
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
    private final List<Ingredient> items = new LinkedList<>();
    private Ingredient selected = null;
    
    @Override
    public int getSize() {
        return items.size();
    }

    @Override
    public String getElementAt(int index) {
        Ingredient item = items.get(index);
        return item.getName();
    }
    
    public void addItem(Ingredient item){
        int idx = items.size();
        items.add(item);
        fireIntervalAdded(this, idx, idx);
    }
    
    public void clear(){
        int size = items.size();
        items.clear();
        fireIntervalRemoved(this, 0, size-1);
    }
    
    public List<Ingredient> getAllItems(){
        return Collections.unmodifiableList(items);
    }
    
    public Ingredient getValueByIndex(int idx){
        return items.get(idx);
    }

    @Override
    public void setSelectedItem(Object anItem) {
        return;
    }

    @Override
    public Object getSelectedItem() {
        return null;
    }
    
}
