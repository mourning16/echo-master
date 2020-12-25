package bio;

import constant.InfoConstant;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description TODO
 * @Author Mourning 16
 * @Date 2020/12/23 10:37
 * @Version 1.0
 */
public class BIOServer {

    public static void main(String[] args) {

        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(InfoConstant.PORT);
            System.out.println("启动一个服务端，端口号为："+ serverSocket.getLocalPort());
            ExecutorService executorService = Executors.newFixedThreadPool(InfoConstant.THREAD_NUM);

            boolean flag = true;

            while(flag){
                executorService.submit(new ClientHandler(serverSocket.accept()));
            }
            executorService.shutdown();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class ClientHandler implements Runnable{

        private Socket client;
        private Scanner scanner;
        private PrintStream printStream;
        private boolean flag = true;

        public ClientHandler(Socket client){
            this.client = client;
            try {
                this.scanner = new Scanner(client.getInputStream());
                //设置换行符
                this.scanner.useDelimiter("\n");
                this.printStream = new PrintStream(client.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run(){

            while(flag){
                if(scanner.hasNext()){
                    String val = scanner.next().trim();
                    System.out.println("线程处理客户端请求  线程Id：" + Thread.currentThread().getId());
                    System.out.println("客户端消息来了：" + val);
                    if(InfoConstant.BYE_BYE.equals(val)){
                        printStream.println("[BIOECHO] : BYEBYE 咧 ，~~");
                        flag = false;
                    }else{
                        printStream.println("服务端响应：" + val);
                    }
                }
            }
            //关闭流
            if(scanner != null){
                scanner.close();
            }

            if(printStream != null){
                printStream.close();
            }

        }
    }
}
