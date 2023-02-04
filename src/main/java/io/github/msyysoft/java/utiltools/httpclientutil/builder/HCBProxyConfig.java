package io.github.msyysoft.java.utiltools.httpclientutil.builder;

/**
 * 只能在使用HttpClientUtil之前调用一次
 */
public class HCBProxyConfig {
    private static String proxy_ip;
    private static int proxy_port;
    private static boolean proxy_switch;

    static {
        setProxy_ip("127.0.0.1");
        setProxy_port(8888);
        setProxy_switch(false);
    }

    public static String getProxy_ip() {
        return proxy_ip;
    }

    public static void setProxy_ip(String proxy_ip) {
        HCBProxyConfig.proxy_ip = proxy_ip;
    }

    public static int getProxy_port() {
        return proxy_port;
    }

    public static void setProxy_port(int proxy_port) {
        HCBProxyConfig.proxy_port = proxy_port;
    }

    public static boolean isProxy_switch() {
        return proxy_switch;
    }

    public static void setProxy_switch(boolean proxy_switch) {
        HCBProxyConfig.proxy_switch = proxy_switch;
    }
}
