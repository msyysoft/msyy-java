package io.github.msyysoft.java.database;

import io.github.msyysoft.java.utiltools.Des3Util;

public class BasicDataSource extends org.apache.commons.dbcp.BasicDataSource {
    @Override
    public synchronized void setUrl(String url) {
        try {
            super.setUrl(Des3Util.decode(url));
        }catch (Exception e){
            super.setUrl(url);
        }
    }

    @Override
    public void setUsername(String username) {
        try {
            super.setUsername(Des3Util.decode(username));
        }catch (Exception e){
            super.setUsername(username);
        }
    }

    @Override
    public void setPassword(String password) {
        try {
            super.setPassword(Des3Util.decode(password));
        }catch (Exception e){
            super.setPassword(password);
        }
    }


}
