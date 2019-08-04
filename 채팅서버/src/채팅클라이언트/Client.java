package ä��Ŭ���̾�Ʈ;

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

// �������� ����� 2���� ���: ���� JFrame�� ��ӹ޾Ƽ� ���� ���� �ְ�, ��ü�� �߻���Ű�� ����� �ִ�.
public class Client extends JFrame implements ActionListener {		

	// [GUI ����1] �α��� GUI ���� (������ ������Ʈ���� ������ ������ ���� �� ������)
	private JFrame Login_GUI = new JFrame();  // �α��� â�� ���� �߻���ų ���̴�.
	private JPanel Login_Pane;
	private JTextField ip_tf;    // ip�� �޴� �ؽ�Ʈ �ʵ�
	private JTextField port_tf;  // port�� �޴� �ؽ�Ʈ �ʵ�
	private JTextField id_tf;    // id�� �޴� �ؽ�Ʈ �ʵ�
	JButton login_btn = new JButton("�����ϱ�");  // ��ư (�ʿ��� �ڿ����� ���������� ��)
	
	
	// [GUI ����2] Main GUI ���� (�̰͵� ������ ������ ���� �� ������) 
	private JPanel contentPane;
	private JTextField message_tf;
	private JButton notesend_btn = new JButton("����������");   // ��ư (�ʿ��� �ڿ����� ���������� ��)
	private JButton joinroom_btn = new JButton("ä�ù� ����");  // ��ư (�ʿ��� �ڿ����� ���������� ��)
	private JButton createroom_btn = new JButton("�� �����"); // ��ư (�ʿ��� �ڿ����� ���������� ��)
	private JButton send_btn = new JButton("����");          // ��ư (�ʿ��� �ڿ����� ���������� ��)
	
	private JList User_list = new JList();  // ��ü ������ List
	private JList Room_list = new JList();  // ��ü �� ��� List
	JTextArea Chat_area = new JTextArea();   // ä���� �� �� â �κ�
	
	
	// [���� ����1] ��Ʈ��ũ�� ���� �ڿ� ����
	private Socket socket;  // Ŭ���̾�Ʈ ����
	private String ip; // = "127.0.0.1";  // 127.0.0.1�� �ڱ� �ڽ�
	private int port; // = 12345;
	
	// [���� ����2] ������ ������ �ְ� �ޱ�
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	
	// [���� ����3] ID �޾ƿ���
	private String id = "";
	
	
	// ������
	Client() 
	{
		Login_init();  // 1. �α��� â ȭ�� ����
		Main_init();   // 2. Main Ŭ���̾�Ʈ â ȭ��
		start(); 	   // 3. ��ư�� ���� �׼� ������ ����
	}
	
	
	
	// [���� 1] ���� ����1 ��� (try-catch ���� ����: ����ڰ� ������ �� ������ �� ���� �����Ƿ�)
	private void Network()
	{
		try {
			socket = new Socket(ip, port);

			// ���� ������ null�� �ƴ� ���
			if(socket != null)
			{
				Connection();
				System.out.println("Ŭ���̾�Ʈ �����: ���������� ������ ����Ǿ����ϴ�");
			}
		} 
		
		catch (UnknownHostException e) {
			System.out.println("Ŭ���̾�Ʈ �����: �ش� ȣ��Ʈ�� ã�� �� ���� �� �߻��ϴ� ����");
			e.printStackTrace();
		} 
		
		catch (IOException e) {
			System.out.println("Ŭ���̾�Ʈ �����: ��Ʈ������ �߻��ϴ� ����");
			e.printStackTrace();
		}
	}
	
	
	// [���� 2] ���� ����2 ��� 
	private void Connection()  
	{
		// ��Ʈ���� ����.
		System.out.println("Ŭ���̾�Ʈ �����: ������ �޽��� �ְ� �ޱ� ���� ��Ʈ���� ���� �κ�");
		
		try {
			is = socket.getInputStream();
			dis = new DataInputStream(is);
			
			os = socket.getOutputStream();
			dos = new DataOutputStream(os);
			
		} catch (IOException e) {
			System.out.println("Ŭ���̾�Ʈ �����: Connection���� ��Ʈ�� ������ �� ������ �߻��� �� �־ try-catch ������ ����");
		}  // ��Ʈ�� ���� ��
		
		
		// ó�� ���ӽ� ������ �ڽ��� ID�� �����Ѵ�.
		send_message(id);
		
		// ���� �����带 ���� �����κ��� ��� �޽����� �޴´�.
		// ������ �Ⱦ� ��� ������: Ŭ���̾�Ʈ�� �����κ��� �޽��� ������ ������ ����ϸ鼭 GUI ȭ���� ��������� (��ư Ŭ�� �Ұ�)
		Thread th = new Thread(new Runnable() {

			@Override
			public void run() {
				
				// ���ѷ����� ���� �޽����� �޾��ֵ��� �Ѵ�.
				while(true)
				{
					try {
						String msg = dis.readUTF();  // �����κ��� �޽��� ����
						System.out.println("�����κ��� ���� �޽���: " + msg);
					} 
					
					catch (IOException e) {
						System.out.println("Ŭ���̾�Ʈ: �����κ��� �޽��� �޾ƿ��� ���� �߻��� ����");
						e.printStackTrace();
					}  
				}
			}
			
		});
		
	}  
	
	
	private void send_message(String str)
	{
		System.out.println("Ŭ���̾�Ʈ �����: Output ��Ʈ���� ���� �������� �޽����� ������ �κ�");
		
		try {
			dos.writeUTF(str);  // ���ڿ��� �޾Ƽ� dos.writeUTF�� ������.
		} 
		
		catch (IOException e) {
			System.out.println("Ŭ���̾�Ʈ send_message ����");
			e.printStackTrace();
		}
		
	}
	
	
	// [GUI 1] �α��� ȭ�� ���� (������ �������� ���� �� �����ͼ� ����)
	private void Login_init()
	{
		// �� ����     (�α��� â�� ������ ������ų ���̹Ƿ� �պκп� Login_GUI.�� �ٿ���)
		Login_GUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		Login_GUI.setBounds(100, 100, 270, 285);
		Login_Pane = new JPanel();
		Login_Pane.setBorder(new EmptyBorder(5, 5, 5, 5));
		Login_GUI.setContentPane(Login_Pane);
		Login_Pane.setLayout(null);
		// �� JFrame�� ��ӹ޾ұ� ������ ���� �տ� this.�� �����Ǿ� �־��µ� Login_GUI.���� ����
		
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
		
		// JButton btnNewButton = new JButton("�����ϱ�");
		login_btn.setBounds(25, 200, 198, 23);
		Login_Pane.add(login_btn);
		// �� ��������� ������ ������ ���� �� ������ �ҽ��ڵ� 
		
		
		Login_GUI.setVisible(true);  // true: ȭ�鿡 ���̰�, false: ȭ�鿡 ������ �ʰ�
	}
	
	

