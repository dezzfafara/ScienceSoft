package sciencesoft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ApplicationPage extends Page {

	private String modelXPathPattern = ".//label[text()='";
	private TreeMap<Double, WebElement> mapOfDiagonals = new TreeMap<Double, WebElement>();
	private String firstElementDescription;
	private final static String firstResultXPath = "(.//div[@class='ModelList__DescBlock'])[1]";
	private String[] arrayOfModels;
	private String[] arrayOfSelectedModels;
	private String[] arrayOfSelectedDiagonals;
	private ArrayList<Double> sortedPrices;
	private final static Logger logger = Logger.getLogger(ApplicationPage.class);

	@FindBy(xpath = ".//a[@href='/kompyutery/']")
	private WebElement computers;

	@FindBy(xpath = ".//span[@title='Ноутбуки']")
	private WebElement laptops;

	@FindBy(xpath = ".//h1[@class='Page__TitleActivePage' and text()='Ноутбуки']")
	private WebElement laptopsHeader;

	@FindBy(xpath = "(.//w-div)[1]")
	private WebElement banner;

	@FindBy(xpath = "(.//w-div//w-span)[10]")
	private WebElement other;

	@FindBy(xpath = "(.//w-div//button)[2]")
	private WebElement send;

	@FindBy(xpath = ".//input[@name='prof_1000' and @checked]//following-sibling::label[contains (@for, 'prof_1000')]")
	private List<WebElement> selectedModels;

	@FindBy(xpath = ".//input[@name='prof_5828' and @checked]//following-sibling::label[contains (@for, 'prof_5828')]")
	private List<WebElement> selectedDiagonals;

	@FindBy(xpath = ".//span[@class='count_passive count_active']")
	private WebElement resultsCount;

	@FindBy(xpath = ".//input[@name='price_before']")
	private WebElement minPrice;

	@FindBy(xpath = ".//input[@name='price_after']")
	private WebElement maxPrice;

	@FindBy(xpath = ".//label[contains (@for, 'prof_5828')]")
	private List<WebElement> diagonals;

	@FindBy(xpath = ".//span[@data-idgroup='prof_5828' and @class='ModelFilter__OpenHideAttrTxt Page__DarkDotLink']")
	private WebElement expandList;

	@FindBy(xpath = ".//div[@class='ModelFilter__NumModelBtn Page__ActiveButtonBg ModelFilter__GALink']")
	private WebElement showButton;

	@FindBy(xpath = "(.//span[@class='chzn-container chzn-container-single'])[2]")
	private WebElement sortOptions;

	@FindBy(xpath = ".//li[contains(text(), 'цене (с дешевых)')]")
	private WebElement lowestPrice;

	@FindBy(xpath = ".//li[contains(text(), 'цене (с дорогих)')]")
	private WebElement highestPrice;

	@FindBy(xpath = "(.//div[@class='ModelList__DescBlock'])[1]")
	private WebElement firstSearchResult;

	@FindBy(xpath = ".//div[@class='ModelList__DescBlock']")
	private List<WebElement> allResults;

	@FindBy(xpath = ".//a[@class='Paging__PageLink ']")
	private List<WebElement> paging;

	@FindBy(xpath = ".//span[@class='PriceBlock__PriceValue' and contains (text(), 'от')]//span")
	private List<WebElement> prices;

	public ApplicationPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
		this.sortedPrices = new ArrayList<Double>();
	}

	public String[] getArrayOfModels() {
		return this.arrayOfModels;
	}

	public String[] getArrayOfSelectedModels() {
		return this.arrayOfSelectedModels;
	}

	public double getFirstPriceInList() {
		return sortedPrices.get(0);
	}

	public int getResultsCount() {
		return allResults.size();
	}

	public int compareDescription() {
		return getElementsText(allResults.get(allResults.size() - 1)).compareTo(firstElementDescription);
	}

	private void initMapOfDiagonals() {
		diagonals.stream()
				.forEach(d -> mapOfDiagonals.put(Double.parseDouble(d.getText().replaceAll("\\([0-9]*\\)", "")), d));
	}

	private void fillArrayOfSelectedItems(String[] array, List<WebElement> list, String replacePattern) {
		int index = 0;
		smartWait(list);
		Iterator<WebElement> iter = list.iterator();
		while (iter.hasNext()) {
			array[index] = iter.next().getText().replaceAll(replacePattern, "");
			index++;
		}
	}

	private void initListOfPrices() {
		smartWait(prices);
		sortedPrices.clear();
		prices.stream().forEach(price -> sortedPrices
				.add(Double.parseDouble(price.getText().replaceAll(" ", "").replaceAll(",", "."))));
	}

	public boolean isSortedAsc() {
		initListOfPrices();
		for (int i = 1; i < sortedPrices.size(); i++) {
			if (sortedPrices.get(i) < sortedPrices.get(i - 1)) {
				return false;
			}
		}
		return true;
	}

	public boolean isSortedDesc() {
		initListOfPrices();
		for (int i = 1; i < sortedPrices.size(); i++) {
			if (sortedPrices.get(i) > sortedPrices.get(i - 1)) {
				return false;
			}
		}
		return true;
	}

	public boolean isCorrectDiagonalsSelected(double minDiagonal, double maxDiagonal) {
		for (String diagonal : arrayOfSelectedDiagonals) {
			if (Double.parseDouble(diagonal) < minDiagonal || Double.parseDouble(diagonal) > maxDiagonal) {
				return false;
			}
		}
		return true;
	}

	public boolean isHeaderPresent() {
		return laptopsHeader.isDisplayed();
	}

	public void moveToLaptops() {
		try {
			moveToAndClick(other);
			moveToAndClick(send);
		} catch (NoSuchElementException e) {
			logger.error("No such element...");
		}
		smartWait(computers);
		new Actions(driver).moveToElement(computers).build().perform();
		smartWait(laptops);
		moveToAndClick(laptops);
	}

	public void selectLaptops(String... models) {
		arrayOfModels = models;
		Arrays.asList(models).stream()
				.forEach(m -> smartClick(driver.findElement(By.xpath(modelXPathPattern + m + "']"))));
		smartWait(resultsCount);
	}

	public void setPriceRange(String minPrice, String maxPrice) {
		clearAndSendKeys(this.minPrice, minPrice);
		clearAndSendKeys(this.maxPrice, maxPrice);
		smartWait(resultsCount);
	}

	public void setDiagonal(double minDiagonal, double maxDiagonal) {
		smartClick(expandList);
		smartWait(diagonals);
		initMapOfDiagonals();
		for (Map.Entry<Double, WebElement> diagonal : mapOfDiagonals.entrySet()) {
			if (diagonal.getKey() >= minDiagonal && diagonal.getKey() <= maxDiagonal) {
				smartClick(diagonal.getValue());
			}
		}
		smartWait(resultsCount);
	}

	public void handleSearchResults() {
		smartWait(resultsCount);
		smartClick(showButton);
		arrayOfSelectedModels = new String[selectedModels.size()];
		arrayOfSelectedDiagonals = new String[selectedDiagonals.size()];
		fillArrayOfSelectedItems(arrayOfSelectedModels, selectedModels, "[^a-zA-z]");
		fillArrayOfSelectedItems(arrayOfSelectedDiagonals, selectedDiagonals, "\\([0-9]*\\)");
	}

	public void sortByPriceAsc() {
		smartClick(sortOptions);
		smartClick(lowestPrice);
		firstElementDescription = getElementsText(driver.findElement(By.xpath(firstResultXPath)));
	}

	public void sortByPriceDesc() {
		smartClick(sortOptions);
		smartClick(highestPrice);
	}

	public void moveToLastElement() {
		paging.get(paging.size() - 1).click();
	}
}
