/**
 *
 */
package com.ptrchain.common.utils.string;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.util.Assert;

import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Json相关工具类
 *
 * @author haijun.sun
 * @email haijunsun2@creditease.cn
 * @date 2019-02-17 23:30
 */
public class JsonUtils {
	private static final Gson gson = new GsonBuilder()
			.setDateFormat("yyyy-MM-dd HH:mm:ss")
			.create();
	
	/**
	 * 序列化对象
	 *
	 * @param obj
	 * @return
	 */
	public static String toJson(Object obj){
		return gson.toJson(obj);
    }

	/**
	 * 反序列化对象
	 *
	 * @param json
	 * @param clazz
	 * @return
	 */
    public static <T> T  fromJson(String json,Class<T> clazz){
    	return gson.fromJson(json, clazz);
    }
    
    /**
     * 反序列化对象
     *
     * @param json
     * @param clazz
     * @return
     */
    public static Object fromJson(String json,Type type){
    	return gson.fromJson(json, type);
    }
    
    /**
	 * 验证一个字符串是否是合法的JSON串
	 *
	 * @param input
	 *            要验证的字符串
	 * @return true-合法 ，false-非法
	 */
	public static boolean validate(String json) {
		return (new com.ptrchain.common.utils.string.JsonValidator()).validate(json);
	}
	
	/**
	 * 校验是否为get方式提交的参数
	 *
	 * @param paras
	 * @return
	 */
	public static boolean validateModel(String paras){
		return Pattern.compile("\\w*[^&=]*=\\w*[^&=]*&?").matcher(paras).find();
	}
	
	/**
	 * 格式化为json格式
	 * 请使用@see{JsonXmlUtils}的 xml2JSON方法
	 *
	 * @param result
	 * @return
	 */
	@Deprecated
	public static String fmt2Json(String result){
		if(validate(result)){
			return result;
		}
		result = result.replaceAll(">\\s*<", "><").replaceAll("<\\?([^>|^\\?]*)\\?>", "");
		String json = result;
		Matcher matcher = Pattern.compile("<([^>|^/]*)>").matcher(result);
		while(matcher.find()){
			for (int i = 0; i < matcher.groupCount(); i++) {
				String s = matcher.group(i+1);
				json = json.replaceAll("<"+s+">([^<|^\"]*)</"+s+">", "\""+s+"\":\"$1\",");
			}
		}
		json = "{"+json.replaceAll(",?</([^<]*)>", "},").replaceAll("<([^<]*)>", "\"$1\":{")+"}";
		json =json.replaceAll(",}","}").replaceAll("(\\s*\\w*)=\"(\\w*)\"\\s*\"?", "\"$1\":\"$2\",")
				.replaceAll("\\s+([^{]*),:" ,  ":{\"@attributes\":{\"$1},").replace("},{", "},")
				.replaceAll("},([^}|^\"]*)}", "},\"@value\":\"$1\"}");
		return json;
	}
	
	/**
	 * 格式化为xml格式
	 * 请使用@see{JsonXmlUtils}的 json2XML方法
	 *
	 * @param json
	 * @return
	 */
	@Deprecated
	public static String fmt2Xml(String json){
		return fmt2Xml(json, "root");
	}
	
