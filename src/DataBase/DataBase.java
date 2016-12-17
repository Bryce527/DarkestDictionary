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

		if(request.charAt(0) == '0') {		//登陆，用户名、密码
		return "1@"+query_user_id_password(sourceStrArray[1], sourceStrArray[2]);
		}
		else if(request.charAt(0) == '1') {		//注册，用户名、密码
			try {
				String res = query_user_id_password(sourceStrArray[1], sourceStrArray[2]);
				if(res.equalsIgnoreCase("1") || res.equalsIgnoreCase("2"))
					return "0@0";		//用户已经存在
				else {
					register_user_id_password(sourceStrArray[1], sourceStrArray[2]);
					return "0@1";		//注册成功
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
	
	private static String query_user_id_password(String id, String password) {
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost/users?characterEncoding=utf8&useSSL=true", "root", "123456");
			Statement sql_statement = connection.createStatement();
			String query = "select * from users.userinfo";
			ResultSet resultset = sql_statement.executeQuery(query); 
			while (resultset.next()) { 
				String ID = resultset.getString("userinfo.id");
				if(id.equalsIgnoreCase(ID)) {
					String PASSWORD = resultset.getString("userinfo.password");
					if(password.equalsIgnoreCase(PASSWORD)) {
						connection.close();
						return "2";		//用户名、密码正确
					}
					else {
						connection.close();
						return "1";	//用户名存在，密码错误
					}
				}
			} 
			connection.close();
		}
		catch(SQLException e) {
			return "0";
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
	
	public void add_dianzan(String website, String word) {
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost/users?characterEncoding=utf8&useSSL=true", "root", "123456");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			PreparedStatement ps = (PreparedStatement) connection.prepareStatement("select * from users.wordinfo where  word = ? and website = ?");
	        ps.setString(1, word);
	        ps.setString(2, website);
	        ResultSet resultset = ps.executeQuery();
			while (resultset.next()) { 
				PreparedStatement add = (PreparedStatement) connection.prepareStatement("update users.wordinfo set number = number+1 where word = ? and website = ?");
				add.setString(1, word);
				add.setString(2, website);
				add.executeUpdate();
				connection.close();
				return;
			}
			//没有找到点赞元组
			PreparedStatement insert = (PreparedStatement) connection.prepareStatement("insert into users.wordinfo(word, website, number) values(?, ?, ?)");
			insert.setString(1, word);
			insert.setString(2, website);
			insert.setInt(3, 1);
			insert.executeUpdate();
			connection.close();
		}
		catch(SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public int query_dianzan(String website, String word) {
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost/users?characterEncoding=utf8&useSSL=true", "root", "123456");
			PreparedStatement ps = (PreparedStatement) connection.prepareStatement("select * from users.wordinfo where  word = ? and website = ?");
	        ps.setString(1, word);
	        ps.setString(2, website);
	        ResultSet resultset = ps.executeQuery();
			while (resultset.next()) { 
				int number = resultset.getInt("wordinfo.number");
				connection.close();
				return number;
			}	
			return 0;
		}
		catch(SQLException ex) {
			return 0;
		}
	}
	
}
