package de.uni.freiburg.iig.telematik.sewol.context.du;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import de.invation.code.toval.types.DataUsage;
import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.ParameterException.ErrorCode;
import de.invation.code.toval.validate.Validate;
import de.uni.freiburg.iig.telematik.sewol.accesscontrol.acl.permission.ObjectPermissionItemEvent;
import de.uni.freiburg.iig.telematik.sewol.accesscontrol.acl.permission.ObjectPermissionItemListener;
import de.uni.freiburg.iig.telematik.sewol.accesscontrol.acl.permission.ObjectPermissionListenerSupport;
import de.uni.freiburg.iig.telematik.sewol.accesscontrol.acl.permission.ObjectPermissionPanel;
import de.uni.freiburg.iig.telematik.sewol.context.process.ProcessContext;

public class DataUsageTableModel extends AbstractTableModel implements ObjectPermissionItemListener {

    private static final long serialVersionUID = -145830408957650293L;

    private static final int MIN_HEADER_WIDTH_ATTRIBUTE = 140;
    private static final int MIN_HEADER_WIDTH_ACCESSMODE = 150;

    private final List<ObjectPermissionPanel> dataUsagePanels = new ArrayList<>();
    private final List<String> attributes = new ArrayList<>();
    private final ObjectPermissionListenerSupport permissionListenerSupport = new ObjectPermissionListenerSupport();
    private ProcessContext context = null;
    private final String[] columnNames = {"Attribute", "Access Mode (R,W,C,D)"};

    public DataUsageTableModel(ProcessContext context) {
        Validate.notNull(context);
        if (!context.containsActivities()) {
            throw new ParameterException(ErrorCode.EMPTY, "Context does not contain any activities");
        }
        this.context = context;
    }

    public boolean isEmpty() {
        return attributes.isEmpty();
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    public void addElements(Collection<String> attributes) {
        for (String activity : attributes) {
            addElement(activity);
        }
    }

    public int getMinHeaderWidth(int column) {
        if (column == 0) {
            return MIN_HEADER_WIDTH_ATTRIBUTE;
        }
        if (column == 1) {
            return MIN_HEADER_WIDTH_ACCESSMODE;
        }
        return 0;
    }

    public Dimension preferredCellSize() {
        if (getRowCount() == 0) {
            Dimension dim = new ObjectPermissionPanel("dummy", context.getValidUsageModes()).getPreferredSize();
            return dim;
        }
        return dataUsagePanels.get(0).getPreferredSize();
    }

    public void addElement(String attribute, Set<DataUsage> dataUsageModes) {
        addElement(attribute);
        dataUsagePanels.get(dataUsagePanels.size() - 1).setPermission(dataUsageModes);
    }

    public void addElement(String attribute) {
        if (attributes.contains(attribute)) {
            return;
        }
        ObjectPermissionPanel newPanel = new ObjectPermissionPanel(attribute, context.getValidUsageModes());
        newPanel.addPermissionItemListener(this);
        dataUsagePanels.add(newPanel);
        attributes.add(attribute);
        fireTableRowsInserted(attributes.size() - 1, attributes.size() - 1);
    }

    public void clear() {
        List<String> attributesToRemove = new ArrayList<>();
        attributesToRemove.addAll(attributes);
        for (String attribute : attributesToRemove) {
            removeElement(attribute);
        }
    }

    public void removeElement(String attribute) {
        if (!attributes.contains(attribute)) {
            return;
        }
        int index = attributes.indexOf(attribute);
        dataUsagePanels.get(index).removePermissionItemListener(this);
        dataUsagePanels.remove(index);
        attributes.remove(attribute);
        fireTableRowsDeleted(index, index);
    }

    public void addPermissionItemListener(ObjectPermissionItemListener listener) {
        permissionListenerSupport.addPermissionItemListener(listener);
    }

    public void removePermissionItemListener(ObjectPermissionItemListener listener) {
        permissionListenerSupport.removePermissionItemListener(listener);
    }

    @Override
    public void permissionChanged(ObjectPermissionItemEvent e) {
        permissionListenerSupport.firePermissionChangedEvent(e);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return attributes.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return attributes.get(rowIndex);
        }
        if (columnIndex == 1) {
            return dataUsagePanels.get(rowIndex);
        }
        return null;
    }

    @Override
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        if(col == 1) {
            return true;
        }
        return false;
    }

}
