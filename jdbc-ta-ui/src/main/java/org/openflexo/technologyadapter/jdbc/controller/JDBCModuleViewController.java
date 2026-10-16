package org.openflexo.technologyadapter.jdbc.controller;

import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.view.GinaViewFactory;
import org.openflexo.technologyadapter.jdbc.model.JDBCLine;
import org.openflexo.technologyadapter.jdbc.model.JDBCResultSet;
import org.openflexo.technologyadapter.jdbc.model.JDBCTable;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.FlexoFIBController;

import java.util.Collections;
import java.util.List;

/**
 * Controller for JDBC Module
 */
public class JDBCModuleViewController extends FlexoFIBController {

	private int pageSize = 20;

	private JDBCTable selectedTable = null;
	private int currentPage = 0;

	private JDBCResultSet resultSet = null;

	public JDBCModuleViewController(FIBComponent component, GinaViewFactory<?> viewFactory) {
		super(component, viewFactory);
	}

	public JDBCModuleViewController(FIBComponent component, GinaViewFactory<?> viewFactory, FlexoController controller) {
		super(component, viewFactory, controller);
	}

	public JDBCTable getSelectedTable() {
		return selectedTable;
	}

	public void setSelectedTable(JDBCTable selectedTable) {
		JDBCTable oldValue = this.selectedTable;
		this.selectedTable = selectedTable;
		getPropertyChangeSupport().firePropertyChange("selectedTable", oldValue, selectedTable);
		updateResultSet();
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		int oldValue = this.currentPage;
		this.currentPage = currentPage;
		getPropertyChangeSupport().firePropertyChange("currentPage", oldValue, currentPage);
		updateResultSet();
	}

	public List<JDBCLine> getLines() {
		return resultSet != null ? resultSet.getLines() : Collections.<JDBCLine>emptyList();
	}

	public void updateResultSet() {
		List<JDBCLine> oldValue = getLines();
		if (selectedTable != null && currentPage >= 0) {
			resultSet = selectedTable.select(null, null, pageSize, currentPage*pageSize);
		} else {
			resultSet = null;
		}
		getPropertyChangeSupport().firePropertyChange("lines", oldValue, getLines());
	}
}
