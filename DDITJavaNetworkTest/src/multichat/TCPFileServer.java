package multichat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPFileServer {
/*
	서버는 클라이언트가 접속하면 서버 컴퓨터의 D:/D_Other폴더에 있는 Tulips.jpg파일을 클라이언트로 전송
 */
	private ServerSocket server;
	private Socket socket;
	private OutputStream out;
	private FileInputStream fis;
	
	public void serverStart() {
		try {
			server= new ServerSocket(7777);
			System.out.println("서버 준비 완료");
			
			while(true) {
				System.out.println("파일 전송 대기 중");
				socket = server.accept();
				System.out.println("파일 전송 시작");
				
				fis = new FileInputStream("d:/D_Other/popcat_open.png");
				
				out = socket.getOutputStream();
				
				BufferedInputStream bis = new BufferedInputStream(fis);
				BufferedOutputStream bos = new BufferedOutputStream(out);
				
				int data = 0;
				while((data = bis.read()) != -1) {
					bos.write(data);
				}
				
				bis.close();
				bos.close();
				
				System.out.println("파일 전송 완료");
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		new TCPFileServer().serverStart();
	}
}
