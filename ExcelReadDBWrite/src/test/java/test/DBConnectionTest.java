package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.excel.read.DBConnection;

public class DBConnectionTest {

	private static Connection connection = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("before");
		connection = DBConnection.getConnection();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		connection.close();
	}

	@Before
	public void setUp() throws Exception {
		System.out.println("setup");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDBConInstance() {
		assertNotNull(connection);
	}

	@Test
	public void testTableCreation() {
		boolean isTableCreated = false;
		if (DBConnection.isTableNotExists()) {
			isTableCreated = DBConnection.createTable();
			assertTrue(isTableCreated);
		} else {
			assertFalse(isTableCreated);
		}

	}

}
