package com.prachi.project.keywordDriven.lib;

import com.prachi.project.keywordDriven.enumPackage.supportedSiteLocale;
import com.prachi.project.keywordDriven.objRepo.deLang_objectRepo;
import com.prachi.project.keywordDriven.objRepo.enLang_objectRepo;
import com.prachi.project.keywordDriven.objRepo.frLang_objectRepo;
import com.prachi.project.keywordDriven.objRepo.objectRepository;
import com.prachi.project.keywordDriven.readXml.apiCase;
import com.relevantcodes.extentreports.LogStatus;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

/**
 * Created by prachi a. pawaskar on 11/30/2015.
 */
public class readTestCase  extends base {

    //Logger defined for class, logs created as per log4j.properties
    private static Logger log = Logger.getLogger(readTestCase.class);

    //Reference to object repository.
    public static objectRepository or = new objectRepository();
    public static enLang_objectRepo comOr = new enLang_objectRepo();
    public static deLang_objectRepo deOr = new deLang_objectRepo();
    public static frLang_objectRepo frOr = new frLang_objectRepo();

    // Generate random string
    public static String getRandomString()  {
        final class RandomGenerator {
            private SecureRandom random = new SecureRandom();

            public String randomString() {
                return new BigInteger(130, random).toString(32);
            }
        }
        return new RandomGenerator().randomString();
    }

    static String randomText = "random_"+ getRandomString();

    public static Boolean runningReadAgain = true;


