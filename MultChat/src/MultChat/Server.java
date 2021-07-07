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
    /** XXX 03. 세번째 중요한것. 사용자들의 정보를 저장하는 맵입니다. */
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
         setTitle("서버부분");
   }
    public void setting() throws IOException {
        Collections.synchronizedMap(clientsMap); //동기화
        serverSocket = new ServerSocket(7777);
        while (true) {
            /** XXX 01. 첫번째. 서버가 할일 분담. 계속 접속받는것. */
        	sst.setText("서버 대기중...");
            socket = serverSocket.accept(); // 먼저 서버가 할일은 계속 반복해서 사용자를 받는다.
            sst.setText(socket.getInetAddress() + "에서 접속했습니다.");
            // 여기서 새로운 사용자 쓰레드 클래스 생성해서 소켓정보를 넣어줘야겠죠?!
            Receiver receiver = new Receiver(socket);
            receiver.start();
        }
    }
 
    public static void main(String[] args) throws IOException {
        Server s = new Server();
        s.setting();
    }
 
    // 맵의내용(클라이언트) 저장과 삭제
    public void addClient(String nick, DataOutputStream out) throws IOException {
        sendMessage(nick + "님이 접속하셨습니다.\n");
        jta.append(nick + "님이 접속하셨습니다.\n");
        clist.append(nick + "\r\n");
        clientsMap.put(nick, out);
    }
 
    public void removeClient(String nick) {
    	int size = clientsMap.size();
        sendMessage(nick + "님이 나가셨습니다.\n");
        jta.append(nick + "님이 나가셨습니다.\n");
        clientsMap.remove(nick);
        clist.setText(""); //접속멤버 텍스트 에리어를 전부 초기화
        //텍스트에리어 업데이트
		for(int i = 0; i < size; i++) { //클라이언트 정보를 저장하는 맵의 사이즈만큼 
			clist.append(nick + "\r\n"); //텍스트에리어에 다시 닉네임을 출력
		}
    }
 
    // 메시지 내용 전파
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

        /** XXX 2. 리시버가 한일은 자기 혼자서 네트워크 처리 계속..듣기.. 처리해주는 것. */
        public Receiver(Socket socket) throws IOException {
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            nick = in.readUTF();
            addClient(nick, out);
        }
        
        public void run() {	
            try {// 계속 듣기만!!
                while (in != null) {
                    msg = in.readUTF();
                    sendMessage(msg);
                    appendMsg(msg);
					}
            } catch (IOException e) {
                // 사용접속종료시 여기서 에러 발생. 그럼나간거에요.. 여기서 리무브 클라이언트 처리 해줍니다.
                removeClient(nick);
            }
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = "서버 : " + jtf.getText() + "\n";
        System.out.print(msg);
        sendMessage(msg);
        jtf.setText("");
    }
 
    public void appendMsg(String msg) {
        jta.append(msg);
    }

}
