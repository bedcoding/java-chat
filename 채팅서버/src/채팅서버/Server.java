package 채팅서버;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Server extends JFrame implements ActionListener {

	// [GUI 변수1] 별개의 프로젝트에서 윈도우 빌더로 만든 뒤 가져옴
	private JPanel contentPane;
	private JTextField port_tf;
	
	
	// [GUI 변수2] 쉽게 쓰기 위해  init() 함수에서 가져와서 전역변수로 바꿈
	JTextArea textArea = new JTextArea();
	JButton start_btn = new JButton("서버 실행");
	JButton stop_btn = new JButton("서버 중지");
	
	
	// [서버 변수1] 네트워크를 위한 자원 변수
	private ServerSocket server_socket;
	private Socket socket;
	private int port;
	
	// [서버 변수2] 클라이언트와 데이터 주고 받기
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	
	
	// 생성자
	Server()
	{
		init();   // 1. 화면 생성 메소드 바로 발동 (GUI 소스코드)
		start();  // 2. 각 버튼에 대한 액션 리스너 지정
	}

	
	// [서버 1] 서버 구축 (사용자가 접속하기를 기다리는 소켓을 생성)
	private void Server_start()
	{
		try {
			server_socket = new ServerSocket(port);  // 포트번호
		}
		
		catch (IOException e) {
			System.out.println("서버: 포트가 이미 열려 있으면, 포트를 열 수 없어서 에러가 발생하므로 try-catch 문으로 묶어준다.");
			e.printStackTrace();
		} 
		
		// 만약 서버 소켓이 정상적으로 열렸을 경우
		if(server_socket != null)
		{
			Connection();  // 사용자가 접속되게 한다.
		}
	}
	
	// [서버 2] 실질적으로 사용자가 들어오는 부분 작성 (사용자가 접속되게 하기 위한 과정)
	private void Connection()
	{
		System.out.println("서버: 쓰레드 접근");
		
		
		// 서버 변수1 (쓰레드를 안 쓰면 accept() 함수에서 사용자 접속을 대기하는 동안 프로그램은 다른 일을 못하고 죽어버린다)
		Thread th = new Thread(new Runnable() 
		{
			@Override
			public void run()
			{
				try {			
					// 클라이언트 접속전
					textArea.append("서버: 사용자 접속 대기중 \n");
					socket = server_socket.accept();  // 사용자 접속 대기 (무한 대기)
					
					// 클라이언트 접속후
					textArea.append("서버: 사용자 접속함 \n");
					
					
					// 서버 변수2 : 소켓을 연 뒤 스트림 열기 (클라이언트와 메시지 주고 받기 위해)
					try {
						is = socket.getInputStream();
						dis = new DataInputStream(is);
						
						os = socket.getOutputStream();
						dos = new DataOutputStream(os);
					}
					
					catch(IOException e) {
						System.out.println("서버: 서버쪽 스트림에서 문제 발생함");
					}
					
					
					// 클라이언트로부터 들어오는 메시지 받기
					String msg = "";
					msg = dis.readUTF();
					textArea.append(msg);
					
				} catch (IOException e) {
					System.out.println("서버: 사용자가 접속할 때 에러가 발생할 수 있기 때문에 try-catch 문으로 묶어준다.");
					e.printStackTrace();
				}  
			}
		});
		
		// 쓰레드 실행!
		th.start();
	}

	
	// [GUI 1] 화면 생성 메소드 (GUI 소스코드)
	private void init()  
	{
		// 자바 GUI (윈도우 빌더로 만든 뒤 가져옴)
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 420);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 10, 412, 269);
		contentPane.add(scrollPane);
		
		// JTextArea textArea = new JTextArea();   // 얘는 전역변수로 따로 뺌
		scrollPane.setViewportView(textArea);
		
		JLabel lblNewLabel = new JLabel("포트 번호");
		lblNewLabel.setBounds(12, 296, 57, 15);
		contentPane.add(lblNewLabel);
		
		port_tf = new JTextField();
		port_tf.setBounds(81, 293, 341, 21);
		contentPane.add(port_tf);
		port_tf.setColumns(10);
		
		//JButton btnNewButton = new JButton("서버 실행");  // 전역으로 뺌
		start_btn.setBounds(12, 342, 200, 23);
		contentPane.add(start_btn);
		
		// JButton btnNewButton_1 = new JButton("서버 중지");  // 전역으로 뺌
		stop_btn.setBounds(222, 342, 200, 23);
		contentPane.add(stop_btn);
		// ↑ 이 윗부분은 전부 윈도우 빌더로 만든 뒤 가져온 소스코드
				
		
		this.setVisible(true);  // true = 화면에 보이게, false = 화면에 보이지 않게  
	}
	
	
	// [GUI 2] 각 버튼에 대한 액션 리스너 지정
	private void start() 
	{
		// this 쓰는 이유: 이 자체 클래스에서 액션 리스너를 상속받았기 때문에
		start_btn.addActionListener(this); 
		stop_btn.addActionListener(this);
	}
	

	@Override
	// [GUI 3] 이벤트 리스너 (인터페이스인 ActionListener를 상속받으면 아래 함수를 재정의 해줘야 한다)
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == start_btn) {
			System.out.println("서버 디버깅: start 버튼 클릭");

			port = Integer.parseInt(port_tf.getText().trim());  // 포트번호
			Server_start();  // [서버 1] 함수 실행
		}
		
		else if(e.getSource() == stop_btn) {
			System.out.println("서버 디버깅: 서버 stop 버튼 클릭");
		}
	}
	
	// 이벤트를 구분하는 방법 2가지 
	// 1. 버튼의 이름으로 구별
	// 2. 바로 직접 비교하는 방법
	
	
	public static void main(String[] args) {
		new Server();  // 익명으로 객체 생성
	}
	
	/*
		이벤트 리스너 쓰는법
	
		1. 직접 상속
		2. 익명클래스로 작성
		3. 내부클래스, 외부클래스
	*/
}