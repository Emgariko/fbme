package org.fbme.smvDebugger.panel.mvc;

import javax.swing.table.DefaultTableModel;

public class UneditableTableModel extends DefaultTableModel {
  public UneditableTableModel(final Object[][] data, final Object[] headers) {
    super(data, headers);
  }

  @Override
  public boolean isCellEditable(final int rowIndex, final int columnIndex) {
    return false;
  }
}
