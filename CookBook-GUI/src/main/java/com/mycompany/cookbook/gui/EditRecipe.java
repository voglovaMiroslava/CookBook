/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.cookbook.gui;

import eu.dominiktousek.pv168.cookbook.EntityNotFoundException;
import eu.dominiktousek.pv168.cookbook.IngredientAmount;
import eu.dominiktousek.pv168.cookbook.IngredientAmountManager;
import eu.dominiktousek.pv168.cookbook.IngredientAmountManagerImpl;
import eu.dominiktousek.pv168.cookbook.Recipe;
import eu.dominiktousek.pv168.cookbook.RecipeManager;
import eu.dominiktousek.pv168.cookbook.RecipeManagerImpl;
import eu.dominiktousek.pv168.cookbook.ServiceFailureException;
import java.time.Duration;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Miroslava
 */
public class EditRecipe extends javax.swing.JFrame {

    final static Logger LOG = LoggerFactory.getLogger(RecipeDetail.class);
    
    private final Long recipeId;
    private final ResourceBundle bundle;
    
    private boolean recipeLoaded = false;
    private boolean ingredientsLoaded = false;
    
    
    private class LoadRecipeInfoWorker extends SwingWorker<Recipe,Void>{

        private final Long recipeId;
        private final javax.swing.JFrame parentForm;

        public LoadRecipeInfoWorker(Long recipeId, javax.swing.JFrame parentForm){
            this.recipeId = recipeId;
            this.parentForm = parentForm;
        }

        @Override
        protected Recipe doInBackground() throws Exception {
            RecipeManager man = new RecipeManagerImpl();
            return man.getRecipeById(recipeId);
        }

        @Override
        protected void done() {   
            try {
                Recipe recipe = this.get();
                jTextField1.setText(recipe.getName());
                Long d = recipe.getDuration().toDays();
                Long h = recipe.getDuration().toHours();
                Long m = recipe.getDuration().toMinutes();
               
                int unit = 0;
                if((h>0)&&(h*60==m)){
                    unit = 1;
                }
                if((d>0)&&(d*24*60==m)){
                    unit = 2;
                }
                
                jSpinner1.setValue((unit==2)?d:(unit==1)?h:m);
                jComboBox1.setSelectedIndex(unit);
                jTextArea1.setText(recipe.getInstructions());
                recipeLoaded = true;
                if(ingredientsLoaded){
                    loadingDone();
                }
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error("Background loading of recipe was not successfull", ex);
                if(parentForm.isDisplayable()){
                    JOptionPane.showMessageDialog(parentForm, bundle.getString("background loading failed"),"",JOptionPane.ERROR_MESSAGE);
                    parentForm.dispose();
                }
            }
        }

    }
    
    private class LoadRecipeIngredientsWorker extends SwingWorker<List<IngredientAmount>,Void>{

        private final Long recipeId;
        private final javax.swing.JFrame parentForm;

        public LoadRecipeIngredientsWorker(Long recipeId, javax.swing.JFrame parentForm){
            this.recipeId = recipeId;
            this.parentForm = parentForm;
        }

        @Override
        protected List<IngredientAmount> doInBackground() throws Exception {
                IngredientAmountManager man = new IngredientAmountManagerImpl();
                return man.getIngredientsByRecipe(recipeId);
        }

        @Override
        protected void done(){   
            try {
                List<IngredientAmount> ingredientAmounts = this.get();
                IngredientAmountTableModel model = (IngredientAmountTableModel) jTable1.getModel();
                
                for(IngredientAmount i : ingredientAmounts){
                    model.addItem(i);
                }
                
                ingredientsLoaded = true;
                if(recipeLoaded){
                    loadingDone();
                }
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error("Background loading of ingredient amounts was not successfull", ex);
                if(parentForm.isDisplayable()){
                    JOptionPane.showMessageDialog(parentForm, bundle.getString("background loading failed"),"",JOptionPane.ERROR_MESSAGE);
                    parentForm.dispose();
                }
            }
        }

    }
    
    private class SaveRecipeInfoWorker extends SwingWorker<Boolean,Void>{

        private final Recipe recipe;
        private final javax.swing.JFrame parentForm;

