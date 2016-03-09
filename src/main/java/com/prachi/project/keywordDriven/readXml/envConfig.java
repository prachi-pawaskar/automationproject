package com.prachi.project.keywordDriven.readXml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by prachi a. pawaskar on 11/30/2015.
 */

//Used to unmarshall/marshall env config file present under package "env/{envPackage}/"
@XmlRootElement
public class envConfig {

    String baseUrl, uiUrl, username, password;

    @XmlElement
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    @XmlElement
    public void setUiUrl(String uiUrl) {
        this.uiUrl = uiUrl;
    }

    public String getUiUrl() {
        return uiUrl;
    }

    @XmlElement
    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @XmlElement
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

}
