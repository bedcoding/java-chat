package 채팅클라이언트;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

// 프레임을 만드는 2가지 방법: 직접 JFrame을 상속받아서 만들 수도 있고, 객체를 발생시키는 방법도 있다.
public class Client extends JFrame implements ActionListener {		

	// [GUI 변수1] 로그인 GUI 변수 (별개의 프로젝트에서 윈도우 빌더로 만든 뒤 가져옴)
	private JFrame Login_GUI = new JFrame();  // 로그인 창은 따로 발생시킬 것이다.
	private JPanel Login_Pane;
	private JTextField ip_tf;    // ip를 받는 텍스트 필드
	private JTextField port_tf;  // port를 받는 텍스트 필드
	private JTextField id_tf;    // id를 받는 텍스트 필드
	JButton login_btn = new JButton("접속하기");  // 버튼 (필요한 자원들을 전역변수로 뺌)
	
	
	// [GUI 변수2] Main GUI 변수 (이것도 윈도우 빌더로 만든 후 가져옴) 
	private JPanel contentPane;
	private JTextField message_tf;
	private JButton notesend_btn = new JButton("쪽지보내기");   // 버튼 (필요한 자원들을 전역변수로 뺌)
	private JButton joinroom_btn = new JButton("채팅방 참여");  // 버튼 (필요한 자원들을 전역변수로 뺌)
	private JButton createroom_btn = new JButton("방 만들기"); // 버튼 (필요한 자원들을 전역변수로 뺌)
	private JButton send_btn = new JButton("전송");          // 버튼 (필요한 자원들을 전역변수로 뺌)
	
	private JList User_list = new JList();  // 전체 접속자 List
	private JList Room_list = new JList();  // 전체 방 목록 List
	JTextArea Chat_area = new JTextArea();   // 채팅을 할 때 창 부분
	
	
	// [서버 변수1] 네트워크를 위한 자원 변수
	private Socket socket;  // 클라이언트 소켓
	private String ip; // = "127.0.0.1";  // 127.0.0.1은 자기 자신
	private int port; // = 12345;
	
	// [서버 변수2] 서버와 데이터 주고 받기
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	
	// [서버 변수3] ID 받아오기
	private String id = "";
	
	
	// 생성자
	Client() 
	{
		Login_init();  // 1. 로그인 창 화면 구성
		Main_init();   // 2. Main 클라이언트 창 화면
		start(); 	   // 3. 버튼에 대한 액션 리스너 지정
	}
	
	
	
