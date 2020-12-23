package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @Description TODO
 * @Author Mourning 16
 * @Date 2020/12/23 10:14
 * @Version 1.0
 * 读取键盘输入的工具类
 */
public class InputUtil {

    public static final BufferedReader KEYBOARD_INPUT = new BufferedReader(new InputStreamReader(System.in));

    //确保不能被实例化，以免gc，浪费JVM开销
    private InputUtil(){}

    public static String input(String prompt){

        boolean flag = true;

        String str = null;
        try{
            while(flag) {
                System.out.println(prompt);
                str = KEYBOARD_INPUT.readLine();
                if (null == prompt || "".equals(prompt)) {
                    System.out.println("该参数不允许为空~~  ");
                } else {
                    flag = false;
                    return str;
                }
            }
        }catch (IOException e){
                System.out.println("该参数不允许为空~~  ");
        }
        return null;
    }




}
