/*
 * (c) Copyright 2013- Openflexo
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openflexo.technologyadapter.jdbc.library;

import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.icon.ImageIconResource;
import org.openflexo.rm.ResourceLocator;

public class JDBCIconLibrary {
	private static final Logger logger = Logger.getLogger(JDBCIconLibrary.class.getPackage().getName());

	public static final ImageIcon JDBC_TECHNOLOGY_BIG_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/jdbc-text_big.gif"));
	public static final ImageIcon JDBC_TECHNOLOGY_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/jdbc-text.gif"));
	public static final ImageIcon JDBC_FILE_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/jdbc-text.gif"));

	public static final ImageIcon JDBC_SCHEMA_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/jdbc-text.gif"));
	public static final ImageIcon JDBC_TABLE_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/jdbc-table.png"));
	public static final ImageIcon JDBC_COLUMN_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/jdbc-column.png"));
	public static final ImageIcon JDBC_ROW_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/jdbc-row.png"));
	public static final ImageIcon JDBC_KEY_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/key_16x16.png"));

	public static final ImageIcon OPEN_TRANSACTION_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/OpenTransaction_16x16.png"));
	public static final ImageIcon COMMIT_TRANSACTION_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Commit_16x16.png"));
	public static final ImageIcon ROLLBACK_TRANSACTION_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/Rollback_16x16.png"));
	public static final ImageIcon PERFORM_SQL_QUERY_ICON = new ImageIconResource(
			ResourceLocator.locateResource("Icons/PerformSQLQuery_16x16.png"));

	public static final ImageIcon SAVE_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Save.png"));
	public static final ImageIcon REFRESH_ICON = new ImageIconResource(ResourceLocator.locateResource("Icons/Refresh.png"));

	public static ImageIcon iconForObject(Class<? extends TechnologyObject<?>> objectClass) {
		return JDBC_TECHNOLOGY_ICON;
	}
}
