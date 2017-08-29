/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Openflexo-technology-adapters-ui, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.technologyadapter.jdbc.gui.fib;

import org.junit.Test;
import org.openflexo.gina.test.GenericFIBInspectorTestCase;
import org.openflexo.rm.FileResourceImpl;
import org.openflexo.rm.ResourceLocator;

public class TestJDBCInspectors extends GenericFIBInspectorTestCase {

	/*
	 * Use this method to print all
	 * Then copy-paste 
	 */
	public static void main(String[] args) {
		System.out.println(generateInspectorTestCaseClass(((FileResourceImpl) ResourceLocator.locateResource("Inspectors/JDBC")).getFile(),
				"Inspectors/JDBC/"));
	}

	@Test
	public void testCreateHbnResourceInspector() {
		validateFIB("Inspectors/JDBC/CreateHbnResource.inspector");
	}

	@Test
	public void testAddJDBCLineInspector() {
		validateFIB("Inspectors/JDBC/EditionAction/AddJDBCLine.inspector");
	}

	@Test
	public void testCreateJDBCResourceInspector() {
		validateFIB("Inspectors/JDBC/EditionAction/CreateJDBCResource.inspector");
	}

	@Test
	public void testSelectJDBCColumnInspector() {
		validateFIB("Inspectors/JDBC/EditionAction/SelectJDBCColumn.inspector");
	}

	@Test
	public void testSelectJDBCLineInspector() {
		validateFIB("Inspectors/JDBC/EditionAction/SelectJDBCLine.inspector");
	}

	@Test
	public void testSelectJDBCTableInspector() {
		validateFIB("Inspectors/JDBC/EditionAction/SelectJDBCTable.inspector");
	}

	@Test
	public void testHbnModelSlotInspector() {
		validateFIB("Inspectors/JDBC/HbnModelSlot.inspector");
	}

	@Test
	public void testJDBCColumnInspector() {
		validateFIB("Inspectors/JDBC/JDBCColumn.inspector");
	}

	@Test
	public void testJDBCConnectionInspector() {
		validateFIB("Inspectors/JDBC/JDBCConnection.inspector");
	}

	@Test
	public void testJDBCSchemaInspector() {
		validateFIB("Inspectors/JDBC/JDBCSchema.inspector");
	}

	@Test
	public void testJDBCTableInspector() {
		validateFIB("Inspectors/JDBC/JDBCTable.inspector");
	}

}
