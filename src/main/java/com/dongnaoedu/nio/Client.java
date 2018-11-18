package com.dongnaoedu.nio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		Socket socket = new Socket("localhost", 8080);
		System.out.println("和8080建立新连接，等待输入内容，客户端端口号：" + socket.getLocalPort());

		// 获取控制台输入的内容
		BufferedReader re = new BufferedReader(new InputStreamReader(System.in));
		String request;
		while (true) {
			request = re.readLine();
			// 发送请求
			socket.getOutputStream().write(request.getBytes());
			// 接收响应
			byte[] response = new byte[1024];
			socket.getInputStream().read(response);
			System.out.println("收到服务端的响应: " + new String(response));
		}
	}
}
