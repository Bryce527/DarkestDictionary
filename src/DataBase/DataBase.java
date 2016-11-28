package DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.PreparedStatement;

public class DataBase {
	private static Connection connection;
	
	public DataBase() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Driver loaded");
			
			//Establish a connection
			connection = DriverManager.getConnection("jdbc:mysql://localhost/users?characterEncoding=utf8&useSSL=true", "root", "123456");
			System.out.println("Datbase connected");
		}
		catch(SQLException ex) {
			System.out.println(ex);
		}
		catch(ClassNotFoundException ex) {
			System.out.println(ex);
		}
	}
	
	public String queryFromClient(String request) {
		StringBuilder result = new StringBuilder();
        String[] sourceStrArray = request.split("@");	//请求按照@分割

		if(request.charAt(0) == '0') {		//注册，用户名、密码
		//形如 0@id@password
			try {
				return query_user_id_password(sourceStrArray[1], sourceStrArray[2]);
			}
			catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(request.charAt(0) == '1') {		//登陆，用户名、密码
			try {
				String res = query_user_id_password(sourceStrArray[1], sourceStrArray[2]);
				if(res.equalsIgnoreCase("1") || res.equalsIgnoreCase("2"))
					return "0";		//用户已经存在
				else {
					register_user_id_password(sourceStrArray[1], sourceStrArray[2]);
					return "1";		//注册成功
				}		
			}
			catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(request.charAt(0) == '2') {		//查单词
		//形如 2@word@111
			
		}
		return result.toString();
		
	}
	
	private static String query_user_id_password(String id, String password) 
	throws SQLException, ClassNotFoundException{
		connection = DriverManager.getConnection("jdbc:mysql://localhost/users?characterEncoding=utf8&useSSL=true", "root", "123456");
		Statement sql_statement = connection.createStatement();
		String query = "select * from users.userinfo";
		ResultSet resultset = sql_statement.executeQuery(query); 
		while (resultset.next()) { 
			String ID = resultset.getString("userinfo.id");
			if(id.equalsIgnoreCase(ID)) {
				String PASSWORD = resultset.getString("userinfo.password");
				if(password.equalsIgnoreCase(PASSWORD)) {
					return "2";		//用户名、密码正确
				}
				else return "1";	//用户名存在，密码错误
			}
		} 
		return "0";		//用户名不存在
	}
	
	private static void register_user_id_password(String id, String password)
	throws SQLException, ClassNotFoundException {
		connection = DriverManager.getConnection("jdbc:mysql://localhost/users?characterEncoding=utf8&useSSL=true", "root", "123456");
		PreparedStatement ps = (PreparedStatement) connection.prepareStatement("insert into users.userinfo(id,password) values(?, ?)");
        ps.setString(1, id);
        ps.setString(2, password);
        ps.executeUpdate();
		connection.close();
	}
	
	private static String query_word_meanings(String word)
	throws SQLException, ClassNotFoundException {
		StringBuilder result = new StringBuilder();
		Statement sql_statement = connection.createStatement();
		String query = "select count(*) from(SELECT count(*) FROM users.wordinfo WHERE (:word = users.wordinfo.word) GROUP BY website) a;";
		return result.toString();
	}
	
}
