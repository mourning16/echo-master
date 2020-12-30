package nio;

import constant.InfoConstant;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description TODO
 * @Author Mourning 16
 * @Date 2020/12/25 10:25
 * @Version 1.0
 * NIO服务端
 */

public class NIOServer {


    public static void main(String args[]){

        //采用线程池管理处理请求的线程
        ExecutorService executorService = Executors.newFixedThreadPool(InfoConstant.THREAD_NUM);

        ServerSocketChannel serverSocketChannel = null;
        try {
            //打开一个channel
            serverSocketChannel = ServerSocketChannel.open();
            //使用非阻塞模式
            serverSocketChannel.configureBlocking(false);
            //绑定一个端口号
            ServerSocketChannel bind = serverSocketChannel.bind(new InetSocketAddress(InfoConstant.PORT));

            //一个selector管理所有的serversocketchannel
            Selector selector = Selector.open();
            //连接时处理
            serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT);
            System.out.println("启动一个NIO模型的客户端，端口号：" + InfoConstant.PORT);

            //Nio采用轮询模式，每来一个请求，启用一个线程处理请求
            int selectKeySize = 0;
            //接收轮询状态
            while((selectKeySize = selector.select())>0){

                //获取全部的key
                Set<SelectionKey> selectionKeys = selector.keys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while(iterator.hasNext()){
                    SelectionKey selectionKey  = iterator.next();
                    if(selectionKey.isAcceptable()) {
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        if (socketChannel != null) {
                            executorService.submit(new NioServerClient(socketChannel));
                        }
                    }
                }
            }

            executorService.shutdown();
            serverSocketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //处理客户端的请求
    public static class  NioServerClient implements Runnable{

        private SocketChannel socketChannel;

        //循环处理的标记
        private boolean flag = true;

        //开辟50个字节的缓冲区
        private ByteBuffer byteBuffer = ByteBuffer.allocate(50);

        public NioServerClient(SocketChannel socketChannel){
            this.socketChannel = socketChannel;
        }

        @Override
        public void run(){

            try {
                while(this.flag){
                    byteBuffer.clear();
                    int read = socketChannel.read(byteBuffer);

                    String readMessage = "";

                    if(read >= 0){
                        readMessage = new String(byteBuffer.array(),0,read).trim();
                    }

                    String writeMessage = "";
                    writeMessage = "【NIOECHO】：" + readMessage + "\n";
                    //清空缓冲区
                    byteBuffer.clear();
                    //放数据
                    byteBuffer.put(writeMessage.getBytes());
                    //重置缓冲区
                    byteBuffer.flip();
                    socketChannel.write(byteBuffer);
                }
                socketChannel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}
