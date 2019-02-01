package com.excel.read;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.derby.database.Database;

public class DBConnection {

	private static String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	private static String protocol = "jdbc:derby:";
	private static Connection con;

	public static Connection getConnection() {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
		if (con == null) {
			try {
				con = DriverManager.getConnection(protocol + "employeeDB;create=true");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return con;
	}

	public static boolean createTable() {
		boolean isCreate = false;
		Statement s = null;
		try {
			if (con == null) {
				con = getConnection();
			}
			s = con.createStatement();
			s.execute(
					"create table employeeDB.employee(empid int, firstname varchar(40), secondname varchar(40), designation varchar(40), "
							+ "joiningDate date, isPermanent boolean)");
			isCreate = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				s.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return isCreate;
	}

	public static boolean isTableNotExists() {
		try {
			if (con == null) {
				con = getConnection();
			}
			DatabaseMetaData dmd = con.getMetaData();
			ResultSet rs = dmd.getTables(null, "EMPLOYEEDB", "EMPLOYEE", null);
			System.out.println(rs.getFetchSize());
			return !rs.next();
		} catch (SQLException ex) {
			Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		}
	}

}
