import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

public class Manager {
    private WebDriverWait wait;
    private WebDriver driver;
    private StringBuffer errorMessages;

    @FindBy(name = "j_username")
    private WebElement loginUsernameInput;

    @FindBy(name = "j_password")
    private WebElement loginPasswordInput;

    @FindBy(id = "yui-gen1-button")
    private WebElement loginSubmitInput;

    @FindBy(xpath = "//a[contains(@href, '/logout')]")
    private WebElement logoutLink;

    @FindBy(linkText = "Manage Jenkins")
    private WebElement manageJenkinsLink;

    @FindBy(xpath = "//dt[text()='Manage Users']")
    private WebElement manageUsersDt;

    @FindBy(xpath = "//dd[text()='Create/delete/modify users that can log in to this Jenkins']")
    private WebElement createDeleteModifyDd;

    @FindBy(xpath = "//a/dl/dt[text() = 'Manage Users']")
    private WebElement manageUsersLink;

    @FindBy(linkText = "Create User")
    private WebElement createUserLink;

    @FindBy(xpath = "//form")
    private WebElement form;

    @FindBy(xpath = "//input[@name='username' and contains (@type, 'text')]")
    private WebElement usernameInput;

    @FindBy(xpath = "//input[@name='password1' and contains (@type, 'password')]")
    private WebElement passwordInput;

    @FindBy(xpath = "//input[@name='password2' and contains (@type, 'password')]")
    private WebElement confirmPasswordInput;

    @FindBy(xpath = "//input[@name='fullname' and contains (@type, 'text')]")
    private WebElement fullNameInput;

    @FindBy(xpath = "//input[@name='email' and contains (@type, 'text')]")
    private WebElement emailInput;

    @FindBy(xpath = "//button[text()='Create User']")
    private WebElement createUserButton;

    @FindBy(xpath = "//tr/td[text()='someuser']")
    private WebElement someUserTdElement;

    @FindBy(xpath = "//a[contains(@href, 'user/someuser/delete')]")
    private WebElement deleteSomeUserLink;

    @FindBy(xpath = "//a[contains(@href, 'user/admin/delete')]")
    private WebElement deleteAdminLink;

    @FindBy(xpath = "//button[text()='Yes']")
    private WebElement yesForDeletionButton;

    @FindBy(xpath = "//a[contains(@href, 'user/someuser/')]")
    private WebElement presentForCreatedSomeUserLink;

    @FindBy(linkText = "ENABLE AUTO REFRESH")
    private WebElement enableAutoRefreshLink;

    @FindBy(linkText = "DISABLE AUTO REFRESH")
    private WebElement disableAutoRefreshLink;

    public Manager(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(this.driver, 30);
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        this.errorMessages = new StringBuffer();
    }

    public void logIn(String login, String password) {
        loginUsernameInput.clear();
        loginUsernameInput.sendKeys(login);
        loginPasswordInput.clear();
        loginPasswordInput.sendKeys(password);
        loginSubmitInput.click();
    }

    public void logOut() {
        if (isElementPresent(logoutLink)) {
            logoutLink.click();
        }
    }

    private boolean isElementPresent(WebElement webElement) {
        try {
            webElement.isDisplayed();
            return true;
        } catch (NoSuchElementException e) {
            this.errorMessages.append(e + "\n");
            return false;
        }
    }

    public boolean assertThatSomeUserIsCreated() {
        return isElementPresent(presentForCreatedSomeUserLink);
    }

    public void clickManageJenkins() {
        if (isElementPresent(manageJenkinsLink)) {
            manageJenkinsLink.click();
        } else {
            this.errorMessages.append("Cannot find element to manage Jenkins\n");
        }
    }

    public boolean findManageUsers() {
        return isElementPresent(manageUsersDt);
    }

    public boolean findCreateDeleteModify() {
        return isElementPresent(createDeleteModifyDd);
    }

    public void clickManageUsers() {
        if (isElementPresent(manageUsersLink)) {
            manageUsersLink.click();
        } else {
            this.errorMessages.append("Cannot find element to manage users\n");
        }
    }

