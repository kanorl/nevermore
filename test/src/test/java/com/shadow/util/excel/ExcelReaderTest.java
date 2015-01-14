package com.shadow.util.excel;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * @author nevermore on 2015/1/14.
 */
public class ExcelReaderTest {

    public static void main(String[] args) {
        URL url = ExcelReaderTest.class.getClassLoader().getResource("resource");
        if (url == null) {
            throw new NullPointerException();
        }
        File dir = new File(url.getFile());
        File[] files = dir.listFiles();
        if (files == null) {
            throw new IllegalStateException();
        }
        for (File file : files) {
            List<List<List<String>>> result = ExcelReader.toList(file);
            for (List<List<String>> lists : result) {
                for (List<String> list : lists) {
                    System.out.println(list);
                }
            }
        }
    }
}
