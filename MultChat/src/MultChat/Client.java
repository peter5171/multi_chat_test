package MultChat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.awt.BorderLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.*;

public class Client extends JFrame implements ActionListener {
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	private String msg;
	private String nickName;
	private static final long serialVersionUID = 1L;
	private JPanel panel = new JPanel();
	private JPanel panel1 = new JPanel();
	private JPanel chat = new JPanel();
	private static JLabel label = new JLabel();
	private JButton login = new JButton("로그인");
	private JTextArea jta = new JTextArea(25, 40);
	private JTextField jtf = new JTextField(35);
	private JTextField id = new JTextField(25);

	public Client() {
		panel1.add(label, BorderLayout.NORTH);
		panel1.add(jta, BorderLayout.CENTER);
		panel1.add(jtf, BorderLayout.SOUTH);
		panel.add(id, BorderLayout.CENTER);
		panel.add(login, BorderLayout.EAST);
		add(panel1, BorderLayout.CENTER);
		add(panel, BorderLayout.SOUTH);
		jtf.addActionListener(this);
		id.addActionListener(this);
		login.addActionListener(this);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		setBounds(800, 100, 460, 600);
		setTitle("클라이언트");
	}

	public void connet() {
		try {
			socket = new Socket("127.0.0.1", 7777);
			System.out.println("서버 연결됨.");
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
			System.out.println("클라이언트 : 메시지 전송완료");
			while (in != null) {
				msg = in.readUTF();
				appendMsg(msg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Client cb = new Client();
		label.setText("닉네임을 입력해주세요.");
			cb.connet();
		}

	@Override
	// 로그인 버튼을 누르면 로그인
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == login) {
			try {
				nickName = id.getText();
				label.setText(nickName + "(이)가 로그인 하였습니다.");
				id.setText("");
				out.writeUTF(nickName);
			} catch (IOException ie) {
				ie.printStackTrace();
			}
		}
		
		try {		
			String msg = nickName + ":" + jtf.getText() + "\n";
			out.writeUTF(msg);
			//jta.append(msg);
			jtf.setText("");
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}
	 
	public void appendMsg(String msg) {
		jta.append(msg);
	}
}