        public SaveRecipeInfoWorker(Recipe recipe, javax.swing.JFrame parentForm){
            this.recipe = recipe;
            this.parentForm = parentForm;
        }

        @Override
        protected Boolean doInBackground() throws Exception {
            try{
                RecipeManager man = new RecipeManagerImpl();
                if(recipe.getId()==null){
                    man.createRecipe(recipe);
                }
                else{
                    man.updateRecipe(recipe);
                }
            }catch(EntityNotFoundException ex){
                LOG.error("Saving recipe was not successfull", ex);
                return false;
            }
            catch(ServiceFailureException ex){
                LOG.error("Saving/creating  recipe was not successfull", ex);
                return false;
            }
            return true;
        }

        @Override
        protected void done() {   
            try {
                if(get()){
                    parentForm.dispose();
                }else{
                    JOptionPane.showMessageDialog(parentForm, bundle.getString("saving failed"),"",JOptionPane.ERROR_MESSAGE);
                }
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error("Saving recipe was not successfull", ex);
                JOptionPane.showMessageDialog(parentForm, bundle.getString("saving failed"),"",JOptionPane.ERROR_MESSAGE);
            }
        }

    }
    
    private class RemoveIngredientAmountWorker extends SwingWorker<Boolean,Void>{

        private final Long iAmountId;
        private final javax.swing.JFrame parentForm;

        public RemoveIngredientAmountWorker(Long iAmountId, javax.swing.JFrame parentForm){
            this.iAmountId = iAmountId;
            this.parentForm = parentForm;
        }

        @Override
        protected Boolean doInBackground() throws Exception {
            try{
                IngredientAmountManager man = new IngredientAmountManagerImpl();
                man.deleteIngredientFromRecipe(iAmountId);
            }catch(EntityNotFoundException | ServiceFailureException ex){
                LOG.error("Removing ingredient form recipe was not successfull", ex);
                return false;
            }
            return true;
        }

        @Override
        protected void done() {   
            try {
                if(!get()){
                    JOptionPane.showMessageDialog(parentForm, bundle.getString("remove failed"),"",JOptionPane.ERROR_MESSAGE);
                }
            } catch (InterruptedException | ExecutionException ex) {
                LOG.info("Removing ingredient form recipe was not successfull", ex);
                JOptionPane.showMessageDialog(parentForm, bundle.getString("remove failed"),"",JOptionPane.ERROR_MESSAGE);
            }
        }

    }
    
    private void loadingDone(){
        statLabel.setText("");
        buttAddIngredientToRecipe.setEnabled(true);
        jButton1.setEnabled(true);
        jTextArea1.setEnabled(true);
        jTextField1.setEnabled(true);
        jSpinner1.setEnabled(true);
    }
    
    private void loadData(){
        statLabel.setText(bundle.getString("loading"));
        new LoadRecipeInfoWorker(recipeId,this).execute();
        //new LoadRecipeIngredientsWorker(recipeId,this).execute();
    }
    
    private void refreshIngredients(){
        if(recipeId!=null){
            IngredientAmountTableModel m = (IngredientAmountTableModel) jTable1.getModel();
            m.clear();
            new LoadRecipeIngredientsWorker(recipeId,this).execute();
        }
    }
    
    /**
     * Creates new form EditRecipe
     */
    public EditRecipe(Long recipeId) {
        bundle = ResourceBundle.getBundle("com/mycompany/cookbook/gui/Bundle");
        this.recipeId = recipeId;
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

        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jComboBox1 = new javax.swing.JComboBox<String>();
        jButton1 = new javax.swing.JButton();
        buttAddIngredientToRecipe = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jSeparator2 = new javax.swing.JSeparator();
        buttCancel = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        statLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/mycompany/cookbook/gui/Bundle"); // NOI18N
        jLabel1.setText(bundle.getString("name")); // NOI18N

        jTextField1.setEnabled(false);

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel2.setText(bundle.getString("duration")); // NOI18N

        jSpinner1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(Long.valueOf(0L), Long.valueOf(0L), null, Long.valueOf(1L)));
        jSpinner1.setEnabled(false);

