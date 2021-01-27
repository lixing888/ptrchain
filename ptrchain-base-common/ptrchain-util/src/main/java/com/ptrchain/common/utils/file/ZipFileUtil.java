package com.ptrchain.common.utils.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * ZipFile 工具类
 *
 * @author haijun.sun
 * @email haijunsun2@creditease.cn
 * @date 2019-02-26 23:30
 */
@Slf4j
public class ZipFileUtil {

    /*    *//**
     * 示例
     *//*
    public static void main(String[] args) {
        File file1 = new File("/Users/haijunsun/temp.jpg");
        File file2 = new File("/Users/haijunsun/temp2.jpg");
        File file3 = new File("/Users/haijunsun/pp.pdf");
        try {
            createZip("/Users/haijunsun/tt.zip",file1,file2,file3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /**
     * 压缩多个文件。
     *
     * @param zipFileName 压缩输出文件名
     * @param files       需要压缩的文件
     * @return
     * @throws Exception
     */
    public static File createZip(String zipFileName, File... files) throws Exception {
        File outFile = new File(zipFileName);
        ZipOutputStream out = null;
        BufferedOutputStream bo = null;
        try {
            out = new ZipOutputStream(new FileOutputStream(outFile));
            bo = new BufferedOutputStream(out);

            for (File file : files) {
                zip(out, file, file.getName(), bo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bo.close();
            } finally {
                out.close(); // 输出流关闭
            }
        }
        return outFile;
    }

    /**
     * @param zipFileName 压缩输出文件名
     * @param inputFile   需要压缩的文件
     * @return
     * @throws Exception
     */
    public static File createZip(String zipFileName, File inputFile) throws Exception {
        File outFile = new File(zipFileName);
        ZipOutputStream out = null;
        BufferedOutputStream bo = null;
        try {
            out = new ZipOutputStream(new FileOutputStream(outFile));
            bo = new BufferedOutputStream(out);
            zip(out, inputFile, inputFile.getName(), bo);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bo.close();
            } finally {
                out.close(); // 输出流关闭
            }
        }
        return outFile;
    }

    private static void zip(ZipOutputStream out, File f, String base, BufferedOutputStream bo) throws Exception { // 方法重载
        if (f.isDirectory()) {
            File[] fl = f.listFiles();
            if (fl == null || fl.length == 0) {
                out.putNextEntry(new ZipEntry(base + "/")); // 创建创建一个空的文件夹
            } else {
                for (int i = 0; i < fl.length; i++) {
                    zip(out, fl[i], base + "/" + fl[i].getName(), bo); // 递归遍历子文件夹
                }
            }

        } else {
            out.putNextEntry(new ZipEntry(base)); // 创建zip压缩进入 base 文件
            System.out.println(base);
            BufferedInputStream bi = new BufferedInputStream(new FileInputStream(f));

            try {
                write2Out(bi, out);
            } catch (IOException e) {
                //Ignore
            } finally {
                bi.close();// 输入流关闭
            }
        }
    }

    private static void write2Out(InputStream input, OutputStream out) throws IOException {
        byte[] b = new byte[1024];
        int c = 0;
        while ((c = input.read(b)) != -1) {
            out.write(b, 0, c);
            out.flush();
        }
        out.flush();
    }


    /**
     * ZipInputStream是逐个目录进行读取，所以只需要循环
     *
     * @param outPath
     * @param inputStream
     * @throws IOException
     */
    private static void decompressionFile(String outPath, ZipInputStream inputStream) throws IOException {
        //读取一个目录
        ZipEntry nextEntry = inputStream.getNextEntry();
        //不为空进入循环
        while (nextEntry != null) {
            String name = nextEntry.getName();
            File file = new File(outPath + name);
            //如果是目录，创建目录
            if (name.endsWith("/")) {
                file.mkdir();
            } else {
                //文件则写入具体的路径中
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                int n;
                byte[] bytes = new byte[1024];
                while ((n = inputStream.read(bytes)) != -1) {
                    bufferedOutputStream.write(bytes, 0, n);
                }
                //关闭流
                bufferedOutputStream.close();
                fileOutputStream.close();
            }
            //关闭当前布姆
            inputStream.closeEntry();
            //读取下一个目录，作为循环条件
            nextEntry = inputStream.getNextEntry();
        }
    }


    /**
     * 解压压缩文件流，并根据解压层次来判断，是否只解析第一层，还是解析所有数据
     * map<name,inputstream>
     *
     * @param in
     * @return
     */
    public static Map<String, Object> unZip(InputStream in) throws IOException {
        if (in == null) {
            return null;
        }
        ZipEntry zipEntry;
        Map<String, Object> map = new HashMap<>();
        ZipInputStream zipIn = new ZipInputStream(in, Charset.forName("GBK"));
        try {
            while ((zipEntry = zipIn.getNextEntry()) != null) {
                //如果是文件夹路径方式，本方法内暂时不提供操作
                if (zipEntry.isDirectory()) {

                } else {
                    //如果是文件，则直接存放在Map中
                    String name = zipEntry.getName();
                    //把压缩文件内的流转化为字节数组，够外部逻辑使用(之后关闭流)
                    byte[] bt = IOUtils.toByteArray(zipIn);
                    map.put(name.substring(name.lastIndexOf(File.separator) + 1), bt);
                    zipIn.closeEntry();
                }
            }
            return map;
        } catch (Exception ex) {
            log.error("in unZip(InputStream in) has an error,e is " + ExceptionUtils.getStackTrace(ex));
            return null;
        } finally {
            IOUtils.closeQuietly(zipIn);
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * 解压压缩文件流，并根据解压层次来判断，是否只解析第一层，还是解析所有数据
     * map<name,inputstream>
     *
     * @param in
     * @return
     */
    public static Map<String, Object> unZipWithFullPath(InputStream in) throws IOException {
        if (in == null) {
            return null;
        }
        ZipEntry zipEntry;
        Map<String, Object> map = new HashMap<>();
        ZipInputStream zipIn = new ZipInputStream(in, Charset.forName("UTF-8"));
        try {
            while ((zipEntry = zipIn.getNextEntry()) != null) {
                //如果是文件夹路径方式，本方法内暂时不提供操作
                if (zipEntry.isDirectory()) {

                } else {
                    //如果是文件，则直接存放在Map中
                    String name = zipEntry.getName();
                    //把压缩文件内的流转化为字节数组，够外部逻辑使用(之后关闭流)
                    byte[] bt = IOUtils.toByteArray(zipIn);
                    map.put(name,bt);
                    zipIn.closeEntry();
                }
            }
            return map;
        } catch (Exception ex) {
            log.error("in unZip(InputStream in) has an error,e is " + ExceptionUtils.getStackTrace(ex));
            return null;
        } finally {
            IOUtils.closeQuietly(zipIn);
            IOUtils.closeQuietly(in);
        }
    }

    public static void main(String[] args) throws IOException {
        FileInputStream fileInputStream = new FileInputStream("/Users/gelinghu/Downloads/批量上传条款.zip");
        Map<String, Object> stringObjectMap = unZip(fileInputStream);
        System.out.println(stringObjectMap.toString());
    }
}
