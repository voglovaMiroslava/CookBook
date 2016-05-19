/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.cookbook.gui;

import eu.dominiktousek.pv168.cookbook.Ingredient;
import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 *
 * @author Miroslava
 */
public class IngredientFormFilterModel extends AbstractListModel {

    private final List<Ingredient> items = new LinkedList<>();

    @Override
    public int getSize() {
        return items.size();
    }

    @Override
    public String getElementAt(int index) {
        Ingredient item = items.get(index);
        return item.getName();
    }

    public void addItem(Ingredient item) {
        items.add(item);
        fireIntervalAdded(this, items.size() - 1, items.size() - 1);
    }

    public void clear() {
        items.clear();
        if (items.size() - 1 >= 0) {
            fireIntervalRemoved(this, 0, items.size() - 1);
        }
    }

    public void removeElementAt(int index) {
        
        if (index >= 0) {
            items.remove(index);
            fireIntervalRemoved(this, index, index);
        }
    }
}
