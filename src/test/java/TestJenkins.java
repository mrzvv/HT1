import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestJenkins {
    private final static String baseURL = "http://localhost:8080";
    private final static String manageURL = "/manage";
    private final static String usersURL = "/securityRealm";
    private final static String createUserURL = "/addUser";

    private final static String LOGINUSER = "mrzv";
    private final static String LOGINPASS = "LeraLera1";

    private final static String USERNAME = "someuser";
    private final static String PASSWORD = "somepassword";
    private final static String FULLNAME = "Some Full Name";
    private final static String EMAIL = "some@addr.com";

    private StringBuffer verificationErrors = new StringBuffer();
    private WebDriver driver = null;
    private Manager page = null;

    @BeforeClass
    public void beforeClass() {
        System.setProperty("webdriver.chrome.driver", "lib/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.setBinary("C:/Program Files (x86)/Google/Chrome/Application/chrome.exe");
        driver = new ChromeDriver(options);
        driver.get(baseURL);
        if (!driver.getTitle().equals("Jenkins")) {
            Assert.fail("[Wrong page at " + baseURL + "!]");
        }
        page = PageFactory.initElements(driver, Manager.class);
        page.logIn(LOGINUSER, LOGINPASS);
    }

    @AfterClass
    public void afterClass() {
        driver.get(baseURL + usersURL);
        if (page.assertThatSomeUserIsCreated()) {
            page.clickDeleteSomeUser();
            page.submitDeletingSomeUser();
        }
        page.logOut();
        driver.close();
        driver.quit();
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            Assert.fail(verificationErrorString);
        }
    }

    @Test
    public void createDeleteModifyTest() {
        page.clickManageJenkins();
        Assert.assertTrue(page.findManageUsers(), "[Cannot find dt element with text = 'Manage Users']");
        Assert.assertTrue(page.findCreateDeleteModify(),
                "[Cannot find dd element with text = 'Create/delete/modify users that can log in to this Jenkins']");
    }

    @Test
    public void createUserTest() {
        driver.get(baseURL + manageURL);
        page.clickManageUsers();
        Assert.assertTrue(page.findCreateUser(), "[Cannot find link with text = 'Create User']");
    }

    @Test
    public void formCheckTest() {
        driver.get(baseURL + usersURL);
        page.clickCreateUser();
        Assert.assertTrue(page.findForm(), "[Cannot find form element]");
        Assert.assertTrue(page.findFormParts(), "[Cannot find one or more of form part(s)]");
        Assert.assertTrue(page.assertEmptyFormParts(), "[One or more form element(s) is(are) not empty]");
    }

    @Test
    public void checkTextElementForSomeUserTest() {
        driver.get(baseURL + usersURL + createUserURL);
        page.fillForm(USERNAME, PASSWORD, FULLNAME, EMAIL);
        page.submitFilledForm();
        Assert.assertTrue(page.findElementForSomeUser(), "[Cannot find td in tr with text = 'someuser']");
    }

    @Test
    public void deletingAssuranceTest() {
        driver.get(baseURL + usersURL + createUserURL);
        page.fillForm(USERNAME, PASSWORD, FULLNAME, EMAIL);
        page.submitFilledForm();
        driver.get(baseURL + usersURL);
        page.clickDeleteSomeUser();
        Assert.assertTrue(page.findDeleteAssertionText(),
                "[Current page does not contain text = 'Are you sure about deleting the user from Jenkins?']");
    }

    @Test
    public void absenceSomeUserAfterDeletingTest() {
        driver.get(baseURL + usersURL + createUserURL);
        page.fillForm(USERNAME, PASSWORD, FULLNAME, EMAIL);
        page.submitFilledForm();
        driver.get(baseURL + usersURL);
        page.clickDeleteSomeUser();
        page.submitDeletingSomeUser();
        Assert.assertFalse(page.findElementForSomeUser(), "[Current page contains element with text = 'someuser']");
        Assert.assertFalse(page.findDeleteHrefForSomeUser(),
                "[Current page contains link with href ='user/someuser/delete']");
    }

    @Test
    public void absenceOfAdminDeletingHrefTest() {
        driver.get(baseURL + usersURL);
        Assert.assertFalse(page.findDeleteHrefForAdmin(),
                "[Current page contains link with href ='user/admin/delete']");
    }

    @Test
    public void prohibitionOfEmptyFullNameTest() {
        driver.get(baseURL + usersURL + createUserURL);
        page.fillForm(USERNAME, PASSWORD, "", EMAIL);
        page.submitFilledForm();
        Assert.assertTrue(page.findProhibitedEmptyUserNameText(),
                "[Current page does not have text = '\"\" is prohibited as a full name for security reasons.']");
    }

    @Test
    public void buttonsColorTest() {
        page.logOut();
        driver.get(baseURL);
        Assert.assertEquals(page.assertThatLoginButtonIsRightlyColored(), "#4b758b",
                "[The color of login button is different from #4b758b]");
        page.logIn(LOGINUSER, LOGINPASS);
        driver.get(baseURL + usersURL + createUserURL);
        Assert.assertEquals(page.assertThatCreateUserButtonIsRightlyColored(), "#4b758b",
                "[The color of 'Create User' button is different from #4b758b]");
        page.fillForm(USERNAME, PASSWORD, FULLNAME, EMAIL);
        page.submitFilledForm();
        driver.get(baseURL + usersURL);
        page.clickDeleteSomeUser();
        Assert.assertEquals(page.assertThatDeleteYesButtonIsRightlyColored(), "#4b758b",
                "[The color of 'Yes' button is different from #4b758b]");
    }

    @Test
    public void cyclicLinkReplacementTest() {
        for (int i = 0; i < 2; i++) {
            page.clickEnableAutoRefresh();
            Assert.assertTrue(page.checkPresenceOfDisableAutoRefreshLink(),
                    "[Current page does not have link with text = 'DISABLE AUTO REFRESH']");
            Assert.assertFalse(page.checkPresenceOfEnableAutoRefreshLink(),
                    "[Current page has link with text = 'ENABLE AUTO REFRESH']");
            page.clickDisableAutoRefresh();
            Assert.assertFalse(page.checkPresenceOfDisableAutoRefreshLink(),
                    "[Current page has link with text = 'DISABLE AUTO REFRESH']");
            Assert.assertTrue(page.checkPresenceOfEnableAutoRefreshLink(),
                    "[Current does not have link with text = 'ENABLE AUTO REFRESH']");
        }
    }
}