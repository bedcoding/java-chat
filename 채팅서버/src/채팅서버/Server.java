package ä�ü���;

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

	// [GUI ����1] ������ ������Ʈ���� ������ ������ ���� �� ������
	private JPanel contentPane;
	private JTextField port_tf;
	
	
	// [GUI ����2] ���� ���� ����  init() �Լ����� �����ͼ� ���������� �ٲ�
	JTextArea textArea = new JTextArea();
	JButton start_btn = new JButton("���� ����");
	JButton stop_btn = new JButton("���� ����");
	
	
	// [���� ����1] ��Ʈ��ũ�� ���� �ڿ� ����
	private ServerSocket server_socket;
	private Socket socket;
	private int port;
	
	// [���� ����2] Ŭ���̾�Ʈ�� ������ �ְ� �ޱ�
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	
	
	// ������
	Server()
	{
		init();   // 1. ȭ�� ���� �޼ҵ� �ٷ� �ߵ� (GUI �ҽ��ڵ�)
		start();  // 2. �� ��ư�� ���� �׼� ������ ����
	}

	
	// [���� 1] ���� ���� (����ڰ� �����ϱ⸦ ��ٸ��� ������ ����)
	private void Server_start()
	{
		try {
			server_socket = new ServerSocket(port);  // ��Ʈ��ȣ
		}
		
		catch (IOException e) {
			System.out.println("����: ��Ʈ�� �̹� ���� ������, ��Ʈ�� �� �� ��� ������ �߻��ϹǷ� try-catch ������ �����ش�.");
			e.printStackTrace();
		} 
		
		// ���� ���� ������ ���������� ������ ���
		if(server_socket != null)
		{
			Connection();  // ����ڰ� ���ӵǰ� �Ѵ�.
		}
	}
	
	// [���� 2] ���������� ����ڰ� ������ �κ� �ۼ� (����ڰ� ���ӵǰ� �ϱ� ���� ����)
	private void Connection()
	{
		System.out.println("����: ������ ����");
		
		
		// ���� ����1 (�����带 �� ���� accept() �Լ����� ����� ������ ����ϴ� ���� ���α׷��� �ٸ� ���� ���ϰ� �׾������)
		Thread th = new Thread(new Runnable() 
		{
			@Override
			public void run()
			{
				try {			
					// Ŭ���̾�Ʈ ������
					textArea.append("����: ����� ���� ����� \n");
					socket = server_socket.accept();  // ����� ���� ��� (���� ���)
					
					// Ŭ���̾�Ʈ ������
					textArea.append("����: ����� ������ \n");
					
					
					// ���� ����2 : ������ �� �� ��Ʈ�� ���� (Ŭ���̾�Ʈ�� �޽��� �ְ� �ޱ� ����)
					try {
						is = socket.getInputStream();
						dis = new DataInputStream(is);
						
						os = socket.getOutputStream();
						dos = new DataOutputStream(os);
					}
					
					catch(IOException e) {
						System.out.println("����: ������ ��Ʈ������ ���� �߻���");
					}
					
					
					// Ŭ���̾�Ʈ�κ��� ������ �޽��� �ޱ�
					String msg = "";
					msg = dis.readUTF();
					textArea.append(msg);
					
				} catch (IOException e) {
					System.out.println("����: ����ڰ� ������ �� ������ �߻��� �� �ֱ� ������ try-catch ������ �����ش�.");
					e.printStackTrace();
				}  
			}
		});
		
		// ������ ����!
		th.start();
	}

	
	// [GUI 1] ȭ�� ���� �޼ҵ� (GUI �ҽ��ڵ�)
	private void init()  
	{
		// �ڹ� GUI (������ ������ ���� �� ������)
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 420);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 10, 412, 269);
		contentPane.add(scrollPane);
		
		// JTextArea textArea = new JTextArea();   // ��� ���������� ���� ��
		scrollPane.setViewportView(textArea);
		
		JLabel lblNewLabel = new JLabel("��Ʈ ��ȣ");
		lblNewLabel.setBounds(12, 296, 57, 15);
		contentPane.add(lblNewLabel);
		
		port_tf = new JTextField();
		port_tf.setBounds(81, 293, 341, 21);
		contentPane.add(port_tf);
		port_tf.setColumns(10);
		
		//JButton btnNewButton = new JButton("���� ����");  // �������� ��
		start_btn.setBounds(12, 342, 200, 23);
		contentPane.add(start_btn);
		
		// JButton btnNewButton_1 = new JButton("���� ����");  // �������� ��
		stop_btn.setBounds(222, 342, 200, 23);
		contentPane.add(stop_btn);
		// �� �� ���κ��� ���� ������ ������ ���� �� ������ �ҽ��ڵ�
				
		
		this.setVisible(true);  // true = ȭ�鿡 ���̰�, false = ȭ�鿡 ������ �ʰ�  
	}
	
	
	// [GUI 2] �� ��ư�� ���� �׼� ������ ����
	private void start() 
	{
		// this ���� ����: �� ��ü Ŭ�������� �׼� �����ʸ� ��ӹ޾ұ� ������
		start_btn.addActionListener(this); 
		stop_btn.addActionListener(this);
	}
	

	@Override
	// [GUI 3] �̺�Ʈ ������ (�������̽��� ActionListener�� ��ӹ����� �Ʒ� �Լ��� ������ ����� �Ѵ�)
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == start_btn) {
			System.out.println("���� �����: start ��ư Ŭ��");

			port = Integer.parseInt(port_tf.getText().trim());  // ��Ʈ��ȣ
			Server_start();  // [���� 1] �Լ� ����
		}
		
		else if(e.getSource() == stop_btn) {
			System.out.println("���� �����: ���� stop ��ư Ŭ��");
		}
	}
	
	// �̺�Ʈ�� �����ϴ� ��� 2���� 
	// 1. ��ư�� �̸����� ����
	// 2. �ٷ� ���� ���ϴ� ���
	
	
	public static void main(String[] args) {
		new Server();  // �͸����� ��ü ����
	}
	
	/*
		�̺�Ʈ ������ ���¹�
	
		1. ���� ���
		2. �͸�Ŭ������ �ۼ�
		3. ����Ŭ����, �ܺ�Ŭ����
	*/
}