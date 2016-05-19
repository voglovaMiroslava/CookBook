/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.cookbook.gui;

import eu.dominiktousek.pv168.cookbook.Ingredient;
import eu.dominiktousek.pv168.cookbook.IngredientAmount;
import eu.dominiktousek.pv168.cookbook.IngredientAmountManagerImpl;
import eu.dominiktousek.pv168.cookbook.IngredientManagerImpl;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.JFrame;
import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dominik
 */
public class AddIngredientToRecipe extends javax.swing.JFrame {

    final static Logger LOG = LoggerFactory.getLogger(AddIngredientToRecipe.class);
    private IngredientAmount amount = new IngredientAmount();
    private Long recipeId;
    
    /**
     * Creates new form MainForm
     */
    public AddIngredientToRecipe(Long id) {
        recipeId= id;
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        panel1 = new java.awt.Panel();
        fieldName = new javax.swing.JTextField();
        label3 = new java.awt.Label();
        label4 = new java.awt.Label();
        jLabel5 = new javax.swing.JLabel();
        buttCreateIng = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        fieldAmount = new javax.swing.JTextField();
        buttCancel = new javax.swing.JButton();
        buttOk = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableIngredient = new javax.swing.JTable();
        buttSearch = new javax.swing.JButton();

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane3.setViewportView(jTextArea1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(433, 229));

        fieldName.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        fieldName.setToolTipText("Search existing ingredient");

        label3.setText("<none>");

        label4.setText("label4");

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/mycompany/cookbook/gui/Bundle"); // NOI18N
        jLabel5.setText(bundle.getString("amount")); // NOI18N

        buttCreateIng.setText(bundle.getString("createNew")); // NOI18N
        buttCreateIng.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttCreateIngActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel6.setText(bundle.getString("or")); // NOI18N

        fieldAmount.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        fieldAmount.setToolTipText("For exampe: '1 Ks'");

        buttCancel.setText(bundle.getString("cancel")); // NOI18N
        buttCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttCancelActionPerformed(evt);
            }
        });

        buttOk.setText(bundle.getString("ok")); // NOI18N
        buttOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttOkActionPerformed(evt);
            }
        });

        tableIngredient.setModel(new IngredientTableModel());
        buttSearchActionPerformed(new java.awt.event.ActionEvent(new Object(),0,"command"));
        jScrollPane2.setViewportView(tableIngredient);

        buttSearch.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        buttSearch.setText(bundle.getString("search")); // NOI18N
        buttSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttSearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(367, 367, 367)
                                .addComponent(buttCancel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(buttOk, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fieldAmount)
                                .addGap(1, 1, 1))))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 550, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addComponent(fieldName, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(buttSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addGap(19, 19, 19)
                        .addComponent(buttCreateIng, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(12, Short.MAX_VALUE))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fieldName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttCreateIng)
                    .addComponent(jLabel6)
                    .addComponent(buttSearch))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(fieldAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttCancel)
                    .addComponent(buttOk))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
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

    private void buttCreateIngActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttCreateIngActionPerformed
        javax.swing.JFrame editIng = new AddEditIngredient(null);
        editIng.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        editIng.setVisible(true);
    }//GEN-LAST:event_buttCreateIngActionPerformed

    private void buttCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttCancelActionPerformed
        super.dispose();
    }//GEN-LAST:event_buttCancelActionPerformed

    private void buttSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttSearchActionPerformed
        ((IngredientTableModel) tableIngredient.getModel()).clear();
        String name = fieldName.getText();
        (new SearchIngredientWorker(name)).execute();

    }//GEN-LAST:event_buttSearchActionPerformed

    private void buttOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttOkActionPerformed
        int rowNum = tableIngredient.getSelectedRow();
        if (rowNum == -1 || fieldAmount.getText().isEmpty()) {
            return;
        }
        
        IngredientTableModel model = (IngredientTableModel) tableIngredient.getModel();
        Ingredient ingr = model.getValueByRow(rowNum);
        amount.setIngredient(ingr);
        amount.setAmount( fieldAmount.getText());
        amount.setRecipeId(recipeId);
        
        (new CreateIngredientAmountWorker(amount, this)).execute();
        
    }//GEN-LAST:event_buttOkActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttCancel;
    private javax.swing.JButton buttCreateIng;
    private javax.swing.JButton buttOk;
    private javax.swing.JButton buttSearch;
    private javax.swing.JTextField fieldAmount;
    private javax.swing.JTextField fieldName;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea jTextArea1;
    private java.awt.Label label3;
    private java.awt.Label label4;
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
    
    private class CreateIngredientAmountWorker extends SwingWorker<Integer, Void>{

        private IngredientAmount amount;
        private JFrame frame;
        
        public CreateIngredientAmountWorker(IngredientAmount amount, JFrame frame){
            this.amount = amount;
            this.frame = frame;
        }
        
        @Override
        protected Integer doInBackground() throws Exception {
            new IngredientAmountManagerImpl().addIngredientInRecipe(amount);
            return 1;
        }
    
        @Override
        protected void done() {
            frame.dispose();
        }
    }

}
