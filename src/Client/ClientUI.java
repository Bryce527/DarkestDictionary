package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ClientUI {
	public static void main(String[] arg){
		UI ui = new UI();
		ui.init();
		ui.Event();
	}
	
	public static class UI{
		//主窗口组件
		private JFrame frame;
		private JButton signIn = new JButton("sign in");
		private JButton search = new JButton("search");
		private JRadioButton rBaidu = new JRadioButton("Baidu");
		private JRadioButton rBing = new JRadioButton("Bing");
		private JRadioButton rYoudao = new JRadioButton("Youdao");
		private JButton upBaidu = new JButton("Baidu");
		private JButton upBing = new JButton("Bing");
		private JButton upYoudao = new JButton("Youdao");
		private JTextArea result = new JTextArea();
		private JTextField searchContent = new JTextField(75);
		//登陆子窗口组件
		private JFrame signInFrame;
		private JButton logIn = new JButton("log in");
		private JButton signUp = new JButton("sign up");
		private JLabel account = new JLabel("account");
		private JLabel password = new JLabel("password");
		private JTextField accountInput = new JTextField(30);
		private JTextField passwordInput = new JTextField(30);
		//数据传送相关
		private DataOutputStream toServer;
		private DataInputStream fromServer;
		private Socket client;
		private int address;
		
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
			//frame.setLayout(null);
			//frame.add(new JButton("east"), BorderLayout.EAST);
			//frame.add(new JButton("east"), BorderLayout.SOUTH);
			//frame.add(new JButton("east"), BorderLayout.WEST);
			//frame.add(new JButton("east"), BorderLayout.NORTH);
			//frame.add(new JButton("east"), BorderLayout.CENTER);
			
			//登陆，输入框，搜索按钮
			JPanel p1 = new JPanel();
			p1.setLayout(new FlowLayout(FlowLayout.LEFT,5,5));
			p1.add(signIn);
			p1.add(searchContent);
			p1.add(search);
			frame.add(p1, BorderLayout.NORTH);
			
			//选择使用的翻译以及点赞
			JPanel p2 = new JPanel();
			//p2.setLayout(new FlowLayout(FlowLayout.CENTER,5,5));
			p2.setLayout(new BorderLayout(5,10));
			//p2.setLayout(null);
			JPanel p3 = new JPanel();
			JPanel p4= new JPanel();
			p3.add(rBaidu);
			//p3.add(new JRadioButton("Bing"));
			p3.add(rBing);
			//JPanel p4= new JPanel();
			//p4.setLayout(new FlowLayout(FlowLayout.LEFT,5,5));
			//p4.add(new JRadioButton("Youdao"));
			p3.add(rYoudao);
			//frame.add(p3,BorderLayout.CENTER);
			//frame.add(p4,BorderLayout.WEST);
			//JPanel p3 = new JPanel();
			//p3.setLayout(new FlowLayout(FlowLayout.LEFT,5,5));
			p4.add(upBaidu);
			p4.add(upBing);
			p4.add(upYoudao);
			p2.add(p3,BorderLayout.NORTH);
			p2.add(p4,BorderLayout.WEST);
			//p3.setPreferredSize(new Dimension(10,10));
			//p2.setPreferredSize(new Dimension(10,10));
			frame.add(p2,BorderLayout.WEST);
			//frame.add(p3, BorderLayout.EAST);
			
			//JPanel p4 = new JPanel();
			//p4.setLayout(new FlowLayout(FlowLayout.LEFT,5,5));
			//p4.add(new JTextArea());
			frame.add(result,BorderLayout.CENTER);
			
			frame.setSize(1024,960);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
			
			try {
				//initNet();
				Socket socket = new Socket("localhost", 8000);
				//Socket socket = new Socket("130.254.204.36", 8000);
				//Socket socket = new Socket("drake.Armstrong.edu", 8000);
				
				//Create an input stream to receive data form the server
				fromServer = new DataInputStream(socket.getInputStream());
				
				toServer = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
		
		//响应
		private class searchListener implements ActionListener{
			public void actionPerformed(ActionEvent e) {
				String word = searchContent.getText();
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
				
				String strSend = "@" + "2" + "@" + rTemp + "@" + word;
				
				//result.setText(strSend);
				try {
					toServer.writeUTF(word);
					toServer.flush();
					String strOutput = fromServer.readUTF();
					result.setText(strOutput + '\n');
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		private class signInListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				signInFrame = new JFrame("sign in");
				signInFrame.setLayout(new BorderLayout(5,10));
				
				JPanel p1 = new JPanel();
				JPanel p2 = new JPanel();
				JPanel p3 = new JPanel();
				p1.add(account);
				p1.add(accountInput);
				p2.add(password);
				p2.add(passwordInput);
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
		}
		private class logInListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				String strAccount = accountInput.getText();
				String strPassword = passwordInput.getText();
				String strSend = "@" + "0" + "@" + strAccount + "@" + strPassword;
				String logInResult = "";
				/*try {
					toServer.writeUTF(strSend);
					toServer.flush();
					logInResult = fromServer.readUTF();
					//result.setText("Area received from the server is " + strOutput + '\n');
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}*/
				//logInResult.compareTo("@suceed") == 0
				if(false){
					signInFrame.dispatchEvent(new WindowEvent(signInFrame,WindowEvent.WINDOW_CLOSING));
				}
				else{
					JFrame msgWindow = new JFrame();
					
					msgWindow.setSize(300,200);
					msgWindow.setLocationRelativeTo(null);
					msgWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					msgWindow.setVisible(true);
				}
			}
		}
		private class signUpListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				String strAccount = accountInput.getText();
				String strPassword = passwordInput.getText();
				String strSend = "@" + "1" + "@" + strAccount + "@" + strPassword;
				String signUpResult = "";
				
				/*try {
					toServer.writeUTF(strSend);
					toServer.flush();
					signUpResult = fromServer.readUTF();
					//result.setText("Area received from the server is " + strOutput + '\n');
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}*/
				
				if(false){
					signInFrame.dispatchEvent(new WindowEvent(signInFrame,WindowEvent.WINDOW_CLOSING));
				}
				else{
					JFrame msgWindow = new JFrame();
					
					msgWindow.setSize(300,200);
					msgWindow.setLocationRelativeTo(null);
					msgWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					msgWindow.setVisible(true);
				}
			}
		}
		private class upBaiduListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				String strSend = "@3@Baidu";
				String logInResult = "";
				
				try {
					toServer.writeUTF(strSend);
					toServer.flush();
					logInResult = fromServer.readUTF();
					//result.setText("Area received from the server is " + strOutput + '\n');
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		private class upBingListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				String strSend = "@3@Bing";
				String logInResult = "";
				
				try {
					toServer.writeUTF(strSend);
					toServer.flush();
					logInResult = fromServer.readUTF();
					//result.setText("Area received from the server is " + strOutput + '\n');
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		private class upYoudaoListener implements ActionListener{
			public void actionPerformed(ActionEvent e){
				String strSend = "@3@Youdao";
				String logInResult = "";
				
				try {
					toServer.writeUTF(strSend);
					toServer.flush();
					logInResult = fromServer.readUTF();
					//result.setText("Area received from the server is " + strOutput + '\n');
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
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
		}
	}
}