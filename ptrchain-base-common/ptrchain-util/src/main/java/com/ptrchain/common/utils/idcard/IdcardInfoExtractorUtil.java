package com.ptrchain.common.utils.idcard;

/**
 * Created by wangmeng on 17/2/20.
 */

import com.ptrchain.common.utils.string.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;


public class IdcardInfoExtractorUtil {

    // 省份
    private String province;
    // 城市
    private String city;
    // 区县
    private String region;
    // 年份
    private int year;
    // 月份
    private int month;
    // 日期
    private int day;
    // 性别
    private String gender;
    // 出生日期
    private Date birthday;



    // 出生几天
    private int ageDays;

    // 出生几天
    private int ageMonths;

    //身份证时候合法
    private boolean isValid;

    public String getBirthdayStr() {
        return birthdayStr;
    }

    public void setBirthdayStr(String birthdayStr) {
        this.birthdayStr = birthdayStr;
    }

    // 出生日期 yyyymmdd
    private String birthdayStr;

    public String getBirthdayStr_s() {
        return birthdayStr_s;
    }

    public void setBirthdayStr_s(String birthdayStr_s) {
        this.birthdayStr_s = birthdayStr_s;
    }

    // 出生日期 yyyy-mm-dd
    private String birthdayStr_s;
    //年龄
    private int age;

    public String getConstellation() {
        return constellation;
    }

    public boolean  getIsValid() {
        return isValid;
    }

    public int getAgeDays() {
        return ageDays;
    }

    public int getAgeMonths() {
        return ageMonths;
    }

    //星座
    private String constellation;

    private Map<String, String> cityCodeMap = new HashMap<String, String>() {
        {
            this.put("11", "北京");
            this.put("12", "天津");
            this.put("13", "河北");
            this.put("14", "山西");
            this.put("15", "内蒙古");
            this.put("21", "辽宁");
            this.put("22", "吉林");
            this.put("23", "黑龙江");
            this.put("31", "上海");
            this.put("32", "江苏");
            this.put("33", "浙江");
            this.put("34", "安徽");
            this.put("35", "福建");
            this.put("36", "江西");
            this.put("37", "山东");
            this.put("41", "河南");
            this.put("42", "湖北");
            this.put("43", "湖南");
            this.put("44", "广东");
            this.put("45", "广西");
            this.put("46", "海南");
            this.put("50", "重庆");
            this.put("51", "四川");
            this.put("52", "贵州");
            this.put("53", "云南");
            this.put("54", "西藏");
            this.put("61", "陕西");
            this.put("62", "甘肃");
            this.put("63", "青海");
            this.put("64", "宁夏");
            this.put("65", "新疆");
            this.put("71", "台湾");
            this.put("81", "香港");
            this.put("82", "澳门");
            this.put("91", "国外");
        }
    };


