package com.dongnaoedu.mina;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class MinaServer {
	
	static int PORT = 7080;
	static IoAcceptor accept = null;
	
	public static void main(String[] args) {
		
		try {
			accept = new NioSocketAcceptor();
			//设置编码过滤器
			accept.getFilterChain().addLast("codec",new ProtocolCodecFilter(
					new TextLineCodecFactory(
							Charset.forName("UTF-8"),
							LineDelimiter.WINDOWS.getValue(),
							LineDelimiter.WINDOWS.getValue())));
			
			accept.getSessionConfig().setReadBufferSize(1024);
			
			//10秒钟无连接进入空闲状态
			accept.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE,10);
			accept.setHandler(new MyHandler());
			accept.bind(new InetSocketAddress(PORT));
			System.out.println("sever -> " + PORT);
		} catch (IOException e) {
			e.printStackTrace();//有异常打印到堆栈里面
		}
	}
}
