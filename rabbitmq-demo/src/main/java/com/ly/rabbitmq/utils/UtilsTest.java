package com.ly.rabbitmq.utils;

import java.util.Objects;
import java.util.Scanner;

/**
 * FileName:UtilsTest.class
 * Author:ly
 * Date:2022/12/2 0002
 * Description:
 */
public class UtilsTest {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入：");
        while (scanner.hasNext()) {
            String s = scanner.next();
            System.out.println(s);
            if ("#".equals(s)) break;
        }
    }
}
