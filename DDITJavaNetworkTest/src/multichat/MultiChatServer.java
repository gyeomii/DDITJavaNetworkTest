package multichat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.omg.CORBA.ServerRequest;

public class MultiChatServer {
	// 대화명, 클라이언트의 Socket을 저장하기 위한 Map변수 선언
	Map<String, Socket> clients;

	public MultiChatServer() {
		// 동기화 처리가 가능하도록 Map객체 생성하기
		clients = Collections.synchronizedMap(new HashMap<String, Socket>());
	}
	
	// 서버 시작 메소드
	public void serverStart() {
		ServerSocket server = null;
		Socket socket = null;
		
		try {
			System.out.println("서버가 시작되었습니다\\(^@^)/");
			
			while(true) {
			//클라이언트의 접속을 대기한다.
			socket = server.accept();
			System.out.println("접속 주소 : [" + socket.getInetAddress() + " : " + socket.getLocalPort() + "]");
			//메세지 전송 처리를 하는 스레드 객체 생성 및 실행
			ServerReceiver receiver = new ServerReceiver(socket);
			receiver.start();
			}
			
		} catch (IOException e) {
			// TODO: handle exception
		}finally {
			if(server != null) {
				try {
					server.close();
				} catch (IOException e2) {
				}
			}
		}
	}

}