	/**
	 * 格式化为xml格式
	 * 请使用@see{JsonXmlUtils}的 json2XML方法
	 *
	 * @param json
	 * @param rootEle
	 * @return
	 */
	@Deprecated
	public static String fmt2Xml(String json, String rootEle){
		if(!validate(json)){
			return fmt2Xml(fmt2Json(json),rootEle);
		}
		rootEle = rootEle.replaceAll("\\W", "");
		rootEle = StringUtils.isNullOrEmpty(rootEle)? "root": rootEle;
//		return json.replaceAll("\"(\\w*)\":\"?([^\",}]*)\"?,?","<$1>$2</$1>").replaceAll("\\{([^\\}]*)\\}", "<?xml version=\"1.0\" encoding=\"utf-8\" ?><"+rootEle+">$1"+"</"+rootEle+">");
		
		//去掉@attributes和@value
		Pattern pattern = Pattern.compile("\"@attributes\":\\{([^}]*)}");
		Matcher matcher = pattern.matcher(json);
		while(matcher.find()){
			String s = "";
			for (int i = 0; i < matcher.groupCount(); i++) {
				s = matcher.group(i+1);
				s = s.replaceAll("\"(\\w*)\":\"([^\"]*)\",?", " $1=$2");
			}
			json = json.replaceAll("[^,]\"(\\w*)\":\\{\"@attributes\":\\{[^}]*},?","{\"$1"+s+"\":{");
			//matcher = pattern.matcher(json);
		}
		json = json.replaceAll("\\{\"@value\":\"([^\"]*)\"}", "\"$1\"");
		
		//处理嵌套
		json = json.replaceAll("\"([\\w|\\s|=]*)\":\"([^\",{}]+)\",?", "<$1>$2</$1>");
		pattern = Pattern.compile("\"(\\w*)\":\\{([^{}]*)},?");
		while(pattern.matcher(json).find()){
			json = pattern.matcher(json).replaceAll("<$1>$2</$1>");
		}
		
		pattern = Pattern.compile("\"([\\w|\\s|=]*)\":([^}\"]*)},?");
		while(pattern.matcher(json).find()){
			json = pattern.matcher(json).replaceAll("<$1>$2</$1>");
		}
		
		json = json.replaceAll("(\\w*)=(\\w*)", "$1=\"$2\"").replaceAll("/(\\w*)\\s[\\w*)=\"\\w*\"\\s?]*", "/$1").replaceAll("[{|}]", "");
		json = "<?xml version=\"1.0\" ?><"+rootEle+">"+json+"</"+rootEle+">";
		return json;
	}
	
	
	/**
	 * 递归
	 * json转换arr进行排序处理
	 */
	public static JSONArray sortJsonBykey(JSONObject jsonObject, Comparator<String> comparator){
		
		JSONArray result = new JSONArray();
		Set<String> inOrderkeys = new TreeSet<>(comparator);
		inOrderkeys.addAll(jsonObject.keySet());
		inOrderkeys.forEach((x) -> {
			Object value = jsonObject.get(x);
			if (value instanceof JSONObject){
				JSONObject tmp = new JSONObject();
				tmp.put(x,sortJsonBykey((JSONObject)value,comparator));
				result.add(tmp);
			}else{
				JSONObject tmp = new JSONObject();
				tmp.put(x,value);
				result.add(tmp);
			}
		});
		return result;
	}
	
	/**
	 * JSON PATH路径追加
	 *
	 * @param pathString
	 * @return
	 */
	public static String compilePath(String... pathString) {
        /*for (int i = 0; i < pathString.length; i++) {
            pathString[i] = pathString[i].replace(".","#");
        }*/
		String path = StringUtils.join(pathString, ".");
		StringBuilder pathsb = new StringBuilder();
		Assert.notNull(path,"path不能为空");
		char[] chars = path.toCharArray();
		for (char c : chars) {
			if ("$\\.ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".contains(String.valueOf(c))) {
				pathsb.append(c);
			} else {
				pathsb.append("\\").append(c);
			}
		}
		return pathsb.toString();
	}
	
	/**
	 * 对象生成 json
	 * @param o
	 * @return
	 */
	public static JSONObject parse(Object o){
		JSONObject result = new JSONObject();
		if(o == null){
			return result;
		}else {
			String s = toJson(o);
			try {
				JSONArray array = JSON.parseArray(s);
				result.put("array", array);
				return result;
			} catch (JSONException e) {
				try {
					return JSON.parseObject(s);
				} catch (JSONException x) {
					if( o instanceof String || o instanceof Number){
						result.put("msg", o);
					}else {
						result.put("object",s);
					}
					return result;
				}
			}
		}
	}

//	public static void main(String[] args) {
//		String str = "<Response a=\"123\" b=\"000\">"
//								+ "<status  c=\"123\" d=\"000\">201</status>"
//								+ "<A><status1>201</status1><message1>APP被用户自己禁用</message1></A>"
//								+ "<A2><status1>201</status1><message1>APP被用户自己禁用</message1></A2>"
//								+ "<B>"
//								+ "	<BB><status1>201</status1><message1>APP被用户自己禁用</message1></BB>"
//								+ "</B>"
//								+ "<message>APP被用户自己禁用，请在控制台解禁</message>"
//								+ "<C><status1>201</status1><message1>APP被用户自己禁用</message1></C>"
//							+ "</Response>";
//
//		String json = fmt2Json(str);
//		String xml = fmt2Xml(json);
//		System.out.println("xml转化为json：" + json);
//		System.out.println("json转化为xml：" + xml);
//		List<Map<String,String>> o = new ArrayList<>();
//		Map<String,String> o1 = new Hashtable<>();
//		o1.put("12313213","#333");
//		o1.put("33333","33333");
//		o.add(o1);
//		o.add(o1);
//		System.out.println(parse(o));
//	}
}
