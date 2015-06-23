/*
 * CelFileGroupPanel.java
 *
 * Created on 15. Mai 2008, 12:45
 */

package de.mpimp.golm.robin.GUI.affy;

import de.mpimp.golm.robin.GUI.StrictRTermFilterDocument;
import de.mpimp.golm.robin.designer.model.GroupModel;
import de.mpimp.golm.common.gui.SimpleErrorMessage;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JList;

/**
 *
 * @author  marc
 */
public class CelFileGroupPanel extends javax.swing.JPanel {

    private String groupName;
    private JList sourceList;
    private GroupModel groupModel;

    /** Creates new form CelFileGroupPanel */
    public CelFileGroupPanel() {
        initComponents();
    }
    
    public CelFileGroupPanel(String name, JList listModel) {
        initComponents();
        groupFileList.setModel(new DefaultListModel());
        this.setGroupName(name);
        this.setSourceList(listModel);
    }

    public boolean addFilesFromSourceList(ArrayList<String> files) {
        for (String file : files) {
            if (this.getSourceFiles().contains(file)) {

                // add file to the group
                DefaultListModel groupFileListModel  = (DefaultListModel) groupFileList.getModel();
                groupFileListModel.addElement(file);

                //... and remove it from the source list
                DefaultListModel sourceListModel = (DefaultListModel) sourceList.getModel();
                int index = sourceListModel.indexOf(file);
                sourceListModel.removeElementAt(index);
            } else {

                // if the EXACT PATH is not found we can't proceed...
                //FIXME i could work around this problem by saving the MD5 hashes
                // of the file data and using them to compare the files,....
                new SimpleErrorMessage(null, "Trying to load files that are not found in the\n"+
                        "input data. Canceling");
                return false;
            }
        }
        return true;
    }

    private ArrayList<String> getSourceFiles() {
        ArrayList<String> fileList = new ArrayList<String>();

        for (int i = 0; i < sourceList.getModel().getSize(); i++ ) {
            fileList.add((String) sourceList.getModel().getElementAt(i));
        }

        return fileList;

    }

    public boolean addFileFromSourceList(String file) {
        if (this.getSourceFiles().contains(file)) {

            // add file to the group
            DefaultListModel groupFileListModel  = (DefaultListModel) groupFileList.getModel();
            groupFileListModel.addElement(file);

            //... and remove it from the source list
            DefaultListModel sourceListModel = (DefaultListModel) sourceList.getModel();
            int index = sourceListModel.indexOf(file);
            sourceListModel.removeElementAt(index);
        } else {

            // if the EXACT PATH is not found we can't proceed...
            //FIXME i could work around this problem by saving the MD5 hashes
            // of the file data and using them to compare the files,....
            //new em(null, "Trying to load files that are not found in the\n"+
            //        "input data. Canceling");
            //return false;
            //DEBUG
            System.out.println("File deleted from previous project: " + file);
        }
        return true;
    }

    public JList getSourceList() {
        return sourceList;
    }
    
    public GroupModel getGroupModel() {
        groupModel = new GroupModel(groupName, this.getFileList());
        return this.groupModel;
    }
    
    public ArrayList<String> getFileList() {
        ArrayList<String> fileList = new ArrayList<String>();
        
        for (int i = 0; i < groupFileList.getModel().getSize(); i++ ) {
            fileList.add((String) groupFileList.getModel().getElementAt(i));            
        }
        return fileList;
    }
    
    public boolean hasFiles() {
        return (this.getFileList().size() > 0) ? true : false;
    }

