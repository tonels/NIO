package com.dongnaoedu.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TonyNioServer {

	public static Selector selector;

	public static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 10, 10, TimeUnit.SECONDS,
			new LinkedBlockingQueue<Runnable>());

	private static ServerSocketChannel serverSocketChannel;

	private static final int port = 8080;

	public static void main(String[] args) throws Exception {

		// serversocket
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		// 开启并监听端口
		serverSocketChannel.bind(new InetSocketAddress(port));
		System.out.println("NIO启动:" + port);
		// 选择器，根据指定的条件，选择需要的东西
		// 获取一个选择器
		TonyNioServer.selector = Selector.open();

		// 在这个socket服务端通道上面，添加刚刚获取的选择器
		// 增加一个过滤条件：OP_ACCEPT 建立新的连接
		serverSocketChannel.register(TonyNioServer.selector, SelectionKey.OP_ACCEPT);

		while (true) {

			// 根据查询器已有的条件去查询(去操作系统底层管理socket连接的地方查询)
			// 查询超时的时间是1秒钟，如果1秒内没查到结果，继续执行下面的代码
			TonyNioServer.selector.select(1000);

			// 获取查询结果
			Set<SelectionKey> selected = TonyNioServer.selector.selectedKeys();
			// 遍历查询结果
			Iterator<SelectionKey> iter = selected.iterator();
			while (iter.hasNext()) {
				// 被封装的查询结果
				SelectionKey key = iter.next();

				// 判断是哪一个根据哪个条件查询出来的
				if (key.isAcceptable()) { // 如果是Acceptable，表明查询条件是OP_ACCEPT，代表有新的连接建立了
					// 处理连接
					// 在服务端通道中，取出新的socket连接
					SocketChannel chan = serverSocketChannel.accept();
					// 设置为非阻塞模式
					chan.configureBlocking(false);
					// 在新的socket连接上，增加一个查询器（复用上面已有的查询器）
					// 并且，查询条件是：OP_READ 有数据传输
					chan.register(TonyNioServer.selector, SelectionKey.OP_READ);

					System.out.println("有新的连接啦，当前线程数量:" + TonyNioServer.threadPoolExecutor.getActiveCount());
				} else if (key.isReadable()) { // 如果是Acceptable，表明这个查询结果所使用的查询条件是OP_READ，代表某个连接有数据传输过来了
					// 从查询结果中，获取到对应的有数据传输的socket连接
					SocketChannel socketChannel = (SocketChannel) key.channel();
					// 取消查询，表示这个socket连接当前正在被处理，不需要被查询到
					key.cancel();
					// 设置为非阻塞模式
					socketChannel.configureBlocking(false);
					// 交给线程池去处理
					TonyNioServer.threadPoolExecutor.execute(new NioSocketProcessor(socketChannel));
				}
			}
			// 清除查询结果
			selected.clear();
			// 清除不需要被查询的记录
			TonyNioServer.selector.selectNow();
		}

	}

}

// socket 处理
class NioSocketProcessor implements Runnable {

	SocketChannel clientSocketChannel;

	public NioSocketProcessor(SocketChannel socketChannel) {
		this.clientSocketChannel = socketChannel;
	}

	public void run() {
		// TODO 忽略...业务处理
		// 简单处理一下...
		try {
			// 申请一个缓冲区
			ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
			// 将数据读到缓冲区
			clientSocketChannel.read(byteBuffer);
			// 读完数据后，将缓冲区设置为读模式
			byteBuffer.flip();
			// 将缓冲区数据读出来并转化为字符串
			String request = new String(byteBuffer.array());
			System.out.println("收到新数据，当前线程数：" + TonyNioServer.threadPoolExecutor.getActiveCount() + "，请求内容：" + request);
			// 清除缓冲区
			byteBuffer.clear();

			// 给客户端返回一串内容
			String response = "tony" + System.currentTimeMillis();
			// 放入一个缓冲区
			ByteBuffer wrap = ByteBuffer.wrap(response.getBytes());
			// 使用通道，将数据响应给客户端
			clientSocketChannel.write(wrap);
			// 清除缓冲区
			wrap.clear();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				clientSocketChannel.configureBlocking(false);
				// 处理完一次请求
				// 继续在socket连接上，增加查询器（复用上面已有的查询器）
				// 查询条件和上面的一致是：OP_READ 有数据传输
				clientSocketChannel.register(TonyNioServer.selector, SelectionKey.OP_READ);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}