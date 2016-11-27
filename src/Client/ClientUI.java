package Client;
import javax.swing.*;
import java.awt.*;

public class ClientUI {
	public static void main(String[] arg){
		UI ui = new UI();
		ui.init();
	}
	
	public static class UI{
		private JFrame frame;
		
		public UI(){
			frame = null;
		};
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
			p1.add(new JButton("sign in"));
			JTextField j1 = new JTextField(75);
			j1.setSize(75, 10);
			p1.add(j1);
			p1.add(new JButton("search"));
			frame.add(p1, BorderLayout.NORTH);
			
			//选择使用的翻译以及点赞
			JPanel p2 = new JPanel();
			//p2.setLayout(new FlowLayout(FlowLayout.CENTER,5,5));
			p2.setLayout(new BorderLayout(5,10));
			//p2.setLayout(null);
			JPanel p3 = new JPanel();
			JPanel p4= new JPanel();
			p3.add(new JRadioButton("Baidu"));
			//p3.add(new JRadioButton("Bing"));
			p3.add(new JRadioButton("Bing"));
			//JPanel p4= new JPanel();
			//p4.setLayout(new FlowLayout(FlowLayout.LEFT,5,5));
			//p4.add(new JRadioButton("Youdao"));
			p3.add(new JRadioButton("Youdao"));
			//frame.add(p3,BorderLayout.CENTER);
			//frame.add(p4,BorderLayout.WEST);
			//JPanel p3 = new JPanel();
			//p3.setLayout(new FlowLayout(FlowLayout.LEFT,5,5));
			p4.add(new JButton("Baidu"));
			p4.add(new JButton("Bing"));
			p4.add(new JButton("Youdao"));
			p2.add(p3,BorderLayout.NORTH);
			p2.add(p4,BorderLayout.WEST);
			//p3.setPreferredSize(new Dimension(10,10));
			//p2.setPreferredSize(new Dimension(10,10));
			frame.add(p2,BorderLayout.WEST);
			//frame.add(p3, BorderLayout.EAST);
			
			//JPanel p4 = new JPanel();
			//p4.setLayout(new FlowLayout(FlowLayout.LEFT,5,5));
			//p4.add(new JTextArea());
			frame.add(new JTextArea(),BorderLayout.CENTER);
			
			frame.setSize(1024,960);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		};
	}
}
