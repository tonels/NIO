package com.dongnaoedu.nio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TonyBioServer {

	// 一个请求过来，会新开一个线程进行处理
	// 处理请求的线程池
	public static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(25, 50, 60, TimeUnit.SECONDS,
			new LinkedBlockingQueue<Runnable>());

	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = null;
		try {
			// bind 监听
			serverSocket = new ServerSocket(8080);
			System.out.println(Thread.currentThread().getName() + " 启动 " + 8080);
			// 循环获取新的连接
			while (true) {
				// 获取连接，此处阻塞，没有新的连接就会一直停在这里
				// 阻塞的获取连接
				Socket socket = serverSocket.accept();
				// 用线程池来处理连接
				threadPoolExecutor.execute(new SocketProcessor(socket));
				System.out.println("有新的连接啦，当前线程数量:" + threadPoolExecutor.getActiveCount());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (serverSocket != null)
				serverSocket.close();
		}

	}
}

// socket 处理
class SocketProcessor implements Runnable {

	Socket socket;

	public SocketProcessor(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		// 给客户端，返回简单字符串
		try {
			// 创建一个字节数组
			byte[] requestBody = new byte[10];
			socket.setSoTimeout(1000000);
			// 阻塞 如果一直没数据，就会等在这里
			socket.getInputStream().read(requestBody);
			String request = new String(requestBody);

			System.out.println("收到数据，当前线程数：" + TonyBioServer.threadPoolExecutor.getActiveCount() + "，请求内容：" + request);
			// 给一个当前时间作为返回值
			socket.getOutputStream().write(("tony" + System.currentTimeMillis()).getBytes());
			socket.getOutputStream().flush();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 处理完一个请求后，继续丢到线程池，继续阻塞等到下一个请求数据InputStream().read()
			TonyBioServer.threadPoolExecutor.execute(new SocketProcessor(socket));
		}
	}
}
