package util;


public class RegexUtils {

    public final static String blankRegex = "\\s*|\t|\r|\n"; 


    public final static String nameRegex = "[\\u4e00-\\u9fa5]+·?[\\u4e00-\\u9fa5]+";
    public final static String phoneRegex = "^[+]?(?:\\s*8\\s*6)?\\s*1\\s*[3|4|5|6|7|8|9]\\s*(?:\\d\\s*){9}\\s*$";
    public final static String idCardRegex = "(\\d{15})|(\\d{18})|(\\d{17}[xX]{1})";


    public final static String intRegex = "-?\\d+"; 
    public final static String floatRegex = "-?\\d+\\.\\d+"; 
    public final static String numberRegex = "-?\\d+(\\.\\d+)?"; 


    public final static String pointMoneyRegex = "([\\d\\.]+角)";
    public final static String rmbMoneyRegex = "([\\d\\.]+块)|([\\d\\.]+元)";
    public final static String dollarMoneyRegex = "([\\d\\.]+美元)";
    public final static String tenThousandMoneyRegex = "([\\d\\.]+万)";
    public final static String hundredThousandMoneyRegex = "([\\d\\.]+十万)";
    public final static String millionMoneyRegex = "([\\d\\.]+百万)";
    public final static String tenMillionMoneyRegex = "([\\d\\.]+千万)";
    public final static String billionMoneyRegex = "([\\d\\.]+亿)";


    public final static String mymdRegex = "(?i)((January|February|March|April|May|June|July|August|September|October|November|December)|((Jan|Feb|Mar|Apr|Aug|Sept|Oct|Nov|Dec)(\\.?)))( ?)(\\d+)((st|nd|rd|th)?)(,?)( ?)(\\d{2,})";
    public final static String yymdRegex = "(?i)(\\d+)((st|nd|rd|th)?)(,?)( ?)((January|February|March|April|May|June|July|August|September|October|November|December)|((Jan|Feb|Mar|Apr|Aug|Sept|Oct|Nov|Dec)(\\.?)))(,?)( ?)(\\d{2,})";
    public final static String ymdSlashRegex = "(\\d{4}/\\d{1,2}/\\d{1,2})"; 
    public final static String ymSlashRegex = "(\\d{4}/\\d{1,2})";
    public final static String mdy2SlashRegex = "(\\d{1,2}/\\d{1,2}/\\d{2})";
    public final static String mdy4SlashRegex = "(\\d{1,2}/\\d{1,2}/\\d{4})";
    public final static String dmy4SlashRegex = "(\\d{1,2}/\\d{1,2}/\\d{4})"; 
    public final static String y_m_dRegex = "(\\d{4}-\\d{1,2}-\\d{1,2})"; 
    public final static String y_mRegex = "(\\d{4}-\\d{1,2})"; 
    public final static String yRegex = "(\\d{4})";
    public final static String ymdRegex = "(\\d{6,8})";
    public final static String ymRegex = "(\\d{5,6})";
    public final static String y_m_d_h_m_sRegex = "(\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}:\\d{1,2})";
    public final static String y_m_d_h_mRegex = "(\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2})";
    public final static String ymdhmsRegex = "(\\d{9,14})";
    public final static String y_m_d_h_m_s_SRegex = "(\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}\\.\\d{1,3})";
    public final static String ymdchineseRegex = "(\\d{4}年\\d{1,2}月\\d{1,2}日)";
    public final static String ymdblankhmschineseRegex = "(\\d{4}年\\d{1,2}月\\d{1,2}日\\s\\d{1,2}时\\d{1,2}分\\d{1,2}秒)";
    public final static String ymdhmschineseRegex = "(\\d{4}年\\d{1,2}月\\d{1,2}日\\d{1,2}时\\d{1,2}分\\d{1,2}秒)";
    public final static String ymdchineseblankhmsRegex = "(\\d{4}年\\d{1,2}月\\d{1,2}日\\s\\d{1,2}:\\d{1,2}:\\d{1,2})";
    public final static String ymdchinesehmsRegex = "(\\d{4}年\\d{1,2}月\\d{1,2}日\\d{1,2}:\\d{1,2}:\\d{1,2})";
    public final static String ymdchineseblankhmRegex = "(\\d{4}年\\d{1,2}月\\d{1,2}日\\s\\d{1,2}:\\d{1,2})";
    public final static String ymdchinesehmRegex = "(\\d{4}年\\d{1,2}月\\d{1,2}日\\d{1,2}:\\d{1,2})";
    public final static String ymchineseRegex = "(\\d{4}年\\d{2}月)";
    public final static String ychineseRegex = "(\\d{4}年)";
    public final static String ymdSlash = "\\d{4}/\\d{1,2}/\\d{1,2}";
    public final static String ymSlash = "\\d{4}/\\d{1,2}";
    public final static String mdy2Slash = "\\d{1,2}/\\d{1,2}/\\d{2}";
    public final static String mdy4Slash = "\\d{1,2}/\\d{1,2}/\\d{4}";
    public final static String y_m_d = "\\d{4}-\\d{1,2}-\\d{1,2}";
    public final static String y_m = "\\d{4}-\\d{1,2}";
    public final static String y = "\\d{4}";
    public final static String ymd = "\\d{6,8}";
    public final static String ym = "\\d{5,6}";
    public final static String y_m_d_h_m_s = "\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}";
    public final static String y_m_d_h_m = "\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}";
    public final static String ymdhms = "\\d{9,14}";
    public final static String y_m_d_h_m_s_S = "\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}\\.\\d{1,3}";
    public final static String ymdchinese = "\\d{4}年\\d{1,2}月\\d{1,2}日";
    public final static String ymdblankhmschinese = "\\d{4}年\\d{1,2}月\\d{1,2}日\\s\\d{1,2}时\\d{1,2}分\\d{1,2}秒";
    public final static String ymdhmschinese = "\\d{4}年\\d{1,2}月\\d{1,2}日\\d{1,2}时\\d{1,2}分\\d{1,2}秒";
    public final static String ymdchineseblankhms = "\\d{4}年\\d{1,2}月\\d{1,2}日\\s\\d{1,2}:\\d{1,2}:\\d{1,2}";
    public final static String ymdchinesehms = "\\d{4}年\\d{1,2}月\\d{1,2}日\\d{1,2}:\\d{1,2}:\\d{1,2}";
    public final static String ymdchineseblankhm = "\\d{4}年\\d{1,2}月\\d{1,2}日\\s\\d{1,2}:\\d{1,2}";
    public final static String ymdchinesehm = "\\d{4}年\\d{1,2}月\\d{1,2}日\\d{1,2}:\\d{1,2}";
    public final static String ymchinese = "\\d{4}年\\d{2}月";
    public final static String ychinese = "\\d{4}年";

}