	// [서버 1] 서버 변수1 사용 (try-catch 쓰는 이유: 사용자가 접속할 때 에러가 날 수도 있으므로)
	private void Network()
	{
		try {
			socket = new Socket(ip, port);

			// 만약 소켓이 null이 아닌 경우
			if(socket != null)
			{
				Connection();
				System.out.println("클라이언트 디버깅: 정상적으로 소켓이 연결되었습니다");
			}
		} 
		
		catch (UnknownHostException e) {
			System.out.println("클라이언트 디버깅: 해당 호스트를 찾을 수 없을 때 발생하는 에러");
			e.printStackTrace();
		} 
		
		catch (IOException e) {
			System.out.println("클라이언트 디버깅: 스트림에서 발생하는 에러");
			e.printStackTrace();
		}
	}
	
	
	// [서버 2] 서버 변수2 사용 
	private void Connection()  
	{
		// 스트림을 연다.
		System.out.println("클라이언트 디버깅: 서버와 메시지 주고 받기 위해 스트림을 여는 부분");
		
		try {
			is = socket.getInputStream();
			dis = new DataInputStream(is);
			
			os = socket.getOutputStream();
			dos = new DataOutputStream(os);
			
		} catch (IOException e) {
			System.out.println("클라이언트 디버깅: Connection에서 스트림 설정할 때 에러가 발생할 수 있어서 try-catch 문으로 감쌈");
		}  // 스트림 연결 끝
		
		
		// 처음 접속시 서버에 자신의 ID를 전송한다.
		send_message(id);
		
		// 이후 쓰레드를 통해 서버로부터 계속 메시지를 받는다.
		// 쓰레드 안쓸 경우 문제점: 클라이언트가 서버로부터 메시지 수신을 무한정 대기하면서 GUI 화면이 멈춰버린다 (버튼 클릭 불가)
		Thread th = new Thread(new Runnable() {

			@Override
			public void run() {
				
				// 무한루프를 통해 메시지를 받아주도록 한다.
				while(true)
				{
					try {
						String msg = dis.readUTF();  // 서버로부터 메시지 수신
						System.out.println("서버로부터 들어온 메시지: " + msg);
					} 
					
					catch (IOException e) {
						System.out.println("클라이언트: 서버로부터 메시지 받아오는 도중 발생한 에러");
						e.printStackTrace();
					}  
				}
			}
			
		});
		
	}  
	
	
	private void send_message(String str)
	{
		System.out.println("클라이언트 디버깅: Output 스트림을 통해 서버에게 메시지를 보내는 부분");
		
		try {
			dos.writeUTF(str);  // 문자열을 받아서 dos.writeUTF로 보낸다.
		} 
		
		catch (IOException e) {
			System.out.println("클라이언트 send_message 에러");
			e.printStackTrace();
		}
		
	}
	
	
	// [GUI 1] 로그인 화면 띄우기 (윈도우 빌더에서 만든 뒤 가져와서 복붙)
	private void Login_init()
	{
		// ↓ 수정     (로그인 창은 별도로 생성시킬 것이므로 앞부분에 Login_GUI.을 붙였다)
		Login_GUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		Login_GUI.setBounds(100, 100, 270, 285);
		Login_Pane = new JPanel();
		Login_Pane.setBorder(new EmptyBorder(5, 5, 5, 5));
		Login_GUI.setContentPane(Login_Pane);
		Login_Pane.setLayout(null);
		// ↑ JFrame을 상속받았기 때문에 본래 앞에 this.가 생략되어 있었는데 Login_GUI.으로 수정
		
		JLabel lblNewLabel = new JLabel("Server IP");
		lblNewLabel.setBounds(25, 30, 57, 15);
		Login_Pane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Server Port");
		lblNewLabel_1.setBounds(25, 80, 73, 15);
		Login_Pane.add(lblNewLabel_1);
		
		JLabel lblId = new JLabel("ID");
		lblId.setBounds(25, 130, 57, 15);
		Login_Pane.add(lblId);
		
		ip_tf = new JTextField();
		ip_tf.setBounds(107, 30, 116, 21);
		Login_Pane.add(ip_tf);
		ip_tf.setColumns(10);
		
		port_tf = new JTextField();
		port_tf.setBounds(107, 80, 116, 21);
		Login_Pane.add(port_tf);
		port_tf.setColumns(10);
		
		id_tf = new JTextField();
		id_tf.setBounds(107, 130, 116, 21);
		Login_Pane.add(id_tf);
		id_tf.setColumns(10);
		
		// JButton btnNewButton = new JButton("접속하기");
		login_btn.setBounds(25, 200, 198, 23);
		Login_Pane.add(login_btn);
		// ↑ 여기까지가 윈도우 빌더로 만든 뒤 가져온 소스코드 
		
		
		Login_GUI.setVisible(true);  // true: 화면에 보이게, false: 화면에 보이지 않게
	}
	
	

