package com.prachi.project.keywordDriven;

import com.prachi.project.keywordDriven.lib.readTestCase;
import com.prachi.project.keywordDriven.lib.uiLogicBase;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by prachi a. pawaskar on 2/8/2016.
 */
public class runDemoTestScripts  extends uiLogicBase {

    //Logger defined for class, logs created as per log4j.properties
    private static Logger log = Logger.getLogger(runDemoTestScripts.class);

    //Data provider present in base.class, picks active test scripts from CSV
    //Method for demo site
    @Test(dataProvider = "testCases")
    public void demoTestCase(List<String> caseName) throws Exception {
        //Demo site
        for (String str : caseName) {
            log.info("Running test script: " + str);
            //Pass Test script package name and test scripts names
            driver.manage().deleteAllCookies();
            driver.get(uiURL);
            readTestCase.readUiCases("demoTestScripts",str);
        }
    }

}
