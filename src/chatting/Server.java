package chatting;
import java.io.*;
import java.net.*;
import java.util.*;
public class Server{
	private boolean bStart = false;
	private ServerSocket ss = null;
	List<Client> clients = new ArrayList<Client>();
	private int index = 0;
	public void tcpMonitor() {
		System.out.println("服务端已在运行！");
		try {
			ss = new ServerSocket(8885);
			bStart = true;	
		} catch (IOException e) {	
			e.printStackTrace();
		}
		
		try {
			while (bStart) {
				index++;
				Socket s = ss.accept();
				Client c = new Client(s);//每建立一个客户端就new一个客户端对象
				clients.add(c);
	            System.out.println("客户端连接成功，服务器上客户数量为：" + index);
				new Thread(c).start();	
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				ss.close();
			} catch (IOException e) {	
				e.printStackTrace();
			}
		}	
	}
	
	
	private class Client implements Runnable {
		DataInputStream dis = null;
		DataOutputStream dos = null;
		Socket s = null;
		boolean bStart = false;
		
		Client(Socket s) {
			this.s = s;
			try {
				dis = new DataInputStream(s.getInputStream());
				dos = new DataOutputStream(s.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			bStart = true;
		}
		public void sendToEveryClient(String str) {
			try {
				dos.writeUTF(str);
				dos.flush();
				//System.out.println(str);
			} catch (IOException e) {	
				clients.remove(this);	
				
			}
		}
	
		public void run() {
			try {
				while (bStart) {
					String str = dis.readUTF();
					//System.out.println(str);
					for (int i = 0; i < clients.size(); i++) {
						Client c = clients.get(i);
						c.sendToEveryClient(str);
					}
				}
			} catch (EOFException e) {
				clients.remove(this);
				index--;
				System.out.println("客户端已断开，服务器上客户数量为：" + (index-1));
			} catch (SocketException e) {
				clients.remove(this);
				index--;
				System.out.println("客户端已断开，服务器上客户数量为：" + (index-1));
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (s != null)
						s.close();
					if (dis != null)
						dis.close();
					if (dos != null)
						dos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		
	}
	public static void main(String[] args) {
		Server ts = new Server();
		ts.tcpMonitor();
	}
}
	