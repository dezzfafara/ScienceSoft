package sciencesoft;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.comparesEqualTo;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.hamcrest.number.OrderingComparison.lessThanOrEqualTo;
import static org.testng.Assert.assertEqualsNoOrder;
import static org.testng.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import helper.TestsHelper;

public class ShopByTests {

	private WebDriver driver;
	private ApplicationPage applicationPage;
	private final static Logger logger = Logger.getLogger(ShopByTests.class);
	private TestsHelper testsHelper;

	@Parameters({ "startUrl" })
	@BeforeTest
	public void setUp(String startUrl) {
		driver = new ChromeDriver();
		applicationPage = new ApplicationPage(driver);
		testsHelper = new TestsHelper(driver);
		driver.manage().window().maximize();
		driver.get(startUrl);
	}

	@Test(priority = 0)
	public void testOpenLaptopsPage() {
		applicationPage.moveToLaptops();
		assertTrue(applicationPage.isHeaderPresent());
		testsHelper.captureScreen(logger);
	}

	@Parameters({ "minPrice", "maxPrice", "lenovo", "hp", "dell", "minDiagonal", "maxDiagonal" })
	@Test(priority = 1)
	public void testSelectLaptops(String minPrice, String maxPrice, String lenovo, String dell, String hp,
			double minDiagonal, double maxDiagonal) {
		applicationPage.setPriceRange(minPrice, maxPrice);
		applicationPage.selectLaptops(lenovo, dell, hp);
		applicationPage.setDiagonal(minDiagonal, maxDiagonal);
		applicationPage.handleSearchResults();
		logger.info("Quantity of elements: " + applicationPage.getResultsCount());
		assertEqualsNoOrder(applicationPage.getArrayOfModels(), applicationPage.getArrayOfSelectedModels());
		assertTrue(applicationPage.isCorrectDiagonalsSelected(minDiagonal, maxDiagonal));
		testsHelper.captureScreen(logger);
	}

	@Parameters({ "minPrice" })
	@Test(priority = 2)
	public void testOrderByPriceAsc(String minPrice) {
		applicationPage.sortByPriceAsc();
		assertTrue(applicationPage.isSortedAsc());
		assertThat(applicationPage.getFirstPriceInList(), greaterThanOrEqualTo(Double.parseDouble(minPrice)));
		testsHelper.captureScreen(logger);
	}

	@Parameters({ "maxPrice" })
	@Test(priority = 3)
	public void testOrderByPriceDesc(String maxPrice) {
		applicationPage.sortByPriceDesc();
		assertTrue(applicationPage.isSortedDesc());
		assertThat(applicationPage.getFirstPriceInList(), lessThanOrEqualTo(Double.parseDouble(maxPrice)));
		testsHelper.captureScreen(logger);
	}

	@Test(priority = 4)
	public void testMoveToLastElement() {
		applicationPage.moveToLastElement();
		assertThat(applicationPage.compareDescription(), comparesEqualTo(0));
		testsHelper.captureScreen(logger);
	}

	@AfterTest
	public void tearDown() {
		driver.quit();
	}
}
