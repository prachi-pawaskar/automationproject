package com.prachi.project.keywordDriven.lib;

import com.prachi.project.keywordDriven.enumPackage.supportedEnv;
import com.prachi.project.keywordDriven.enumPackage.supportedBrowser;
import com.prachi.project.keywordDriven.enumPackage.supportedTag;
import com.prachi.project.keywordDriven.enumPackage.supportedSiteLocale;
import com.prachi.project.keywordDriven.readXml.envConfig;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.ReporterType;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by prachi a. pawaskar on 11/30/2015.
 */
public class base  {

    //Logger defined for class, logs created as per log4j.properties
    private Logger log = Logger.getLogger(base.class);



    // Extend reports and test variable
    public static ExtentReports extent;
    public static ExtentTest etest;

    //Variables
    public static String ENV = System.getProperty("env"),
            BROWSER = System.getProperty("browser"),
            TAG = System.getProperty("tag"),
            SITELOCALE = System.getProperty("siteLocale");

    public static String baseURL, URI,
            assertStatus, assertResponse, assertTitle,objectType, assertElementVisible,
            uiURL, username, password;

    public static ArrayList<String> assertResponseMultipleVar;
    public String ENVRESOURCEDIR  ;
    public String ENVCONFIGFILENAME = "envConfig.xml";

    // Jersey variables for API
    public static Client client ;
    public static WebResource webResource ;

    //Webdriver variables for UI
    public static ClientResponse response;
    public static WebDriver driver;

    // Report html created under /reports/*
    long millis = System.currentTimeMillis();
    public String reportLocation = "reports/detailTestReport_" +  millis + ".html";

    @BeforeSuite
    @Parameters({ "env","browser","tag","siteLocale" })
    public void setUp(String env, String browser,String tag, String siteLocale) throws Exception {

        //Initialize extend reports
        extent = new ExtentReports(reportLocation, true);
        extent.startReporter(ReporterType.DB, (new File(reportLocation)).getParent() + File.separator + "extent.db");
        extent.addSystemInfo("Log File","applog.log");

        //Calling methods
        setDefaultENV(env,browser,tag,siteLocale);
        setConfigs();

        //Command to delete system %TEMP% files
        Runtime.getRuntime().exec("cmd /c rd /s /q %temp%");

    }



    //Setting default env, reference to /resources/env/* if no env passed

    public void setDefaultENV(String env, String browser, String tag, String siteLocale)  throws Exception{
        //If testng parameter for env is blank, default is qa_red
        if (env == null || env == "") {env = supportedEnv.qa_red.name();}
        //If mvn parameter for env is blank, default is qa_red
        if (ENV == null || ENV == "") {ENV = env;}
        log.info("Env running on: " +ENV);
        extent.addSystemInfo("Environment",ENV);

        //If testng parameter for browser is blank, default is firefox
        if (browser == null || browser == "") {browser = supportedBrowser.firefox.name();}
        //If mvn parameter for browser is blank, default is firefox
        if (BROWSER == null || BROWSER == "") {BROWSER = browser;}
        log.info("UI running on: "+ BROWSER);
        extent.addSystemInfo("Browser: ",browser);

        //If testng parameter for tag is blank, default is regression
        if (tag == null || tag == "") {tag = supportedTag.regression.name();}
        //If mvn parameter for env is blank, default is qa_red
        if (TAG == null || TAG == "") {TAG = tag;}
        log.info("Active Tag: " +TAG);
        extent.addSystemInfo("Active tag: ",TAG);

        //If testng parameter for siteLocale is blank, default is com
        if (siteLocale == null || siteLocale == "") {siteLocale = supportedSiteLocale.com.name();}
        //If mvn parameter for env is blank, default is qa_red
        if (SITELOCALE == null || SITELOCALE == "") {SITELOCALE = siteLocale;}
        log.info("Site locale running on: " +SITELOCALE);
        extent.addSystemInfo("Site locale: ",SITELOCALE);

     }

