package Client;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ClientMain extends JFrame{
	private JTextField jtf = new JTextField();
	
	private JTextArea jta = new JTextArea();
	
	private DataOutputStream toServer;
	private DataInputStream fromServer;
	
	public static void main(String[] args) {
		new ClientMain();
	}
	
	public ClientMain() {
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(new JLabel("Enter radius"), BorderLayout.EAST);
		p.add(jtf, BorderLayout.CENTER);
		jtf.setHorizontalAlignment(JTextField.RIGHT);
		
		setLayout(new BorderLayout());
		add(p, BorderLayout.NORTH);
		add(new JScrollPane(jta), BorderLayout.CENTER);
		
		jtf.addActionListener(new TextFieldListener());
		
		setTitle("Client");
		setSize(500, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
		try {
			Socket socket = new Socket("localhost", 8000);
			//Socket socket = new Socket("130.254.204.36", 8000);
			//Socket socket = new Socket("drake.Armstrong.edu", 8000);
			
			//Create an input stream to receive data form the server
			fromServer = new DataInputStream(socket.getInputStream());
			
			toServer = new DataOutputStream(socket.getOutputStream());
		}
		catch(IOException ex) {
			jta.append(ex.toString() + '\n');
		}
	}
	
	private class TextFieldListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
//				double radius = Double.parseDouble(jtf.getText().trim());
				String word = jtf.getText();
				System.out.println(word);
//				toServer.writeBytes(word);
//				toServer.writeChars(word);
				toServer.writeUTF(word);
				toServer.flush();
				
				
				String area = fromServer.readUTF();
				
//				jta.append("Radius is " + radius + "\n");
				jta.append("Area received from the server is " + area + '\n');
			}
			catch(IOException ex) {
				System.err.println(ex);
			}
		}
	}

}
