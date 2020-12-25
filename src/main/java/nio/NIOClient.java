package nio;

import constant.InfoConstant;
import util.InputUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @Description TODO
 * @Author Mourning 16
 * @Date 2020/12/25 17:34
 * @Version 1.0
 */

public class NIOClient {

    public static void main(String[] args) {

        SocketChannel socketChannel = null;

        boolean flag = true;

        ByteBuffer byteBuffer = null;

        try {
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress(InfoConstant.IP,InfoConstant.PORT));
            System.out.println("已经连接上服务端~~ "+socketChannel.isConnected());
            byteBuffer = ByteBuffer.allocate(50);

            while(flag){

                String inputStr = InputUtil.input("请输入要传递给服务端的内容：");

                byteBuffer.clear();
                byteBuffer.put(inputStr.getBytes());
                byteBuffer.flip();
                socketChannel.write(byteBuffer);


                byteBuffer.clear();
                int read = socketChannel.read(byteBuffer);
                byteBuffer.flip();
                String resStr = new String(byteBuffer.array(),0,read);
                System.out.println(resStr);
                if(InfoConstant.BYE_BYE.equals(inputStr.toLowerCase())){
                    flag = false;
                }

                if(!flag){
                    System.out.println("拜拜咧您~~");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(socketChannel!=null){
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
