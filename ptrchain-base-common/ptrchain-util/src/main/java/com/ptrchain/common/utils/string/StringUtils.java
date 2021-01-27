package com.ptrchain.common.utils.string;

import com.google.common.base.Strings;
import com.ptrchain.common.utils.reflect.ReflectUtils;
import com.ptrchain.common.utils.string.enums.DataFormat;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * @author arron
 * @version 1.0
 * @date 2015年9月14日 上午10:29:15
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    /**
     * 判断是否是空字符串
     *
     * @param str 字符串
     * @return
     */
    public static boolean isNullOrEmpty(String str) {
        return Strings.isNullOrEmpty(str);
    }

    /**
     * 输出字符串，如果为空，则输出null
     *
     * @param str 字符串
     * @return
     */
    public static String emptyToNull(String str) {
        return Strings.emptyToNull(str);
    }

    /**
     * 输出字符串，如果为null，则输出""
     *
     * @param str 字符串
     * @return
     */
    public static String nullToEmpty(String str) {
        return Strings.nullToEmpty(str);
    }

    public static String list2String(List<String> list,String separation){
        if(list==null||list.isEmpty()){
            return "";
        }
        StringBuffer str = new StringBuffer();
        for (String item:list) {
            str.append(item+separation);
        }
        return str.substring(0,str.length()-1);
    }

    public static List<String> sort(List<String> list){
        if(list==null||list.isEmpty()){
            return list;
        }
        for(String value:list){
            if(!StringUtils.isNum(value)){
                Collections.sort(list);
                return list;
            }
        }
        List<BigDecimal> nums = new ArrayList<>();
        for (String num :list) {
            nums.add(new BigDecimal(num));
        }
        Collections.sort(nums);
        list = new ArrayList<>();
        for (BigDecimal num :nums) {
            list.add(num.toPlainString());
        }
        return list;
    }

    /**
     * 对象字段如果为空，则转化为null
     *
     * @param obj 对象
     * @return
     */
    public static <T> T emptyToNull(T obj) {
        Field[] fields = ReflectUtils.getFields(obj.getClass());
        for (Field field : fields) {
            field.setAccessible(true);
            if (java.util.List.class.isAssignableFrom(field.getType())) {
                try {
                    Collection<?> c = (Collection<?>) field.get(obj);
                    for (Object object : c) {
                        emptyToNull(object);
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    if (field.get(obj).equals("")) {
                        field.set(obj, null);
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return obj;
    }

    /**
     * 替换字符串的内容
     *
     * @param src 需要处理的字符串
     * @param map 需要替换的内容
     * @return 返回替换后的
     */
    public static String replaceStr(String src, LinkedHashMap<String, String> map) {
        String result = src;
        Set<Map.Entry<String, String>> entry = map.entrySet();
        for (Map.Entry<String, String> obj : entry) {
            String k = obj.getKey();
            String v = obj.getValue();
            if (v == null) {
                v = "";
            }
            result = result.replace(k, v);
        }
        return result;
    }

    /**
     * 对象字段如果为null，则转化为空
     *
     * @param obj 对象
     * @return
     */
    public static <T> T nullToEmpty(T obj) {
        Field[] fields = ReflectUtils.getFields(obj.getClass());
        for (Field field : fields) {
            field.setAccessible(true);
            if (java.util.List.class.isAssignableFrom(field.getType())) {
                try {
                    Collection<?> c = (Collection<?>) field.get(obj);
                    for (Object object : c) {
                        emptyToNull(object);
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    field.set(obj, Strings.nullToEmpty(String.valueOf(field.get(obj))));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return obj;
    }

    /**
     * 如果长度为达到最小长度，在尾部使用指定的字符，补齐字符串
     *
     * @param str       字符串
     * @param minLength 最小长度
     * @param padChar   补齐所用的字符
     * @return
     */
    public static String padEnd(String str, int minLength, char padChar) {
        return Strings.padEnd(str, minLength, padChar);
    }

    /**
     * 如果长度为达到最小长度，在头部使用指定的字符，补齐字符串
     *
     * @param str       字符串
     * @param minLength 最小长度
     * @param padChar   补齐所用的字符
     * @return
     */
    public static String padStart(String str, int minLength, char padChar) {
        return Strings.padStart(str, minLength, padChar);
    }

    /**
     * 生成指定重复次数的字符串
     *
     * @param str   字符串
     * @param count 重复次数
     * @return
     */
    public static String repeat(String str, int count) {
        return Strings.repeat(str, count);
    }

    /**
     * 从头开始截取
     *
     * @param str 字符串
     * @param end 结束位置
     * @return
     */
    public static String subStrStart(String str, int end) {
        return subStr(str, 0, end);
    }

    /**
     * 从尾开始截取
     *
     * @param str   字符串
     * @param start 开始位置
     * @return
     */
    public static String subStrEnd(String str, int start) {
        return subStr(str, str.length() - start, str.length());
    }

    /**
     * 截取字符串 （支持正向、反向截取）<br/>
     *
     * @param str    待截取的字符串
     * @param length 长度 ，>=0时，从头开始向后截取length长度的字符串；<0时，从尾开始向前截取length长度的字符串
     * @return 返回截取的字符串
     * @throws RuntimeException
     */
    public static String subStr(String str, int length) throws RuntimeException {
        if (str == null) {
            throw new NullPointerException("字符串为null");
        }
        int len = str.length();
        if (len < Math.abs(length)) {
            throw new StringIndexOutOfBoundsException("最大长度为" + len + "，索引超出范围为:" + (len - Math.abs(length)));
        }
        if (length >= 0) {
            return subStr(str, 0, length);
        } else {
            return subStr(str, len - Math.abs(length), len);
        }
    }


    /**
     * 截取字符串 （支持正向、反向选择）<br/>
     *
     * @param str   待截取的字符串
     * @param start 起始索引 ，>=0时，从start开始截取；<0时，从length-|start|开始截取
     * @param end   结束索引 ，>=0时，从end结束截取；<0时，从length-|end|结束截取
     * @return 返回截取的字符串
     * @throws RuntimeException
     */
    public static String subStr(String str, int start, int end) throws RuntimeException {
        if (str == null) {
            throw new NullPointerException("");
        }
        int len = str.length();
        int s = 0;//记录起始索引
        int e = 0;//记录结尾索引
        if (len < Math.abs(start)) {
            throw new StringIndexOutOfBoundsException("最大长度为" + len + "，索引超出范围为:" + (len - Math.abs(start)));
        } else if (start < 0) {
            s = len - Math.abs(start);
        } else if (start < 0) {
            s = 0;
        } else {//>=0
            s = start;
        }
        if (len < Math.abs(end)) {
            throw new StringIndexOutOfBoundsException("最大长度为" + len + "，索引超出范围为:" + (len - Math.abs(end)));
        } else if (end < 0) {
            e = len - Math.abs(end);
        } else if (end == 0) {
            e = len;
        } else {//>=0
            e = end;
        }
        if (e < s) {
            throw new StringIndexOutOfBoundsException("截至索引小于起始索引:" + (e - s));
        }

        return str.substring(s, e);
    }

    /**
     * 用指定字符串数组相连接，并返回
     *
     * @param strs     字符串数组
     * @param splitStr 连接数组的字符串
     * @return
     */
    public static String join(String[] strs, String splitStr) {
        if (strs != null) {
            if (strs.length == 1) {
                return strs[0];
            }
            StringBuffer sb = new StringBuffer();
            for (String str : strs) {
                sb.append(str).append(splitStr);
            }
            if (sb.length() > 0) {
                sb.delete(sb.length() - splitStr.length(), sb.length());
            }
            return sb.toString();
        }
        return null;
    }

    /**
     * 格式化字符串
     *
     * @param str 原字符串
     * @param fmt 格式化类型
     * @return 返回格式化后的数据
     */
    public static String fmt(String str, DataFormat fmt) {
        String result = "";
        switch (fmt.getCode()) {
            case 0://JSON
                //result = JsonUtil.fmt2Json(str);
                result = JsonXmlUtils.xml2JSON(str);
                break;

            case 1://XML
                //result = JsonUtil.fmt2Xml(str, "root");
                result = JsonXmlUtils.json2XML(str);
                break;

            default:
                result = str;
                break;
        }
        return result;
    }

    /**
     * 通过正则表达式获取内容
     *
     * @param regex 正则表达式
     * @param from  原字符串
     * @return
     */
    public static String[] regex(String regex, String from) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(from);
        List<String> results = new ArrayList<String>();
        while (matcher.find()) {
            for (int i = 0; i < matcher.groupCount(); i++) {
                results.add(matcher.group(i + 1));
            }
        }
        return results.toArray(new String[]{});
    }

    /**
     * 通过格式化内容
     *
     * @param template   格式化内容
     * @param replaceStr 带替换的字符
     * @param paras      参数列表
     * @return
     */
    public static String format(String template, String replaceStr, Object... paras) {
        //获取长度
        int len = template.split(replaceStr).length;
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < len; i++) {
            buf.append(template.split(replaceStr)[i]);
            if (i < paras.length) {
                buf.append(paras[i]);
            }
        }
        return buf.toString();
    }

    public static boolean isNum(String str) {
        if(StringUtils.isBlank(str)){
            return false;
        }
        String expression = "-?[0-9]*";
        Pattern pattern = Pattern.compile(expression);
        //判断是否有小数点
        if (str.indexOf(".") > 0) {
            if (str.indexOf(".") == str.lastIndexOf(".") && str.split("\\.").length == 2) { //判断是否只有一个小数点
                return pattern.matcher(str.replace(".", "")).matches();
            } else {
                return false;
            }
        } else {
            return pattern.matcher(str).matches();
        }

    }

    public static StringBuffer buildMsg(StringBuffer sb, String format) {
        if (sb.length() != 0) {
            sb.append("|");
            sb.append(format);
        } else {
            sb.append(format);
        }
        return sb;
    }

    //修改算法，调整为支持表达式，特殊字符，一次遍历
    public static String formatMsg(String format, Object... params) {
        char[] src = format.toCharArray();
        StringBuffer sb = new StringBuffer();
        int cnt = 0;
        for (int i = 0; i < src.length; i++) {
            if(i+1 < src.length && '{' == src[i] && '}' == src[i+1]){
                if(cnt<params.length) {
                    sb.append(params[cnt]);
                    cnt++;
                }
            } else if('}' == src[i]){
                continue;
            } else {
                sb.append(src[i]);
            }
        }
        String dst = sb.toString();
        return dst;
    }
    
    
    /**
     * 全角转半角的 转换函数
     * @Methods Name full2HalfChange
     * @Create In 2012-8-24 By xiaowei
     * @param QJstr
     * @return String
     */
    public static  String full2HalfChange(String QJstr)
    {
        StringBuffer outStrBuf = new StringBuffer("");
        String Tstr = "";
        byte[] b = null;
        for (int i = 0; i < QJstr.length(); i++) {
            Tstr = QJstr.substring(i, i + 1);
            // 全角空格转换成半角空格
            if (Tstr.equals("　")) {
                outStrBuf.append(" ");
                continue;
            }
            try {
                b = Tstr.getBytes("unicode");
                // 得到 unicode 字节数据
                if (b[2] == -1) {
                    // 表示全角
                    b[3] = (byte) (b[3] + 32);
                    b[2] = 0;
                    outStrBuf.append(new String(b, "unicode"));
                } else {
                    outStrBuf.append(Tstr);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            
        } // end for.
        return outStrBuf.toString();
        
        
    }
    
    
    
    /**
     * 半角转全角
     * @Methods Name half2Fullchange
     * @Create In 2012-8-24 By xiaowei
     * @param QJstr
     * @return String
     */
    public static  String half2Fullchange(String QJstr)
    {
        StringBuffer outStrBuf = new StringBuffer("");
        String Tstr = "";
        byte[] b = null;
        for (int i = 0; i < QJstr.length(); i++) {
            Tstr = QJstr.substring(i, i + 1);
            if (Tstr.equals(" ")) {
                // 半角空格
                outStrBuf.append(Tstr);
                continue;
            }
            try {
                b = Tstr.getBytes("unicode");
                if (b[2] == 0) {
                    // 半角
                    b[3] = (byte) (b[3] - 32);
                    b[2] = -1;
                    outStrBuf.append(new String(b, "unicode"));
                } else {
                    outStrBuf.append(Tstr);
                }
                return outStrBuf.toString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            
        }
        return outStrBuf.toString();
    }
    
    /** 数字加单位
     *
     * @param amount
     * @return
     */
    public static String addUnit(BigDecimal amount){
        String result = "0";
        String[] scales= {"","万","亿","万亿","亿亿","万亿亿","亿亿亿","万亿亿亿","亿亿亿亿","万亿亿亿亿","亿亿亿亿亿"};
        if(amount != null){
            String intVal = amount.setScale( 0, BigDecimal.ROUND_DOWN).toPlainString();
            int scale = (intVal.length() - 1) / 4;
            if(scale > 10){
                scale = 10;
            }
            result = amount.divide(new BigDecimal(10000).pow(scale),2,BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString() + scales[scale] ;
        }
        return result;
    }
	
	/*public static void main(String[] args) {
		String str = "12345abcde";
		System.out.println("--------------------------------");
		System.out.println("正向截取长度为4，结果：\n" + StringUtils.subStr(str, 4));
		System.out.println("反向截取长度为4，结果：\n" + StringUtils.subStr(str, -4));
		System.out.println("--------------------------------");
		System.out.println("正向截取到第4个字符的位置，结果：\n" + StringUtils.subStrStart(str, 4));
		System.out.println("反向截取到第4个字符的位置，结果：\n" + StringUtils.subStrEnd(str, 4));
		System.out.println("--------------------------------");
		System.out.println("从第2个截取到第9个，结果：\n" + StringUtils.subStr(str, 1, 9));
		System.out.println("从第2个截取到倒数第1个，结果：\n" + StringUtils.subStr(str, 1, -1));
		System.out.println("从倒数第4个开始截取，结果：\n" + StringUtils.subStr(str, -4, 0));
		System.out.println("从倒数第4个开始截取，结果：\n" + StringUtils.subStr(str, -4, 10));
		
		String[] ss = {"aaa","bbb","ccc"};
		System.out.println(StringUtils.join(ss, "#|#"));
		
		String template = "<root><id>#@#</id><name>#@#</name><tel>#@#</tel><addr>#@#</addr></root>";
		String[] paras={"999","001","aaa","海淀"};
		String data = StringUtils.format(template, "#@#", paras);
		System.out.println(data);
	}*/
    
    public static void main(String[] args) {
        System.out.println(isNum("-1.0"));
    }
}
