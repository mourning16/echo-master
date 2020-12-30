package aio;

import constant.InfoConstant;

import javax.sound.sampled.Line;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * @Description TODO
 * @Author Mourning 16
 * @Date 2020/12/30 9:48
 * @Version 1.0
 */
//实现处理客户端请求报文的回调
class Echohandler implements CompletionHandler<Integer,ByteBuffer>{

    //和客户端的通信通道
    private AsynchronousSocketChannel asynchronousSocketChannel;

    //是否退出
    private boolean exit = false;

    public Echohandler(AsynchronousSocketChannel asynchronousSocketChannel){
        this.asynchronousSocketChannel = asynchronousSocketChannel;
    }

    @Override
    public void completed(Integer integer,ByteBuffer byteBuffer){
        byteBuffer.flip();
        String readMessage = new String(byteBuffer.array(),0,byteBuffer.remaining()).trim() ;
        String writeMessage = "【ECHO】" + readMessage + "\n";  // 回应的数据信息
        if (InfoConstant.BYE_BYE.equalsIgnoreCase(readMessage)) {
            writeMessage = "【EXIT】拜拜，下次再见！" + "\n";
            this.exit = true ; // 结束后期的交互
        }
        echoWrite(writeMessage);
    }

    private void echoWrite(String content) {

        ByteBuffer buffer = ByteBuffer.allocate(100);
        buffer.put(content.getBytes()) ;//向缓存中保存数据
        buffer.flip();
        this.asynchronousSocketChannel.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer buf) {
                if (buf.hasRemaining()) {   // 缓存中是否有数据
                    Echohandler.this.asynchronousSocketChannel.write(buf,buf,this);
                } else {
                    if(Echohandler.this.exit == false) {    // 还没有结束
                        ByteBuffer readBuffer = ByteBuffer.allocate(100);
                        Echohandler.this.asynchronousSocketChannel.read(readBuffer,readBuffer,new Echohandler(Echohandler.this.asynchronousSocketChannel)) ;
                    }
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                try {
                    Echohandler.this.asynchronousSocketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void failed(Throwable ex,ByteBuffer byteBuffer){
        System.out.println("响应客户端失败~~");
        try {
            this.asynchronousSocketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

//实现客户端连接的回调
class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel,AIOServerThread>{

    @Override
    public void  completed(AsynchronousSocketChannel asynchronousSocketChannel , AIOServerThread aioServerThread){
        ByteBuffer byteBuffer = ByteBuffer.allocate(200);
        asynchronousSocketChannel.read(byteBuffer,byteBuffer,new Echohandler(asynchronousSocketChannel));
    }

    @Override
    public void failed(Throwable ex,AIOServerThread aioServerThread){
        System.out.println("客户端连接失败~~");
        aioServerThread.getLatch().countDown();
    }
}

class AIOServerThread implements Runnable{

    private AsynchronousServerSocketChannel asynchronousServerSocketChannel;//服务器socket通道

    private CountDownLatch latch;//做一个同步处理

    public AIOServerThread() throws Exception{
        //打开服务器通道
        this.asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();

        this.latch = new CountDownLatch(1);//等待线程数量为1
        this.asynchronousServerSocketChannel.bind(new InetSocketAddress(InfoConstant.PORT));
        System.out.println("成功启动一个AIO模型的服务端~~，端口号为：" + InfoConstant.PORT);
    }

    @Override
    public void run(){
        asynchronousServerSocketChannel.accept(this,new AcceptHandler());
        try {
            this.latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public AsynchronousServerSocketChannel getServerChannel() {
        return asynchronousServerSocketChannel;
    }

    public CountDownLatch getLatch() {
        return latch;
    }

}

//启动AIO模型服务器
public class AIOServer {

    public static void main(String args[]) throws Exception{
        new Thread(new AIOServerThread()).start();
    }

}


