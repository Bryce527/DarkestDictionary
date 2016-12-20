package DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbcp2.BasicDataSource;

import com.mysql.jdbc.PreparedStatement;

public class DataBase {
	private static Connection connection;
	private static BasicDataSource bs = null;
	
	public DataBase() {
			connection = getConnection();
			System.out.println("Datbase connected!!!!");
	}
	
	public static BasicDataSource getDataSource(){
		if(bs == null) {
			bs = new BasicDataSource();
            bs.setDriverClassName("com.mysql.jdbc.Driver");  
            bs.setUrl("jdbc:mysql://localhost/users?characterEncoding=utf8&useSSL=true");  
            bs.setUsername("root");  
            bs.setPassword("123456");
            bs.setMaxTotal(100);//������󲢷���  
            bs.setInitialSize(10);//���ݿ��ʼ��ʱ�����������Ӹ���  
            bs.setMinIdle(5);//��С����������  
            bs.setMaxIdle(100);//���ݿ����������
            bs.setMaxWaitMillis(1000 * 5);//�������ȴ�ʱ��
		}
		return bs;
	}
	
    public static Connection getConnection() {  
        Connection con=null;  
        try {  
            if(bs!=null){  
                con=bs.getConnection();  
            }else{  
                con=getDataSource().getConnection();
            }  
        } catch (Exception e) {
        	System.out.println("Error");
        }  
        return con;  
    } 
	
	public String queryFromClient(String request) {
		StringBuilder result = new StringBuilder();
        String[] sourceStrArray = request.split("@");	//������@�ָ�

		if(request.charAt(0) == '0') {		//��½���û���������
		return "1@"+query_user_id_password(sourceStrArray[1], sourceStrArray[2]);
		}
		else if(request.charAt(0) == '1') {		//ע�ᣬ�û���������
			try {
				String res = query_user_id_password(sourceStrArray[1], sourceStrArray[2]);
				if(res.equalsIgnoreCase("1") || res.equalsIgnoreCase("2"))
					return "0@0";		//�û��Ѿ�����
				else {
					register_user_id_password(sourceStrArray[1], sourceStrArray[2]);
					return "0@1";		//ע��ɹ�
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
						return "2";		//�û�����������ȷ
					}
					else {
						connection.close();
						return "1";	//�û������ڣ��������
					}
				}
			} 
			connection.close();
		}
		catch(SQLException e) {
			return "0";
		}
		return "0";		//�û���������
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
			//û���ҵ�����Ԫ��
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
