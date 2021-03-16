package com.wisdom.web.entity.cn.gov.saac.standards.erm;

/**
 * Created by SunK on 2020/7/8 0008.
 */

import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author lhh 标准化处理类
 */
public final class StandardHandler {

    private static StandardHandler uniqueInstance;

    private static List<String> fromlist;

    private static List<String> tolist;

    private static HashMap<Character, Character> numberfs;

    private static String yearFs = "年|\\.|\\-|\\||．|。|－";

    private static String monthFs = "月|\\.|\\-|\\||．|。|－";

    private static String dayFs = "日";

    private static String[] formats = {"yyyy", "yyyyMM", "yyyyMMdd"};//目标日期格式

    private static String[] altFormats = {"yyyyMMdd", "yyyyMM", "yyyy"};//标准化日期格式

    private static String bracketFs_left = "\\(|\\[|\\\"|'|\\{|﹛|‹|<|〈|（|【|〖|《|﹝|［|｛|＜|「|『";

    private static String bracketFs_right = "\\)|\\]|\\\"|'|\\}|﹜|›|>|〉|）|】|〗|》|﹞|］|｝|＞|」|』";

    private static String bracket_left = "〔";

    private static String bracket_right = "〕";

    private static void initNumberFs() {
        // 初始化映射值
        if (numberfs == null)
            numberfs = new HashMap<Character, Character>();
        numberfs.clear();
        numberfs.put('零', '0');
        numberfs.put('o', '0');
        numberfs.put('O', '0');
        numberfs.put('〇', '0');
        numberfs.put('一', '1');
        numberfs.put('二', '2');
        numberfs.put('三', '3');
        numberfs.put('四', '4');
        numberfs.put('五', '5');
        numberfs.put('六', '6');
        numberfs.put('七', '7');
        numberfs.put('八', '8');
        numberfs.put('九', '9');
        numberfs.put('０', '0');
        numberfs.put('○', '0');
        numberfs.put('ο', '0');
        numberfs.put('ｏ', '0');
        numberfs.put('Ο', '0');
        numberfs.put('Ｏ', '0');
        numberfs.put('１', '1');
        numberfs.put('２', '2');
        numberfs.put('３', '3');
        numberfs.put('４', '4');
        numberfs.put('５', '5');
        numberfs.put('６', '6');
        numberfs.put('７', '7');
        numberfs.put('８', '8');
        numberfs.put('９', '9');
        numberfs.put('壹', '1');
        numberfs.put('贰', '2');
        numberfs.put('叁', '3');
        numberfs.put('肆', '4');
        numberfs.put('伍', '5');
        numberfs.put('陆', '6');
        numberfs.put('柒', '7');
        numberfs.put('捌', '8');
        numberfs.put('玖', '9');
    }

    static {
        initNumberFs();
    }

