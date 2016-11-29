package chatting;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
//用户端
public class Client extends JFrame{
	Socket s = null;
	DataOutputStream dos = null;
	DataInputStream dis = null;
	boolean bConnected = false;
	Thread t = new Thread(new RecToServer());
	
    TextArea taContent = new TextArea();
	JTextArea tfTxt = new JTextArea(10,5);
	JScrollPane sp=new JScrollPane(tfTxt);
	JButton send = new JButton("发送");
	JButton connect = new JButton("连接");
	JButton clear = new JButton("清空");
	JPanel p2 = new JPanel();
public void launchFrame() {
		p2.add(send);
		p2.add(connect);
		p2.add(clear);
	    	
		Container con = this.getContentPane();
		con.add(taContent, "North");
		con.add(sp,"Center");
		con.add(p2, "South");
		
		
		this.setSize(300, 400);
		this.setLocation(400, 400);
		this.setTitle("聊天");
		tfTxt.setLineWrap(true);
		taContent.setEditable(false);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		connect.addActionListener(new Connect());
		send.addActionListener(new SendMsg());
		clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				taContent.setText("");
			}
		});
		}
public static void main(String[] args) {
	Client tc = new Client();
		tc.launchFrame();
}

public void connectToServer() {
	try {	
		s = new Socket("localhost", 8885);
		dos = new DataOutputStream(s.getOutputStream());
		dis = new DataInputStream(s.getInputStream());
		bConnected = true;	
	} catch (BindException e) {
		System.out.println("没有连接到服务器");
	} catch (UnknownHostException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	
}

public void disConnect() {
	try {
		if (s != null) {
			s.close();
		}	
		if (dos != null) {
			dos.close();
		}
		if (dis != null) {
			dis.close();
		}
		System.exit(0);
	} catch (IOException e) {
		e.printStackTrace();
	}
}

private class Connect implements ActionListener {
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == "连接") {
			//System.out.println("连接成功");
			connectToServer();
			JOptionPane.showMessageDialog(Client.this,
					"连接到服务器", "成功提示", 1);
			try {
				t.start();
			} catch (IllegalThreadStateException ex) {
			
			}
			connect.setText("退出");} 
		else if (e.getActionCommand() == "退出"){
			disConnect();
			/*//System.out.println("断开服务器");
			JOptionPane.showMessageDialog(Client.this,
					"断开服务器", "断开提示", 1);*/
		}	
	}
}
private class SendMsg implements ActionListener {
	public void actionPerformed(ActionEvent e) {
		if (connect.getActionCommand() == "连接") {
			JOptionPane.showMessageDialog(Client.this,
					"没有连接到服务器", "错误提示", 1);
		} else {
			String str = tfTxt.getText();
			tfTxt.setText("");	
			try {
				dos.writeUTF(str);
				dos.flush();//清空缓冲区
			} catch (SocketException ex) {
				JOptionPane.showMessageDialog(Client.this,
						"没有连接到服务器", "错误提示", 1);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}}}
private class RecToServer implements Runnable {
	public void run() {
		try {
			while (bConnected) {
				String str = dis.readUTF();
				taContent.append(str + "\n");
			}
		} catch (SocketException e) {
	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

}
		
	
	

