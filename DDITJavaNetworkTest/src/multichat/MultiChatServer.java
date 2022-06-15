package multichat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
			System.out.println("\t서버가 시작되었습니다 \\(^@^)/");
			System.out.println("\t|\\_/|");
			System.out.println("\t|q p|   /}");
			System.out.println("\t( 0 )\"\"\"\\");
			System.out.println("\t|\"^\"`    |");
			System.out.println("\t||_/=\\\\__|");

			server = new ServerSocket(7777);

			while (true) {
				// 클라이언트의 접속을 대기한다.
				socket = server.accept();
				System.out.println("접속 주소 : [" + socket.getInetAddress() + " : " + socket.getLocalPort() + "]");
				// 메세지 전송 처리를 하는 스레드 객체 생성 및 실행
				ServerReceiver receiver = new ServerReceiver(socket);
				receiver.start();
			}

		} catch (IOException e) {
			// TODO: handle exception
		} finally {
			if (server != null) {
				try {
					server.close();
				} catch (IOException e2) {
				}
			}
		}
	}
	
	/**
	 * 대화방 즉, Map에 저장된 전체 유저에게 안내메시지를 전송하는 메서드
	 * @param msg 안내 메시지
	 */
	public void sendMessage(String msg) {
		Iterator<String> it = clients.keySet().iterator();
		while (it.hasNext()) {
			try {
				String name = it.next();
				DataOutputStream dos = new DataOutputStream(clients.get(name).getOutputStream());
				
				dos.writeUTF(msg);//메세지 보내기
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 대화방 즉, Map에 저장된 전체 유저에게 대화메세지를 전송하는 메소드
	 * @param msg
	 * @param from
	 */
	public void sendMessage(String msg, String from) {
		sendMessage("[" + from + "] >> " + msg);
	}
	
	/**
	 * 대화방에서 Map에 저장된 유저 중에 특정 유저에게 귓속말을 전송하는 메소드
	 * \w 이름 내용
	 * @param msg
	 * @param from
	 * @param to
	 */
	public void sendMessage(String msg, String from, String to) {
		try {
			DataOutputStream dos = new DataOutputStream(clients.get(to).getOutputStream());//보낼사람의 소켓을 가져옴
			if(from.equals("system")) {
				dos.writeUTF("[" + from + "] " + msg);
			}else {
			dos.writeUTF("[" + from + "님의 귓속말] " + msg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 서버에서 클라이언트로 메시지를 전송할 Thread를 Inner클래스로 정의 (Inner클래스 에서는 부모클래스의 멤버들을 직접 사용할 수
	 * 있다.)
	 * 
	 * @author PC 06
	 */
	class ServerReceiver extends Thread {
		private Socket socket;
		private DataInputStream dis;
		private String name;

		public ServerReceiver(Socket socket) {
			this.socket = socket;

			try {
				dis = new DataInputStream(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			try {
				// 서버에서 클라이언트가 보내는 최초의 메세지 즉, 대화명을 수신해야한다.
				name = dis.readUTF();

				// 대화명을 받아서 다른 모든 클라이언트에세 대화방 참여 메시지를 보낸다.
				sendMessage("#" + name + "님이 입장~! \\(^@^)/");
				sendMessage("귓속말을 하려면 [/w 닉네임 메시지] 형식으로 입력하세요.");

				// 대화명과 소켓 정보를 Map에 저장한다.
				clients.put(name, socket);

				System.out.println("현재 서버 접속자 수 : " + clients.size() + "명");

				// 이후 메세지 처리는 반복문으로 처리한다.
				// 한 클라이언트가 보낸 메세지를 다른 모든 클라이언트에게 보내준다.
				while (dis != null) {
					String userMessage = dis.readUTF();
					//0이면 귓속말, -1이면 일반 대화
					if(userMessage.indexOf("/w")==0) {
						//귓속말 전용 데이터 분리
						String[] whisperMessage = userMessage.split(" ");
						//수신자 이름 데이터 서버에 출력
						String toUser = whisperMessage[1];
						for (String user : clients.keySet()) {
							if(toUser.equals(user)) {
								System.out.println("구분 : 귓속말");
								System.out.println("귓속말 수신자 : " + toUser);
								//귓속말 내용을 msg에 담아서 서버에 출력
								int length = whisperMessage.length;
								String msg = "";
								for(int i = 2 ; i < length ; i++) {
									msg += whisperMessage[i] + " ";
								}
								System.out.println("귓속말 내용 : " + msg);
								
								sendMessage(msg, name, toUser);
							}else {
								sendMessage("귓속말 대상이 잘못되었습니다.", "system", name);
							}
						}
						//귓속말 기능 사용시 서버에 출력
					}else {
						System.out.println("구분 : 일반 채팅");
						sendMessage(userMessage, name); 
					}
				}
			} catch (Exception e) {
				// e.printStackTrace();
			} finally {
				// 이 finally 영역이 실행된다는 의미는 클라이언트의 접속이 종료되었다는 의미
				sendMessage(name + "님이 퇴장... (T^T)");

				// Map에서 해당 대화명을 삭제한다.
				clients.remove(name);

				System.out.println("접속 종료 : [" + socket.getInetAddress() + " : " + socket.getLocalPort() + "]");
				System.out.println("현재 서버 접속자 수 : " + clients.size() + "명");
			}

		}
	}
	
	public static void main(String[] args) {
		new MultiChatServer().serverStart();
	}
}