    private StandardHandler() {
        int len;
        Element element;
        InputStream finput = null;
        fromlist = new ArrayList<String>();
        tolist = new ArrayList<String>();
        try {
            finput = getClass().getResourceAsStream("/DateExchange.xml");
            XPath xpath = XPath.newInstance("info");
            List list = xpath.selectNodes(new SAXBuilder().build(finput));
            Element root = (Element) list.get(0);
            List children = root.getChildren("altFormat");
            len = children.size();
            for (int i = 0; i < len; i++) {
                element = (Element) children.get(i);
                if (element.getValue().trim().length() > 0) {
                    fromlist.add(element.getChild("from").getText());
                    tolist.add(element.getChild("to").getText());
                }
            }

            element = root.getChild("formats");
            if (element != null) {
                children = element.getChildren("format");
                len = children.size();
                if (len > 0) {
                    formats = new String[len];
                    for (int i = 0; i < len; i++)
                        formats[i] = ((Element) children.get(i)).getText();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized StandardHandler getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new StandardHandler();
        }
        return uniqueInstance;
    }

    public String getDateExchange(String dateStr) {
        dateStr = toNumber(dateStr);
        for (int i = 0; i < fromlist.size(); i++) {
            try {
                SimpleDateFormat df = new SimpleDateFormat(fromlist.get(i));
                Date cDate = df.parse(dateStr);
                df = new SimpleDateFormat(tolist.get(i));
                return df.format(cDate);
            } catch (Exception e) {
            }
        }
        return null;
    }

    private char convertChar(char c, HashMap<Character, Character> fs) {
        return fs.containsKey(c) ? fs.get(c) : c;
    }

    private boolean isNSN(char c, HashMap<Character, Character> fs) {
        boolean nsn = !(c >= '0' && c <= '9');
        if (nsn) {
            char t = convertChar(c, fs);
            nsn = t >= '0' && t <= '9';
        }
        return nsn;
    }

    private boolean isDate(String dateStr) {
        boolean isDate = false;
        if (dateStr != null && dateStr.charAt(0) != '0') {
            String newDateStr = null;
            SimpleDateFormat sdf = null;
            for (String f : altFormats) {
                if (f.length() == dateStr.length()) {
                    sdf = new SimpleDateFormat(f);
                    try {
                        newDateStr = sdf.format(sdf.parse(dateStr));
                    } catch (Exception e) {}
                    if (dateStr.equals(newDateStr)) {
                        isDate = true;
                        break;
                    }
                }
            }
        }
        return isDate;
    }

    private String toNumber(String dateStr) {
        String newDateStr = "";
        HashMap<Character, Character> fs = numberfs;

        for (int i = 0, l = dateStr.length(); i < l; ++i) {
            char d = convertChar(dateStr.charAt(i), fs);
            if (d == '十') {
                if (isNSN(dateStr.charAt(i - 1), fs)) {
                    if (isNSN(dateStr.charAt(i + 1), fs)) {
                        newDateStr += "";
                    } else {
                        newDateStr += "0";
                    }
                } else {
                    if (isNSN(dateStr.charAt(i + 1), fs)) {
                        newDateStr += "1";
                    } else {
                        newDateStr += "10";
                    }
                }
            } else if (d == '廿') {
                if (isNSN(dateStr.charAt(i + 1), fs)) {
                    newDateStr += "2";
                } else {
                    newDateStr += "20";
                }
            } else if (!Pattern.matches("\\s|　", String.valueOf(d))) {
                newDateStr += d;
            }
        }
        return newDateStr;
    }

    private String standardDate(String dateStr) {
        if (!isDate(dateStr)){
            dateStr = toNumber(dateStr);
            String newDateStr = "";
            for (int i = 0, l = dateStr.length(); i < l; ++i) {
                char d = dateStr.charAt(i);
                if (Pattern.matches(yearFs, String.valueOf(d)) && newDateStr.length() <= 4) {
                    if (newDateStr.length() < 4)
                        newDateStr = String.format("%0" + (4 - newDateStr.length()) + "d", Integer.parseInt(newDateStr));
                } else if (Pattern.matches(monthFs, String.valueOf(d)) && newDateStr.length() <= 6) {
                    if (newDateStr.length() == 5)
                        newDateStr = newDateStr.substring(0, 4) + "0" + newDateStr.charAt(4);
                } else if (Pattern.matches(dayFs, String.valueOf(d)) && newDateStr.length() <= 8) {
                    if (newDateStr.length() == 7)
                        newDateStr = newDateStr.substring(0, 6) + "0" + newDateStr.charAt(6);
                } else {
                    newDateStr += d;
                }
            }

            dateStr = newDateStr;

            if (dateStr.length() == 5) {
                dateStr = dateStr.substring(0, 4) + "0" + dateStr.charAt(4);
            } else if (dateStr.length() == 6) {
                if (!isDate(dateStr)) {
                    dateStr = dateStr.substring(0, 4) + "0" + dateStr.charAt(4) + "0" + dateStr.charAt(5);
                }
            } else if (dateStr.length() == 7) {
                newDateStr = dateStr.substring(0, 4) + "0" + dateStr.charAt(4) + dateStr.substring(5, 7);
                if (isDate(newDateStr)) {
                    if (!isDate(dateStr.substring(0, 6) + "0" + dateStr.charAt(6)))
                        dateStr = newDateStr;
                } else {
                    newDateStr = dateStr.substring(0, 6) + "0" + dateStr.charAt(6);
                    if (isDate(newDateStr)) {
                        dateStr = newDateStr;
                    }
                }
            }
        }
        return dateStr;
    }

    private String formatDate(String dateStr) {
        String newDateStr = null;
        if (isDate(dateStr)) {
            Date date = null;
            SimpleDateFormat sdf = null;
            for (int i = 0, len = altFormats.length; i < len; ++i) {
                sdf = new SimpleDateFormat(altFormats[i]);
                try {
                    date = sdf.parse(dateStr);
                    if (date != null) {
                        sdf = new SimpleDateFormat(formats[i]);
                        newDateStr = sdf.format(date);
                        break;
                    }
                } catch (Exception e) {}
            }
        }
        return newDateStr;
    }

    private Date parseDate(String dateStr) {
        Date date = null;
        if (isDate(dateStr)) {
            if (dateStr.length() == 4) {
                dateStr += "12";
            }
            if (dateStr.length() == 6) {
                int year = Integer.parseInt(dateStr.substring(0, 4));
                int month = Integer.parseInt(dateStr.substring(4, 6));
                if (month == 2) {
                    dateStr += ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
                            ? "29" : "28";
                } else if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
                    dateStr += "31";
                } else {
                    dateStr += "30";
                }
            }
            try {
                date = new SimpleDateFormat(altFormats[0]).parse(dateStr);
            } catch (ParseException e) {
                date = null;
            }
        }
        return date;
    }

    public String toDate(String dateStr) {
        dateStr = standardDate(dateStr);
        return formatDate(dateStr);
    }

    public Date toDateFull(String dateStr) {
        dateStr = standardDate(dateStr);
        return parseDate(dateStr);
    }

    public String toFileno(String fileno) {
        fileno = fileno.replaceAll(bracketFs_left, bracket_left);
        fileno = fileno.replaceAll(bracketFs_right, bracket_right);
        return fileno;
    };

    public static void main(String[] args) throws Exception {
        StandardHandler ud = StandardHandler.getInstance();

        System.out.println(ud.toFileno("文件变{二零零五]re"));

        System.out.println(ud.toDate("2011331"));
        System.out.println(ud.toDate("20111"));


//		Calendar c1 = Calendar.getInstance();
//		c1.setTime(ud.toDateFull("一九八一年五月二十四日"));
//		c1.set(Calendar.YEAR, c1.get(Calendar.YEAR) + 30);
//		c1.set(Calendar.HOUR_OF_DAY, 0);
//		c1.set(Calendar.MINUTE, 0);
//		c1.set(Calendar.SECOND, 0);
//		System.out.println(new SimpleDateFormat(altFormats[0]).format(c1.getTime()));
//
//		Calendar c2 = Calendar.getInstance();
//		c2.setTime(new Date());
//		c2.set(Calendar.HOUR_OF_DAY, 0);
//		c2.set(Calendar.MINUTE, 0);
//		c2.set(Calendar.SECOND, 0);
//		System.out.println(new SimpleDateFormat(altFormats[0]).format(c2.getTime()));
//
//		System.out.println(c2.after(c1));

//		System.out.println(new SimpleDateFormat(altFormats[0]).format(new Date()));
//		System.out.println(new SimpleDateFormat(altFormats[0]).format(ud.toDateFull("二零零五年")));
//		System.out.println(new SimpleDateFormat(altFormats[0]).format(ud.toDateFull("二零零五年二月")));
//		System.out.println(new SimpleDateFormat(altFormats[0]).format(ud.toDateFull("二零零四年二月")));
//
//		System.out.println(ud.getDateExchange("0997.01.01"));
//		System.out.println(ud.toDate("0997.01.01"));
//
//
//		System.out.println(ud.getDateExchange("二零零五年"));
//		System.out.println(ud.getDateExchange("二零零五年陆月"));
//
//		System.out.println(ud.toDate("二零零五年"));
//		System.out.println(ud.toDate("二零零五年陆月"));
//
//		System.out.println(ud.toDate("二零零四年二月二十九日"));
//		System.out.println(ud.toDate("二零零五年二月二十九日"));
//
//		System.out.println(ud.toDate("1997.01.01"));
//		System.out.println(ud.toDate("1997.1.1"));
//		System.out.println(ud.toDate("1997.1.31"));
//		System.out.println(ud.toDate("1997.10.1"));
//		System.out.println(ud.toDate("1997.10.31"));
//
//		System.out.println(ud.toDate("１９９７．０１．０１"));
//		System.out.println(ud.toDate("１９９７．１．１"));
//		System.out.println(ud.toDate("１９９７．１．３１"));
//		System.out.println(ud.toDate("１９９７．１０．１"));
//		System.out.println(ud.toDate("１９９７．１０．３１"));
//
//		System.out.println(ud.toDate("1997-01-01"));
//		System.out.println(ud.toDate("1997-1-1"));
//		System.out.println(ud.toDate("1997-1-31"));
//		System.out.println(ud.toDate("1997-10-1"));
//		System.out.println(ud.toDate("1997-10-31"));
//
//		System.out.println(ud.toDate("１９９７－０１－０１"));
//		System.out.println(ud.toDate("１９９７－１－１"));
//		System.out.println(ud.toDate("１９９７－１－３１"));
//		System.out.println(ud.toDate("１９９７－１０－３１"));
//		System.out.println(ud.toDate("１９９７－１０－１"));
//
//		System.out.println(ud.toDate("2000年1月1日"));
//		System.out.println(ud.toDate("2000年01月01日"));
//		System.out.println(ud.toDate("2000年1月31日"));
//		System.out.println(ud.toDate("2000年10月1日"));
//		System.out.println(ud.toDate("2000年12月31日"));
//
//		System.out.println(ud.toDate("２０００年０１月０１日"));
//		System.out.println(ud.toDate("２０００年１月１日"));
//		System.out.println(ud.toDate("２０００年１月３１日"));
//		System.out.println(ud.toDate("２０００年１０月１日"));
//		System.out.println(ud.toDate("２０００年１２月３１日"));
//
//		System.out.println(ud.toDate("一九九七年一月一日"));
//		System.out.println(ud.toDate("一九九七年一月三十一日"));
//		System.out.println(ud.toDate("一九九七年十月一日"));
//		System.out.println(ud.toDate("一九九七年十二月三十一日"));
//		System.out.println(ud.toDate("一九九七年一月廿一日"));
//
//		System.out.println(ud.toDate("二000年一月一日"));
//		System.out.println(ud.toDate("二００一年一月三十一日"));
//		System.out.println(ud.toDate("二ｏｏ一年十二月三十一日"));
//		System.out.println(ud.toDate("二ＯＯ一年九月三十日"));
//		System.out.println(ud.toDate("二○○○年十二月三十一日"));
//		System.out.println(ud.toDate("二οοο年十二月三十一日"));
//		System.out.println(ud.toDate("二ΟΟΟ年十二月三十一日"));
    }
}