    /**
     * 通过构造方法初始化各个成员属性
     */
    public IdcardInfoExtractorUtil(String idcard) {
        idcard =  StringUtils.full2HalfChange(idcard);//全角转半角
        try {
            this.isValid=IdcardValidator.isValidatedAllIdcard(idcard);
            if ( this.isValid ) {
                if (idcard.length() == 15) {
                    idcard = IdcardValidator.convertIdcarBy15bit(idcard);
                }
                // 获取省份
                String provinceId = idcard.substring(0, 2);
                Set<String> key = this.cityCodeMap.keySet();
                for (String id : key) {
                    if (id.equals(provinceId)) {
                        this.province = this.cityCodeMap.get(id);
                        break;
                    }
                }

                // 获取性别
                String id17 = idcard.substring(16, 17);
                if (Integer.parseInt(id17) % 2 != 0) {
                    this.gender = "男";
                } else {
                    this.gender = "女";
                }

                // 获取出生日期
                //String birthday =
                this.birthdayStr =  idcard.substring(6, 14);
                this.birthdayStr_s = birthdayStr.substring(0,4)+"-"+birthdayStr.substring(4,6)+"-"+birthdayStr.substring(6,8);
                Date birthdate = new SimpleDateFormat("yyyyMMdd")
                        .parse(this.birthdayStr);
                this.birthday = birthdate;
                GregorianCalendar currentDay = new GregorianCalendar();
                int yearNow = currentDay.get(Calendar.YEAR);
                int monthNow = currentDay.get(Calendar.MONTH)+1;
                int dayOfMonthNow = currentDay.get(Calendar.DAY_OF_MONTH);

                currentDay.setTime(birthdate);
                this.year = currentDay.get(Calendar.YEAR);
                this.month = currentDay.get(Calendar.MONTH) + 1;
                this.day = currentDay.get(Calendar.DAY_OF_MONTH);

                //获取年龄
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy");
                String year=simpleDateFormat.format(new Date());

                this.age=getAge(birthdate);//Integer.parseInt(year)-this.year;
                //if(this.age==0){
                    int days = (int) ((new Date().getTime() - birthdate.getTime()) / (1000*3600*24));
                    this.ageDays = days;
                //}

                int months = (this.age*12);
                if(this.month>monthNow) {
                    months = months + (12 + monthNow - this.month);
                }else if (this.month==monthNow){
                   if(yearNow> this.year){
                       months = months + (12 + monthNow - this.month);
                   }else{
                       months = 0;
                   }
                }else {
                    months =months+(monthNow-this.month);
                }
                if( dayOfMonthNow  < this.day ){
                    months= months-1;
                }

                this.ageMonths = months<0?0:months;



                //根据生日获得星座
              //  this.constellation = ConstellationUtil.getConstellationByBirthday( this.birthday);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the province
     */
    public String getProvince() {
        return province;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @return the region
     */
    public String getRegion() {
        return region;
    }

    /**
     * @return the year
     */
    public int getYear() {
        return year;
    }

    /**
     * @return the month
     */
    public int getMonth() {
        return month;
    }

    /**
     * @return the day
     */
    public int getDay() {
        return day;
    }

    /**
     * @return the gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * @return the birthday
     */
    public Date getBirthday() {
        return birthday;
    }

    @Override
    public String toString() {
            return "身份证是否有效:"+this.isValid+",省份:" + this.province + ",性别:" + this.gender + ",出生日期:"
                + this.birthdayStr + ",星座:"+this.constellation + ",年龄:"+this.age+ ",天龄:"+this.ageDays+ ",月龄:"+this.ageMonths;
    }


    public static Integer getAge(Date birthDay){
        if(birthDay==null)
            return -1;
        //long t1 = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance();

        if (cal.before(birthDay)) {
            return 0;
        }

        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH)+1;
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

        cal.setTime(birthDay);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH)+1;
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                //monthNow==monthBirth
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                }
            } else {
                //monthNow>monthBirth
                age--;
            }
        }
        //long t2 = System.currentTimeMillis();
        //System.out.println("getAge cost time ="+(t2-t1));
        return age;
    }

    public static int getAgeDays(Date birthdate){
    	  GregorianCalendar currentDay = new GregorianCalendar();
          currentDay.setTime(birthdate);
          int days = (int) ((new Date().getTime() - birthdate.getTime()) / (1000*3600*24));
          return days;
    }
    public static int getAgeMonths(Date birthdate){
    	  GregorianCalendar currentDay = new GregorianCalendar();
          int yearNow = currentDay.get(Calendar.YEAR);
          int monthNow = currentDay.get(Calendar.MONTH)+1;
          int dayOfMonthNow = currentDay.get(Calendar.DAY_OF_MONTH);

          currentDay.setTime(birthdate);
          int year = currentDay.get(Calendar.YEAR);
          int month = currentDay.get(Calendar.MONTH) + 1;
          int day = currentDay.get(Calendar.DAY_OF_MONTH);

          int age=getAge(birthdate);//Integer.parseInt(year)-this.year;

          int months = (age*12);
          if(month>monthNow) {
              months = months + (12 + monthNow - month);
          }else if (month==monthNow){
             if(yearNow> year){
                 months = months + (12 + monthNow - month);
             }else{
                 months = 0;
             }
          }else {
              months =months+(monthNow-month);
          }
          if( dayOfMonthNow  < day ){
              months= months-1;
          }

         return months<0?0:months;
    }
    public static void main(String[] args) {

        int max=32;
        int min=0;
        Random random = new Random();

        int s = random.nextInt(max)%(max-min+1) + min;
//        System.out.println(s);


        IdcardInfoExtractorUtil idInfo =  new IdcardInfoExtractorUtil("51390119901224482Ｘ");
        Integer age = idInfo.getAge();
        System.out.println(age);
        String idcard = "110101201703051313";//2017-03-05
//        IdcardValidator iv = new IdcardValidator();
//        boolean flag = false;
//        flag = iv.isValidate18Idcard(idcard);
//        System.out.println("身份证是否合法:"+flag);
    
        IdcardInfoExtractorUtil ie = new IdcardInfoExtractorUtil(idcard);
        System.out.println(ie.toString());

        idcard = "110101201702171874";//2017-02-17
        ie = new IdcardInfoExtractorUtil(idcard);
        System.out.println(ie.toString());

        idcard = "110101201701019539";//2017-01-01
        ie = new IdcardInfoExtractorUtil(idcard);
        System.out.println(ie.toString());


        idcard = "110101201609016810";//2016-09-01
        ie = new IdcardInfoExtractorUtil(idcard);
        System.out.println(ie.toString());

        idcard = "110101201603201994";//2016-03-20
        ie = new IdcardInfoExtractorUtil(idcard);
        System.out.println(ie.toString());

        idcard = "11010120160201947X";//2016-02-01
        ie = new IdcardInfoExtractorUtil(idcard);
        System.out.println(ie.toString());
    }

    public int getAge() {
        return age;
    }


}