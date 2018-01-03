package com.qiucong.service;

import com.qiucong.util.Gregorian2LunarCalendarMappingConverter;
import com.qiucong.util.Pdf2TextParser;
import org.junit.Test;

import java.io.InputStream;
import java.util.Arrays;


public class Pdf2TextParserTest {


    @Test
    public void test() throws Exception {
        InputStream pdfFile = this.getClass().getClassLoader().getResourceAsStream("1901e.pdf");
        System.out.println(Arrays.toString(new Gregorian2LunarCalendarMappingConverter().getAllLines(Pdf2TextParser.parse(pdfFile)).toArray()));
    }
}