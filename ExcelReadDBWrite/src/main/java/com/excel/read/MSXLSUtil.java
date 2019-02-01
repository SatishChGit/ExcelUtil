package com.excel.read;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import test.model.Employee;

public class MSXLSUtil {

	static String file = "EmployeeInfo.xlsx";
	Connection con = DBConnection.getConnection();

	public void storeEmployeeInfo() {
		parseFileStream();
	}

	public void deleteexistsDatafromDB() {

		Statement st = null;
		try {
			con.setAutoCommit(true);
			st = con.createStatement();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		try {
			int deleted = st.executeUpdate("delete from EMPLOYEE");
			System.out.println(deleted);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void showDataFromDB() {
		Statement st = null;
		try {
			con.setAutoCommit(true);
			st = con.createStatement();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		try {
			ResultSet rs = st.executeQuery("select * from EMPLOYEE");
			while (rs.next()) {
				System.out.print(rs.getString(1) + "  " + rs.getString(2) + "  " + rs.getString(3) + "  "
						+ rs.getString(4) + "  " + rs.getString(5) + "  " + rs.getString(6));
				System.out.println("");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	private FileInputStream readFile() {
		FileInputStream input = null;
		try {
			input = new FileInputStream(file);
		} catch (Exception e) {
		}
		return input;
	}

	private void parseFileStream() {
		XSSFWorkbook workbook = null;
		InputStream stream = null;
		XSSFSheet sheet = null;
		try {
			stream = readFile();
			workbook = new XSSFWorkbook(stream);
			sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.rowIterator();
			List<Employee> liEmp = getEmployeeData(rowIterator);

			if (DBConnection.isTableNotExists()) {
				DBConnection.createTable();
			}
			con.setAutoCommit(false);
			PreparedStatement prepStmt = con.prepareStatement(
					"insert into employee(empid,firstname,secondname,designation, joiningDate, isPermanent) values (?,?,?,?,?,?)");
			int j = 0;
			for (Employee employee : liEmp) {
				j = 0;
				prepStmt.setInt(++j, employee.getEmpId());
				prepStmt.setString(++j, employee.getFirstName());
				prepStmt.setString(++j, employee.getSecondName());
				prepStmt.setString(++j, employee.getDesignation());
				prepStmt.setString(++j, employee.getJoiningDate());
				prepStmt.setBoolean(++j, employee.isPermanent());
				prepStmt.addBatch();
			}
			int[] numUpdates = prepStmt.executeBatch();
			prepStmt.close();
			for (int i = 0; i < numUpdates.length; i++) {
				if (numUpdates[i] == -2)
					System.out.println("Execution " + i + ": unknown number of rows updated");
				else
					System.out.println("Execution " + i + "successful: " + numUpdates[i] + " rows updated");
			}
			con.commit();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				workbook.close();
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private List<Employee> getEmployeeData(Iterator<Row> rowIterator) {
		List<Employee> liEmp = new ArrayList<>();
		Row row = null;
		Iterator<Cell> cellIterator = null;
		Cell cell = null;
		Employee emp = null;
		// headers not required to read so skipping
		rowIterator.next();
		while (rowIterator.hasNext()) {
			row = rowIterator.next();
			cellIterator = row.cellIterator();
			emp = new Employee();
			while (cellIterator.hasNext()) {
				cell = cellIterator.next();
				switch (cell.getColumnIndex()) {
				case 0:
					if (cell.getCellType() == CellType.NUMERIC) {
						emp.setEmpId((int) cell.getNumericCellValue());
					}
					break;
				case 1:
					if (cell.getCellType() == CellType.STRING) {
						emp.setFirstName(cell.getStringCellValue());
					}
					break;
				case 2:
					if (cell.getCellType() == CellType.STRING) {
						emp.setSecondName(cell.getStringCellValue());
					}
					break;
				case 3:
					if (cell.getCellType() == CellType.STRING) {
						emp.setDesignation(cell.getStringCellValue());
					}
					break;
				case 4:
					if (cell.getCellType() == CellType.NUMERIC) {
						if (DateUtil.isCellDateFormatted(cell)) {
							SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
							emp.setJoiningDate(dateFormat.format(cell.getDateCellValue()));
						}
					}
					break;
				case 5:
					if (cell.getCellType() == CellType.BOOLEAN) {
						emp.setPermanent(cell.getBooleanCellValue());
					}
					break;
				default:
					break;

				}
			}
			liEmp.add(emp);
		}
		return liEmp;
	}

}