    //Read /env/*/envConfig.xml and set variable values
    public void setConfigs()  throws Exception{

        ENVRESOURCEDIR = "/env/" + ENV;
        String resourcePath = ENVRESOURCEDIR;
        String filePath = ENVCONFIGFILENAME;
        String resourceFilePath = resourcePath + "/" + filePath;
        log.info("Env file in use:" + resourceFilePath);

        URL urlToData = this.getClass().getResource(resourceFilePath);
        String resourceURL = urlToData.getPath();
        log.info("Reading envConfig.xml absolute path: " + resourceURL);

        try {
            File file = new File(resourceURL);
            JAXBContext jaxbContext = JAXBContext.newInstance(envConfig.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            envConfig configClass = (envConfig) jaxbUnmarshaller.unmarshal(file);

            //Setting base API url
            baseURL = configClass.getBaseUrl();
            log.info("Reading API base URL: " + baseURL);

            //Setting UI url
            uiURL = configClass.getUiUrl();
            log.info("Reading UI URL: " + uiURL);


//            if(SITELOCALE.equalsIgnoreCase(supportedSiteLocale.com.name())){ //Do no change
//            }else {
//                //Change the locale for site
//                artnetUrl = artnetUrl.replace(".com","."+SITELOCALE.toString());
//            }

            //Setting username to test
            username = configClass.getUsername();
            log.info("Reading username: " + username);

            //Setting password to test
            password = configClass.getPassword();
            log.info("Reading password: " + password);

        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    // Read active test scripts to be executed
    @DataProvider(name="testCases")
    public Object[][] TestToRun(Method m) throws IOException {
        String temp = "";
        String type = m.getName();

        //Choose csv to run test scripts
        if (type == "apiTestCase") {temp = "apiActiveCase";}
        else if (type == "demoTestCase") {temp = "demoActiveCase";}


        else {log.info("Wrong method name passed!");}

        //Select csv file
        String csvFile = "src/test/java/com/prachi/project/keywordDriven/testCases/"+temp+".csv";

        //Reading Active script names
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        br = new BufferedReader(new FileReader(csvFile));
        List<String> list = new ArrayList<String>();
        while ((line = br.readLine()) != null) {
            if (line.startsWith("#")){
                //Don't read the line, it's a comment
            } else {
                Object[] active = line.split(cvsSplitBy);
                log.info("Test scripts mentioned in CSV file: " + active[0] + ", " + active[1]+", " + active[2]);

                String runTag = active[1].toString();

                //Handling blank tag in csv file
                if(runTag.isEmpty() || runTag == "") {runTag = supportedTag.regression.name();}

                //Select only active test scripts
                if (active[0].toString().equalsIgnoreCase("active")) {
                    //Select test scripts as per tag
                    if(TAG.equalsIgnoreCase(supportedTag.bat.name()))
                    {
                        if (runTag.equalsIgnoreCase(supportedTag.bat.name()))
                        {list.add(active[2].toString());}
                    }else if(TAG.equalsIgnoreCase(supportedTag.sanity.name()))
                    {
                        if (runTag.equalsIgnoreCase(supportedTag.bat.name()) ||
                                runTag.equalsIgnoreCase(supportedTag.sanity.name()))
                        {list.add(active[2].toString());}
                    }else if(TAG.equalsIgnoreCase(supportedTag.regression.name()))
                    {
                        if (runTag.equalsIgnoreCase(supportedTag.bat.name()) ||
                                runTag.equalsIgnoreCase(supportedTag.sanity.name()) ||
                                runTag.equalsIgnoreCase(supportedTag.regression.name()) ||
                                runTag.equalsIgnoreCase(supportedTag.negative.name())                                )
                        {list.add(active[2].toString());}
                    }else if(TAG.equalsIgnoreCase(supportedTag.negative.name()))
                    {
                        if (runTag.equalsIgnoreCase(supportedTag.negative.name()))
                        {list.add(active[2].toString());}
                    }

                }
            }
        }

        //
        log.info("Following are ACTIVE test scripts to be executed : " + list.toString());
        return new Object[][] { { list } };
    }


}
