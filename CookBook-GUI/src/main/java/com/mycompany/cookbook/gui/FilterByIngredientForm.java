/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.cookbook.gui;

import eu.dominiktousek.pv168.cookbook.Ingredient;
import eu.dominiktousek.pv168.cookbook.IngredientManagerImpl;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.JList;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dominik
 */
public class FilterByIngredientForm extends javax.swing.JFrame {

    private IngredientFormFilterModel selectedIngredients;

    final static Logger LOG = LoggerFactory.getLogger(MainForm.class);
    
    private MainForm form;

    /**
     * Creates new form MainForm
     */
    public FilterByIngredientForm(MainForm form) {
        this.form = form;
        initComponents();
        selectedIngredients = (IngredientFormFilterModel) jList2.getModel();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel1 = new java.awt.Panel();
        fieldName = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        buttAddToFilter = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        buttCancel = new javax.swing.JButton();
        buttSearch = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableIngredient = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(700, 700));
        setPreferredSize(new java.awt.Dimension(500, 500));

        panel1.setPreferredSize(new java.awt.Dimension(800, 800));

        fieldName.setToolTipText("Type here your search query");

        jList2.setModel(new IngredientFormFilterModel());
        jList2.setCellRenderer(new CheckboxListCellRenderer());
        jList2.setName(""); // NOI18N
        jList2.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                removeChckbox(evt);
            }
        });
        jScrollPane2.setViewportView(jList2);

        buttAddToFilter.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/mycompany/cookbook/gui/Bundle"); // NOI18N
        buttAddToFilter.setText(bundle.getString("addToFilter")); // NOI18N
        buttAddToFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttAddToFilterActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jButton2.setText(bundle.getString("ok")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        buttCancel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        buttCancel.setText(bundle.getString("cancel")); // NOI18N
        buttCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttCancelActionPerformed(evt);
            }
        });

        buttSearch.setText(bundle.getString("search")); // NOI18N
        buttSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttSearchActionPerformed(evt);
            }
        });

        jScrollPane3.setPreferredSize(new java.awt.Dimension(700, 700));

        tableIngredient.setModel(new IngredientTableModel()
        );
        jScrollPane3.setViewportView(tableIngredient);

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addComponent(buttAddToFilter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(buttCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2))
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addComponent(fieldName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttSearch))
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE))))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fieldName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttSearch))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttAddToFilter)
                    .addComponent(jButton2)
                    .addComponent(buttCancel))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void removeChckbox(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_removeChckbox
        JList list = (JList) evt.getSource();
        if (list.getSelectedIndex() < 0) {
            return;
        }

        IngredientFormFilterModel model = (IngredientFormFilterModel) list.getModel();
        model.removeElementAt(list.getSelectedIndex());

    }//GEN-LAST:event_removeChckbox

    private void buttCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttCancelActionPerformed
        super.dispose();
    }//GEN-LAST:event_buttCancelActionPerformed

    private void buttSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttSearchActionPerformed
        ((IngredientTableModel) tableIngredient.getModel()).clear();
        String name = fieldName.getText();
        (new SearchIngredientWorker(name)).execute();
    }//GEN-LAST:event_buttSearchActionPerformed

    private void buttAddToFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttAddToFilterActionPerformed
        int rowNum = tableIngredient.getSelectedRow();
        if (rowNum == -1) {
            return;
        }
        IngredientTableModel model = (IngredientTableModel) tableIngredient.getModel();
        selectedIngredients.addItem(model.getValueByRow(rowNum));
       

    }//GEN-LAST:event_buttAddToFilterActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // take all items and give them somewhere
        
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttAddToFilter;
    private javax.swing.JButton buttCancel;
    private javax.swing.JButton buttSearch;
    private javax.swing.JTextField fieldName;
    private javax.swing.JButton jButton2;
    private javax.swing.JList jList2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private java.awt.Panel panel1;
    private javax.swing.JTable tableIngredient;
    // End of variables declaration//GEN-END:variables

    private class SearchIngredientWorker extends SwingWorker<List<Ingredient>, Void> {

        private String name;

        public SearchIngredientWorker(String name) {
            this.name = name;
        }

        @Override
        protected List<Ingredient> doInBackground() throws Exception {
            return new IngredientManagerImpl().searchByName(name);
        }

        @Override
        protected void done() {
            IngredientTableModel model = (IngredientTableModel) tableIngredient.getModel();
            try {
                List<Ingredient> ingres = this.get();

                for (Ingredient ing : ingres) {
                    model.addItem(ing);
                }
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex.getMessage());
            }

        }

    }

}
