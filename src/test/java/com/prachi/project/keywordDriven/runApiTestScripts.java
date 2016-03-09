package com.prachi.project.keywordDriven;

import com.prachi.project.keywordDriven.lib.base;
import com.prachi.project.keywordDriven.lib.readTestCase;
import org.apache.log4j.Logger;
import org.testng.annotations.*;

import java.util.*;

/**
 * Created by prachi a. pawaskar on 11/30/2015.
 */

//This is the parent class to run all API cases
public class runApiTestScripts extends base {

    //Logger defined for class, logs created as per log4j.properties
    private static Logger log = Logger.getLogger(runApiTestScripts.class);

    //Data provider present in base.class, picks active test scripts from CSV
    @Test (dataProvider = "testCases")
    public static void apiTestCase(List<String> caseName) throws Exception{
        for (String str : caseName) {
            log.info("Running test script: " + str);
            //Pass test scripts names
            readTestCase.readApiCases(str);}
    }


}
