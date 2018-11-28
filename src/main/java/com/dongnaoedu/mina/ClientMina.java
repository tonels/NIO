package com.dongnaoedu.mina;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

//客户端程序编写
public class ClientMina {
	
	private static  String host = "127.0.0.1";
	private static  int port = 7080;
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		
		IoSession session =null;
		IoConnector connector = new NioSocketConnector();
		connector.setConnectTimeout(3000);
		
		/*设置过滤器*/
		connector.getFilterChain().addLast(
				"coderc",
					new ProtocolCodecFilter(
					new TextLineCodecFactory(
						Charset.forName("UTF-8"),
						LineDelimiter.WINDOWS.getValue(),
						LineDelimiter.WINDOWS.getValue())));
		
		connector.setHandler(new MyClientHandler());
		
		ConnectFuture future = connector.connect(new InetSocketAddress(host,port));
		//等待我们的连接
		future.awaitUninterruptibly();
		session = future.getSession();
		session.write("你好，liangshuai");
		session.getCloseFuture().awaitUninterruptibly();
		connector.dispose();
	}

}
