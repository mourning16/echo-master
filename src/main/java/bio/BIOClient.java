package bio;

import constant.InfoConstant;
import util.InputUtil;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * @Description TODO
 * @Author Mourning 16
 * @Date 2020/12/23 11:25
 * @Version 1.0
 */
public class BIOClient {

    public static void main(String[] args) {
        Socket socket = null;
        PrintStream printStream = null;
        Scanner scanner = null;

        try {
            socket = new Socket(InfoConstant.IP,InfoConstant.PORT);

            boolean flag = true;
            while(flag){
                String val = InputUtil.input("请输入要给客户端的信息~~");

                if(null == val || "".equals(val) || InfoConstant.BYE_BYE.equals(val)){
                    flag = false;
                }else{
                    printStream = new PrintStream(socket.getOutputStream());
                    printStream.println(val);
                    scanner = new Scanner(socket.getInputStream());
                    String resVal = scanner.nextLine().trim();
                    System.out.println(resVal);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(printStream != null){
                printStream.close();
            }

            if(scanner != null){
                scanner.close();
            }

        }
    }
}
