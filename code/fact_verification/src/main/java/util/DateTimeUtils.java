package util;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DateTimeUtils {
    public static SimpleDateFormat formatAYMD = new SimpleDateFormat("MMM d yyyy",Locale.ENGLISH);
    public static SimpleDateFormat formatYYMD = new SimpleDateFormat("d MMM yyyy",Locale.ENGLISH);
    public static SimpleDateFormat formatAYMD1 = new SimpleDateFormat("MMM d, yyyy",Locale.ENGLISH);
    public static SimpleDateFormat formatYYMD1 = new SimpleDateFormat("d MMM, yyyy",Locale.ENGLISH);
    public static SimpleDateFormat formatAMD = new SimpleDateFormat("MMM yyyy",Locale.ENGLISH);
    public static SimpleDateFormat formatAMD1 = new SimpleDateFormat("MMM, yyyy",Locale.ENGLISH);
    public static SimpleDateFormat formatYMDSlash = new SimpleDateFormat("yyyy/MM/dd");
    public static SimpleDateFormat formatYMSlash = new SimpleDateFormat("yyyy/MM");
    public static SimpleDateFormat formatMDY2Slash = new SimpleDateFormat("MM/dd/yy");
    public static SimpleDateFormat formatMDY4Slash = new SimpleDateFormat("MM/dd/yyyy");
    public static SimpleDateFormat formatDMY4Slash = new SimpleDateFormat("dd/MM/yyyy");
    public static SimpleDateFormat formatY_M_D = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat formatY_M = new SimpleDateFormat("yyyy-MM");
    public static SimpleDateFormat formatY = new SimpleDateFormat("yyyy");
    public static SimpleDateFormat formatYMD = new SimpleDateFormat("yyyyMMdd");
    public static SimpleDateFormat formatYM = new SimpleDateFormat("yyyyMM");
    public static SimpleDateFormat formatY_M_D_HMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat formatY_M_D_HM = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static SimpleDateFormat formatYMD_HMS = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
    public static SimpleDateFormat formatYMDHMS = new SimpleDateFormat("yyyyMMddHHmmss");
    public static SimpleDateFormat formatY_M_D_HMSMILLS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    public static SimpleDateFormat formatYMDChinese = new SimpleDateFormat("yyyy年MM月dd日");
    public static SimpleDateFormat formatYMDBlankHMSChinese = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
    public static SimpleDateFormat formatYMDHMSChinese = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
    public static SimpleDateFormat formatYMDChineseBlankHMS = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
    public static SimpleDateFormat formatYMDChineseHMS = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
    public static SimpleDateFormat formatYMDChineseBlankHM = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
    public static SimpleDateFormat formatYMDChineseHM = new SimpleDateFormat("yyyy年MM月dd日HH:mm");
    public static SimpleDateFormat formatYMChinese = new SimpleDateFormat("yyyy年MM月");
    public static SimpleDateFormat formatYChinese = new SimpleDateFormat("yyyy年");
    private static Logger logger = Logger.getLogger(DateTimeUtils.class);

    static {
        formatYMDSlash.setLenient(false);
        formatYMSlash.setLenient(false);
        formatMDY2Slash.setLenient(false);
        formatMDY4Slash.setLenient(false);
        formatY_M_D.setLenient(false);
        formatY_M.setLenient(false);
        formatY.setLenient(false);
        formatYMD.setLenient(false);
        formatYM.setLenient(false);
        formatY_M_D_HMS.setLenient(false);
        formatY_M_D_HM.setLenient(false);
        formatYMD_HMS.setLenient(false);
        formatYMDHMS.setLenient(false);
        formatY_M_D_HMSMILLS.setLenient(false);
        formatYMDChinese.setLenient(false);
        formatYMDBlankHMSChinese.setLenient(false);
        formatYMDHMSChinese.setLenient(false);
        formatYMDChineseHMS.setLenient(false);
        formatYMDChineseBlankHMS.setLenient(false);
        formatYMDChineseHM.setLenient(false);
        formatYMDChineseBlankHM.setLenient(false);
        formatYMChinese.setLenient(false);
        formatYChinese.setLenient(false);
    }


    public static String dateStr2DateStr(String dateStr,boolean flag) {
        String returnStr = dateStr;
        try {
            String dateTimeFromStr = getDateStrFromStr(dateStr);
//            System.out.println(dateTimeFromStr);
            Date date = parseStringToDate(dateTimeFromStr);
            if (date != null) {
//                returnStr = getDateFormat2(dateTimeFromStr).format(date);
                returnStr = formatY_M_D.format(date);
            }
        } catch (Exception e) {
            logger.error("date string " + dateStr + " transform to format date string exception!", e);
            if(flag)
                returnStr="";
        }
        return returnStr;
    }


    public static Date parseStringToDate(String dateStr) {
        if (dateStr == null || "".equals(dateStr.trim()))
            return null;
        dateStr = dateStr.trim();
        Date date = null;
        try {
            if(dateStr.matches(RegexUtils.yymdRegex)){
                try {
                    date = formatYYMD.parse(dateStr);
                }catch (Exception e){
                    try {
                        date = formatYYMD1.parse(dateStr);
                    }catch (Exception e1){
                        try {
                            date = formatAMD.parse(dateStr);
                        }catch (Exception e2){
                            date = formatAMD1.parse(dateStr);
                        }
                    }
                }
                System.out.println(date);
            }else if(dateStr.matches(RegexUtils.mymdRegex)){
                try {
                    date = formatAYMD.parse(dateStr);
                }catch (Exception e){
                    try {
                        date = formatAYMD1.parse(dateStr);
                    }catch (Exception e1){
                        try {
                            date = formatAMD.parse(dateStr);
                        }catch (Exception e2){
                            date = formatAMD1.parse(dateStr);
                        }
                    }
                }
                System.out.println(date);
            }else if (dateStr.matches(RegexUtils.ymdSlash)) {
                date = formatYMDSlash.parse(dateStr);
            } else if (dateStr.matches(RegexUtils.ymSlash)) {
                date = formatYMSlash.parse(dateStr);
            } else if (dateStr.matches(RegexUtils.y_m_d)) {
                date = formatY_M_D.parse(dateStr);
            } else if (dateStr.matches(RegexUtils.y_m)) {
                date = formatY_M.parse(dateStr);
            } else if (dateStr.matches(RegexUtils.y)) {
                date = formatY.parse(dateStr);
            } else if (dateStr.matches(RegexUtils.ymd)) {
                try {
                    date = formatYMD.parse(dateStr);
                } catch (Exception e) {
                    date = formatYM.parse(dateStr);
                }
            } else if (dateStr.matches(RegexUtils.ym)) {
                date = formatYM.parse(dateStr);
            } else if (dateStr.matches(RegexUtils.ymdhms)) {
                date = formatYMDHMS.parse(dateStr);
            } else if (dateStr.matches(RegexUtils.y_m_d_h_m)) {
                date = formatY_M_D_HM.parse(dateStr);
            } else if (dateStr.matches(RegexUtils.y_m_d_h_m_s)) {
                date = formatY_M_D_HMS.parse(dateStr);
            } else if (dateStr.matches(RegexUtils.y_m_d_h_m_s_S)) {
                date = formatY_M_D_HMSMILLS.parse(dateStr);
            } else if (dateStr.matches(RegexUtils.mdy2Slash)) {
                date = formatMDY2Slash.parse(dateStr);
            } else if (dateStr.matches(RegexUtils.mdy4Slash)) {
                try {
                    date = formatMDY4Slash.parse(dateStr);
                }catch (Exception e){
                    date =formatDMY4Slash.parse(dateStr);
                }

            } else if (dateStr.matches(RegexUtils.ymdchinese)) {
                date = formatYMDChinese.parse(dateStr);
            } else if (dateStr.matches(RegexUtils.ymdhmschinese)) {
                date = formatYMDHMSChinese.parse(dateStr);
            } else if (dateStr.matches(RegexUtils.ymdblankhmschinese)) {
                date = formatYMDBlankHMSChinese.parse(dateStr);
            } else if (dateStr.matches(RegexUtils.ymdchinesehms)) {
                date = formatYMDChineseHMS.parse(dateStr);
            } else if (dateStr.matches(RegexUtils.ymdchineseblankhms)) {
                date = formatYMDChineseBlankHMS.parse(dateStr);
            } else if (dateStr.matches(RegexUtils.ymdchinesehm)) {
                date = formatYMDChineseHM.parse(dateStr);
            } else if (dateStr.matches(RegexUtils.ymdchineseblankhm)) {
                date = formatYMDChineseBlankHM.parse(dateStr);
            } else if (dateStr.matches(RegexUtils.ymchinese)) {
                date = formatYMChinese.parse(dateStr);
            } else if (dateStr.matches(RegexUtils.ychinese)) {
                date = formatYChinese.parse(dateStr);
            } else {
                logger.info("please enter the normal date string: " + dateStr);
            }
        } catch (Exception e) {
            logger.error("exception: " + e + ", date string " + dateStr + " is not valid!");
        }
        return date;
    }


    public static String getDateStrFromStr(String str) {
        if (str == null) return null;
        String returnStr = "";
        try {
            Pattern mymdRPattern = Pattern.compile(RegexUtils.yymdRegex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = mymdRPattern.matcher(str);
            if (matcher.find()) {
                returnStr = matcher.group();
                return returnStr;
            }
            Pattern yymdPattern = Pattern.compile(RegexUtils.mymdRegex, Pattern.CASE_INSENSITIVE);
            matcher = yymdPattern.matcher(str);
            if (matcher.find()) {
                returnStr = matcher.group();
                return returnStr;
            }
            Pattern ymdSlashPattern = Pattern.compile(RegexUtils.ymdSlashRegex);
            matcher = ymdSlashPattern.matcher(str);
            if (matcher.find()) {
                returnStr = matcher.group();
                return returnStr;
            }
            Pattern ymSlashPattern = Pattern.compile(RegexUtils.ymSlashRegex);
            matcher = ymSlashPattern.matcher(str);
            if (matcher.find()) {
                returnStr = matcher.group();
                return returnStr;
            }
            Pattern mdy4SlashPattern = Pattern.compile(RegexUtils.mdy4SlashRegex);
            matcher = mdy4SlashPattern.matcher(str);
            if (matcher.find()) {
                returnStr = matcher.group();
                return returnStr;
            }
            Pattern mdy2SlashPattern = Pattern.compile(RegexUtils.mdy2SlashRegex);
            matcher = mdy2SlashPattern.matcher(str);
            if (matcher.find()) {
                returnStr = matcher.group();
                return returnStr;
            }
            Pattern y_m_d_h_m_s_SPattern = Pattern.compile(RegexUtils.y_m_d_h_m_s_SRegex);
            matcher = y_m_d_h_m_s_SPattern.matcher(str);
            if (matcher.find()) {
                returnStr = matcher.group();
                return returnStr;
            }
            Pattern y_m_d_h_m_sPattern = Pattern.compile(RegexUtils.y_m_d_h_m_sRegex);
            matcher = y_m_d_h_m_sPattern.matcher(str);
            if (matcher.find()) {
                returnStr = matcher.group();
                return returnStr;
            }
            Pattern y_m_d_h_mPattern = Pattern.compile(RegexUtils.y_m_d_h_mRegex);
            matcher = y_m_d_h_mPattern.matcher(str);
            if (matcher.find()) {
                returnStr = matcher.group();
                return returnStr;
            }
            Pattern y_m_dPattern = Pattern.compile(RegexUtils.y_m_dRegex);
            matcher = y_m_dPattern.matcher(str);
            if (matcher.find()) {
                returnStr = matcher.group();
                return returnStr;
            }
            Pattern y_mPattern = Pattern.compile(RegexUtils.y_mRegex);
            matcher = y_mPattern.matcher(str);
            if (matcher.find()) {
                returnStr = matcher.group();
                return returnStr;
            }
            Pattern ymdblankhmschinesePattern = Pattern.compile(RegexUtils.ymdblankhmschineseRegex);
            matcher = ymdblankhmschinesePattern.matcher(str);
            if (matcher.find()) {
                returnStr = matcher.group();
                return returnStr;
            }
            Pattern ymdhmschinesePattern = Pattern.compile(RegexUtils.ymdhmschineseRegex);
            matcher = ymdhmschinesePattern.matcher(str);
            if (matcher.find()) {
                returnStr = matcher.group();
                return returnStr;
            }
            Pattern ymdchineseblankhmsPattern = Pattern.compile(RegexUtils.ymdchineseblankhmsRegex);
            matcher = ymdchineseblankhmsPattern.matcher(str);
            if (matcher.find()) {
                returnStr = matcher.group();
                return returnStr;
            }
            Pattern ymdchineseblankhmPattern = Pattern.compile(RegexUtils.ymdchineseblankhmRegex);
            matcher = ymdchineseblankhmPattern.matcher(str);
            if (matcher.find()) {
                returnStr = matcher.group();
                return returnStr;
            }
            Pattern ymdchinesehmsPattern = Pattern.compile(RegexUtils.ymdchinesehmsRegex);
            matcher = ymdchinesehmsPattern.matcher(str);
            if (matcher.find()) {
                returnStr = matcher.group();
                return returnStr;
            }
            Pattern ymdchinesehmPattern = Pattern.compile(RegexUtils.ymdchinesehmRegex);
            matcher = ymdchinesehmPattern.matcher(str);
            if (matcher.find()) {
                returnStr = matcher.group();
                return returnStr;
            }
            Pattern ymdchinesePattern = Pattern.compile(RegexUtils.ymdchineseRegex);
            matcher = ymdchinesePattern.matcher(str);
            if (matcher.find()) {
                returnStr = matcher.group();
                return returnStr;
            }
            Pattern ymchinesePattern = Pattern.compile(RegexUtils.ymchineseRegex);
            matcher = ymchinesePattern.matcher(str);
            if (matcher.find()) {
                returnStr = matcher.group();
                return returnStr;
            }
            Pattern ychinesePattern = Pattern.compile(RegexUtils.ychineseRegex);
            matcher = ychinesePattern.matcher(str);
            if (matcher.find()) {
                returnStr = matcher.group();
                return returnStr;
            }
            Pattern ymdhmsPattern = Pattern.compile(RegexUtils.ymdhmsRegex);
            matcher = ymdhmsPattern.matcher(str);
            if (matcher.find()) {
                returnStr = matcher.group();
                return returnStr;
            }
            Pattern ymdPattern = Pattern.compile(RegexUtils.ymdRegex);
            matcher = ymdPattern.matcher(str);
            if (matcher.find()) {
                returnStr = matcher.group();
                return returnStr;
            }
            Pattern ymPattern = Pattern.compile(RegexUtils.ymRegex);
            matcher = ymPattern.matcher(str);
            if (matcher.find()) {
                returnStr = matcher.group();
                return returnStr;
            }
            Pattern yPattern = Pattern.compile(RegexUtils.yRegex);
            matcher = yPattern.matcher(str);
            if (matcher.find()) {
                returnStr = matcher.group();
                return returnStr;
            }
            Pattern dmy4Pattern = Pattern.compile(RegexUtils.dmy4SlashRegex);
            matcher = dmy4Pattern.matcher(str);
            if (matcher.find()) {
                returnStr = matcher.group();
                return returnStr;
            }
        } catch (Exception e) {
            logger.error("get date time from string " + str + " exception!", e);
        }
        return returnStr;
    }


    public static SimpleDateFormat getDateFormat(String dateStr) {
        if (dateStr == null) return null;
        if (dateStr.matches(RegexUtils.ymdSlash)) {
            return formatYMDSlash;
        } else if (dateStr.matches(RegexUtils.ymSlash)) {
            return formatYMSlash;
        } else if (dateStr.matches(RegexUtils.y_m)) {
            return formatY_M;
        } else if (dateStr.matches(RegexUtils.y_m_d)) {
            return formatY_M_D;
        } else if (dateStr.matches(RegexUtils.y_m)) {
            return formatY_M;
        } else if (dateStr.matches(RegexUtils.y)) {
            return formatY;
        } else if (dateStr.matches(RegexUtils.ymd)) {
            if (dateStr.length() == 6) {
                if (!"0".equals(String.valueOf(dateStr.charAt(4)))) {
                    return formatYMD;
                } else {
                    return formatYM;
                }
            }
            return formatYMD;
        } else if (dateStr.matches(RegexUtils.ym)) {
            return formatYM;
        } else if (dateStr.matches(RegexUtils.ymdhms)) {
            return formatYMDHMS;
        } else if (dateStr.matches(RegexUtils.y_m_d_h_m)) {
            return formatY_M_D_HM;
        } else if (dateStr.matches(RegexUtils.y_m_d_h_m_s)) {
            return formatY_M_D_HMS;
        } else if (dateStr.matches(RegexUtils.y_m_d_h_m_s_S)) {
            return formatY_M_D_HMSMILLS;
        } else if (dateStr.matches(RegexUtils.mdy2Slash)) {
            return formatMDY2Slash;
        } else if (dateStr.matches(RegexUtils.mdy4Slash)) {
            return formatMDY4Slash;
        } else if (dateStr.matches(RegexUtils.ymdchinese)) {
            return formatYMDChinese;
        } else if (dateStr.matches(RegexUtils.ymdhmschinese)) {
            return formatYMDHMSChinese;
        } else if (dateStr.matches(RegexUtils.ymdblankhmschinese)) {
            return formatYMDBlankHMSChinese;
        } else if (dateStr.matches(RegexUtils.ymdchinesehms)) {
            return formatYMDChineseHMS;
        } else if (dateStr.matches(RegexUtils.ymdchineseblankhms)) {
            return formatYMDChineseBlankHMS;
        } else if (dateStr.matches(RegexUtils.ymdchinesehm)) {
            return formatYMDChineseHM;
        } else if (dateStr.matches(RegexUtils.ymdchineseblankhm)) {
            return formatYMDChineseBlankHM;
        } else if (dateStr.matches(RegexUtils.ymchinese)) {
            return formatYMChinese;
        } else if (dateStr.matches(RegexUtils.ychinese)) {
            return formatYChinese;
        } else {
            return null;
        }
    }


    public static SimpleDateFormat getDateFormat2(String dateStr) {
        if (dateStr == null) return null;
        if(dateStr.matches(RegexUtils.yymdRegex)){
            return formatYMDSlash;
        }else  if(dateStr.matches(RegexUtils.mymdRegex)){
            return formatYMDSlash;
        }else if (dateStr.matches(RegexUtils.ymdSlash)) {
            return formatY_M_D_HMS;
        } else if (dateStr.matches(RegexUtils.ymSlash)) {
            return formatY_M;
        } else if (dateStr.matches(RegexUtils.y_m)) {
            return formatY_M;
        } else if (dateStr.matches(RegexUtils.y_m_d)) {
            return formatY_M_D;
        } else if (dateStr.matches(RegexUtils.y_m)) {
            return formatY_M;
        } else if (dateStr.matches(RegexUtils.y)) {
            return formatY;
        } else if (dateStr.matches(RegexUtils.ymd)) {
            if (dateStr.length() == 6) {
                if (!"0".equals(String.valueOf(dateStr.charAt(4)))) {
                    return formatY_M_D;
                } else {
                    return formatY_M;
                }
            }
            return formatY_M_D;
        } else if (dateStr.matches(RegexUtils.ym)) {
            return formatY_M_D;
        } else if (dateStr.matches(RegexUtils.ymdhms)) {
            return formatY_M_D_HMS;
        } else if (dateStr.matches(RegexUtils.y_m_d_h_m)) {
            return formatY_M_D_HM;
        } else if (dateStr.matches(RegexUtils.y_m_d_h_m_s)) {
            return formatY_M_D_HMS;
        } else if (dateStr.matches(RegexUtils.y_m_d_h_m_s_S)) {
            return formatY_M_D_HMSMILLS;
        } else if (dateStr.matches(RegexUtils.mdy2Slash)) {
            return formatY_M_D;
        } else if (dateStr.matches(RegexUtils.mdy4Slash)) {
            return formatY_M_D;
        } else if (dateStr.matches(RegexUtils.ymdchinese)) {
            return formatY_M_D;
        } else if (dateStr.matches(RegexUtils.ymdhmschinese)) {
            return formatY_M_D_HMS;
        } else if (dateStr.matches(RegexUtils.ymdblankhmschinese)) {
            return formatY_M_D_HMS;
        } else if (dateStr.matches(RegexUtils.ymdchinesehms)) {
            return formatY_M_D_HMS;
        } else if (dateStr.matches(RegexUtils.ymdchineseblankhms)) {
            return formatY_M_D_HMS;
        } else if (dateStr.matches(RegexUtils.ymdchinesehm)) {
            return formatY_M_D_HM;
        } else if (dateStr.matches(RegexUtils.ymdchineseblankhm)) {
            return formatY_M_D_HM;
        } else if (dateStr.matches(RegexUtils.ymchinese)) {
            return formatY_M;
        } else if (dateStr.matches(RegexUtils.ychinese)) {
            return formatY;
        } else {
            return null;
        }
    }


    public static String dateStrToyMdHms(String dateStr) {
        String returnStr = "";
        try {
            Date date = parseStringToDate(dateStr);
            returnStr = formatY_M_D_HMS.format(date);
        } catch (Exception e) {
            logger.error(dateStr + " transform to yyyy-MM-dd HH:mm:ss exception: " + e);
        }
        return returnStr;
    }


    public static String dateStrToyMd(String dateStr) {
        String returnStr = "";
        try {
            Date date = parseStringToDate(dateStr);
            returnStr = formatY_M_D.format(date);
        } catch (Exception e) {
            logger.error(dateStr + " transform to yyyy-MM-dd exception: " + e);
        }
        return returnStr;
    }


    public static String getMaxTime(String dateS1, String dateS2) {
        String maxTime = "";
        Date date1;
        Date date2;
        try {
            date1 = DateTimeUtils.parseStringToDate(dateS1);
            date2 = DateTimeUtils.parseStringToDate(dateS2);
            if (date1 != null) {
                if (date2 != null && date2.getTime() > date1.getTime()) {
                    maxTime = dateS2;
                } else {
                    maxTime = dateS1;
                }
            } else if (date2 != null) {
                maxTime = dateS2;
            }
        } catch (Exception e) {
            logger.error("get max time exception: " + e + ", the two date string is " + dateS1 + " and " + dateS2, e);
        }
        return maxTime;
    }


    public static boolean compareTime(String time1, String time2) {
        boolean flag = false;
        try {
            if (time1 == null || time1.length() == 0) {
                flag = false;
            } else if (time2 == null || time2.length() == 0) {
                flag = true;
            } else {
                Date date1 = parseStringToDate(time1);
                Date date2 = parseStringToDate(time2);
                if (date1 != null) {
                    if (date2 != null) {
                        if (date1.getTime() >= date2.getTime()) flag = true;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("compare time exception: " + e + ", the two compare time is " + time1 + " and " + time2, e);
        }
        return flag;
    }


    public static java.sql.Date strToSqlDate(String str) {
        java.sql.Date returnDate = null;
        try {
            Date date = parseStringToDate(str);
            if (date != null) returnDate = new java.sql.Date(date.getTime());
        } catch (Exception e) {
            logger.error("string " + str + " to java.sql.Date exception!", e);
        }
        return returnDate;
    }


    public static void dateTransform(JSONObject jsonObject, String key) {
        if (jsonObject == null) return;
        if (jsonObject.containsKey(key)) {
            String transDate = jsonObject.getString(key);
            try {
                if (transDate.matches("\\d{6}")) {
                    transDate = transDate.substring(0, 4) + "-" + transDate.substring(4);
                }
                if (transDate.matches("\\d{8}")) {
                    transDate = transDate.substring(0, 4) + "-" + transDate.substring(4, 6) + "-" + transDate.substring(6);
                }
                jsonObject.put(key, transDate);
            } catch (Exception e) {
                logger.error("转换日期格式失败：" + transDate, e);
            }
        }
    }


    public static String timestampToDateStr(Date date) {
        String returnStr = "";
        try {
            returnStr = formatY_M_D_HMS.format(date);
        } catch (Exception e) {
            logger.error("timestamp or date " + date + " to string exception!", e);
        }
        return returnStr;
    }


    public static String currentTimeStr() {
        String format = null;
        try {
            format = formatY_M_D_HMS.format(new Date());
        } catch (Exception e) {
            logger.error("format current date time exception!", e);
        }
        return format;
    }


    public static String currentDateStr() {
        String format = null;
        try {
            format = formatY_M_D.format(new Date());
        } catch (Exception e) {
            logger.error("format current date time exception!", e);
        }
        return format;
    }


    public static int getAgeFromDateStr(String birthTimeString) {

        String strs[] = birthTimeString.trim().split("-");
        int selectYear = Integer.parseInt(strs[0]);
        int selectMonth = Integer.parseInt(strs[1]);
        int selectDay = Integer.parseInt(strs[2]);

        Calendar cal = Calendar.getInstance();
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH) + 1;
        int dayNow = cal.get(Calendar.DATE);


        int yearMinus = yearNow - selectYear;
        int monthMinus = monthNow - selectMonth;
        int dayMinus = dayNow - selectDay;

        int age = yearMinus;
        if (yearMinus < 0) {
            age = 0;
        } else if (yearMinus == 0) {
            if (monthMinus < 0) {
                age = 0;
            } else if (monthMinus == 0) {
                if (dayMinus < 0) {
                    age = 0;
                } else if (dayMinus >= 0) {
                    age = 1;
                }
            } else if (monthMinus > 0) {
                age = 1;
            }
        } else if (yearMinus > 0) {
            if (monthMinus < 0) {
            } else if (monthMinus == 0) {
                if (dayMinus < 0) {
                } else if (dayMinus >= 0) {
                    age = age + 1;
                }
            } else if (monthMinus > 0) {
                age = age + 1;
            }
        }
        return age;
    }

    public static void main(String[] args){
        String test = "May 6 2011  Limited";
        System.out.println(dateStr2DateStr(test,false));

    }

}