	// [GUI 2] Main Ŭ���̾�Ʈ ȭ��
	private void Main_init()
	{
		// �ڹ� GUI (������ �������� ���� �� �����ͼ� ����)
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // JFrame�� ��ӹ޾ұ� ������ �տ� this.�� �����Ǿ� ����
		setBounds(100, 100, 553, 460);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("������");
		lblNewLabel.setBounds(46, 21, 45, 15);
		contentPane.add(lblNewLabel);
		
		// JList list = new JList();  // �������� ��
		User_list.setBounds(12, 46, 105, 111);
		contentPane.add(User_list);
		
		// JButton button = new JButton("����������"); // �������� ��
		notesend_btn.setBounds(12, 163, 105, 23);
		contentPane.add(notesend_btn);
		
		JLabel label = new JLabel("ä�ù� ���");
		label.setBounds(32, 202, 85, 15);
		contentPane.add(label);
		
		// JList list_1 = new JList();  // �������� ��
		Room_list.setBounds(12, 227, 105, 120);
		contentPane.add(Room_list);
		
		// JButton btnNewButton = new JButton("ä�ù� ����");  // �������� ��
		joinroom_btn.setBounds(12, 357, 105, 23);
		contentPane.add(joinroom_btn);
		
		// JButton btnNewButton_1 = new JButton("�� �����");  // �������� ��
		createroom_btn.setBounds(12, 388, 105, 23);
		contentPane.add(createroom_btn);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(129, 46, 404, 338);
		contentPane.add(scrollPane);
		
		// JTextArea textArea = new JTextArea();  // ä���� �� �� â �κ� (�������� ��)
		scrollPane.setViewportView(Chat_area);
		
		message_tf = new JTextField();
		message_tf.setBounds(129, 389, 295, 21);
		contentPane.add(message_tf);
		message_tf.setColumns(10);
		
		// JButton btnNewButton_2 = new JButton("����");   // �������� ��
		send_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		send_btn.setBounds(428, 388, 105, 23);
		contentPane.add(send_btn);
		
		this.setVisible(true);  // true: ȭ�� ���̰�, false: ȭ�� �Ⱥ��̰�
	}
	
	
	
	// [GUI 3] ��ư�� ���� �׼� ������ ����
	private void start()
	{
		// this ���� ����: �� ��ü Ŭ�������� �׼� �����ʸ� ��ӹ޾ұ� ������
		login_btn.addActionListener(this);       // �α��� ��ư ������
		notesend_btn.addActionListener(this);    // ���������� ��ư ������
		joinroom_btn.addActionListener(this);    // ä�ù� ���� ��ư ������
		createroom_btn.addActionListener(this);  // ä�ù� ����� ��ư ������
		send_btn.addActionListener(this);        // ä�� ���� ��ư ������
	}
	
	
	
	@Override
	// [GUI 4] �̺�Ʈ ������: �������̽��� ActionListener�� ��ӹ����� �Ʒ� �Լ��� ������ ����� �Ѵ�.
	public void actionPerformed(ActionEvent e) {

		if(e.getSource() == login_btn)
		{
			System.out.println("Ŭ���̾�Ʈ �����: �α��� ��ư Ŭ��");
			
			// �Է�â 3��
			ip = ip_tf.getText().trim();
			port = Integer.parseInt(port_tf.getText().trim());
			id = id_tf.getText().trim();  // id
			
			
			// �Է� �� [����1] �Լ� ����
			Network();
		}
		
		else if(e.getSource() == notesend_btn)
		{
			System.out.println("Ŭ���̾�Ʈ �����: ���� ������ ��ư Ŭ��");
		}
		
		else if(e.getSource() == joinroom_btn)
		{
			System.out.println("Ŭ���̾�Ʈ �����: �� ���� ��ư Ŭ��");
		}
		
		else if(e.getSource() == createroom_btn)
		{
			System.out.println("Ŭ���̾�Ʈ �����: �� ����� ��ư Ŭ��");
		}
		
		else if(e.getSource() == send_btn)
		{
			System.out.println("Ŭ���̾�Ʈ �����: ä�� ���� ��ư Ŭ��");
			send_message("�ӽ� �׽�Ʈ");
		}
	}
	
	
	
	public static void main(String[] args) {
		
		new Client();  // ���� ���� ���� �Ȱ��� �͸� �Լ��� ����
	}
}