    public boolean findCreateUser() {
        return isElementPresent(createUserLink);
    }

    public void clickCreateUser() {
        if (isElementPresent(createUserLink)) {
            createUserLink.click();
        } else {
            this.errorMessages.append("Cannot find element to create user\n");
        }
    }

    public boolean findForm() {
        return isElementPresent(form);
    }

    public boolean findFormParts() {
        return (findForm()) && (isElementPresent(usernameInput) && isElementPresent(passwordInput) &&
                isElementPresent(confirmPasswordInput) && isElementPresent(fullNameInput) &&
                isElementPresent(emailInput));
    }

    public boolean assertEmptyFormParts() {
        return (findFormParts()) && usernameInput.getText().equals("") &&
                passwordInput.getText().equals("") &&
                confirmPasswordInput.getText().equals("") &&
                fullNameInput.getText().equals("") &&
                emailInput.getText().equals("");
    }

    public void fillForm(String username, String password, String fullName, String email) {
        if (findFormParts()) {
            usernameInput.sendKeys(username);
            passwordInput.sendKeys(password);
            confirmPasswordInput.sendKeys(password);
            fullNameInput.sendKeys(fullName);
            emailInput.sendKeys(email);
        }
    }

    public void submitFilledForm() {
        if (isElementPresent(createUserButton)) {
            createUserButton.click();
        } else {
            this.errorMessages.append("Cannot find element to submit form\n");
        }
    }

    public boolean findElementForSomeUser() {
        return isElementPresent(someUserTdElement);
    }

    public void clickDeleteSomeUser() {
        if (isElementPresent(deleteSomeUserLink)) {
            deleteSomeUserLink.click();
        } else {
            this.errorMessages.append("Cannot find link to delete someuser\n");
        }
    }

    public void submitDeletingSomeUser() {
        if (isElementPresent(yesForDeletionButton)) {
            yesForDeletionButton.click();
        } else {
            this.errorMessages.append("Cannot find button 'Yes' to submit deleting someuser\n");
        }
    }

    public boolean findDeleteAssertionText() {
        String source = driver.getPageSource();
        return source.contains("Are you sure about deleting the user from Jenkins?");
    }

    public boolean findDeleteHrefForSomeUser() {
        return isElementPresent(deleteSomeUserLink);
    }

    public boolean findDeleteHrefForAdmin() {
        return isElementPresent(deleteAdminLink);
    }

    public boolean findProhibitedEmptyUserNameText() {
        String source = driver.getPageSource();
        return source.contains("\"\" is prohibited as a full name for security reasons.");
    }

    public String assertThatLoginButtonIsRightlyColored() {
        if (isElementPresent(loginSubmitInput)) {
            return Color.fromString(loginSubmitInput.getCssValue("background-color")).asHex();
        } else {
            return "";
        }
    }

    public String assertThatCreateUserButtonIsRightlyColored() {
        if (isElementPresent(createUserButton)) {
            return Color.fromString(createUserButton.getCssValue("background-color")).asHex();
        } else {
            return "";
        }
    }

    public String assertThatDeleteYesButtonIsRightlyColored() {
        if (isElementPresent(yesForDeletionButton)) {
            return Color.fromString(yesForDeletionButton.getCssValue("background-color")).asHex();
        } else {
            return "";
        }
    }

    public void clickEnableAutoRefresh() {
        if (isElementPresent(enableAutoRefreshLink)) {
            enableAutoRefreshLink.click();
        } else {
            this.errorMessages.append("Cannot find button 'ENABLE AUTO REFRESH'\n");
        }
    }

    public boolean checkPresenceOfEnableAutoRefreshLink() {
        return (isElementPresent(enableAutoRefreshLink));
    }

    public void clickDisableAutoRefresh() {
        if (isElementPresent(disableAutoRefreshLink)) {
            disableAutoRefreshLink.click();
        } else {
            this.errorMessages.append("Cannot find button 'DISABLE AUTO REFRESH'\n");
        }
    }

    public boolean checkPresenceOfDisableAutoRefreshLink() {
        return (isElementPresent(disableAutoRefreshLink));
    }
}