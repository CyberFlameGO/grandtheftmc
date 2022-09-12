package net.grandtheftmc.Bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Settings {
    private final Map<String, String> servers = new HashMap<>();
    private Configuration config;
    private Configuration mysqlConfig;
    private Configuration permsConfig;
    private Configuration motdConfig;
    private Configuration helpConfig;
    private Configuration gtmConfig;
    private String motd;
    private String host = "error";
    private String port = "error";
    private String database = "error";
    private String user = "error";
    private String password = "error";

    Settings() {
        this.servers.clear();
        Set<String> s = ProxyServer.getInstance().getServers().keySet();
        for (String c : s)
            this.servers.put(c, c);
        this.servers.put("creative", "creative1");
        this.servers.put("crea", "creative1");
        this.servers.put("gtm", "gtm1");
        this.servers.put("legacy", "legacygtm");
        this.servers.put("gtmlegacy", "legacygtm");
        this.servers.put("oldgtm", "legacy");
        this.servers.put("gliders", "gliders1");
        this.servers.put("dev", "gtm0");
        this.servers.put("vice","vice1");
    }

    public Configuration getMySQLConfig() {
        return this.mysqlConfig;
    }

    public void setMySQLConfig(Configuration mysqlConfig) {
        this.mysqlConfig = mysqlConfig;
    }

    public Configuration getHelpConfiguration(){
        return this.helpConfig;
    }

    public void setHelpConfig(Configuration config) {
        this.helpConfig = config;
    }

    public Map<String, String> getServers() {
        return this.servers;
    }

    public Configuration getPermsConfig() {
        return this.permsConfig;
    }

    public void setPermsConfig(Configuration permsConfig) {
        this.permsConfig = permsConfig;
    }

    public String getMotd() {
        return Utils.f(this.motd);
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }

    public Configuration getMotdConfig() {
        return this.motdConfig;
    }

    public void setMotdConfig(Configuration motdConfig) {
        this.motdConfig = motdConfig;
    }

    public Configuration getGtmConfig() {
        return this.gtmConfig;
    }

    public void setGtmConfig(Configuration gtmConfig) {
        this.gtmConfig = gtmConfig;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return this.port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDatabase() {
        return this.database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}