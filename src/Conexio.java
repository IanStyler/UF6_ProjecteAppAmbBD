import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Conexio {

	public static Connection getConnection() throws SQLException {

		Connection conn = null;

		try (FileInputStream f = new FileInputStream("src//database.properties")) {

			// carrega el fitxer de propietats
			Properties pros = new Properties();
			pros.load(f);

			// assigna els paràmetres de la base de dades
			String url = pros.getProperty("url");
			String user = pros.getProperty("user");
			String password = pros.getProperty("password");

			// crea una connexió amb la base de dades
			conn = DriverManager.getConnection(url, user, password);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return conn;
	}
}