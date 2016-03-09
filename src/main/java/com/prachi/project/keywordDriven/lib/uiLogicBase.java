package com.prachi.project.keywordDriven.lib;

import com.prachi.project.keywordDriven.enumPackage.supportedBrowser;
import org.apache.log4j.Logger;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Created by prachi a. pawaskar on 12/17/2015.
 */

//Used to unmarshall/marshall UI test scripts present under package "apiTestScripts"
public class uiLogicBase extends base{

    //Logger defined for class, logs created as per log4j.properties
    private static Logger log = Logger.getLogger(uiLogicBase.class);

    //Staring the driver and hitting UI url
    @BeforeClass
    public static void startDriver()  throws Exception{
        log.info("Starting web driver");
        if (BROWSER.equalsIgnoreCase(supportedBrowser.firefox.name()) || (BROWSER == null || BROWSER == "")){
            log.info("UI test running on Firefox browser.");

            driver = new FirefoxDriver();
            driver.manage().window().maximize();
            //Adding implicit wait in seconds.
            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        }
        else if(BROWSER.equalsIgnoreCase(supportedBrowser.chrome.name()))
        {
            log.info("UI test running on Chrome browser.");

            String resourceFilePath = "src/main/java/com/prachi/project/keywordDriven/drivers/";
            String resourceURL = new File(resourceFilePath).getAbsolutePath();
            System.setProperty("webdriver.chrome.driver", resourceURL +"/chromedriver.exe");
            driver = new ChromeDriver();
            driver.manage().window().maximize();

            //Adding implicit wait in seconds.
            driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
        }
        else if(BROWSER.equalsIgnoreCase(supportedBrowser.ie.name()))
        {
            log.info("UI test running on IE browser.");

            String resourceFilePath = "src/main/java/com/prachi/project/keywordDriven/drivers/";
            String resourceURL = new File(resourceFilePath).getAbsolutePath();
            System.setProperty("webdriver.ie.driver", resourceURL +"/IEDriverServer.exe");
            driver = new InternetExplorerDriver();
            driver.manage().window().maximize();

            //Adding implicit wait in seconds.
            driver.manage().timeouts().implicitlyWait(90, TimeUnit.SECONDS);
        }


    }

    //Stop the driver
    @AfterClass
    public static void stopDriver() throws Exception{
        //To close all browser windows
        driver.close();
        //To stop driver process in background
        driver.quit();
    }

}
