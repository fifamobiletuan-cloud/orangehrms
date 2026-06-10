package nguyenhuynhanhtuan;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;

// ExtentReports
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class TLULoginTest {
    private WebDriver driver;
    private static ExtentReports extent;
    private static ExtentSparkReporter spark;
    private ExtentTest test;

    @BeforeClass
    public static void setupExtent() {
        extent = new ExtentReports();
        spark = new ExtentSparkReporter("target/surefire-reports/extent-report.html");
        extent.attachReporter(spark);
    }

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        String ci = System.getenv("CI");
        if (ci != null && ci.equals("true")) {
            options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080", "--no-sandbox", "--disable-dev-shm-usage");
        }
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
    }

    @Test
    public void testLoginSuccess() {
        test = extent.createTest("TLU - Test Đăng Nhập Thành Công", "Kiểm tra đăng nhập TLU với tài khoản hợp lệ.");
        
        test.log(Status.INFO, "Đang mở trang https://sinhvien1.tlu.edu.vn/ ...");
        driver.get("https://sinhvien1.tlu.edu.vn/");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        test.log(Status.INFO, "Đang tìm ô nhập tài khoản...");
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        usernameField.clear();
        test.log(Status.INFO, "Nhập tài khoản: 2351067119");
        usernameField.sendKeys("2351067119");

        test.log(Status.INFO, "Nhập mật khẩu...");
        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.clear();
        passwordField.sendKeys("079205011830");

        test.log(Status.INFO, "Đang bấm nút đăng nhập...");
        WebElement loginButton = driver.findElement(By.cssSelector("button[data-ng-click='vm.login()']"));
        loginButton.click();

        test.log(Status.INFO, "Kiểm tra kết quả đăng nhập (chờ ô đăng nhập biến mất)...");
        boolean isSuccess = false;
        try {

            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("username")));
            isSuccess = true;
            test.log(Status.PASS, "Đăng nhập thành công, ô nhập username đã biến mất!");
        } catch (Exception e) {
            isSuccess = false;
            test.log(Status.FAIL, "Đăng nhập thất bại hoặc tải trang quá lâu.");
        }

        Assert.assertTrue(isSuccess, "Đăng nhập TLU phải thành công!");
    }

    @Test
    public void testLoginFail() {
        test = extent.createTest("TLU - Test Đăng Nhập Thất Bại", "Cố tình đăng nhập TLU sai mật khẩu để tạo lỗi.");
        
        test.log(Status.INFO, "Đang mở trang https://sinhvien1.tlu.edu.vn/ ...");
        driver.get("https://sinhvien1.tlu.edu.vn/");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        test.log(Status.INFO, "Đang tìm ô nhập tài khoản...");
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        usernameField.clear();
        test.log(Status.INFO, "Nhập tài khoản: 2351067119");
        usernameField.sendKeys("2351067119");

        test.log(Status.INFO, "Nhập mật khẩu sai...");
        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.clear();
        passwordField.sendKeys("wrong_password_here");

        test.log(Status.INFO, "Đang bấm nút đăng nhập...");
        WebElement loginButton = driver.findElement(By.cssSelector("button[data-ng-click='vm.login()']"));
        loginButton.click();

        test.log(Status.INFO, "Kiểm tra kết quả đăng nhập...");
        boolean isSuccess = false;
        try {

            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("username")));
            isSuccess = true;
            test.log(Status.PASS, "Đăng nhập thành công (Bất thường)!");
        } catch (Exception e) {
            isSuccess = false;
            test.log(Status.FAIL, "Đăng nhập thất bại đúng như kỳ vọng (ô nhập username vẫn hiển thị).");
        }


        Assert.assertFalse(isSuccess, "Cố tình tạo lỗi: Đăng nhập thất bại do nhập sai mật khẩu");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) driver.quit();
    }

    @AfterClass
    public static void tearDownExtent() {
        extent.flush();
    }
}