        jComboBox1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { bundle.getString("minute(s)"), bundle.getString("hour(s)"), bundle.getString("day(s)") }));

        jButton1.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jButton1.setText(bundle.getString("save")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        buttAddIngredientToRecipe.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        buttAddIngredientToRecipe.setText(bundle.getString("addIngredient")); // NOI18N
        buttAddIngredientToRecipe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttAddIngredientToRecipeActionPerformed(evt);
            }
        });

        jPanel1.setBorder(new javax.swing.border.MatteBorder(null));

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel4.setText(bundle.getString("ingredients")); // NOI18N

        jTable1.setModel(new IngredientAmountTableModel());
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jTable1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTable1PropertyChange(evt);
            }
        });
        jScrollPane2.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("name")); // NOI18N
            jTable1.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("amount")); // NOI18N
        }

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel4)))
                .addContainerGap(336, Short.MAX_VALUE))
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32))
        );

        jPanel2.setBorder(new javax.swing.border.MatteBorder(null));

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel3.setText(bundle.getString("description")); // NOI18N

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setEnabled(false);
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                .addContainerGap())
        );

        buttCancel.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        buttCancel.setText(bundle.getString("cancel")); // NOI18N
        buttCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttCancelActionPerformed(evt);
            }
        });

        jButton4.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jButton4.setText(bundle.getString("remove")); // NOI18N
        jButton4.setEnabled(false);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(statLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(buttCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel1)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 384, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addComponent(jButton4)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(buttAddIngredientToRecipe)
                                    .addGap(2, 2, 2)))
                            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(buttAddIngredientToRecipe)
                            .addComponent(jButton4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(statLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(buttCancel)
                            .addComponent(jButton1))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttCancelActionPerformed
        super.dispose();
    }//GEN-LAST:event_buttCancelActionPerformed

    private void buttAddIngredientToRecipeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttAddIngredientToRecipeActionPerformed
        javax.swing.JFrame addToRecipe = new AddIngredientToRecipe(recipeId);
        addToRecipe.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addToRecipe.setVisible(true);
    }//GEN-LAST:event_buttAddIngredientToRecipeActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        if(recipeId!=null){
            loadData();
        }
        else{
            loadingDone();
        }
    }//GEN-LAST:event_formWindowOpened

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if(jTextField1.getText().isEmpty()){
           JOptionPane.showMessageDialog(this,
            bundle.getString("empty name"),
            "",
            JOptionPane.WARNING_MESSAGE); 
           return;
        }
        if(jTextArea1.getText().isEmpty()){
           JOptionPane.showMessageDialog(this,
            bundle.getString("empty instructions"),
            "",
            JOptionPane.WARNING_MESSAGE); 
           return;
        }
        Recipe r = new Recipe();
        r.setId(recipeId);
        r.setName(jTextField1.getText());
        r.setInstructions(jTextArea1.getText());
        switch(jComboBox1.getSelectedIndex()){
            case 0 : 
                r.setDuration(Duration.ofMinutes((Long)jSpinner1.getValue()));
            case 1 : 
                r.setDuration(Duration.ofHours((Long)jSpinner1.getValue()));
            case 2 : 
                r.setDuration(Duration.ofDays((Long)jSpinner1.getValue()));
        }
        
        new SaveRecipeInfoWorker(r, this).execute();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTable1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTable1PropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_jTable1PropertyChange

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        if(jTable1.getSelectedRow()>-1){
            jButton4.setEnabled(true);
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        Object[] options = {
            bundle.getString("yes"),
            bundle.getString("no")
        };
            
        int n = JOptionPane.showOptionDialog(this,
            bundle.getString("confirm delete"),
            "",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[1]);
        if(n==0){
            IngredientAmountTableModel model = (IngredientAmountTableModel) jTable1.getModel();
            IngredientAmount item = model.getValueByRow(jTable1.convertRowIndexToModel(jTable1.getSelectedRow()));
            new RemoveIngredientAmountWorker(item.getId(), this).execute();
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        
    }//GEN-LAST:event_formFocusGained

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        
    }//GEN-LAST:event_formWindowActivated

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
        refreshIngredients();
    }//GEN-LAST:event_formWindowGainedFocus

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(EditRecipe.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EditRecipe.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EditRecipe.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EditRecipe.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //new EditRecipe().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttAddIngredientToRecipe;
    private javax.swing.JButton buttCancel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel statLabel;
    // End of variables declaration//GEN-END:variables
}
