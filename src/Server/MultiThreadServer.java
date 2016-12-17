package Server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
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
//			ServerSocket serverSocket = new ServerSocket(8080);
			
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
				Set<Map.Entry<String, DataOutputStream>> entrySet1 = online_client.entrySet();
				for(Map.Entry<String, DataOutputStream> entry: entrySet1) {
					System.out.println("sdfasdfasdf");
					entry.getValue().writeUTF("1@hello thread");
				}
//				outputToClient.writeUTF(request+"@"+r);
				while(true) {
					//Recieve radius from the client
					String request = inputFromClient.readUTF();
					System.out.println("request  " + request);
					if(request.charAt(0) == '0') {		//登陆、注册
						String[] requestArray = request.split("@", 2);
						System.out.println("requestArray" + requestArray[1]);
						String r = database.queryFromClient(requestArray[1]);
						System.out.println("r value "+r);
						String[] id_password = requestArray[1].split("@");
						if(r.equalsIgnoreCase("1@2")) {	//登陆成功，online_client添加一项
//							String[] id_password = requestArray[1].split("@");
							System.out.println("in here");
							online_client.put(id_password[1], outputToClient);
						}
						Set<Map.Entry<String, DataOutputStream>> entrySet = online_client.entrySet();
						for(Map.Entry<String, DataOutputStream> entry: entrySet)
							System.out.println(entry.getKey() + "\t" + entry.getValue());
						System.out.println("0@"+r+"@"+id_password[1]);
						outputToClient.writeUTF("0@"+r+"@"+id_password[1]);
					}
					else if(request.charAt(0) == '2') {		//登出
						String[] requestArray = request.split("@");
						System.out.println("requestArray[1] " + requestArray[1]);
						online_client.remove(requestArray[1]);
					}
					else if(request.charAt(0) == '1') {	//查单词
						String[] requestArray = request.split("@");
						String meaning = look_up_words(requestArray[1], requestArray[2]); 
						System.out.println("1@"+meaning);
						outputToClient.writeUTF("1@"+meaning);
					}
					else if(request.charAt(0) == '3') {		//点赞
						String[] requestArray = request.split("@");
						database.add_dianzan(requestArray[1], requestArray[2]); 
					}
					else if(request.charAt(0) == '4') {
						String[] requestArray = request.split("@");
						boolean online = is_online(requestArray[2]);
						if(online) {	//发送给在线用户
							
							
						}
						else {	//发送给离线用户
							task_list.put(requestArray[2], requestArray[1]+requestArray[3]);
						}
						//HashMap<String, HashMap<String, String>> task_list
						
					}
				}
			}
			catch(IOException ex) {
				System.err.println(ex);
			}
		}
	}
	
	private boolean is_online(String id) {
		boolean result = false;
		Set<Map.Entry<String, DataOutputStream>> entrySet = online_client.entrySet();
		for(Map.Entry<String, DataOutputStream> entry: entrySet) {
			if(entry.getKey().equalsIgnoreCase(id))
				return true;
		}
		return result;
	}
	
	public String look_up_words(String flag, String word) {
		StringBuilder result = new StringBuilder();
//		ArrayList<Integer> up = new ArrayList<Integer>();
		TreeMap<String, Integer> up = new TreeMap<String, Integer>();
		up.put("jinshan", database.query_dianzan("jinshan", word));
		up.put("youdao", database.query_dianzan("youdao", word));
		up.put("bing", database.query_dianzan("bing", word));
		TreeMap<String, Integer> after_up = new TreeMap<String, Integer>(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				// TODO Auto-generated method stub
				return o1.compareTo(o2);
			}
			
		});
/*		List<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(up.entrySet());
		Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {   
		    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {      
		        //return (o2.getValue() - o1.getValue()); 
		        return (o1.getKey()).toString().compareTo(o2.getKey());
		    }
		}); */

//		int up_jinshan = database.query_dianzan("jinshan", word);
//		int up_youdao = database.query_dianzan("youdao", word);
//		int up_bing = database.query_dianzan("bing", word);
		String jinshan_meaning, youdao_meaning, bing_meaning;
		jinshan_meaning = jinshandic.lookup(word);
		youdao_meaning = youdaodic.lookup(word);
		bing_meaning = bingdic.lookup(word);
		Set<Map.Entry<String, Integer>> entrySet = up.entrySet();
		for(Map.Entry<String, Integer> entry: entrySet) {
			String web = entry.getKey();
			if(web.equalsIgnoreCase("youdao")&&flag.charAt(2) == '1')
				result.append("有道@"+youdao_meaning+"@"+entry.getValue()+"@");
			else if(web.equalsIgnoreCase("bing")&&flag.charAt(1) == '1')
				result.append("必应@"+bing_meaning+"@"+entry.getValue()+"@");
			else if(web.equalsIgnoreCase("jinshan")&&flag.charAt(0) == '1')
				result.append("金山@"+jinshan_meaning+"@"+entry.getValue()+"@");
		}
/*		if(flag.charAt(0) == '1') {		//jinshandic
			jinshan_meaning = jinshandic.lookup(word);
//			result.append(temp);
//			result.append("@");
		}
		if(flag.charAt(1) == '1') {		//youdaodic
			youdao_meaning = youdaodic.lookup(word);
//			result.append(temp);
//			result.append("@");
		}
		if(flag.charAt(2) == '1') {		//bingdic
			bing_meaning = bingdic.lookup(word);
//			result.append(temp);
		}*/
		
		return result.toString();
	}
	
}
