package com.github.bluecatlee.common.date;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 年龄工具类
 */
public class AgeUtils {

    /**
     * 根据生日字符串计算年龄
     * @param birthday 生日字符串yyyy-MM-dd
     * @param showMonth 是否展示月份
     * @return
     */
    public static String getAge(String birthday, boolean showMonth) {
        if (StringUtils.isBlank(birthday)) {
            return null;
        }
        if (!showMonth) {
            Integer age = AgeUtils.getAge(birthday);
            if (age == null) {
                return null;
            }
            return "" + age + "岁";
        } else {
            Integer[] age = AgeUtils.getAgeWithMonth(birthday);
            if (age == null) {
                return null;
            }
            int ageYear = age[0];
            int ageMonth = age[1];

            if (ageMonth == 0) {
                if (ageYear == 0) {
                    return "" + ageMonth + "个月";
                }
                return "" + ageYear + "岁整";
            } else if (ageMonth < 10) {
                return "" + ageYear + "岁零" + ageMonth + "个月";
            } else {
                return "" + ageYear + "岁" + ageMonth + "个月";
            }
        }
    }

    @SuppressWarnings("all")
    public static Integer getAge(Date birthDay) {
        if (birthDay == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        if (birthDay.compareTo(new Date()) >= 0) { //出生日期晚于当前时间，无法计算
            return 0;
        }
        int yearNow = cal.get(Calendar.YEAR);  //当前年份
        int monthNow = cal.get(Calendar.MONTH);  //当前月份
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH); //当前日期
        cal.setTime(birthDay);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        int age = yearNow - yearBirth;   //计算整岁数
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--; //当前日期在生日之前，年龄减一
                }
            }else{
                age--; //当前月份在生日之前，年龄减一
            }
        }
        return age;
    }

    @SuppressWarnings("all")
    private static Integer[] getAgeWithMonth(Date birthDay) {
        if (birthDay == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        if (birthDay.compareTo(new Date()) >= 0) { //出生日期晚于当前时间，无法计算
            return new Integer[]{0, 0};
        }
        int yearNow = cal.get(Calendar.YEAR);  //当前年份
        int monthNow = cal.get(Calendar.MONTH);  //当前月份
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH); //当前日期
        cal.setTime(birthDay);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        int age = yearNow - yearBirth;   //计算整岁数
        int ageMonth = 0;

        if (monthBirth > monthNow) {
            age--;
            ageMonth = 12 - monthBirth + monthNow;
            if (dayOfMonthBirth > dayOfMonthNow) {
                ageMonth--;
            }
        } else if (monthBirth == monthNow){
            if (dayOfMonthBirth > dayOfMonthNow) {
                age--;
                ageMonth = 12 - monthNow + monthBirth;
                ageMonth--;
            }
        } else {
            ageMonth = monthNow - monthBirth;
            if (dayOfMonthBirth > dayOfMonthNow) {
                ageMonth--;
            }
        }

        return new Integer[]{age, ageMonth};
    }

    public static Integer getAge(String birthDay) {
        return getAge(parse(birthDay));
    }

    private static Integer[] getAgeWithMonth(String birthDay) {
        return getAgeWithMonth(parse(birthDay));
    }

    private static Date parse(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        String age = getAge("1993-09-22", true);
        System.out.println(age);
    }

}