    //Method reads test scripts, asserts and add entry to reports - API
    //Active Test script name is passed to this method
    public static void readApiCases(String testCaseFile)  throws Exception {

        //Prints the test case name and info to report
        etest = extent.startTest("API Test Script: "+ testCaseFile);
        etest.log(LogStatus.INFO, "Test Script '" + testCaseFile + "' is running.");


        //Get test script data to read.
        String resourceFilePath = "src/test/java/com/prachi/project/keywordDriven/apiTestScripts/" + testCaseFile + ".xml";
        log.info("Test Script relative path: " + resourceFilePath);
        String resourceURL = new File(resourceFilePath).getAbsolutePath();
        log.info("Test Script obsolete path: " + resourceURL);

        String methodType = null,postType = null;

        try {

            //Parsing the test script
            File file = new File(resourceURL);
            JAXBContext jaxbContext = JAXBContext.newInstance(apiCase.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            apiCase apiCaseClass = (apiCase) jaxbUnmarshaller.unmarshal(file);

            //Reading method type
            try {
                methodType = apiCaseClass.getMethodType();
                log.info("Reading method type: " + methodType);
                if(methodType.isEmpty()){
                    log.error("<methodType> is null.");
                    etest.log(LogStatus.ERROR, "Error: methodType is null.");
                    etest.log(LogStatus.FAIL, "Failed: As methodType is null.");
                }
            }catch (Exception e){
                log.error(e);
                log.error("<methodType> is null.");
                etest.log(LogStatus.ERROR, "Error: methodType is null.");
                etest.log(LogStatus.FAIL, "Failed: As methodType is null.");
            }


            //Reading uri
            try {
                URI = apiCaseClass.getUri();
                log.info("Reading API URI: " + URI);
                if(URI.isEmpty()){
                    log.error("<uri> is null.");
                    etest.log(LogStatus.ERROR, "Error: uri is null.");
                    etest.log(LogStatus.FAIL, "Failed: As uri is null.");
                }
            }catch (Exception e){
                log.error(e);
                log.error("<uri> is null.");
                etest.log(LogStatus.ERROR, "Error: uri is null.");
                etest.log(LogStatus.FAIL, "Failed: uri is null.");
            }

            //Reading expected status
            try {
                assertStatus = apiCaseClass.getassertStatus();
                log.info("Reading assertStatus: " + assertStatus);
                if(assertStatus.isEmpty()){
                    log.error("<assertStatus> is null.");
                    etest.log(LogStatus.ERROR, "Error: assertStatus is null.");
                    etest.log(LogStatus.FAIL, "Failed: assertStatus is null.");
                }
            }catch (Exception e){
                log.error(e);
                log.error("<assertStatus> is null.");
                etest.log(LogStatus.ERROR, "Error: assertStatus is null.");
                etest.log(LogStatus.FAIL, "Failed: assertStatus is null.");
            }

            //Hitting the api
            try {
                if(methodType.isEmpty() || methodType.equalsIgnoreCase("get")) {
                    apiLogicBase.doGet(baseURL, URI, null);
                }
                else if(methodType.equalsIgnoreCase("post")){
                    //Reading post type
                        postType = apiCaseClass.getPostType();
                        log.info("Reading post data type: " + postType);
                        if(postType.isEmpty()){
                            log.error("<postType> is null.");
                            etest.log(LogStatus.ERROR, "Error: postType is null.");
                            etest.log(LogStatus.FAIL, "Failed: As postType is null.");
                        }
                    //Reading postData
                    String requestResource = "src/main/resources/apiRequest/"+apiCaseClass.getRequestFile()+"."+postType;
                    File requestResourceFile = new File(requestResource);
                    String postData = FileUtils.readFileToString(requestResourceFile);

                    //Hitting doPost api
                    apiLogicBase.doPost(baseURL, URI, null,postData,postType.toLowerCase());
                }
            } catch (Exception e){
                log.error(e);
                log.error(postType.toUpperCase() + " api is unsuccessful.");
                etest.log(LogStatus.ERROR, postType.toUpperCase() + " api throwing error.");
                etest.log(LogStatus.FAIL, postType.toUpperCase() + " api is unsuccessful.");
            }

            //Logging test expected and actual
            log.info("Following assertion active: Checking apache status");
            log.info("Expected: " + assertStatus);
            log.info("Actual: " + apiLogicBase.apiStatus);

            //Performing assertions
            Boolean apiStatusMatch = false;
            apiStatusMatch = assertStatus.equalsIgnoreCase(apiLogicBase.apiStatus);
                if (apiStatusMatch == true) {
                    log.info("Test Passed: Apache status do match");
                    etest.log(LogStatus.PASS, "Apache status code check", "Expected apache code: " + "\'" + assertStatus + "\'" + " \nActual: " + "\'" + apiLogicBase.apiStatus + "\'");
                }
                else {
                    log.info("Test Failed: Apache status do not match");
                    etest.log(LogStatus.FAIL, "Apache status code check", "Expected apache code: " + "\'" + assertStatus + "\'" + " \nActual: " + "\'" + apiLogicBase.apiStatus + "\'");
                }


            //Reading <assertResponse> check in test scripts
            String responseToAssert = apiCaseClass.getassertResponse();

            //This is <assertResponse> check
            if (responseToAssert == null || responseToAssert.isEmpty()) {
                log.info("Tag: assertResponse not present hence no API response check active!");
            } else {

                //Reading expected API response
                assertResponse = apiCaseClass.getassertResponse();

                //Logging test expected and actual
                log.info("Following assertion active: Checking API response, tag: <assertResponse> without considering spaces");
                log.info("Expected: " + assertResponse.replaceAll("\\s+",""));
                log.info("Actual: " + apiLogicBase.apiResponse.replaceAll("\\s+",""));

                //Performing assertions
                Boolean apiResponseMatch = false;
                apiResponseMatch = assertResponse.replaceAll("\\s+","").equals(apiLogicBase.apiResponse.replaceAll("\\s+",""));
                if(apiResponseMatch == true)
                {
                    log.info("Test Passed: API response does match.");
                    etest.log(LogStatus.PASS, "Single API response check", "Expected response: " + "\'" + assertResponse + "\'" + " present in actual response." );
                }else  {
                    log.info("Test Failed: API response does not match.");
                    etest.log(LogStatus.FAIL, "Single API response check", "Expected response: " + "\'" + assertResponse + "\'" + " not present in actual response." );
                }
            }


            // Reading <assertResponseMultiple> check in test scripts
            assertResponseMultipleVar = apiCaseClass.getassertResponseMultiple();

            //This is <assertResponseMultiple> check
            if (assertResponseMultipleVar == null || assertResponseMultipleVar.isEmpty()) {
                log.info("Tag: assertResponseMultiple not present hence no multiple response asserts active!");
            } else {
                for (int i = 0; i < assertResponseMultipleVar.size(); i++) {

                        //Logging test expected and actual
                        log.info("Following assertion active: Checking API multiple response, tag: <assertResponseMultiple>");
                        log.info("Expected in response: " + assertResponseMultipleVar.get(i));
                        log.info("Actual in response: " + apiLogicBase.apiResponse);
                        Boolean apiMultiResponseMatch = false;
                        apiMultiResponseMatch = apiLogicBase.apiResponse.contains(assertResponseMultipleVar.get(i));
                        if(apiMultiResponseMatch == true) {
//                        Assert.assertTrue(apiLogicBase.apiResponse.contains(assertResponseMultipleVar.get(i)), "API response does not match: ");
                            etest.log(LogStatus.PASS, "API response check", "Expected response: " + "\'" + assertResponseMultipleVar.get(i) + "\'" + " present in actual response.");
                            log.info("Test Passed:  API response does match");
                        }else {
                            log.info("Test Failed: API response does not match");
                            etest.log(LogStatus.FAIL, "API response check", "Expected response: " + "\'" + assertResponseMultipleVar.get(i) + "\'" + " not present in actual response.");
                        }
                    }
                }

        } catch (JAXBException e) {
            e.printStackTrace();
        }
        extent.endTest(etest);
        extent.flush();

    }


    //Method to link object repo lib\objectRepository.java to <objectType> from test scripts
    @SuppressWarnings("rawtypes")
    public static Object getValueOf(Object clazz, String lookingForValue)
            throws Exception {
        Field field = clazz.getClass().getField(lookingForValue);
        Class clazzType = field.getType();
        if (clazzType.toString().equals("double"))
            return field.getDouble(clazz);
        else if (clazzType.toString().equals("int"))
            return field.getInt(clazz);
        // else other type ...
        // and finally
        return field.get(clazz);
    }


    //Method to check if modal alert is displayed or not.
    public static boolean isAlertPresent(String alertType) {
        Boolean alertExist = false;
        if (alertType.equalsIgnoreCase("modal")) {
            try {
                driver.findElement(By.className("modal-content")).isDisplayed();
                log.info("Modal alert is displayed.");
                alertExist = true;
            } catch (NoAlertPresentException Ex) {
                log.info("Modal alert is not displayed.");
                alertExist = false;
            }
        } else if (alertType.equalsIgnoreCase("windows")) {
            try {
                driver.switchTo().alert();
                log.info("Windows alert is displayed.");
                alertExist = true;
            } catch (NoAlertPresentException Ex) {
                log.info("Windows alert is not displayed.");
                alertExist = false;
            }
        }
        return alertExist;
    }

    //Method to take screenshot for report
    public static String takeScreenshot(){
        String imageLocation = null;
        String imageName = "reportImage_" + getRandomString();
        // Take screenshot and store as a file format
        File src= ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        try {
            //Get test script data to read.
            String resourceImageFilePath = "reports/reportImages/";
            String resourceImageURL = new File(resourceImageFilePath).getAbsolutePath();
            // now copy the  screenshot to desired location using copyFile //method
            FileUtils.copyFile(src, new File(resourceImageURL +"\\"+ imageName+".png"));
            imageLocation = resourceImageURL +"\\" + imageName+".png";
        }
        catch (IOException e)
        {
            log.error("Error in capturing screenshot: " + e);
        }
        return imageLocation;
    }

    //Method reads test scripts, asserts and add entry to reports - UI
    //Active Test script name is passed to this method
    public static void readUiCases(String packageName, String testCaseFile) throws Exception {

        //Prints the test case name and info to report
        if(runningReadAgain == true){
        etest = extent.startTest(packageName + "- UI Test Script: "+testCaseFile);
        etest.log(LogStatus.INFO, "Test Script: " +testCaseFile + " is running.");}



        //Get test script data to read.
        String resourceFilePath = "src/test/java/com/prachi/project/keywordDriven/"+packageName+"/" + testCaseFile + ".xml";
        log.info("Test Script relative path: " + resourceFilePath);
        String resourceURL = new File(resourceFilePath).getAbsolutePath();
        log.info("Test Script obsolete path: " + resourceURL);

        //Reading test scripts step by step
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = (Document) builder.parse(new File(resourceURL));
        NodeList nodeList = document.getDocumentElement().getChildNodes();


        //Perform activities for each step.
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;

                //Read tag
                String tag = elem.getTagName();
                log.info("Tag name: " + tag);

                //Read <type>
                String type = elem.getElementsByTagName("type")
                        .item(0).getChildNodes().item(0).getNodeValue();
                log.info("Type: " + type);

                String objectType = null,tagValue = null,value=null;
                if(type.equalsIgnoreCase("alert")||type.equalsIgnoreCase("windowAlert"))
                {   //Skip reading <objectType> and <value>
                }else{

                    //Read <objectType>
                    objectType = elem.getElementsByTagName("objectType")
                        .item(0).getChildNodes().item(0).getNodeValue();
                    log.info("ObjectType: " + objectType);

                    //Read <value>
                    tagValue = elem.getElementsByTagName("value")
                            .item(0).getChildNodes().item(0).getNodeValue();


                    // Setting value

                    //Checking if value needs to be overwritten or not
                    if(tagValue.equalsIgnoreCase("overwrite")){

                        //Overwrite the value.
                        log.info("Value will be overwritten!");
                        try {
                            value = elem.getElementsByTagName("overwriteValue")
                                    .item(0).getChildNodes().item(0).getNodeValue();

                            //Replace the <overwriteValue> config_alphaRandom to random
                            if (value.contains("config_alphaRandom")){
                                value = value.replace("config_alphaRandom",randomText);
                                log.info("Replaced Overwritten Value: " + value);
                            }
                        } catch (Exception e){
                            log.error(e);
                            log.error("<overwriteValue> cannot be empty if <value>overwrite</value>!");
                            etest.log(LogStatus.ERROR, "overwriteValue not present");
                            etest.log(LogStatus.FAIL, "overwriteValue not present");
                        }
                        log.info("Overwritten Value: " + value);


                        } else if (objectType.endsWith("TestScripts") || objectType.equalsIgnoreCase("wait")){
                            //Don't change the value
                            value = tagValue;
                        log.info("Value: " + value);
                        } else if(tag.equalsIgnoreCase("assertTag") && objectType.equalsIgnoreCase("title")){
                            //Don't map value to objectRepo, it will be mapped to lang repo
                        }
                        else
                        {
                        //Picking object type value from object repo
                        value = (String) readTestCase.getValueOf(or, tagValue);
                        log.info("Value: " + value);
                        }

                }

                //********************************
                //Performing actions based on tag
                //********************************

                //Steps to perform for <assertTag>
                if (tag.equalsIgnoreCase("assertTag")) {
                    Boolean isDisplayed = false;
                    // driver.findElement(By.className("")).isDisplayed();

                    //Assert based on <type> for VISIBLE
                    if (type.equalsIgnoreCase("visible")) {
                        //"By" based on <objectType> value
                        try {
                            if (objectType.equalsIgnoreCase("class")) {
                                isDisplayed = driver.findElement(By.className(value)).isDisplayed();
                            }
                            if (objectType.equalsIgnoreCase("xpath")) {
                                isDisplayed = driver.findElement(By.xpath(value)).isDisplayed();
                            }
                            if (objectType.equalsIgnoreCase("id")) {
                                isDisplayed = driver.findElement(By.id(value)).isDisplayed();
                            }
                            if (objectType.equalsIgnoreCase("tagName")) {
                                isDisplayed = driver.findElement(By.tagName(value)).isDisplayed();
                            }
                            if (objectType.equalsIgnoreCase("name")) {
                                isDisplayed = driver.findElement(By.name(value)).isDisplayed();
                            }
                            if (objectType.equalsIgnoreCase("linkText")) {
                                isDisplayed = driver.findElement(By.linkText(value)).isDisplayed();
                            }
                            if (objectType.equalsIgnoreCase("partialLinkText")) {
                                isDisplayed = driver.findElement(By.partialLinkText(value)).isDisplayed();
                            }
                            if (objectType.equalsIgnoreCase("css")) {
                                isDisplayed = driver.findElement(By.cssSelector(value)).isDisplayed();
                            }
                        } catch (Exception e){
                            log.error(e);
                            log.error("Error in visibility check for element.");
                            etest.log(LogStatus.ERROR, "Error in visibility check for element: " + value);
                            etest.log(LogStatus.FAIL, "Fail visibility check for element: " + value);
                        }

                        //Asertions

                        //Logging test expected and actual
                        log.info("Expected: \'" + value + "\' to be visible");

                            //Performing assertions
                        if (isDisplayed == true) {
                            log.info("Actual: \'" + value + "\' is visible");
                            log.info("Test Passed: Element visible");
                            etest.log(LogStatus.PASS, "Element Visibility check for " + "\'" + value + "\' , \n" + "\'" + value + "\'" + " is visible on UI");
                            }else
                         {
                            log.info("Actual: \'" + value + "\' is not visible");
                            log.info("Test Failed: Element not visible");
                            etest.log(LogStatus.FAIL, "Element Visibility check for " + "\'" + value + "\', \n" + "\'" +value +"\'" + " is not visible on UI" );
                            String image = etest.addScreenCapture(takeScreenshot());
                            etest.log(LogStatus.FAIL,"Image:" + image);
                        }
                    }


                    //Assert based on <type> for VISIBLE
                    if (type.equalsIgnoreCase("notVisible")) {
                        //Waiting for 1 second
                        Thread.sleep(1000);
                        //"By" based on <objectType> value
                        try {
                            if (objectType.equalsIgnoreCase("class")) {
                                isDisplayed = driver.findElement(By.className(value)).isDisplayed();
                            }
                            if (objectType.equalsIgnoreCase("xpath")) {
                                isDisplayed = driver.findElement(By.xpath(value)).isDisplayed();
                            }
                            if (objectType.equalsIgnoreCase("id")) {
                                isDisplayed = driver.findElement(By.id(value)).isDisplayed();
                            }
                            if (objectType.equalsIgnoreCase("tagName")) {
                                isDisplayed = driver.findElement(By.tagName(value)).isDisplayed();
                            }
                            if (objectType.equalsIgnoreCase("name")) {
                                isDisplayed = driver.findElement(By.name(value)).isDisplayed();
                            }
                            if (objectType.equalsIgnoreCase("linkText")) {
                                isDisplayed = driver.findElement(By.linkText(value)).isDisplayed();
                            }
                            if (objectType.equalsIgnoreCase("partialLinkText")) {
                                isDisplayed = driver.findElement(By.partialLinkText(value)).isDisplayed();
                            }
                            if (objectType.equalsIgnoreCase("css")) {
                                isDisplayed = driver.findElement(By.cssSelector(value)).isDisplayed();
                            }
                        } catch (Exception e){
                            //Do nothing, "isDisplayed()" will return error as we are checking non visibility for element.
                        }

                        //Assertions

                        //Logging test expected and actual
                        log.info("Expected: \'" + value + "\' not to be visible");

                        //Performing assertions
                        if (isDisplayed == false) {
                            log.info("Actual: \'" + value + "\' is not visible");
                            log.info("Test Passed: Element not visible");
                            etest.log(LogStatus.PASS, "Element non Visibility check for " + "\'" + value + "\' , \n" + "\'" + value + "\'" + " is not visible on UI");
                        }else
                        {
                            log.info("Actual: \'" + value + "\' is visible");
                            log.info("Test Failed: Element visible");
                            etest.log(LogStatus.FAIL, "Element non Visibility check for " + "\'" + value + "\', \n" + "\'" +value +"\'" + " is visible on UI" );
                            String image = etest.addScreenCapture(takeScreenshot());
                            etest.log(LogStatus.FAIL,"Image:" + image);
                        }
                    }

                    String visibleText = null;
                    //Assert based on <type> for VISIBLETEXT
                    if (type.equalsIgnoreCase("visibleText")) {

                        Boolean isTextDisplayed = false;

                        //Check <text> value
                        if (elem.getElementsByTagName("text").item(0).getChildNodes().item(0).getNodeValue() == null) {
                            log.error("<text> tag is empty.");
                            etest.log(LogStatus.ERROR, "Blank passed text passed to verify visibility for value: '" + value + "\'");
                            etest.log(LogStatus.FAIL, "Blank passed text passed to verify visibility for value: '" + value + "\'");
                        } else {

                            //Set visibleText value
                            visibleText = elem.getElementsByTagName("text").item(0).getChildNodes().item(0).getNodeValue();

                            //Replace <text> value if variable declared in test scripts
                            if (visibleText.equalsIgnoreCase("config_username")) {
                                visibleText = username;
                            } else if (visibleText.equalsIgnoreCase("config_password")) {
                                visibleText = password;
                            } else if(visibleText.equalsIgnoreCase("config_alphaRandom")){
                                visibleText = randomText;
                            }

                                //Change the title as per locale
                                if(SITELOCALE.equalsIgnoreCase(supportedSiteLocale.de.name())){
                                    visibleText = (String) readTestCase.getValueOf(deOr, visibleText);}
                                else if(SITELOCALE.equalsIgnoreCase(supportedSiteLocale.fr.name())){
                                    visibleText = (String) readTestCase.getValueOf(frOr, visibleText);}
                                else if(SITELOCALE.equalsIgnoreCase(supportedSiteLocale.com.name())){
                                    visibleText = (String) readTestCase.getValueOf(comOr, visibleText);}
                                log.info("Picking up value from lang repo: " + visibleText);

                            log.info("Text: " +visibleText);
                            //"By" based on <objectType> value
                            try {
                                if (objectType.equalsIgnoreCase("class")) {
                                    isTextDisplayed = driver.findElement(By.className(value)).getText().contentEquals(visibleText);
                                }
                                if (objectType.equalsIgnoreCase("xpath")) {
                                    isTextDisplayed = driver.findElement(By.xpath(value)).getText().contentEquals(visibleText);
                                }
                                if (objectType.equalsIgnoreCase("id")) {
                                    isTextDisplayed = driver.findElement(By.id(value)).getText().contentEquals(visibleText);
                                }
                                if (objectType.equalsIgnoreCase("tagName")) {
                                    isTextDisplayed = driver.findElement(By.tagName(value)).getText().contentEquals(visibleText);
                                }
                                if (objectType.equalsIgnoreCase("name")) {
                                    isTextDisplayed = driver.findElement(By.name(value)).getText().contentEquals(visibleText);
                                }
                                if (objectType.equalsIgnoreCase("linkText")) {
                                    isTextDisplayed = driver.findElement(By.linkText(value)).getText().contentEquals(visibleText);
                                }
                                if (objectType.equalsIgnoreCase("partialLinkText")) {
                                    isTextDisplayed = driver.findElement(By.partialLinkText(value)).getText().contentEquals(visibleText);
                                }
                                if (objectType.equalsIgnoreCase("css")) {
                                    isTextDisplayed = driver.findElement(By.cssSelector(value)).getText().contentEquals(visibleText);
                                }
                            } catch (Exception e) {
                                log.error(e);
                                log.error("Error is text visibility check for element.");
                                etest.log(LogStatus.ERROR, "Error is text visibility check for element.");
                                etest.log(LogStatus.FAIL, "Fail text visibility check for element.");
                            }
                        }

                        //Asertions

                        //Logging test expected and actual
                        log.info("Expected: \'" + visibleText + "\' to be visible for element \'" + value + "\'");

                        //Performing assertions
                        if (isTextDisplayed == true) {
                            log.info("Actual: \'" + visibleText + "\' is visible for element \'" + value + "\'");
                            log.info("Test Passed: Element text is visible");
                            etest.log(LogStatus.PASS, "Text Visibility check for " + "\'" + visibleText + "\' , \n" + "Text \'" + visibleText + "\'" + " is visible on UI for element \'" + value + "\'");
                        }else
                        {
                            log.info("Actual: \'" + visibleText + "\' is not visible");
                            log.info("Test Failed: Element text is not visible");
                            etest.log(LogStatus.FAIL, "Text Visibility check for " + "\'" + visibleText + "\', \n" + "Text \'" +visibleText +"\'" + " is not visible on UI for element \'" + value + "\'" );
                            String image = etest.addScreenCapture(takeScreenshot());
                            etest.log(LogStatus.FAIL,"Image:" + image);
                        }
                    }

                    //Assert based on <type> for TITLE
                    if (type.equalsIgnoreCase("title")) {


                        if (objectType.equalsIgnoreCase("title")) {
                            String windowTitle = null;
                            Boolean windowTitleMatch = false;
                            //Add explicit wait for title only to handle chrome diver.
                            try {

                                    //Change the title as per locale
                                    if(SITELOCALE.equalsIgnoreCase(supportedSiteLocale.de.name())){
                                    value = (String) readTestCase.getValueOf(deOr, tagValue);}
                                    else if(SITELOCALE.equalsIgnoreCase(supportedSiteLocale.fr.name())){
                                        value = (String) readTestCase.getValueOf(frOr, tagValue);}
                                    else if(SITELOCALE.equalsIgnoreCase(supportedSiteLocale.com.name())){
                                        value = (String) readTestCase.getValueOf(comOr, tagValue);}
                                    log.info("Picking up value from lang repo: " + value);

                                WebDriverWait wait = new WebDriverWait(driver, 30);
                                wait.until(ExpectedConditions.titleContains(value));
                                windowTitle = driver.getTitle();

                            } catch (Exception e){
                                log.error(e);
                                log.error("Error in title load.");
                                etest.log(LogStatus.ERROR, "Error in title load.");
                                etest.log(LogStatus.FAIL, "Window title failed to load in time!");
                            }

                            //Logging test expected and actual
                            log.info("Expected title: " + value);
                            log.info("Actual title: " + windowTitle);

                            try{
                            windowTitleMatch = windowTitle.equals(value);}
                            catch (Exception e){
                                //Do nothing
                                 }

                            if(windowTitleMatch == true) {
                                //Performing assertions
                                log.info("Test Passed: Title do match");
                                etest.log(LogStatus.PASS, "Window title check", "Expected title : " + "\'" + value + "\'" + " \nActual title : " + "\'" + windowTitle + "\'");
                            }else
                             {
                                log.info("Test Failed: Title do not match!");
                                etest.log(LogStatus.FAIL, "Window title check", "Expected title : " + "\'" +value + "\'" +" \nActual title : " +"\'" + windowTitle +"\'" );
                                 String image = etest.addScreenCapture(takeScreenshot());
                                 etest.log(LogStatus.FAIL,"Image:" + image);
                            }
                        } else {
                            log.error("<objectType> should be title as well if <type> is title");
                            etest.log(LogStatus.ERROR, "Error: objectType should be title as well if type is title");
                            etest.log(LogStatus.FAIL, "objectType should be title as well if type is title");
                        }

                    }

                    //Assert based on <type> for NEWWINDOWTITLE
                    if (type.equalsIgnoreCase("newWindowTitle")) {

                        // Store the current window handle
                        String winHandleBefore = driver.getWindowHandle();

                        //Marker for new window
                        Boolean noNewWindow = true;

                        try{
                            // Switch to latest window opened
                            for(String winHandle : driver.getWindowHandles()){
                                String temp = winHandle;
                                noNewWindow = winHandleBefore.equalsIgnoreCase(temp);
                                driver.switchTo().window(winHandle);
                            }
                        }catch (Exception e){
                            log.error("Error in newWindowTitle execution: " + e);
                            etest.log(LogStatus.ERROR, "Error: No new window!");
                            etest.log(LogStatus.FAIL, "No new window opened to assert!");
                        }

                        if (objectType.equalsIgnoreCase("title")) {
                            String windowTitle = null;
                            Boolean windowTitleMatch = false;
                            //Add explicit wait for title only to handle chrome diver.
                            try {

                                    //Change the title as per locale
                                    if(SITELOCALE.equalsIgnoreCase(supportedSiteLocale.de.name())){
                                        value = (String) readTestCase.getValueOf(deOr, tagValue);}
                                    else if(SITELOCALE.equalsIgnoreCase(supportedSiteLocale.fr.name())){
                                        value = (String) readTestCase.getValueOf(frOr, tagValue);}
                                    else if(SITELOCALE.equalsIgnoreCase(supportedSiteLocale.com.name())){
                                        value = (String) readTestCase.getValueOf(comOr, tagValue);}
                                    log.info("Picking up value from lang repo: " + value);

                                WebDriverWait wait = new WebDriverWait(driver, 30);
                                wait.until(ExpectedConditions.titleContains(value));
                                windowTitle = driver.getTitle();

                            } catch (Exception e){
                                log.error(e);
                                log.error("Error in title load.");
                                etest.log(LogStatus.ERROR, "Error in title load.");
                                etest.log(LogStatus.FAIL, "Window title failed to load in time!");
                            }

                            //Logging test expected and actual
                            log.info("Expected title: " + value);
                            log.info("Actual title: " + windowTitle);

                            try{
                                windowTitleMatch = windowTitle.equals(value);}
                            catch (Exception e){
                                //Do nothing
                            }

                            if(windowTitleMatch == true) {
                                //Performing assertions
                                log.info("Test Passed: Title do match");
                                etest.log(LogStatus.PASS, "Window title check", "Expected title : " + "\'" + value + "\'" + " \nActual title : " + "\'" + windowTitle + "\'");
                            }else
                            {
                                log.info("Test Failed: Title do not match!");
                                etest.log(LogStatus.FAIL, "Window title check", "Expected title : " + "\'" +value + "\'" +" \nActual title : " +"\'" + windowTitle +"\'" );
                                String image = etest.addScreenCapture(takeScreenshot());
                                etest.log(LogStatus.FAIL,"Image:" + image);
                            }
                        } else {
                            log.error("<objectType> should be title as well if <type> is title");
                            etest.log(LogStatus.ERROR, "Error: objectType should be title as well if type is title");
                            etest.log(LogStatus.FAIL, "objectType should be title as well if type is title");
                        }

                        //Close the latest window only if new window is opened
                        if (noNewWindow){
                            //Do nothing
                        } else {
                            driver.close();
                            //Switch back to original browser (first window)
                            driver.switchTo().window(winHandleBefore);
                        }
                    }

                    //Assert based on <type> for checkAttributeValue
                    if (type.equalsIgnoreCase("checkAttributeValue")) {

                        String attributeToCheck = null,attributeText = null;
                        Boolean isAttributeValueDisplayed = false;

                        //Check <attribute> value
                        if (elem.getElementsByTagName("attribute").item(0).getChildNodes().item(0).getNodeValue() == null) {
                            log.error("<attribute> tag is empty.");
                            etest.log(LogStatus.ERROR, "Blank attribute passed to verify visibility for value: '" + value + "\'");
                            etest.log(LogStatus.FAIL, "Blank attribute passed to verify visibility for value: '" + value + "\'");
                        }
                        //Check <text> value
                        else if(elem.getElementsByTagName("text").item(0).getChildNodes().item(0).getNodeValue() == null){
                            log.error("<text> tag is empty.");
                            etest.log(LogStatus.ERROR, "Blank text passed to verify visibility for value: '" + value + "\'");
                            etest.log(LogStatus.FAIL, "Blank text passed to verify visibility for value: '" + value + "\'");
                        }
                        else {

                            //Set attribute to get
                            attributeToCheck = elem.getElementsByTagName("attribute").item(0).getChildNodes().item(0).getNodeValue();

                            //Set visibleText value
                            attributeText = elem.getElementsByTagName("text").item(0).getChildNodes().item(0).getNodeValue();

                            //Replace <text> value if variable declared in test scripts
                            if (attributeText.equalsIgnoreCase("config_username")) {
                                attributeText = username;
                            } else if (attributeText.equalsIgnoreCase("config_password")) {
                                attributeText = password;
                            } else if(attributeText.equalsIgnoreCase("config_alphaRandom")){
                                attributeText = randomText;
                            }

                                //Change the title as per locale
                                if(SITELOCALE.equalsIgnoreCase(supportedSiteLocale.de.name())){
                                    attributeText = (String) readTestCase.getValueOf(deOr, attributeText);}
                                else if(SITELOCALE.equalsIgnoreCase(supportedSiteLocale.fr.name())){
                                    attributeText = (String) readTestCase.getValueOf(frOr, attributeText);}
                                else if(SITELOCALE.equalsIgnoreCase(supportedSiteLocale.com.name())){
                                    attributeText = (String) readTestCase.getValueOf(comOr, attributeText);}
                                log.info("Picking up value from lang repo: " + attributeText);

                            log.info("Text: " +attributeText);
                            //"By" based on <objectType> value
                            try {
                                if (objectType.equalsIgnoreCase("class")) {
                                    isAttributeValueDisplayed = driver.findElement(By.className(value)).getAttribute(attributeToCheck).contentEquals(attributeText);
                                }
                                if (objectType.equalsIgnoreCase("xpath")) {
                                    isAttributeValueDisplayed = driver.findElement(By.xpath(value)).getAttribute(attributeToCheck).contentEquals(attributeText);
                                }
                                if (objectType.equalsIgnoreCase("id")) {
                                    isAttributeValueDisplayed = driver.findElement(By.id(value)).getAttribute(attributeToCheck).contentEquals(attributeText);
                                }
                                if (objectType.equalsIgnoreCase("tagName")) {
                                    isAttributeValueDisplayed = driver.findElement(By.tagName(value)).getAttribute(attributeToCheck).contentEquals(attributeText);
                                }
                                if (objectType.equalsIgnoreCase("name")) {
                                    isAttributeValueDisplayed = driver.findElement(By.name(value)).getAttribute(attributeToCheck).contentEquals(attributeText);
                                }
                                if (objectType.equalsIgnoreCase("linkText")) {
                                    isAttributeValueDisplayed = driver.findElement(By.linkText(value)).getAttribute(attributeToCheck).contentEquals(attributeText);
                                }
                                if (objectType.equalsIgnoreCase("partialLinkText")) {
                                    isAttributeValueDisplayed = driver.findElement(By.partialLinkText(value)).getAttribute(attributeToCheck).contentEquals(attributeText);
                                }
                                if (objectType.equalsIgnoreCase("css")) {
                                    isAttributeValueDisplayed = driver.findElement(By.cssSelector(value)).getAttribute(attributeToCheck).contentEquals(attributeText);
                                }
                            } catch (Exception e) {
                                log.error(e);
                                log.error("Error in attribute text visibility check for element.");
                                etest.log(LogStatus.ERROR, "Error in attribute text visibility check for element.");
                                etest.log(LogStatus.FAIL, "Fail attribute text visibility check for element.");
                            }
                        }

                        //Asertions

                        //Logging test expected and actual
                        log.info("Expected: \'" + attributeText + "\' to be visible for attribute \'" + attributeToCheck + "\'");

                        //Performing assertions
                        if (isAttributeValueDisplayed == true) {
                            log.info("Actual: \'" + attributeText + "\' is visible for attribute \'" + attributeToCheck + "\'");
                            log.info("Test Passed: Attribute text is visible");
                            etest.log(LogStatus.PASS, "Attribute Text Visibility check for " + "\'" + attributeText + "\' , \n" + "Text \'" + attributeText + "\'" + " is visible on UI for Attribute \'" + attributeToCheck + "\'");
                        }else
                        {
                            log.info("Actual: \'" + attributeText + "\' is not visible");
                            log.info("Test Failed: Attribute text is not visible");
                            etest.log(LogStatus.FAIL, "Attribute Text Visibility check for " + "\'" + attributeText + "\', \n" + "Text \'" +attributeText +"\'" + " is not visible on UI for Attribute \'" + attributeToCheck + "\'" );
                            String image = etest.addScreenCapture(takeScreenshot());
                            etest.log(LogStatus.FAIL,"Image:" + image);
                        }
                    }

                    String listText = null;
                    Boolean isListExtDisplyed = false;
                    Select driverList = null;
                    //Action based on <type> for selectDropdown
                    if (type.equalsIgnoreCase("selectDropdown"))
                    {
                        //Check <text> value
                        if (elem.getElementsByTagName("text").item(0).getChildNodes().item(0).getNodeValue() == null) {
                            log.error("<text> tag is empty.");
                            etest.log(LogStatus.ERROR, "selectDropdown keys is passing blank text for value: '" + value + "\'");
                            etest.log(LogStatus.FAIL, "selectDropdown keys is passing blank text for value: '" + value + "\'");
                        }
                        else {
                            //Get <text> value
                            listText = elem.getElementsByTagName("text").item(0).getChildNodes().item(0).getNodeValue();

                            //Replace <text> value if variable declared in test scripts
                            if (listText.equalsIgnoreCase("config_username")) {
                                listText = username;
                            } else if (listText.equalsIgnoreCase("config_password")) {
                                listText = password;
                            } else if(listText.equalsIgnoreCase("config_alphaRandom")){
                                listText = randomText;
                            }


                                //Change the title as per locale
                                if(SITELOCALE.equalsIgnoreCase(supportedSiteLocale.de.name())){
                                    listText = (String) readTestCase.getValueOf(deOr, listText);}
                                else if(SITELOCALE.equalsIgnoreCase(supportedSiteLocale.fr.name())){
                                    listText = (String) readTestCase.getValueOf(frOr, listText);}
                                else if(SITELOCALE.equalsIgnoreCase(supportedSiteLocale.com.name())){
                                    listText = (String) readTestCase.getValueOf(comOr, listText);}
                                log.info("Picking up value from lang repo: " + listText);

                            log.info("Text: " +listText);
                            //Type value in text box
                            try {
                                //"By" based on <objectType> value
                                if (objectType.equalsIgnoreCase("class")) {
                                    driverList = new Select(driver.findElement(By.className(value)));
                                }
                                if (objectType.equalsIgnoreCase("xpath")) {
                                    driverList = new Select(driver.findElement(By.xpath(value)));
                                }
                                if (objectType.equalsIgnoreCase("id")) {
                                    driverList = new Select(driver.findElement(By.id(value)));
                                }
                                if (objectType.equalsIgnoreCase("tagName")) {
                                    driverList = new Select(driver.findElement(By.tagName(value)));
                                }
                                if (objectType.equalsIgnoreCase("name")) {
                                    driverList = new Select(driver.findElement(By.name(value)));
                                }
                                if (objectType.equalsIgnoreCase("linkText")) {
                                    driverList = new Select(driver.findElement(By.linkText(value)));
                                }
                                if (objectType.equalsIgnoreCase("partialLinkText")) {
                                    driverList = new Select(driver.findElement(By.partialLinkText(value)));
                                }
                                if (objectType.equalsIgnoreCase("css")) {
                                    driverList = new Select(driver.findElement(By.cssSelector(value)));
                                }
                                List<WebElement> listOptions = driverList.getOptions();
                                for(WebElement temp : listOptions){
                                    if(temp.getText().equals(listText)){
                                        isListExtDisplyed = true;
                                        break;
                                    }
                                }
                                //Asertions

                                //Logging test expected and actual
                                log.info("Expected: \'" + listText + "\' to be visible for list \'" + value + "\'");

                                //Performing assertions
                                if (isListExtDisplyed == true) {
                                    log.info("Actual: \'" + listText + "\' is visible for list \'" + value + "\'");
                                    log.info("Test Passed: Text is visible in the list");
                                    etest.log(LogStatus.PASS, "Text Visibility check for " + "\'" + listText + "\' , \n" + "Text \'" + listText + "\'" + " is visible on UI for list \'" + value + "\'");
                                }else
                                {
                                    log.info("Actual: \'" + listText + "\' is not visible");
                                    log.info("Test Failed: Text is not visible in the list");
                                    etest.log(LogStatus.FAIL, "Text Visibility check for " + "\'" + listText + "\' , \n" + "Text \'" + listText + "\'" + " is not visible on UI for list \'" + value + "\'");
                                    String image = etest.addScreenCapture(takeScreenshot());
                                    etest.log(LogStatus.FAIL,"Image:" + image);
                                }
                            }
                            catch (Exception e)
                            {
                                log.error(e);
                                log.error("Error in selectDropdown Assert Tag: " + e.toString());
                                etest.log(LogStatus.ERROR, "Error in selectDropdown assert tag.");
                                etest.log(LogStatus.FAIL, "Failed in selectDropdown assert tag.");
                                String image = etest.addScreenCapture(takeScreenshot());
                                etest.log(LogStatus.FAIL,"Image:" + image);
                            }
                        }
                    }


                    //Assert based on <type> for Alert
                    String caseText,expectedText = null,actualAlertText;
                    if (type.equalsIgnoreCase("alert")) {

                        Boolean isAlertDisplayed = false;
                        try{
                            isAlertDisplayed = isAlertPresent("modal");
                        } catch (Exception e){
                            //Do nothing, exception as alert no present
                        }

                        //Check is alert is displayed
                        if (isAlertDisplayed == false) {
                            log.info("Alert not present.");
                            etest.log(LogStatus.FAIL, "Expected Alert not present!");
                            String image = etest.addScreenCapture(takeScreenshot());
                            etest.log(LogStatus.FAIL,"Image:" + image);
                        }
                        else {
                            log.info("Alert present as desired.");
                            etest.log(LogStatus.INFO, "Expected Alert present.");

                            //Get <verifyText> value
                            caseText = elem.getElementsByTagName("verifyText").item(0).getChildNodes().item(0).getNodeValue();

                            //Get <verifyText> value from object repository class
//                            expectedText = (String) readTestCase.getValueOf(or, caseText);

                                //Change the title as per locale
                                if(SITELOCALE.equalsIgnoreCase(supportedSiteLocale.de.name())){
                                    expectedText = (String) readTestCase.getValueOf(deOr, caseText);}
                                else if(SITELOCALE.equalsIgnoreCase(supportedSiteLocale.fr.name())){
                                    expectedText = (String) readTestCase.getValueOf(frOr, caseText);}
                                else if(SITELOCALE.equalsIgnoreCase(supportedSiteLocale.com.name())){
                                    expectedText = (String) readTestCase.getValueOf(comOr, caseText);}
                                log.info("Picking up value from lang repo: " + expectedText);

                            try {

                                String textXpath = "//div[@class='modal-body']/p";
                                actualAlertText = driver.findElement(By.xpath(textXpath)).getText();

                                //Performing alert text assertions
                                Boolean alertTestMatch = false;
                                alertTestMatch = expectedText.equals(actualAlertText);
                                if (alertTestMatch == true) {
                                    log.info("Test Passed: Alert text matches");
                                    etest.log(LogStatus.PASS, "Expected Alert text: " + "\'" + expectedText + "\' , \n" + " matches Actual text: \'" + actualAlertText + "\'.");
                                }else
                                {
                                    log.info("Test Failed: Alert text do not match.");
                                    etest.log(LogStatus.FAIL, "Expected Alert text: " + "\'" + expectedText + "\' , \n" + " do not match Actual text: \'" + actualAlertText + "\'.");
                                    String image = etest.addScreenCapture(takeScreenshot());
                                    etest.log(LogStatus.FAIL,"Image:" + image);
                                }

                            } catch (Exception e) {
                                log.info("Error in Alert Tag: " + e.toString());
                                etest.log(LogStatus.ERROR, "Error in getting alert text");
                                etest.log(LogStatus.FAIL, "Failed in getting alert text");
                            }


                            //Perform action on alert based on <action>
                            //Get <action> value
                            String alertAction = elem.getElementsByTagName("action").item(0).getChildNodes().item(0).getNodeValue();

                            //Perform action based on <action> value
                            if (alertAction.equalsIgnoreCase("accept")) {
                                try{
                                    String actionXpath = "//div[@class='modal-footer']/button[1]";
                                    driver.findElement(By.xpath(actionXpath)).click();
                                    log.info("Alert accepted.");
                                    etest.log(LogStatus.INFO, "Alert accepted.");
                                }catch(Exception e)
                                {
                                    log.error("Error in Alert Action Tag: " + e.toString());
                                    etest.log(LogStatus.ERROR, "Error in accepting alert.");
                                    etest.log(LogStatus.FAIL, "Failed in accepting alert.");
                                    String image = etest.addScreenCapture(takeScreenshot());
                                    etest.log(LogStatus.FAIL,"Image:" + image);
                                }
                            }else if (alertAction.equalsIgnoreCase("deny"))
                            {
                                try{
                                    String actionXpath = "//div[@class='modal-footer']/button[2]";
                                    driver.findElement(By.xpath(actionXpath)).click();
                                    log.info("Alert not accepted.");
                                    etest.log(LogStatus.INFO, "Alert not accepted.");
                                }catch(Exception e){
                                    log.error(e);
                                    log.error("Error in Alert Action Tag: " + e.toString());
                                    etest.log(LogStatus.ERROR, "Error in denying alert.");
                                    etest.log(LogStatus.FAIL, "Failed in denying alert.");
                                    String image = etest.addScreenCapture(takeScreenshot());
                                    etest.log(LogStatus.FAIL,"Image:" + image);
                                }
                            }
                        }
                    }

                    //Assert based on <type> for Alert
                    String caseWinText,expectedWinText = null,actualWinAlertText;
                    if (type.equalsIgnoreCase("windowAlert")) {

                        Boolean isWinAlertDisplayed = false;
                        try{
                            isWinAlertDisplayed = isAlertPresent("windows");
                        } catch (Exception e){
                            //Do nothing, exception as alert no present
                        }

                        //Check is alert is displayed
                        if (isWinAlertDisplayed == false) {
                            log.info("Alert not present.");
                            etest.log(LogStatus.FAIL, "Expected Alert not present!");
                            String image = etest.addScreenCapture(takeScreenshot());
                            etest.log(LogStatus.FAIL,"Image:" + image);
                        }
                        else {
                            log.info("Alert present as desired.");
                            etest.log(LogStatus.INFO, "Expected Alert present.");

                            //Get <verifyText> value
                            caseWinText = elem.getElementsByTagName("verifyText").item(0).getChildNodes().item(0).getNodeValue();

                                //Change the title as per locale
                                if(SITELOCALE.equalsIgnoreCase(supportedSiteLocale.de.name())){
                                    expectedWinText = (String) readTestCase.getValueOf(deOr, caseWinText);}
                                else if(SITELOCALE.equalsIgnoreCase(supportedSiteLocale.fr.name())){
                                    expectedWinText = (String) readTestCase.getValueOf(frOr, caseWinText);}
                                else if(SITELOCALE.equalsIgnoreCase(supportedSiteLocale.com.name())){
                                    expectedWinText = (String) readTestCase.getValueOf(comOr, caseWinText);}
                                log.info("Picking up value from lang repo: " + expectedWinText);

                            try {

//                                String textXpath = "//div[@class='modal-body']/p";
                                actualWinAlertText = driver.switchTo().alert().getText();

                                //Performing alert text assertions
                                Boolean alertTestMatch = false;
                                alertTestMatch = expectedWinText.equals(actualWinAlertText);
                                if (alertTestMatch == true) {
                                    log.info("Test Passed: Alert text matches");
                                    etest.log(LogStatus.PASS, "Expected Alert text: " + "\'" + expectedWinText + "\' , \n" + " matches Actual text: \'" + actualWinAlertText + "\'.");
                                }else
                                {
                                    log.info("Test Failed: Alert text do not match.");
                                    etest.log(LogStatus.FAIL, "Expected Alert text: " + "\'" + expectedWinText + "\' , \n" + " do not match Actual text: \'" + actualWinAlertText + "\'.");
                                    String image = etest.addScreenCapture(takeScreenshot());
                                    etest.log(LogStatus.FAIL,"Image:" + image);
                                }

                            } catch (Exception e) {
                                log.info("Error in Alert Tag: " + e.toString());
                                etest.log(LogStatus.ERROR, "Error in getting alert text");
                                etest.log(LogStatus.FAIL, "Failed in getting alert text");
                            }


                            //Perform action on alert based on <action>
                            //Get <action> value
                            String alertAction = elem.getElementsByTagName("action").item(0).getChildNodes().item(0).getNodeValue();

                            //Perform action based on <action> value
                            if (alertAction.equalsIgnoreCase("accept")) {
                                try{
//                                    String actionXpath = "//div[@class='modal-footer']/button[1]";
//                                    driver.findElement(By.xpath(actionXpath)).click();
                                    driver.switchTo().alert().accept();
                                    log.info("Alert accepted.");
                                    etest.log(LogStatus.INFO, "Alert accepted.");
                                }catch(Exception e)
                                {
                                    log.error("Error in Alert Action Tag: " + e.toString());
                                    etest.log(LogStatus.ERROR, "Error in accepting alert.");
                                    etest.log(LogStatus.FAIL, "Failed in accepting alert.");
                                    String image = etest.addScreenCapture(takeScreenshot());
                                    etest.log(LogStatus.FAIL,"Image:" + image);
                                }
                            }else if (alertAction.equalsIgnoreCase("deny"))
                            {
                                try{
//                                    String actionXpath = "//div[@class='modal-footer']/button[2]";
//                                    driver.findElement(By.xpath(actionXpath)).click();
                                    driver.switchTo().alert().dismiss();
                                    log.info("Alert not accepted.");
                                    etest.log(LogStatus.INFO, "Alert not accepted.");
                                }catch(Exception e){
                                    log.error(e);
                                    log.error("Error in Alert Action Tag: " + e.toString());
                                    etest.log(LogStatus.ERROR, "Error in denying alert.");
                                    etest.log(LogStatus.FAIL, "Failed in denying alert.");
                                    String image = etest.addScreenCapture(takeScreenshot());
                                    etest.log(LogStatus.FAIL,"Image:" + image);
                                }
                            }
                        }
                    }
                }

                //Steps to perform for <actionTag>
                if (tag.equalsIgnoreCase("actionTag")) {

                    //Action based on <type> for CLICK
                    if (type.equalsIgnoreCase("click")) {
                        try {
                            //"By" based on <objectType> value
                            if (objectType.equalsIgnoreCase("class")) {
                                driver.findElement(By.className(value)).click();
                            }
                            if (objectType.equalsIgnoreCase("xpath")) {
                                driver.findElement(By.xpath(value)).click();
                            }
                            if (objectType.equalsIgnoreCase("id")) {
                                driver.findElement(By.id(value)).click();
                            }
                            if (objectType.equalsIgnoreCase("tagName")) {
                                driver.findElement(By.tagName(value)).click();
                            }
                            if (objectType.equalsIgnoreCase("name")) {
                                driver.findElement(By.name(value)).click();
                            }
                            if (objectType.equalsIgnoreCase("linkText")) {
                                driver.findElement(By.linkText(value)).click();
                            }
                            if (objectType.equalsIgnoreCase("partialLinkText")) {
                                driver.findElement(By.partialLinkText(value)).click();
                            }
                            if (objectType.equalsIgnoreCase("css")) {
                                driver.findElement(By.cssSelector(value)).click();
                            }
                            log.info("Performing Action: Clicking '" + value + "' of type '" + objectType+ "\'");
                            etest.log(LogStatus.INFO, "Performing Action: Clicking '" + value + "' of type '" + objectType+ "\'");
                        } catch (Exception e) {
                            log.error(e);
                            log.error("Error in click Action Tag: "+ e.toString());
                            etest.log(LogStatus.ERROR, "Error in clicking action: " + value);
                            etest.log(LogStatus.FAIL, "Failed in clicking action: " + value);
                            String image = etest.addScreenCapture(takeScreenshot());
                            etest.log(LogStatus.FAIL,"Image:" + image);
                        }
                    }

                    String text = null;
                    //Action based on <type> for SendKeys
                    if (type.equalsIgnoreCase("sendkeys"))
                        {
                            //Check <text> value
                            if (elem.getElementsByTagName("text").item(0).getChildNodes().item(0).getNodeValue() == null) {
                                log.error("<text> tag is empty.");
                                etest.log(LogStatus.ERROR, "Send keys is passing blank text for value: '" + value + "\'");
                                etest.log(LogStatus.FAIL, "Send keys is passing blank text for value: '" + value + "\'");
                        }
                        else {
                                //Get <text> value
                                text = elem.getElementsByTagName("text").item(0).getChildNodes().item(0).getNodeValue();
                                log.info("Text: " +text);
                                //Replace <text> value if variable declared in test scripts
                                if (text.equalsIgnoreCase("config_username")) {
                                    text = username;
                                } else if (text.equalsIgnoreCase("config_password")) {
                                    text = password;
                                } else if(text.contains("config_alphaRandom")){
                                    text = text.replace("config_alphaRandom",randomText);
                                }

                                //Type value in text box
                                try {
                                    //"By" based on <objectType> value
                                    if (objectType.equalsIgnoreCase("class")) {
                                        driver.findElement(By.className(value)).sendKeys(text);
                                    }
                                    if (objectType.equalsIgnoreCase("xpath")) {
                                        driver.findElement(By.xpath(value)).sendKeys(text);
                                    }
                                    if (objectType.equalsIgnoreCase("id")) {
                                        driver.findElement(By.id(value)).sendKeys(text);
                                    }
                                    if (objectType.equalsIgnoreCase("tagName")) {
                                        driver.findElement(By.tagName(value)).sendKeys(text);
                                    }
                                    if (objectType.equalsIgnoreCase("name")) {
                                        driver.findElement(By.name(value)).sendKeys(text);
                                    }
                                    if (objectType.equalsIgnoreCase("linkText")) {
                                        driver.findElement(By.linkText(value)).sendKeys(text);
                                    }
                                    if (objectType.equalsIgnoreCase("partialLinkText")) {
                                        driver.findElement(By.partialLinkText(value)).sendKeys(text);
                                    }
                                    if (objectType.equalsIgnoreCase("css")) {
                                        driver.findElement(By.cssSelector(value)).sendKeys(text);
                                    }
                                    log.info("Performing Action: Entering '" + text + "'  for '" + value + "\'");
                                    etest.log(LogStatus.INFO, "Performing Action: Entering '" + text + "'  for '" + value + "\'");
                                    }
                                catch (Exception e)
                                {
                                    log.error(e);
                                    log.error("Error in sendkeys Action Tag: " + e.toString());
                                    etest.log(LogStatus.ERROR, "Error in sendkeys action tag.");
                                    etest.log(LogStatus.FAIL, "Failed in sendkeys action tag.");
                                    String image = etest.addScreenCapture(takeScreenshot());
                                    etest.log(LogStatus.FAIL,"Image:" + image);
                                }
                            }
                    }

                    //Action based on <type> for Clear
                    if (type.equalsIgnoreCase("clear")) {
                            try {
                                //"By" based on <objectType> value
                                if (objectType.equalsIgnoreCase("class")) {
                                    driver.findElement(By.className(value)).clear();
                                }
                                if (objectType.equalsIgnoreCase("xpath")) {
                                    driver.findElement(By.xpath(value)).clear();
                                }
                                if (objectType.equalsIgnoreCase("id")) {
                                    driver.findElement(By.id(value)).clear();
                                }
                                if (objectType.equalsIgnoreCase("tagName")) {
                                    driver.findElement(By.tagName(value)).clear();
                                }
                                if (objectType.equalsIgnoreCase("name")) {
                                    driver.findElement(By.name(value)).clear();
                                }
                                if (objectType.equalsIgnoreCase("linkText")) {
                                    driver.findElement(By.linkText(value)).clear();
                                }
                                if (objectType.equalsIgnoreCase("partialLinkText")) {
                                    driver.findElement(By.partialLinkText(value)).clear();
                                }
                                if (objectType.equalsIgnoreCase("css")) {
                                    driver.findElement(By.cssSelector(value)).clear();
                                }
                                log.info("Performing Action: Clear '"  + value + "\'");
                                etest.log(LogStatus.INFO, "Performing Action: Clear '"  + value + "\'");
                            } catch (Exception e) {
                                log.error(e);
                                log.error("Error in Action Tag: " + e.toString());
                                etest.log(LogStatus.ERROR,"Error in clearing Text.");
                                etest.log(LogStatus.FAIL,"Failed in clearing Text.");
                                String image = etest.addScreenCapture(takeScreenshot());
                                etest.log(LogStatus.FAIL,"Image:" + image);
                            }

                    }

                    //Action based on title
                    if (type.equalsIgnoreCase("title")) {
                        if (objectType.equalsIgnoreCase("title")) {
                            try{
                                driver.manage().deleteAllCookies();
                                driver.get(value);
                                log.info("Moving to url: " + value);
                                etest.log(LogStatus.INFO,"Moving to Url: '" + value + "\'");
                            }
                            catch (Exception e){
                                log.error(e);
                                log.error("Error in title Action Tag: " + e.toString());
                                etest.log(LogStatus.ERROR,"Error in moving to url: '" + value + "\'");
                                etest.log(LogStatus.FAIL,"Failed in moving to url: '" + value + "\'");
                                String image = etest.addScreenCapture(takeScreenshot());
                                etest.log(LogStatus.FAIL,"Image:" + image);
                            }
                        }
                    }

                    //Action based on appendTitletitle
                    if (type.equalsIgnoreCase("appendTitle")) {
                        String appendUrl, completeUrl = null;
                        if (objectType.equalsIgnoreCase("title")) {
                            //Check <text> value
                            if (elem.getElementsByTagName("text").item(0).getChildNodes().item(0).getNodeValue() == null) {
                                log.error("<text> tag is empty.");
                                etest.log(LogStatus.ERROR, "appendTitle keys is passing blank text for value: '" + value + "\'");
                                etest.log(LogStatus.FAIL, "appendTitle keys is passing blank text for value: '" + value + "\'");
                            }
                            else {
                                //Get <text> value
                                appendUrl = elem.getElementsByTagName("text").item(0).getChildNodes().item(0).getNodeValue();

                                //Replace <text> value if variable declared in test scripts
                                if (appendUrl.equalsIgnoreCase("config_username")) {
                                    appendUrl = username;
                                } else if (appendUrl.equalsIgnoreCase("config_password")) {
                                    appendUrl = password;
                                } else if(appendUrl.equalsIgnoreCase("config_alphaRandom")){
                                    appendUrl = randomText;
                                }
                                log.info("Text: " +appendUrl);

                                //Create a complete Url and hit
                                try{
                                    driver.manage().deleteAllCookies();
                                    completeUrl = value+appendUrl;
                                    driver.get(completeUrl);
                                    log.info("Moving to url: " + completeUrl);
                                    etest.log(LogStatus.INFO,"Moving to Url: '" + completeUrl + "\'");
                                }
                                catch (Exception e){
                                    log.error(e);
                                    log.error("Error in appendTitle Action Tag: " + e.toString());
                                    etest.log(LogStatus.ERROR,"Error in moving to url: '" + completeUrl + "\'");
                                    etest.log(LogStatus.FAIL,"Failed in moving to url: '" + completeUrl + "\'");
                                    String image = etest.addScreenCapture(takeScreenshot());
                                    etest.log(LogStatus.FAIL,"Image:" + image);
                                }
                            }

                        }
                    }

                    //Action based on runTestScripts
                    if (type.equalsIgnoreCase("runTestScript")) {

                            try{
                                //Marker to avoid running extent.endTest on re-run
                                runningReadAgain = false;
                                readTestCase.readUiCases(objectType,value);
                                runningReadAgain = true;
                            }
                            catch (Exception e){
                                log.error(e);

                            }
                    }

                    String listText = null;
                    Select driverList = null;
                    //Action based on <type> for selectDropdown
                    if (type.equalsIgnoreCase("selectDropdown"))
                    {
                        //Check <text> value
                        if (elem.getElementsByTagName("text").item(0).getChildNodes().item(0).getNodeValue() == null) {
                            log.error("<text> tag is empty.");
                            etest.log(LogStatus.ERROR, "selectDropdown keys is passing blank text for value: '" + value + "\'");
                            etest.log(LogStatus.FAIL, "selectDropdown keys is passing blank text for value: '" + value + "\'");
                        }
                        else {
                            //Get <text> value
                            listText = elem.getElementsByTagName("text").item(0).getChildNodes().item(0).getNodeValue();

                            //Replace <text> value if variable declared in test scripts
                            if (listText.equalsIgnoreCase("config_username")) {
                                listText = username;
                            } else if (listText.equalsIgnoreCase("config_password")) {
                                listText = password;
                            } else if(listText.equalsIgnoreCase("config_alphaRandom")){
                                listText = randomText;
                            }

                                //Change the title as per locale
                                if(SITELOCALE.equalsIgnoreCase(supportedSiteLocale.de.name())){
                                    listText = (String) readTestCase.getValueOf(deOr, listText);}
                                else if(SITELOCALE.equalsIgnoreCase(supportedSiteLocale.fr.name())){
                                    listText = (String) readTestCase.getValueOf(frOr, listText);}
                                else if(SITELOCALE.equalsIgnoreCase(supportedSiteLocale.com.name())){
                                    listText = (String) readTestCase.getValueOf(comOr, listText);}
                                log.info("Picking up value from lang repo: " + listText);

                            log.info("Text: " +listText);
                            //Type value in text box
                            try {
                                //"By" based on <objectType> value
                                if (objectType.equalsIgnoreCase("class")) {
                                    driverList = new Select(driver.findElement(By.className(value)));
                                }
                                if (objectType.equalsIgnoreCase("xpath")) {
                                    driverList = new Select(driver.findElement(By.xpath(value)));
                                }
                                if (objectType.equalsIgnoreCase("id")) {
                                    driverList = new Select(driver.findElement(By.id(value)));
                                }
                                if (objectType.equalsIgnoreCase("tagName")) {
                                    driverList = new Select(driver.findElement(By.tagName(value)));
                                }
                                if (objectType.equalsIgnoreCase("name")) {
                                    driverList = new Select(driver.findElement(By.name(value)));
                                }
                                if (objectType.equalsIgnoreCase("linkText")) {
                                    driverList = new Select(driver.findElement(By.linkText(value)));
                                }
                                if (objectType.equalsIgnoreCase("partialLinkText")) {
                                    driverList = new Select(driver.findElement(By.partialLinkText(value)));
                                }
                                if (objectType.equalsIgnoreCase("css")) {
                                    driverList = new Select(driver.findElement(By.cssSelector(value)));
                                }
                                driverList.selectByVisibleText(listText);
                                log.info("Performing Action: Selecting '" + listText + "'  from list '" + value + "\'");
                                etest.log(LogStatus.INFO, "Performing Action: Selecting '" + listText + "'  from list '" + value + "\'");
                            }
                            catch (Exception e)
                            {
                                log.error(e);
                                log.error("Error in selectDropdown Action Tag: " + e.toString());
                                etest.log(LogStatus.ERROR, "Error in selectDropdown action tag.");
                                etest.log(LogStatus.FAIL, "Failed in selectDropdown action tag.");
                                String image = etest.addScreenCapture(takeScreenshot());
                                etest.log(LogStatus.FAIL,"Image:" + image);
                            }
                        }
                    }

                    //Action based on wait
                    if (type.equalsIgnoreCase("wait")) {

                        try{
                            float secondToMillisec = Integer.parseInt(value)*1000;
                            Thread.sleep((long) secondToMillisec);
                            log.info("Waiting for " +value+ " seconds.");
                            etest.log(LogStatus.INFO,"Waiting for " +value+ " seconds.");
                        }
                        catch (Exception e){
                            log.error(e);
                            log.error("Error in wait tag");
                            etest.log(LogStatus.ERROR, "Error in wait action tag.");
                            etest.log(LogStatus.FAIL, "Failed in wait action tag.");
                        }
                    }
                    //Wait for two second after every action
                    Thread.sleep(2000);
                }
            }
        }
        if(runningReadAgain == true){
        extent.endTest(etest);
        extent.flush();}
    }
}



