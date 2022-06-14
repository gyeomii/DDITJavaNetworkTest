package TCP;

import java.io.IOException;
import java.net.Socket;

public class TcpChatClient {
	public static void main(String[] args) {
		Socket socket = null;
		try {
			socket = new Socket("192.168.141.33", 7777);
			
			System.out.println("서버에 연결 되었습니다.\\(^@^)/");
			
			Sender sender = new Sender(socket);
			Receiver receiver = new Receiver(socket);
			
			sender.start();
			receiver.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}