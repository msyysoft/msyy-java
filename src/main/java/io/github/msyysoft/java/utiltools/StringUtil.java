package io.github.msyysoft.java.utiltools;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    /**
     * 获取末尾字符串
     *
     * @param uri
     * @param indexString
     * @return
     */
    public static String getLastSubString(String uri, String indexString) {
        if (!StringUtils.isEmpty(uri) && !StringUtils.isEmpty(indexString)) {
            int index = uri.lastIndexOf(indexString);
            return index + indexString.length() > uri.length() ? uri : uri.substring(index + indexString.length());
        } else {
            return uri;
        }
    }

    /**
     * 获取GUID
     */
    public static String getGUID() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    /**
     * 判断字符串是否为数字
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    public static String getNumberSTR(String str) {
        if (StringUtils.isEmpty(str))
            return "";
        return str.replaceAll("[^0-9]", "");
    }

    /**
     * 生成N位数字验证码
     *
     * @param
     * @return
     */
    public static String genRandomNumber(int n) {
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < n; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 生成6位数字验证码
     *
     * @param
     * @return
     */
    public static String genRandomNumber6() {
        return genRandomNumber(6);
    }

    /**
     * 生成固定长度的随机字符串
     *
     * @param length 生成字符串的长度
     * @return
     */
    public static String generateRandomString(int length) {
        return generateRandomString(length,true,true,true);
    }

    /**
     * 生成固定长度的随机字符串
     *
     * @param length 生成字符串的长度
     * @return
     */
    public static String generateRandomString(int length,boolean useNumber,boolean userLower,boolean userUpper) {
        StringBuffer baseBuf = new StringBuffer();
        if (useNumber)
            baseBuf.append("0123456789");
        if (userLower)
            baseBuf.append("abcdefghijklmnopqrstuvwxyz");
        if (userUpper)
            baseBuf.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        String base = baseBuf.toString();
        if (base.length()==0)
            return "";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }


    /**
     * 验证手机号是否正确
     *
     * @param mobiles
     * @return
     */
    public static boolean isPhoneNO(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }


    /**
     * 当以不确定的空格为分隔符时使用此方法
     *
     * @param source
     * @return
     */
    public static String[] splitBySpace(String source) {
        return source.trim().replaceAll("\\s+", " ").split(" ");
    }

    /**
     * 提取url的host地址
     *
     * @param url
     * @return
     */
    public static String getUrlHost(String url) {
        if (!StringUtils.isEmpty(url)) {
            URI uri = URI.create(url);
            try {
                //URI(String scheme, String userInfo, String host, int port, String path, String query,String fragment)
                return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), null, null, null).toString();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * IP Address
     *
     * @param request
     * @return
     */
    public static String getIPAddress(HttpServletRequest request) {
        String ip = null;

        //X-Forwarded-For：Squid 服务代理
        String ipAddresses = request.getHeader("X-Forwarded-For");

        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //Proxy-Client-IP：apache 服务代理
            ipAddresses = request.getHeader("Proxy-Client-IP");
        }

        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //WL-Proxy-Client-IP：weblogic 服务代理
            ipAddresses = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //HTTP_CLIENT_IP：有些代理服务器
            ipAddresses = request.getHeader("HTTP_CLIENT_IP");
        }

        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //X-Real-IP：nginx服务代理
            ipAddresses = request.getHeader("X-Real-IP");
        }

        //有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
        if (ipAddresses != null && ipAddresses.length() != 0) {
            ip = ipAddresses.split(",")[0];
        }

        //还是不能获取到，最后再通过request.getRemoteAddr();获取
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 解析前台获得的交互数据
     *
     * @param request
     * @return
     * @throws IOException
     */
    public static String getJsonStrFromRequest(HttpServletRequest request) throws IOException {
        String resultStr = "";
        String readLine;
        StringBuffer sb = new StringBuffer();
        BufferedReader responseReader = null;
        OutputStream outputStream = null;
        try {
            responseReader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
            while ((readLine = responseReader.readLine()) != null) {
                sb.append(readLine).append("\n");
            }
            responseReader.close();
            resultStr = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
        return resultStr;
    }

    private static Pattern linePattern = Pattern.compile("_(\\w)");
    /**
     * 下划线转驼峰
     * @param str
     * @return
     */
    public static String lineToHump(String str) {
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
