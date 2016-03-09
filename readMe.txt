/**
 * Created by prachi a. pawaskar on 11/30/2015.
*/

=========================
Details:
=========================
This QA automation framework is created for Artnet project to handle API and UI cases.


=========================
System requirement:
=========================
> Java 7/7+ [Refer http://www.oracle.com/technetwork/java/javase/downloads/index.html]
> Maven [Refer http://www.avajava.com/tutorials/lessons/what-is-maven-and-how-do-i-install-it.html]


=========================
Framework details:
=========================
> Build tool/Repository manager: Maven
> Testing framework: TestNG
> Framework type: Key word driven
> Web UI testing :  Selenium Webdriver
> Web API testing :  Jersey Client
> Logger used: Log4j logger
> Reporting used: ExtendReports v2


=========================
Execution instructions:
=========================
> Through command line -
1. mvn test -Denv={envValue} -Dbrowser={browser} -Dtag={tag name} -DsiteLocale={locale}
    //envValue can be production,staging, qa_black, qa_blue, qa_green or qa_red
    //browser can be firefox or chrome or ie [Note: IE 11 can create issues due MS change in browser actions.]
    //tag can be bat, sanity[includes bat and sanity], negative, regression[includes bat, sanity and regression]
    //siteLocale can be com, de and fr
    eg: mvn test -Denv=staging -Dbrowser=firefox -Dtag=regression -DsiteLocale=fr
2. mvn test     //envValue=qa_red, browser=firefox, tag=regression and -DsiteLocale=com default value

> Through IDE -
Run testng.xml and append VM options -Denv=staging -Dbrowser=firefox -Dtag=regression -DsiteLocale=com
Run testng.xml and change env, browser and tag, siteLocale parameters

Note: Only uncommented classes from testng.xml will be executed.


=========================
Test case execution:
=========================
> Only active cases from "/testCases/{*Case}.csv" will be executed.
    eg: /testCases/apiActiveCase.csv
> Csv file format: {active/incative},{bat/sanity/negative/regression},{testScriptName}
    eg: active,regression,simpleFeedApi
> {testScriptName} can be found under test script packages {*TestScripts}
    eg: com/prachi/project/artnet/apiTestScripts/simpleFeedApi.xml


=========================
Reports:
=========================
> HTML reports are created under: /reports/{detailTestReport_*}.html
> Reports have two sections:
1. Tests [for test scripts details]
2. Dashboard [for test summary]
> New report is created for each execution

Note: Please do not commit this file to bit bucket.

=========================
Logs:
=========================
> Logs are written in log file: /applog.log
> Only one file is maintained, contents are overwritten after each execution.

Note: Avoid committing this file to bit bucket.

=========================
Adding new test case:
=========================
> Create test script in xml format under test script packages {*TestScripts}
    eg: sample.xml
> Add the test script name in Csv file: "/testCases/{*Case}.csv"
> Mark the test script name as "active" and add appropriate tag
    eg:  active,regression,sampleRun


=========================
Test case format:
=========================
> Test Case variable:
config_username =  Username from envConfig.xml
config_password =  Password from envConfig.xml
config_alphaRandom = Returns alphanumeric value.

> POST Api Case
<apiCase>
    <methodType>post</methodType> ----------------> Mandatory, can be post/get
    <postType>xml</postType> ----------------> Mandatory if <methodType> is post, can be xml/json
    <requestFile>sampleXmlPost</requestFile> ----------------> Mandatory if <methodType> is post
    <uri>/api/?type=feed1</uri> ----------------> Mandatory, uri for the base API url
    <assertStatus>400</assertStatus> ----------------> Mandatory, assertion of apache status code
    <assertResponse>{"status":400,"data":"Invalid: Request Type"}</assertResponse> ----------------> Assert single liner response, can be used only once, it compares content removing spaces.
    <assertResponseMultiple>status</assertResponseMultiple> ----------------> Assert response in fragments, can be used multiple
    <assertResponseMultiple>400</assertResponseMultiple>
</apiCase>

> GET Api Case
<apiCase>
    <methodType>get</methodType> ----------------> Mandatory, can be post/get
    <uri>/api/?type=feed1</uri> ----------------> Mandatory, uri for the base API url
    <assertStatus>400</assertStatus> ----------------> Mandatory, assertion of apache status code
    <assertResponse>{"status":400,"data":"Invalid: Request Type"}</assertResponse> ----------------> Assert single liner response, can be used only once, it compares content removing spaces.
    <assertResponseMultiple>status</assertResponseMultiple> ----------------> Assert response in fragments, can be used multiple
    <assertResponseMultiple>400</assertResponseMultiple>
</apiCase>

> UI Test case
<uiTestCase>
    <assertTag> ----------------> Assertion title tag
        <type>title</type> ----------------> Assertion title
        <objectType>title</objectType> ----------------> Object type is title
        <value>taxonomy_title</value> ----------------> Corresponding value to Object Repo
    </assertTag>
    <assertTag>
        <type>newWindowTitle</type> ----------------> Assertion title tag when the link opens in new window
        <objectType>title</objectType> ----------------> Object type is title
        <value>artnetPrivacy_title</value> ----------------> Corresponding value to Object Repo
    </assertTag>
    <assertTag>
        <type>visible</type> ----------------> Object displayed assertion
        <objectType>id</objectType> ----------------> Object type, can have id,xpath,class,tagName,name,linkText,partialLinkText,css
        <value>cmsLoginName_id</value> ----------------> Corresponding value to Object Repo
    </assertTag>
    <assertTag>
        <type>notVisible</type> ----------------> Object not displayed assertion
		<objectType>xpath</objectType> ----------------> Object type, can have id,xpath,class,tagName,name,linkText,partialLinkText,css
		<value>cmsLoginName_id</value> ----------------> value when overwrite, checks for overwriteValue instead of object repo
    </assertTag>
    <assertTag>
        <type>visibleText</type> ----------------> Object text displayed assertion
        <objectType>xpath</objectType> ----------------> Object type, can have id,xpath,class,tagName,name,linkText,partialLinkText,css
        <value>newsletterSignup_xpath</value> ----------------> Corresponding value to Object Repo
        <text>Newsletter Signup</text> ----------------> Text to be displayed,Corresponding value to lang Object Repo
    </assertTag>
	<assertTag>
        <type>alert</type> ----------------> Assertion for pop up modal alert
		<verifyText>successMessage_alert</verifyText> ----------------> Assert alert text
		<action>accept</action> ----------------> Take action on alert, accept or deny
    </assertTag>
	<assertTag>
        <type>windowAlert</type> ----------------> Assertion for pop up windows alert
		<verifyText>successMessage_alert</verifyText> ----------------> Assert alert text
		<action>accept</action> ----------------> Take action on alert, accept or deny
    </assertTag>
    <assertTag>
        <type>visible</type> ----------------> Object displayed assertion
		<objectType>xpath</objectType> ----------------> Object type, can have id,xpath,class,tagName,name,linkText,partialLinkText,css
		<value>overwrite</value> ----------------> value when overwrite, checks for overwriteValue instead of object repo
		<overwriteValue>//span[@title='config_alphaRandom']</overwriteValue> ----------------> Use this as object locator
    </assertTag>
    <assertTag>
        <type>checkAttributeValue</type> ----------------> To check attribute value
        <objectType>id</objectType>  ----------------> Object type, can have id,xpath,class,tagName,name,linkText,partialLinkText,css
        <value>galleryInquiryFormToValue_id</value> ----------------> Corresponding value to Object Repo
        <attribute>value</attribute> ----------------> Attribute name whose value needs to be verified. eg: placeholder
        <text>Jackson Fine Art</text> ----------------> Attribute text that needs to be verified,Corresponding value to lang Object Repo
    </assertTag>
    <assertTag>
        <type>selectDropdown</type> ----------------> Assert tag to verify text from drop down
        <objectType>id</objectType> ----------------> Object type, can have id,xpath,class,tagName,name,linkText,partialLinkText,css
        <value>galleryInquiryFormSubject_id</value> ----------------> Corresponding value to Object Repo
        <text>Inquire about an artist</text> ----------------> Visible Text to be verified from drop down,Corresponding value to lang Object Repo
    </assertTag>
    <actionTag>  ----------------> Action tag
        <type>click</type> ----------------> Object type, can have id,xpath,class,tagName,name,linkText,partialLinkText,css
        <objectType>xpath</objectType> ----------------> Object type is xpath
        <value>taxonomyLink_xpath</value> ----------------> Corresponding value to Object Repo
    </actionTag>
    <actionTag>
        <type>clear</type>  ----------------> Action type is click
        <objectType>id</objectType> ----------------> Object type, can have id,xpath,class,tagName,name,linkText,partialLinkText,css
        <value>internalName_id</value> ----------------> Corresponding value to Object Repo
    </actionTag>
    <actionTag>
        <type>sendkeys</type> ----------------> Action type is sendkeys, used for text box
        <objectType>id</objectType> ----------------> Object type, can have id,xpath,class,tagName,name,linkText,partialLinkText,css
        <value>internalName_id</value> ----------------> Corresponding value to Object Repo
        <text>config_alphaRandom</text> ----------------> Text to send to test box
    </actionTag>
	<actionTag>
        <type>title</type> ----------------> Action tag with title used to navigate to the URL
		<objectType>title</objectType> ----------------> Object type should be title as well
		<value>newsSite</value> ----------------> Corresponding value to Object Repo
    </actionTag>
    <actionTag>
        <type>appendTitle</type> ----------------> Action tag with will append title and navigate to the complete URL
        <objectType>title</objectType> ----------------> Object type should be title as well
        <value>artnetSite</value> ----------------> Corresponding value to Object Repo
        <text>/galleries/jackson-fine-art/</text>  ----------------> Url to append to the <value>, start with "/"
    </actionTag>
    <actionTag>
        <type>runTestScript</type> ----------------> Action tag to run another test script
        <objectType>demoTestScripts</objectType> ----------------> Name of the test script package
        <value>shouldLogin</value> ----------------> Name of the test script to run
    </actionTag>
    <actionTag>
        <type>selectDropdown</type> ----------------> Action tag to select text from drop down
        <objectType>id</objectType> ----------------> Object type, can have id,xpath,class,tagName,name,linkText,partialLinkText,css
        <value>demoAccount_id</value> ----------------> Corresponding value to Object Repo
        <text>Loan</text> ----------------> Visible Text to be selected from drop down,Corresponding value to lang Object Repo
    </actionTag>
    <actionTag>
        <type>wait</type> ----------------> Action tag to wait execution
        <objectType>wait</objectType> ----------------> Object type should be wait as well.
        <value>10</value> ----------------> Value holds wait in seconds. NO DECIMAL ALLOWED!
    </actionTag>
</uiTestCase>

