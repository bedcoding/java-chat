package 채팅클라이언트;

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

	// 1. GUI 변수들
	static JTextArea ta = new JTextArea(20, 40);
	static JScrollPane scroll = new JScrollPane(ta);

	JTextField textField_msg = new JTextField(20);
	JPanel panel1 = new JPanel();
	JPanel panel2 = new JPanel();

	Socket socket;
	Sender sender; // 소켓 생성 및 메시지 전송 클래스
	String msg; // 텍스트 필드에서 메세지를 저장할 변수

	
	// 2. 서버 변수들
	String ip = "218.39.221.69";
	int port = 2222;
	boolean connect = false; // 접속 여부 판단

	
	
	// 생성자
	Client() {		
		Start1_GUI();      // 1. GUI 실행
		Start2_Server();   // 2. 서버 실행
	}
	
	
	
	
	
	// 1. GUI	
	public void Start1_GUI() {

		setTitle("채팅");
		setLocation(200, 200);
		setSize(500, 450);

		// 구글링해보니 다이얼로그 창으로 입력받을 수도 있길래 넣었다 ㅋㅋ
		String inputName = JOptionPane.showInputDialog("당신의 닉네임을 입력하세요!");
		
		// 패널1 : 채팅이 보이는 곳
		panel1.add(scroll, BorderLayout.CENTER);

		// 패널2 : 입력하는 곳
		panel2.add(textField_msg);

		// 프레임에 패널 추가
		panel1.add(panel2, BorderLayout.SOUTH);
		add(panel1, BorderLayout.CENTER);

		// 텍스트 필드 키보드 이벤트
		textField_msg.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {

				// 엔터키 입력시 메시지를 보냄
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					msg = textField_msg.getText();
					
					// 맨 처음에 입력했던 닉네임이 입력한 메시지랑 같이 날아가게 꼼수를 부렸다.
					sender.sendMsg(inputName + ": " + msg);
					textField_msg.setText("");
				}
			}
		});

		// 윈도우 종료시키기
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		setVisible(true);
	}

	
	
	
	
	// 2. 서버 접속
	public void Start2_Server() {

		if (!connect) {
			try {
				socket = new Socket(ip, port);
				System.out.println("서버에 연결되었습니다.");
				ta.append("서버에 연결되었습니다.\n");

				sender = new Sender(socket);
				Thread t2 = new inThread(socket);
				t2.start();
				connect = true; // 접속중인지 아닌지를 판별할 카운터
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
				JOptionPane.showMessageDialog(this, "접속 실패", "알림", JOptionPane.ERROR_MESSAGE);
			}
		}

		// 접속중이 아니라면 소켓을 닫음
		else if (connect) {
			try {
				socket.close();
				System.out.println("서버와 연결이 끊어졌습니다.");
				ta.append("서버와 연결이 끊어졌습니다.\n");
				connect = false; // 접속중인지 아닌지를 판별할 카운터
			}

			catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	// 메인
	public static void main(String args[]) {
		new Client();
	}
}





// 클라이언트 → 서버
class Sender {
	//PrintWriter writer;
	DataOutputStream out;
	Socket socket;

	// 소캣 생성
	Sender(Socket socket) {
		this.socket = socket;

		try {
			// OutputStream
			//writer = new PrintWriter(socket.getOutputStream());
			out = new DataOutputStream(socket.getOutputStream());
		} catch (Exception e) {
		}
	}

	// 서버로 메시지를 넘김
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





// 서버 → 클라이언트
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
			System.out.println("클라이언트: 쓰레드 시작");
			System.out.println("클라이언트: " + System.getProperty("file.encoding"));
			byte[] by;

			String str = "";
			int count;
			
			while (true) {
				count = in.available();  // 현재 서버로부터 읽을 수 있는 바이트 수를 얻는다.

				if (count > 0) {
					by = new byte[count];
					for (int i = 0; i < count; i++) {
						by[i] = in.readByte();  // 서버로부터 받은 내용
					}
					
					str = new String(by, "UTF-8");
					System.out.println("[클라이언트 ← 서버] " + str);
					Client.ta.append(str + "\n");
					
					// 쓰레드 돌 때는 스크롤바가 자동으로 내려가지 않음 (수평 스크롤바 위치를 따로 잡아줘야 함)
					Client.scroll.getVerticalScrollBar().setValue(Client.scroll.getVerticalScrollBar().getMaximum());
				}
			}
			// System.out.println("쓰레드 끝");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
