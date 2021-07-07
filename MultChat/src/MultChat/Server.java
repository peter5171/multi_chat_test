package MultChat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JLabel;


public class Server extends JFrame implements ActionListener{
    private ServerSocket serverSocket;
    private Socket socket;
    private String msg;
    /** XXX 03. ����° �߿��Ѱ�. ����ڵ��� ������ �����ϴ� ���Դϴ�. */
    private Map<String, DataOutputStream> clientsMap = new HashMap<String, DataOutputStream>();
    private static final long serialVersionUID = 1L;
    private static JLabel sst = new JLabel();
    private JTextArea jta = new JTextArea(40, 25);
    private JTextArea clist = new JTextArea(20, 25);
    private JTextField jtf = new JTextField(25);

    public Server() throws IOException  {
    	 add(sst, BorderLayout.NORTH);
    	 add(jta, BorderLayout.CENTER);
         add(jtf, BorderLayout.SOUTH);
         add(clist, BorderLayout.EAST);
         jtf.addActionListener(this);
  
         setDefaultCloseOperation(EXIT_ON_CLOSE);
         setVisible(true);
         setBounds(200, 100, 400, 600);
         setTitle("�����κ�");
   }
    public void setting() throws IOException {
        Collections.synchronizedMap(clientsMap); //����ȭ
        serverSocket = new ServerSocket(7777);
        while (true) {
            /** XXX 01. ù��°. ������ ���� �д�. ��� ���ӹ޴°�. */
        	sst.setText("���� �����...");
            socket = serverSocket.accept(); // ���� ������ ������ ��� �ݺ��ؼ� ����ڸ� �޴´�.
            sst.setText(socket.getInetAddress() + "���� �����߽��ϴ�.");
            // ���⼭ ���ο� ����� ������ Ŭ���� �����ؼ� ���������� �־���߰���?!
            Receiver receiver = new Receiver(socket);
            receiver.start();
        }
    }
 
    public static void main(String[] args) throws IOException {
        Server s = new Server();
        s.setting();
    }
 
    // ���ǳ���(Ŭ���̾�Ʈ) ����� ����
    public void addClient(String nick, DataOutputStream out) throws IOException {
        sendMessage(nick + "���� �����ϼ̽��ϴ�.\n");
        jta.append(nick + "���� �����ϼ̽��ϴ�.\n");
        clist.append(nick + "\r\n");
        clientsMap.put(nick, out);
    }
 
    public void removeClient(String nick) {
    	int size = clientsMap.size();
        sendMessage(nick + "���� �����̽��ϴ�.\n");
        jta.append(nick + "���� �����̽��ϴ�.\n");
        clientsMap.remove(nick);
        clist.setText(""); //���Ӹ�� �ؽ�Ʈ ����� ���� �ʱ�ȭ
        //�ؽ�Ʈ������ ������Ʈ
		for(int i = 0; i < size; i++) { //Ŭ���̾�Ʈ ������ �����ϴ� ���� �����ŭ 
			clist.append(nick + "\r\n"); //�ؽ�Ʈ����� �ٽ� �г����� ���
		}
    }
 
    // �޽��� ���� ����
    public void sendMessage(String msg) {
        Iterator<String> it = clientsMap.keySet().iterator();
        String key = "";
        while (it.hasNext()) {
            key = it.next();
            try {
                clientsMap.get(key).writeUTF(msg);    
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
 
    // -----------------------------------------------------------------------------
    class Receiver extends Thread {
        private DataInputStream in;
        private DataOutputStream out;
        private String nick;

        /** XXX 2. ���ù��� ������ �ڱ� ȥ�ڼ� ��Ʈ��ũ ó�� ���..���.. ó�����ִ� ��. */
        public Receiver(Socket socket) throws IOException {
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            nick = in.readUTF();
            addClient(nick, out);
        }
        
        public void run() {	
            try {// ��� ��⸸!!
                while (in != null) {
                    msg = in.readUTF();
                    sendMessage(msg);
                    appendMsg(msg);
					}
            } catch (IOException e) {
                // ������������ ���⼭ ���� �߻�. �׷������ſ���.. ���⼭ ������ Ŭ���̾�Ʈ ó�� ���ݴϴ�.
                removeClient(nick);
            }
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = "���� : " + jtf.getText() + "\n";
        System.out.print(msg);
        sendMessage(msg);
        jtf.setText("");
    }
 
    public void appendMsg(String msg) {
        jta.append(msg);
    }

}
