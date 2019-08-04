package ä��Ŭ���̾�Ʈ;

import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.Socket;

import java.awt.*;
import java.io.*;
import javax.swing.*;

public class Client extends Frame {

	// 1. GUI ������
	static JTextArea ta = new JTextArea(20, 40);
	static JScrollPane scroll = new JScrollPane(ta);

	JTextField textField_msg = new JTextField(20);
	JPanel panel1 = new JPanel();
	JPanel panel2 = new JPanel();

	Socket socket;
	Sender sender; // ���� ���� �� �޽��� ���� Ŭ����
	String msg; // �ؽ�Ʈ �ʵ忡�� �޼����� ������ ����

	
	// 2. ���� ������
	String ip = "218.39.221.69";
	int port = 2222;
	boolean connect = false; // ���� ���� �Ǵ�

	
	
	// ������
	Client() {		
		Start1_GUI();      // 1. GUI ����
		Start2_Server();   // 2. ���� ����
	}
	
	
	
	
	
	// 1. GUI	
	public void Start1_GUI() {

		setTitle("ä��");
		setLocation(200, 200);
		setSize(500, 450);

		// ���۸��غ��� ���̾�α� â���� �Է¹��� ���� �ֱ淡 �־��� ����
		String inputName = JOptionPane.showInputDialog("����� �г����� �Է��ϼ���!");
		
		// �г�1 : ä���� ���̴� ��
		panel1.add(scroll, BorderLayout.CENTER);

		// �г�2 : �Է��ϴ� ��
		panel2.add(textField_msg);

		// �����ӿ� �г� �߰�
		panel1.add(panel2, BorderLayout.SOUTH);
		add(panel1, BorderLayout.CENTER);

		// �ؽ�Ʈ �ʵ� Ű���� �̺�Ʈ
		textField_msg.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {

				// ����Ű �Է½� �޽����� ����
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					msg = textField_msg.getText();
					
					// �� ó���� �Է��ߴ� �г����� �Է��� �޽����� ���� ���ư��� �ļ��� �ηȴ�.
					sender.sendMsg(inputName + ": " + msg);
					textField_msg.setText("");
				}
			}
		});

		// ������ �����Ű��
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		setVisible(true);
	}

	
	
	
	
	// 2. ���� ����
	public void Start2_Server() {

		if (!connect) {
			try {
				socket = new Socket(ip, port);
				System.out.println("������ ����Ǿ����ϴ�.");
				ta.append("������ ����Ǿ����ϴ�.\n");

				sender = new Sender(socket);
				Thread t2 = new inThread(socket);
				t2.start();
				connect = true; // ���������� �ƴ����� �Ǻ��� ī����
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
				JOptionPane.showMessageDialog(this, "���� ����", "�˸�", JOptionPane.ERROR_MESSAGE);
			}
		}

		// �������� �ƴ϶�� ������ ����
		else if (connect) {
			try {
				socket.close();
				System.out.println("������ ������ ���������ϴ�.");
				ta.append("������ ������ ���������ϴ�.\n");
				connect = false; // ���������� �ƴ����� �Ǻ��� ī����
			}

			catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	// ����
	public static void main(String args[]) {
		new Client();
	}
}





// Ŭ���̾�Ʈ �� ����
class Sender {
	//PrintWriter writer;
	DataOutputStream out;
	Socket socket;

	// ��Ĺ ����
	Sender(Socket socket) {
		this.socket = socket;

		try {
			// OutputStream
			//writer = new PrintWriter(socket.getOutputStream());
			out = new DataOutputStream(socket.getOutputStream());
		} catch (Exception e) {
		}
	}

	// ������ �޽����� �ѱ�
	public void sendMsg(String msg) {
		if (out != null) {
			try {
				byte[] b = msg.getBytes("UTF-8");
				out.write(b);
				out.flush();
				//writer.println(msg);
				//writer.flush();
			} catch (Exception e) {
			}
		}
	}
}





// ���� �� Ŭ���̾�Ʈ
class inThread extends Thread {
	DataInputStream in;
	InputStream is;

	inThread(Socket socket) {
		try {
			is = socket.getInputStream();
			in = new DataInputStream(is);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void run() {
		try {
			System.out.println("Ŭ���̾�Ʈ: ������ ����");
			System.out.println("Ŭ���̾�Ʈ: " + System.getProperty("file.encoding"));
			byte[] by;

			String str = "";
			int count;
			
			while (true) {
				count = in.available();  // ���� �����κ��� ���� �� �ִ� ����Ʈ ���� ��´�.

				if (count > 0) {
					by = new byte[count];
					for (int i = 0; i < count; i++) {
						by[i] = in.readByte();  // �����κ��� ���� ����
					}
					
					str = new String(by, "UTF-8");
					System.out.println("[Ŭ���̾�Ʈ �� ����] " + str);
					Client.ta.append(str + "\n");
					
					// ������ �� ���� ��ũ�ѹٰ� �ڵ����� �������� ���� (���� ��ũ�ѹ� ��ġ�� ���� ������ ��)
					Client.scroll.getVerticalScrollBar().setValue(Client.scroll.getVerticalScrollBar().getMaximum());
				}
			}
			// System.out.println("������ ��");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
