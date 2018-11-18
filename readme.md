Http11Protocol 阻塞
endpoint 端
JIoEndpoint
多个线程跑Acceptor socket.accept();
1 绑定端口
2 获取连接
3 丢到线程池



# NIO new io
Channel
Buffer
Selector


1 绑定端口
2 启动线程获取连接、启动多个poller线程
3 封装连接,丢给poller处理(selector)
4 丢到线程池


tomcat很多内容 
nio 连接器
netty nio
dubbo nio

连接池，类加载，多线程



老污龟
长沙 tomcat 大保健
200 技师 去迎客

10来个人真正给了钱






获取连接后
继续注册新的监听
只有当有数据传递的时候，才会丢到线程池里面去处理

Http11NioProtocol 非阻塞




























1、tomcat 
Bootstrap.start  启动类
Catalina.start   执行参数加载等
StandardServer.startInternal 启动容器
StandardService.startInternal  初始化相关的组件，比如 connectTor网络连接器

# 一系列的初始化操作
Connector.startInternal    
Http11NioProtocol.start    
AbstractEndpoint.start  

NioEndpoint.bind  绑定端口
NioEndpoint.startInternal  处理socket连接


2、jIo
acceptor获取连接，线程池去处理。 线程池中通过一系列转换，成为我们常用的httpRequest和httpRespose

3、nio
Acceptor接收以后交给 Poller处理 连接
poller是通过selector讲连接与读数据分开


重要区别：
开启keepalive情况下，jIo线程资源极高，nio则不受影响
连接数多，但是数据交互少的情况下，nIo可以支持海量的连接，而jIo无法支撑



tomcat各版本源码：https://github.com/apache

openjdk源码：https://github.com/dmlloyd/openjdk

jdk内置的NIO http服务器：src/jdk.httpserver/share/classes/sun/net/httpserver/ServerImpl.java