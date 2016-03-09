package com.prachi.project.keywordDriven.readXml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

/**
 * Created by prachi a. pawaskar on 11/30/2015.
 */

//Used to unmarshall/marshall API test scripts present under package "apiTestScripts"
@XmlRootElement
public class apiCase {

    String uri,assertStatus, assertResponse,methodType,postType,requestFile;
    ArrayList<String> assertResponseMultiple;

    @XmlElement
    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    @XmlElement
    public void setassertStatus(String assertStatus) {
        this.assertStatus = assertStatus;
    }

    public String getassertStatus() {
        return assertStatus;
    }

    @XmlElement
    public void setassertResponse(String assertResponse) {
        this.assertResponse = assertResponse;
    }

    public String getassertResponse() {
        return assertResponse;
    }

    @XmlElement
    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }

    public String getMethodType() {
        return methodType;
    }

    @XmlElement
    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getPostType() {
        return postType;
    }

    @XmlElement
    public void setRequestFile(String requestFile) {
        this.requestFile = requestFile;
    }

    public String getRequestFile() {
        return requestFile;
    }

    @XmlElement
    public void setassertResponseMultiple(ArrayList<String> assertResponseMultiple) {
        this.assertResponseMultiple = assertResponseMultiple;
    }

    public ArrayList<String> getassertResponseMultiple() {
        return assertResponseMultiple;
    }



}
