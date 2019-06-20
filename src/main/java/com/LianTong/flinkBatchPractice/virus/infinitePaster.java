package com.LianTong.flinkBatchPractice.virus;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: Aji
 * \* Date: 2019/6/14
 * \* Time: 9:11
 * \* Description: This file is used to generate big Files
 *                 in order to test the performance of the flink framework
 */

import java.io.*;

public class infinitePaster {
    public static void main(String[] args) {

        String srcPath = "P:\\projects\\flinkDataProcess\\src\\main\\resources\\test_original.REQ";
        String targetPath = "P:\\projects\\flinkDataProcess\\src\\main\\resources\\test_BIG.REQ";
        String virus = readToString(srcPath);
        int n = 0;
        while(true){
            writeToFile(virus,targetPath);
            System.out.println(n++);
        }
    }

    private static String readToString(String fileName) {
        String encoding = "UTF-8";
        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
            return null;
        }
    }

    private static void writeToFile(String virus, String fileName) {
        FileWriter writer;
        try {
            writer = new FileWriter(fileName,true);
            writer.write(virus);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
