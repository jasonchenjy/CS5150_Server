import java.sql.*;
import java.io.*;
import java.util.*;
import java.util.Calendar;

public class Connector {
	public static final String SUCCESSFUL = "1";
	public static final String FAILED = "0";
	public static final String SIGN_UP_USERNAME_CRASHED = "2";
	public static final String ADD_NEW_USERNAME_NOT_FOUND = "2";
	public static final String USER_APPROVED = "1";
	// Connection to the database. Kept open between calls.
	
	private static final String host="sql5.freemysqlhosting.net";
	private static final String user_name="sql554902";
	private static final String port="3306";
	private static final String password="zX7%sW9!";

	public boolean isConnected;
	private String dburl;
	private Connection con;

	// Constructors
	public Connector(String url) {
		dburl = url;
		isConnected = false;
	}

	public Connector() {
		this("jdbc:mysql://"+host+":"+port+"/"+user_name);
	}

	public boolean Connect() {
		if (isConnected)
			return true;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(dburl, user_name, password);
			isConnected = true;
		} catch (Exception e) {	
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void Disconnect() {
		if (!isConnected)
			return;
		try {
			con.close();
			isConnected = false;
		} catch (Exception e) {
		}
	}

	private synchronized ResultSet executeQuery(String query) {
		Connect();
		ResultSet output = null;
		if (!isConnected)
			return null;
		try {
			Statement stmt = con.createStatement();
			output = stmt.executeQuery(query);
		} catch (Exception e) {
			output = null;
		}
		return output;
	}

	private synchronized boolean execute(String query) {
		Connect();
		boolean output = true;
		if (!isConnected)
			return false;
		try {
			Statement stmt = con.createStatement();
			stmt.execute(query);
		} catch (Exception e) {
			e.printStackTrace();
			output = false;
		}
		Disconnect();
		return output;
	}

	private String EscapeString(String input) {
		String output = "";
		for (int i = 0; i < input.length(); i++) {
			int charvalue = (int) input.charAt(i);
			if ((charvalue >= 48 && charvalue <= 57)
					|| (charvalue >= 65 && charvalue <= 90)
					|| (charvalue >= 97 && charvalue <= 122)) {
				output += input.charAt(i);
			} else {
				output += "#" + String.valueOf(charvalue) + "#";
			}
		}
		return output;
	}

	private String UnescapeString(String input) {
		if (input == null)
			return "";
		String output = "";
		boolean converting = false;
		int charvalue = 0;
		for (int i = 0; i < input.length(); i++) {
			char curchar = input.charAt(i);
			if (!converting) {
				// looking at normal
				if (curchar == '#') {
					converting = true;
					continue;
				}
				output += curchar;
			} else {
				if (curchar == '#') {
					output += (char) charvalue;
					charvalue = 0;
					converting = false;
					continue;
				}
				charvalue *= 10;
				charvalue += Character.getNumericValue(curchar);
			}
		}
		return output;
	}

	private String ResultSetToString(ResultSet rs) {
		String output = "";
		if (rs == null) {
			// TODO
			Disconnect();
			return "";
		}
		try {
			boolean first = true;
			while (rs.next()) {
				if (!first) {
					output = output + ":;:";
				}
				int cols = rs.getMetaData().getColumnCount();
				for (int i = 1; i <= cols; i++) {
					if (i != 1)
						output = output + ":;:";
					output = output + UnescapeString(rs.getString(i));
				}
				first = false;
			}
		} catch (SQLException e) {
			output = "";
			e.printStackTrace();
		}
		// TODO
		Disconnect();
		return output;
	}

	/* execute */
	public String get_chicken(String component, String age){
		component = EscapeString(component);
		age = EscapeString(age);
		String query = "SELECT `"+ age +"` FROM `Chicken_Leghorn-Type` WHERE Component='"+component+"';";
		System.out.println("query: "+query);
		return ResultSetToString(executeQuery(query));
	} 
	
	public static void main(String[] args) {

		Connector c = new Connector();
		c.Connect();
	}

}