    public void setSourceList(JList sourceList) {
        this.sourceList = sourceList;
    }    

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
        this.groupNameField.setText(groupName);
    }
    
    public boolean isMarkedForDeletion() {
        return deleteGroupBox.isSelected();
    }
    
    public void salvageFileEntries() {
        // add all selected items to the group
        DefaultListModel sourceListModel  = (DefaultListModel) sourceList.getModel();
        for (int i = 0; i < groupFileList.getModel().getSize(); i++ ) {
            sourceListModel.addElement(groupFileList.getModel().getElementAt(i));        
        }
        
        // flush the group  
        ((DefaultListModel) groupFileList.getModel()).clear();
    }
  

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        groupFileList = new javax.swing.JList() {
            public String getToolTipText(MouseEvent evt) {
                // Get item index
                int index = locationToIndex(evt.getPoint());

                // Get item
                Object item = getModel().getElementAt(index);

                // Return the tool tip text
                return (String) item;
            }
        };
        groupNameField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        addFileToGroupButton = new javax.swing.JButton();
        removeFileFromGroupButton = new javax.swing.JButton();
        deleteGroupBox = new javax.swing.JCheckBox();

        setBackground(new java.awt.Color(214, 232, 255));
        setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jScrollPane1.setBackground(new java.awt.Color(214, 232, 255));
        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Files"));

        jScrollPane1.setViewportView(groupFileList);

        groupNameField.setDocument(new StrictRTermFilterDocument());
        groupNameField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                groupNameFieldFocusLost(evt);
            }
        });

        jLabel1.setText("Name:");

        addFileToGroupButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/DoubleRightArrowHS.png"))); // NOI18N
        addFileToGroupButton.setText("Add selected");
        addFileToGroupButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFileToGroupButtonActionPerformed(evt);
            }
        });

        removeFileFromGroupButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mpimp/golm/robin/resources/DoubleLeftArrowHS.png"))); // NOI18N
        removeFileFromGroupButton.setText("Remove selected");
        removeFileFromGroupButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeFileFromGroupButtonActionPerformed(evt);
            }
        });

        deleteGroupBox.setBackground(new java.awt.Color(214, 232, 255));
        deleteGroupBox.setText("<html><small>delete group ?</small></html>");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(groupNameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 108, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(removeFileFromGroupButton)
                        .add(addFileToGroupButton))
                    .add(deleteGroupBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE))
        );

        layout.linkSize(new java.awt.Component[] {addFileToGroupButton, removeFileFromGroupButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(groupNameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1))
                .add(18, 18, 18)
                .add(addFileToGroupButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(removeFileFromGroupButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(deleteGroupBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 143, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

private void addFileToGroupButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFileToGroupButtonActionPerformed
    
    // add all selected items to the group
    DefaultListModel groupFileListModel  = (DefaultListModel) groupFileList.getModel();
    for (Object sourceItem : sourceList.getSelectedValues() ) {
        groupFileListModel.addElement(sourceItem);        
    }
    
    //... and remove them from the source list
    DefaultListModel sourceListModel = (DefaultListModel) sourceList.getModel();
    int len = sourceList.getSelectedIndices().length;
    for (int i = len-1; i >= 0; i-- ) {
        sourceListModel.removeElementAt(sourceList.getSelectedIndices()[i]);
    }
}//GEN-LAST:event_addFileToGroupButtonActionPerformed

private void removeFileFromGroupButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeFileFromGroupButtonActionPerformed
    // add all selected items to the group
    DefaultListModel sourceListModel  = (DefaultListModel) sourceList.getModel();
    for (Object groupListItem : groupFileList.getSelectedValues() ) {
        sourceListModel.addElement(groupListItem);        
    }
    
    //... and remove them from the source list
    DefaultListModel groupFileListModel = (DefaultListModel) groupFileList.getModel();
    int len = groupFileList.getSelectedIndices().length;
    for (int i = len-1; i >= 0; i-- ) {
        groupFileListModel.removeElementAt(groupFileList.getSelectedIndices()[i]);
    }
}//GEN-LAST:event_removeFileFromGroupButtonActionPerformed

private void groupNameFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_groupNameFieldFocusLost
    String rawGroupName = groupNameField.getText();
    // delete leading and trailing whitespace
    rawGroupName = rawGroupName.trim();
    
    if (rawGroupName.matches("\\d+.*")) {
        new SimpleErrorMessage(this, "Group names must not start with a number.\nAutomatically deleting the leading numbers...");
        String badname = rawGroupName;
        String goodname = badname.replaceFirst("\\d+", "");
        groupNameField.setText(goodname);       
        this.setGroupName(goodname);
        System.out.println("Groupname: "+this.getGroupName());
        return;             
    }    
    this.setGroupName(rawGroupName);
    System.out.println("Groupname: "+this.getGroupName());
}//GEN-LAST:event_groupNameFieldFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addFileToGroupButton;
    private javax.swing.JCheckBox deleteGroupBox;
    private javax.swing.JList groupFileList;
    private javax.swing.JTextField groupNameField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton removeFileFromGroupButton;
    // End of variables declaration//GEN-END:variables
    
}
