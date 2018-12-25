package sciencesoft;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Page {
	private final static int WAIT_PERIOD = 60;
	private final static int DURATION = 200;
	protected WebDriver driver;
	protected WebDriverWait wait;
	protected Wait<WebDriver> fluentWait;

	public Page(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, WAIT_PERIOD);
		fluentWait = new FluentWait<>(driver).withTimeout(Duration.ofSeconds(WAIT_PERIOD))
				.pollingEvery(Duration.ofMillis(DURATION)).ignoring(StaleElementReferenceException.class);
	}

	protected void moveToAndClick(WebElement element) {
		new Actions(driver).moveToElement(element).build().perform();
		element.click();
	}

	protected void clearAndSendKeys(WebElement element, String value) {
		element.clear();
		element.sendKeys(value);
	}

	protected void smartWait(WebElement element) {
		wait.ignoring(StaleElementReferenceException.class).until(ExpectedConditions
				.and(ExpectedConditions.visibilityOf(element), ExpectedConditions.elementToBeClickable(element)));
	}

	protected void smartWait(List<WebElement> elements) {
		wait.until(ExpectedConditions.visibilityOfAllElements(elements));
	}

	protected void smartClick(WebElement element) {
		fluentWait.until(ExpectedConditions.and(ExpectedConditions.visibilityOf(element),
				ExpectedConditions.elementToBeClickable(element)));
		element.click();
	}

	protected String getElementsText(WebElement element) {
		smartWait(element);
		return element.getText();
	}
}
