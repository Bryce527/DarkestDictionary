package Server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.awt.*;
import javax.swing.*;

public class MultiThreadServer extends JFrame{

	private JTextArea jta = new JTextArea();
	
	private website.youdaodic youdaodic = new website.youdaodic();
	private website.bingdic bingdic = new website.bingdic();
	private website.jinshandic jinshandic = new website.jinshandic();
	
	private DataBase.DataBase database = new DataBase.DataBase();
	
	private HashMap<String, DataOutputStream> online_client = new HashMap<String, DataOutputStream>();
	private HashMap<String, String> task_list = new HashMap<String, String>();
	
	public static void main(String[] args) {
		new MultiThreadServer();
	}
	
	public MultiThreadServer() {
		setLayout(new BorderLayout());
		add(new JScrollPane(jta), BorderLayout.CENTER);
		setTitle("Server");
		setSize(500, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
		try {
			ServerSocket serverSocket = new ServerSocket(8000);
			
			int clientNo = 1;
			
			while(true) {
				Socket socket = serverSocket.accept();
				
				InetAddress inetAddress = socket.getInetAddress();
				System.out.println(inetAddress);
				
				HandleAClient task = new HandleAClient(socket);
				
				new Thread(task).start();
				
				System.out.println(clientNo);
				clientNo ++;
			}
		}
		catch(IOException ex) {
			System.err.println(ex);
		}
	}
	
	//Inner Class
	class HandleAClient implements Runnable {
		private Socket socket;
		private DataInputStream inputFromClient;
		private DataOutputStream outputToClient;// = new DataOutputStream(socket.getOutputStream());
		
		public HandleAClient(Socket socket) {
			this.socket = socket;
		}
		
		public void run() {
			try {				
				//Create data input and output streams
				inputFromClient = new DataInputStream(socket.getInputStream());
				outputToClient = new DataOutputStream(socket.getOutputStream());
				while(true) {
					//Recieve infomation from the client
					String request = inputFromClient.readUTF();
					if(request.charAt(0) == '0') {		//登陆、注册
						String[] requestArray = request.split("@", 2);
						String r = database.queryFromClient(requestArray[1]);
						String[] id_password = requestArray[1].split("@");
						if(r.equalsIgnoreCase("1@2")) {	//登陆成功，online_client添加一项
							online_client.put(id_password[1], outputToClient);
							
							StringBuilder wordinfo = new StringBuilder();
							Set<Map.Entry<String, String>> entrySet1 = task_list.entrySet();
							DataOutputStream receiver = null;
							for(Map.Entry<String, String> entry: entrySet1) {
								if(entry.getKey().equalsIgnoreCase(id_password[1])) {
									wordinfo.append("@"+entry.getValue());									
									task_list.remove(entry.getKey());
								}
							}	
							
							StringBuilder userinfo = new StringBuilder();
							userinfo.append("5@");
							Set<Map.Entry<String, DataOutputStream>> entrySet = online_client.entrySet();
							for(Map.Entry<String, DataOutputStream> entry: entrySet) {
								userinfo.append(entry.getKey() + "@");
								if(entry.getKey().equalsIgnoreCase(id_password[1])) {
									receiver = entry.getValue();	
									//break;
								}
							}
							if(receiver != null) {
								String wordinfo2 = wordinfo.toString();
								wordinfo2 = wordinfo2.replace("%20", " ");
								receiver.writeUTF("4"+wordinfo2);		
							}
							
							Set<Map.Entry<String, DataOutputStream>> entrySet3 = online_client.entrySet();
							for(Map.Entry<String, DataOutputStream> entry: entrySet3) {						
									receiver = entry.getValue();	
									if(receiver != null)
										receiver.writeUTF(userinfo.toString());
							}		
							
							//no_list.put(clientNo, id_password[1]);
						}
						outputToClient.writeUTF("0@"+r+"@"+id_password[1]);
					}
					else if(request.charAt(0) == '2') {		//登出
						String[] requestArray = request.split("@");
						online_client.remove(requestArray[1]);
						StringBuilder userinfo = new StringBuilder();
						userinfo.append("5@");
						Set<Map.Entry<String, DataOutputStream>> entrySet = online_client.entrySet();
						for(Map.Entry<String, DataOutputStream> entry: entrySet) {
							userinfo.append(entry.getKey() + "@");
						}
						DataOutputStream receiver = null;
						Set<Map.Entry<String, DataOutputStream>> entrySet3 = online_client.entrySet();
						for(Map.Entry<String, DataOutputStream> entry: entrySet3) {						
								receiver = entry.getValue();	
								if(receiver != null)
									receiver.writeUTF(userinfo.toString());
						}	
						
					}
					else if(request.charAt(0) == '1') {	//查单词
						String[] requestArray = request.split("@");
						String meaning = look_up_words(requestArray[1], requestArray[2]); 
						outputToClient.writeUTF("1@"+meaning);
					}
					else if(request.charAt(0) == '3') {		//点赞
						String[] requestArray = request.split("@");
						database.add_dianzan(requestArray[1], requestArray[2]); 
					}
					else if(request.charAt(0) == '4') {		//发送单词卡
						String[] requestArray = request.split("@");
						DataOutputStream receiver = null;
						if(requestArray[2].equalsIgnoreCase("*")) {		//群发
							String meaning = word_card_value(requestArray[1], requestArray[3]);
							Set<Map.Entry<String, DataOutputStream>> entrySet = online_client.entrySet();
							for(Map.Entry<String, DataOutputStream> entry: entrySet) {
								if(!entry.getKey().equalsIgnoreCase(requestArray[1])) {
									receiver = entry.getValue();
									if(receiver != null)
										receiver.writeUTF("4@" + meaning);
								}
							}
						}
						else {
							boolean online = is_online(requestArray[2]);
							if(online) {	//发送给在线用户
								String meaning = word_card_value(requestArray[1], requestArray[3]);
								receiver = get_stream(requestArray[2]);
								if(receiver != null) {
									meaning = meaning.replace("%20", " ");
									receiver.writeUTF("4@" + meaning);
								}
								
							}
							else {	//发送给离线用户
								//单独转发
								String meaning = word_card_value(requestArray[1], requestArray[3]);
								if(task_list.containsKey(requestArray[2])) {
									//多条消息
									Set<Map.Entry<String, String>> entrySet = task_list.entrySet();
									for(Map.Entry<String, String> entry: entrySet) {
										if(entry.getKey().equalsIgnoreCase(requestArray[2])) {
											entry.setValue(entry.getValue()+"@"+meaning);
											break;								
										}
									}
								}
								else 
									task_list.put(requestArray[2], meaning);								
							}
						}
						
					}
				}
			}
			catch(IOException ex) {
				System.err.println(ex);
			}
		}
	}
	
	//根据id判断用户是否在线
	private boolean is_online(String id) {
		boolean result = false;
		Set<Map.Entry<String, DataOutputStream>> entrySet = online_client.entrySet();
		for(Map.Entry<String, DataOutputStream> entry: entrySet) {
			if(entry.getKey().equalsIgnoreCase(id))
				return true;
		}
		return result;
	}
	
	//根据id得到outputstream
	private DataOutputStream get_stream(String id) {
		Set<Map.Entry<String, DataOutputStream>> entrySet = online_client.entrySet();
		for(Map.Entry<String, DataOutputStream> entry: entrySet) {
			if(entry.getKey().equalsIgnoreCase(id))
				return entry.getValue();
		}
		return null;
	}
	
	//准备查单词的信息
	public String look_up_words(String flag, String word) {
		StringBuilder result = new StringBuilder();
		TreeMap<String, Integer> up = new TreeMap<String, Integer>();
		up.put("jinshan", database.query_dianzan("jinshan", word));
		up.put("youdao", database.query_dianzan("youdao", word));
		up.put("bing", database.query_dianzan("bing", word));
		
		List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(up.entrySet());  
         
	    Collections.sort(list,new Comparator<Map.Entry<String,Integer>>() {  
	            //升序排序  
	        public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {  
	            return o2.getValue().compareTo(o1.getValue());  
	        }  
	    });  

		String jinshan_meaning, youdao_meaning, bing_meaning;
		jinshan_meaning = jinshandic.lookup(word);
		youdao_meaning = youdaodic.lookup(word);
		bing_meaning = bingdic.lookup(word);
		
        for (Entry<String, Integer> entry: list) {  
			String web = entry.getKey();
			if(web.equalsIgnoreCase("youdao")&&flag.charAt(2) == '1')
				result.append("有道@"+youdao_meaning+"@"+entry.getValue()+"@");
			else if(web.equalsIgnoreCase("bing")&&flag.charAt(1) == '1')
				result.append("必应@"+bing_meaning+"@"+entry.getValue()+"@");
			else if(web.equalsIgnoreCase("jinshan")&&flag.charAt(0) == '1')
				result.append("金山@"+jinshan_meaning+"@"+entry.getValue()+"@");
		}       
		
		return result.toString();
	}
	
	//准备发送的单词卡的信息
	public String word_card_value(String sender, String word) {
		StringBuilder result = new StringBuilder();
		result.append(sender + "#" + word + "#");
		TreeMap<String, Integer> up = new TreeMap<String, Integer>();
		up.put("jinshan", database.query_dianzan("jinshan", word));
		up.put("youdao", database.query_dianzan("youdao", word));
		up.put("bing", database.query_dianzan("bing", word));
		
		List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(up.entrySet());  
         
	    Collections.sort(list,new Comparator<Map.Entry<String,Integer>>() {  
	            //升序排序  
	        public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {  
	            return o2.getValue().compareTo(o1.getValue());  
	        }  
	    });  

		String jinshan_meaning, youdao_meaning, bing_meaning;
		jinshan_meaning = jinshandic.lookup(word);
		youdao_meaning = youdaodic.lookup(word);
		bing_meaning = bingdic.lookup(word);
		
        for (Entry<String, Integer> entry: list) {  
			String web = entry.getKey();
			if(web.equalsIgnoreCase("youdao"))
				result.append("有道#"+youdao_meaning+"#"+entry.getValue()+"#");
			else if(web.equalsIgnoreCase("bing"))
				result.append("必应#"+bing_meaning+"#"+entry.getValue()+"#");
			else if(web.equalsIgnoreCase("jinshan"))
				result.append("金山#"+jinshan_meaning+"#"+entry.getValue()+"#");
		}       
		
		return result.toString();
	}
}
