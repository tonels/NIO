package com.dongnaoedu.mina;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class MyClientHandler extends IoHandlerAdapter {

	//重写其中两个方法
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		System.out.println("exceptionCaught……");
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		System.out.println("messageReceived……");
	}

}