	// [GUI 2] Main 클라이언트 화면
	private void Main_init()
	{
		// 자바 GUI (윈도우 빌더에서 만든 뒤 가져와서 복붙)
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // JFrame을 상속받았기 때문에 앞에 this.가 생략되어 있음
		setBounds(100, 100, 553, 460);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("접속자");
		lblNewLabel.setBounds(46, 21, 45, 15);
		contentPane.add(lblNewLabel);
		
		// JList list = new JList();  // 전역으로 뺌
		User_list.setBounds(12, 46, 105, 111);
		contentPane.add(User_list);
		
		// JButton button = new JButton("쪽지보내기"); // 전역으로 뺌
		notesend_btn.setBounds(12, 163, 105, 23);
		contentPane.add(notesend_btn);
		
		JLabel label = new JLabel("채팅방 목록");
		label.setBounds(32, 202, 85, 15);
		contentPane.add(label);
		
		// JList list_1 = new JList();  // 전역으로 뺌
		Room_list.setBounds(12, 227, 105, 120);
		contentPane.add(Room_list);
		
		// JButton btnNewButton = new JButton("채팅방 참여");  // 전역으로 뺌
		joinroom_btn.setBounds(12, 357, 105, 23);
		contentPane.add(joinroom_btn);
		
		// JButton btnNewButton_1 = new JButton("방 만들기");  // 전역으로 뺌
		createroom_btn.setBounds(12, 388, 105, 23);
		contentPane.add(createroom_btn);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(129, 46, 404, 338);
		contentPane.add(scrollPane);
		
		// JTextArea textArea = new JTextArea();  // 채팅을 할 때 창 부분 (전역으로 뺌)
		scrollPane.setViewportView(Chat_area);
		
		message_tf = new JTextField();
		message_tf.setBounds(129, 389, 295, 21);
		contentPane.add(message_tf);
		message_tf.setColumns(10);
		
		// JButton btnNewButton_2 = new JButton("전송");   // 전역으로 뺌
		send_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		send_btn.setBounds(428, 388, 105, 23);
		contentPane.add(send_btn);
		
		this.setVisible(true);  // true: 화면 보이게, false: 화면 안보이게
	}
	
	
	
	// [GUI 3] 버튼에 대한 액션 리스너 지정
	private void start()
	{
		// this 쓰는 이유: 이 자체 클래스에서 액션 리스너를 상속받았기 때문에
		login_btn.addActionListener(this);       // 로그인 버튼 리스너
		notesend_btn.addActionListener(this);    // 쪽지보내기 버튼 리스너
		joinroom_btn.addActionListener(this);    // 채팅방 참여 버튼 리스너
		createroom_btn.addActionListener(this);  // 채팅방 만들기 버튼 리스너
		send_btn.addActionListener(this);        // 채팅 전송 버튼 리스너
	}
	
	
	
	@Override
	// [GUI 4] 이벤트 리스너: 인터페이스인 ActionListener를 상속받으면 아래 함수를 재정의 해줘야 한다.
	public void actionPerformed(ActionEvent e) {

		if(e.getSource() == login_btn)
		{
			System.out.println("클라이언트 디버깅: 로그인 버튼 클릭");
			
			// 입력창 3개
			ip = ip_tf.getText().trim();
			port = Integer.parseInt(port_tf.getText().trim());
			id = id_tf.getText().trim();  // id
			
			
			// 입력 후 [서버1] 함수 실행
			Network();
		}
		
		else if(e.getSource() == notesend_btn)
		{
			System.out.println("클라이언트 디버깅: 쪽지 보내기 버튼 클릭");
		}
		
		else if(e.getSource() == joinroom_btn)
		{
			System.out.println("클라이언트 디버깅: 방 참여 버튼 클릭");
		}
		
		else if(e.getSource() == createroom_btn)
		{
			System.out.println("클라이언트 디버깅: 방 만들기 버튼 클릭");
		}
		
		else if(e.getSource() == send_btn)
		{
			System.out.println("클라이언트 디버깅: 채팅 전송 버튼 클릭");
			send_message("임시 테스트");
		}
	}
	
	
	
	public static void main(String[] args) {
		
		new Client();  // 서버 만들 때와 똑같이 익명 함수로 생성
	}
}