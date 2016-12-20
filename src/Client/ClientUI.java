package Client;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ClientUI {
	public static void main(String[] arg){
		try {
			org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		UI ui = new UI();
		ui.init();
		ui.Event();
	}
	
	public static class UI{
		//主窗口组件
		private Icon beforeLogin = new ImageIcon("images/beforelogin.png");
		private Icon afterLogin = new ImageIcon("images/afterlogin.png");
		private Icon beforeUp = new ImageIcon("images/beforedianzan.png");
		private Icon afterUp = new ImageIcon("images/afterdianzan.png");
		private Icon sendPicture = new ImageIcon("images/zhuanfa1.png");
		
		private JFrame frame;
		private JButton signIn = new JButton(beforeLogin);
		private JButton search = new JButton(new ImageIcon("images/search_query_32px_1063411_easyicon.net.png"));;
		private JRadioButton rBaidu = new JRadioButton("金山");
		private JRadioButton rBing = new JRadioButton("必应");
		private JRadioButton rYoudao = new JRadioButton("有道");
		private JButton upBaidu = new JButton(beforeUp);
		private JButton upBing = new JButton(beforeUp);
		private JButton upYoudao = new JButton(beforeUp);
		private JButton sendCard = new JButton(sendPicture);
		private JTextArea result = new JTextArea();
		private JTextField searchContent = new JTextField(75);
		//点赞
		private boolean isJinshan = false;
		private boolean isBing = false;
		private boolean isYoudao = false;
		private String wordTemp;
		//登陆子窗口组件
		boolean isExit = false; //isExit为true,用户为登陆状态
		private String userName;//记录本机登陆用户名
		
		private JFrame signInFrame;
		private JButton logIn = new JButton("log in");
		private JButton signUp = new JButton("sign up");
		private JLabel account = new JLabel("account");
		private JLabel password = new JLabel("password");
		private JTextField accountInput = new JTextField(30);
		private JTextField passwordInput = new JPasswordField(30);
		//数据传送相关
		private DataOutputStream toServer;
		private DataInputStream fromServer;
		private Socket client;
		private int address;
		
		//单词卡相关
		private Integer nPic = 0;//离线时获得的单词卡数目
		private JButton wordCard = new JButton(new ImageIcon("images/wordCard.png"));
		private JLabel showNum = new JLabel();
		
		private JFrame sendWordFrame;
		private JFrame wordCardFrame;
		
		private JLabel sendWord = new JLabel("word");
		private JLabel sendAccount = new JLabel("account");
		private JTextField cardInput = new JTextField(30);
		private JTextField sendInput = new JTextField(30);
		private JButton send = new JButton("发送");
		private JButton sendGroup = new JButton("群发");
		
		private JButton nextWord = new JButton(new ImageIcon("images/next.png"));
		private JButton saveWord = new JButton(new ImageIcon("images/save.png"));
		private JButton exitFrame = new JButton(new ImageIcon("images/exit.png"));
		
		private ArrayList<String> myWords = new ArrayList<String>();
		private int index = -1;
		//显示在线用户
		private JFrame usersFrame;
		private JButton checkUsers = new JButton(new ImageIcon("images/friends.png"));
		private JList userList = new JList();
		private DefaultListModel users = new DefaultListModel();
		//初始化
		public UI(){
			frame = null;
		};
		private void initNet() throws IOException{
			address = (int)(1025 + Math.random()*65535);
			client = new Socket("localhost",1025);
			fromServer = new DataInputStream(client.getInputStream());
			toServer = new DataOutputStream(client.getOutputStream());
		}
		public void init(){

			frame = new JFrame("DarkestDictionary");
			frame.setLayout(new BorderLayout(5,10));
			
			//登陆，输入框，搜索按钮
			JPanel p1 = new JPanel();
			p1.setLayout(new BorderLayout(0, 0));
			p1.add(signIn, BorderLayout.WEST);
			p1.add(searchContent, BorderLayout.CENTER);
			p1.add(search, BorderLayout.EAST);
			frame.add(p1, BorderLayout.NORTH);
			
			//选择使用的翻译以及点赞
			JPanel p2 = new JPanel();
			p2.setLayout(new BorderLayout(5,10));
			JPanel p3 = new JPanel();
			p3.setLayout(new BorderLayout(5,10));
			JPanel p4= new JPanel();
			JPanel p5 = new JPanel();
			JPanel p6 = new JPanel();
			JPanel p7 = new JPanel();
			JPanel p8 = new JPanel();
			p4.add(rBaidu);
			p5.add(rBing);
			p6.add(rYoudao);
			p4.add(upBaidu);
			p5.add(upBing);
			p6.add(upYoudao);
			p3.add(p4,BorderLayout.NORTH);
			p3.add(p5,BorderLayout.CENTER);
			p3.add(p6,BorderLayout.SOUTH);
			
			p8.setLayout(new BorderLayout(5,10));
			p7.add(sendCard);
			showNum.setText("单词卡数量: " + myWords.size());
			p8.add(showNum, BorderLayout.SOUTH);
			p8.add(wordCard,BorderLayout.CENTER);
			p8.add(checkUsers,BorderLayout.NORTH);
			
			p2.add(p3,BorderLayout.NORTH);
			p2.add(p7,BorderLayout.CENTER);
			p2.add(p8,BorderLayout.SOUTH);
			frame.add(p2,BorderLayout.WEST);
			frame.add(result,BorderLayout.CENTER);
			result.setFont(new Font("SansSerif", Font.BOLD, 16));
			frame.setSize(800,600);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
			
			result.setLineWrap(true);
			result.setWrapStyleWord(true);
		};
		
		//响应
		
		private class checkUsersListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				if(isExit == true){
					usersFrame = new JFrame("users online");
					usersFrame.setLayout(new BorderLayout());
				
					JPanel p1 = new JPanel();
					userList.setModel(users);
					p1.add(userList);
				
					usersFrame.add(userList,BorderLayout.CENTER);
				
					usersFrame.setSize(500, 500);
					usersFrame.setLocationRelativeTo(null);
					usersFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					usersFrame.setVisible(true);
				}
			}
		}
		
		private class searchListener implements ActionListener{
			public void actionPerformed(ActionEvent e) {
				String word = searchContent.getText();
				if(!word.matches("[a-zA-Z ]+")){
					JOptionPane.showMessageDialog(null, "wrong input");
					return;
				}
				word = word.replace(" ", "%20");
				String rTemp = "";
				if(rBaidu.isSelected())
					rTemp = rTemp + "1";
				else
					rTemp = rTemp + "0";
				if(rBing.isSelected())
					rTemp = rTemp + "1";
				else
					rTemp = rTemp + "0";
				if(rYoudao.isSelected())
					rTemp = rTemp + "1";
				else
					rTemp = rTemp + "0";
				
				String strSend = "1" + "@" + rTemp + "@" + word;
				
				if(word.isEmpty()){
					JOptionPane.showMessageDialog(null, "wrong input");
					//System.out.println("null");
					wordTemp = "";
				}
				else{
				
				//result.setText(strSend);
					wordTemp = word;
					try {
						toServer.writeUTF(strSend);
						toServer.flush();
						upBaidu.setIcon(beforeUp);
						upBing.setIcon(beforeUp);
						upYoudao.setIcon(beforeUp);
						isJinshan = false;
						isBing = false;
						isYoudao = false;
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
		
		private class signInListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				if(isExit == false){
					signInFrame = new JFrame("sign in");
					signInFrame.setLayout(new BorderLayout(5,10));			
					JPanel p1 = new JPanel();
					JPanel p2 = new JPanel();
					JPanel p3 = new JPanel();
					p1.add(account);
					p1.add(accountInput);
					accountInput.setText("");
					p2.add(password);
					p2.add(passwordInput);
					passwordInput.setText("");
					p3.add(logIn);
					p3.add(signUp);
					signInFrame.add(p1, BorderLayout.NORTH);
					signInFrame.add(p2, BorderLayout.CENTER);
					signInFrame.add(p3, BorderLayout.SOUTH);
					
					signInFrame.setSize(500,300);
					signInFrame.setLocationRelativeTo(null);
					signInFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					signInFrame.setVisible(true);
				}
				else{
					String strSend = "2@" + userName;
					isExit = false;
					try {
						toServer.writeUTF(strSend);
						toServer.flush();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					signIn.setIcon(beforeLogin);
				}
			}
		}
		
		private class logInListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				String strAccount = accountInput.getText();
				String strPassword = passwordInput.getText();
				String strSend = "0" + "@" + "0" + "@" + strAccount + "@" + strPassword;
				if((!strAccount.isEmpty())&&(!strPassword.isEmpty())){
					try {
						toServer.writeUTF(strSend);
						toServer.flush();
						
					} catch (IOException e1) {
					// TODO Auto-generated catch block
						e1.printStackTrace();
					}	
				}
				else{
					JOptionPane.showMessageDialog(null, "wrong input");
				}
			}
		}
		
		private class signUpListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				String strAccount = accountInput.getText();
				String strPassword = passwordInput.getText();
				String strSend = "0" + "@" + "1" + "@" + strAccount + "@" + strPassword;
				if((!strAccount.isEmpty())&&(!strPassword.isEmpty())){
					try {
						toServer.writeUTF(strSend);
						toServer.flush();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else{
					JOptionPane.showMessageDialog(null, "wrong input");
				}
			}
		}
		private class upBaiduListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				if(isExit == true&&wordTemp.isEmpty() == false){
					String strSend = "3@jinshan@" + wordTemp;
					String logInResult = "";
				
					upBaidu.setIcon(afterUp);
					try {
						toServer.writeUTF(strSend);
						toServer.flush();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
		private class upBingListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				if(isExit == true&&wordTemp.isEmpty() == false){
					String strSend = "3@bing@" + wordTemp;
					String logInResult = "";
				
					upBing.setIcon(afterUp);
					try {
						toServer.writeUTF(strSend);
						toServer.flush();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
		private class upYoudaoListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				if(isExit == true&&wordTemp.isEmpty() == false){
					String strSend = "3@youdao@" + wordTemp;
					String logInResult = "";
				
					upYoudao.setIcon(afterUp);
					try {
						toServer.writeUTF(strSend);
						toServer.flush();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
		
		private class sendCardListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				if(isExit == true){
					sendWordFrame = new JFrame("sendWord");
					sendWordFrame.setLayout(new BorderLayout(5,10));
					
					JPanel p1 = new JPanel();
					JPanel p2 = new JPanel();
					JPanel p3 = new JPanel();
					p1.add(sendWord);
					p1.add(cardInput);
					cardInput.setText("");
					p2.add(sendAccount);
					p2.add(sendInput);
					sendInput.setText("");
					p3.add(send);
					p3.add(sendGroup);
					sendWordFrame.add(p1, BorderLayout.NORTH);
					sendWordFrame.add(p2, BorderLayout.CENTER);
					sendWordFrame.add(p3, BorderLayout.SOUTH);
					
					sendWordFrame.setSize(500,300);
					sendWordFrame.setLocationRelativeTo(null);
					sendWordFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					sendWordFrame.setVisible(true);
				}
				//}
			}
		}
		
		private class exitFrameListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				if(index > -1){
					for(int i = 0;i <= index;i++){
						//System.out.println(i);
						myWords.remove(0);
					}
					showNum.setText("数量: " + myWords.size());
				}
				else{
					showNum.setText("数量: 0");
				}
				wordCardFrame.dispatchEvent(new WindowEvent(wordCardFrame,WindowEvent.WINDOW_CLOSING));
			}
		}
		
		private class wordCardListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				wordCardFrame = new JFrame("wordCard");
				wordCardFrame.setLayout(new BorderLayout(5,10));
				
				JPanel p2 = new JPanel();
				
				p2.setLayout(new BorderLayout(5,10));
				p2.add(exitFrame, BorderLayout.WEST);
				p2.add(nextWord, BorderLayout.EAST);
				p2.add(saveWord, BorderLayout.CENTER);
				
				wordCardFrame.add(new ImagePanelAnother("单词卡"),BorderLayout.CENTER);
				wordCardFrame.add(p2, BorderLayout.SOUTH);
				
				index = -1;
				
				wordCardFrame.setSize(665,820);
				wordCardFrame.setLocationRelativeTo(null);
				wordCardFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				wordCardFrame.setVisible(true);
			}
		}
		
		private class nextWordListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				index++;
				if(index < myWords.size()){
					wordCardFrame.add(new ImagePanel(myWords.get(index)),BorderLayout.CENTER);
				}
				else{
					wordCardFrame.add(new ImagePanelAnother("最后一张"),BorderLayout.CENTER);
					index--;
				}
				wordCardFrame.setVisible(true);
			}
		}
		
		private class saveWordListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				String temp1 = myWords.get(index);
				String t[] = temp1.split("#");
				String name = t[1];
				ImagePanel temp = new ImagePanel(myWords.get(index));
				
				String fileName = "test/" + name + ".png";
				File file = new File(fileName);
				try {
					System.out.println("succeed");
					ImageIO.write(temp.getImage(), "png", file);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

		private class sendListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				if(isExit == true){
					String strAccount = sendInput.getText();
					String strCard = cardInput.getText();
					strCard = strCard.replaceAll(" ", "%20");
					String strSend = "4@" + userName + "@" + strAccount + "@" + strCard;
					if((!strAccount.isEmpty())&&(!strCard.isEmpty())){
						try{
							toServer.writeUTF(strSend);
							toServer.flush();
						}
						catch (IOException e1){
							e1.printStackTrace();
						}
					}
					else{
						JOptionPane.showMessageDialog(null, "wrong input");
					}
				}
				else{
					
				}
				sendWordFrame.dispatchEvent(new WindowEvent(sendWordFrame,WindowEvent.WINDOW_CLOSING));
			}
		}
		
		private class sendGroupListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				if(isExit == true){
					//String strAccount = sendInput.getText();
					String strCard = cardInput.getText();
					strCard = strCard.replaceAll(" ", "%20");
					String strSend = "4@" + userName + "@" + "*" + "@" + strCard;
					if(!strCard.isEmpty()){
						try{
							toServer.writeUTF(strSend);
							toServer.flush();
						}
						catch (IOException e1){
							e1.printStackTrace();
						}
					}
				}
				sendWordFrame.dispatchEvent(new WindowEvent(sendWordFrame,WindowEvent.WINDOW_CLOSING));
			}
		}
		
		private void Event(){
			signIn.addActionListener(new signInListener());
			search.addActionListener(new searchListener());
			logIn.addActionListener(new logInListener());
			signUp.addActionListener(new signUpListener());
			upBaidu.addActionListener(new upBaiduListener());
			upBing.addActionListener(new upBingListener());
			upYoudao.addActionListener(new upYoudaoListener());
			wordCard.addActionListener(new wordCardListener());
			nextWord.addActionListener(new nextWordListener());
			saveWord.addActionListener(new saveWordListener());
			sendCard.addActionListener(new sendCardListener());
			send.addActionListener(new sendListener());
			sendGroup.addActionListener(new sendGroupListener());
			exitFrame.addActionListener(new exitFrameListener());
			checkUsers.addActionListener(new checkUsersListener());
			
			try {
				Socket socket = new Socket("114.212.132.159", 8000);
				
				//Create an input stream to receive data form the server
				fromServer = new DataInputStream(socket.getInputStream());
				
				toServer = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			while(true){
				try {
					String strOutput = fromServer.readUTF();
					String[] outputArray = strOutput.split("@");
					System.out.println(strOutput);
					
					
					if(outputArray[0].equalsIgnoreCase("1")){//search
						StringBuilder show = new StringBuilder();
						for(int i = 1; i < outputArray.length;) {
							show.append(outputArray[i]);
							show.append("\n");
							i++;
							show.append(outputArray[i]);
							show.append("\n");
							i++;
							show.append("点赞次数: " + outputArray[i]);
							i++;
							show.append("\n\n");
						}
						result.setText(show.toString() + '\n');
					}
					else if(outputArray[0].equalsIgnoreCase("0")){
						if(outputArray[1].equalsIgnoreCase("0")){//注册
							if(outputArray[2].equalsIgnoreCase("1")){
								signInFrame.dispatchEvent(new WindowEvent(signInFrame,WindowEvent.WINDOW_CLOSING));
								
								isExit = true;
								userName = outputArray[3];
								signIn.setIcon(afterLogin);
							}
							else{
								JOptionPane.showMessageDialog(null, "用户名已存在");
							}
						}
						else if(outputArray[1].equalsIgnoreCase("1")){//登陆
							if(outputArray[2].equalsIgnoreCase("2")){
								signInFrame.dispatchEvent(new WindowEvent(signInFrame,WindowEvent.WINDOW_CLOSING));
								
								isExit = true;
								userName = outputArray[3];
								signIn.setIcon(afterLogin);
							}
							else if(outputArray[2].equalsIgnoreCase("1")){
								JOptionPane.showMessageDialog(null, "密码错误");
							}
							else if(outputArray[2].equalsIgnoreCase("0")){
								JOptionPane.showMessageDialog(null, "用户名不存在");
							}
						}
					}
					else if(outputArray[0].equalsIgnoreCase("4")){
						System.out.println("rajsbabfabfk:" + strOutput);
						for(int m = 1;m < outputArray.length;m++){
							myWords.add(outputArray[m]);
						}
						showNum.setText("数量: " + myWords.size());
					}
					else if(outputArray[0].equalsIgnoreCase("5")){
						users.clear();
						for(int m = 1;m < outputArray.length;m++)
							users.addElement(outputArray[m]);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}

class ImagePanel extends JPanel {  
	  
    private BufferedImage imageThis;  
  
    public ImagePanel(String wordsInput) {
    	int id = (int)(1 + Math.random()*2);
    	ImageIcon imgIcon = new ImageIcon("images/background" + id + ".png");
    	Image img = imgIcon.getImage();
    
   		int width = img.getWidth(null);//400,300
   		int height = img.getHeight(null);
   		System.out.println(wordsInput);
    	
   		String[] printWord = wordsInput.split("#");
   		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
   		Graphics g2 = (Graphics)image.getGraphics();
   		g2.setColor(Color.BLACK);
   		g2.drawImage(img,0,0,null);   		
   		g2.setFont(new Font("隶书",Font.BOLD,20));
   		g2.drawString("发送用户: " + printWord[0], 5, 20);
   		g2.drawString("单词: " + printWord[1], 5, 40);
   		int nLine = 0;
   		int perLine = 30;
   		for(int i = 2;i < printWord.length;i++){
   			int k = (printWord[i].length()/perLine)+1;
   			//String[] temp = new String[k];
   			String temp;
   			for(int j = 0;j < k;j++){
   				if(j == k-1)
   					temp = printWord[i].substring(j*perLine, printWord[i].length());
   				else
   					temp = printWord[i].substring(j*perLine, (j+1)*perLine);
   				g2.drawString(temp, 5, 20*(nLine+j+3));
   			}
   			nLine += k;
    	}
   		imageThis = image;
    }  
    
    public BufferedImage getImage(){
    	return imageThis;
    }
    
    @Override  
    public void paintComponent(Graphics g) {  
        g.drawImage(imageThis, 0, 0, null);   
    }  
  
}

class ImagePanelAnother extends JPanel {  
	  
    private BufferedImage imageThis;  
  
    public ImagePanelAnother(String wordsInput) {
    	int id = (int)(1 + Math.random()*2);
    	ImageIcon imgIcon = new ImageIcon("images/background" + id + ".png");
    	Image img = imgIcon.getImage();
    
   		int width = img.getWidth(null);//400,300
   		int height = img.getHeight(null);
    	
   		String[] printWord = wordsInput.split("#");
   		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
   		Graphics g2 = (Graphics)image.getGraphics();
   		g2.setColor(Color.BLUE);
   		g2.drawImage(img,0,0,null);
   		g2.setFont(new Font("隶书",Font.BOLD,40));
   		g2.drawString(printWord[0], 220, 300);
   		for(int i = 1;i < printWord.length;i++){
   			g2.drawString(printWord[i], 5, 20*(i+5));
    	}
   		imageThis = image;
    }  
    
    public BufferedImage getImage(){
    	return imageThis;
    }
    
    @Override  
    public void paintComponent(Graphics g) {  
        g.drawImage(imageThis, 0, 0, null);   
    }  
  
}