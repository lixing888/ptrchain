package com.ptrchain.common.utils.excel;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ExcelUtilTest {
    
    public void readDutyDefExcel() {
//        Map<String, List<Object>> objects = ExcelUtil.readDutyDefExcel("");
//        System.out.println(objects.toString());
    
    }
    
    public void readExcel(){
        List<Object> objects = ExcelUtil.readExcel("/Users/lengxiangjun/Downloads/险种责任定义模板.xlsx", null);
        System.out.println(objects);
    }
}