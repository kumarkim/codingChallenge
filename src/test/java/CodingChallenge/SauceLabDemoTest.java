package CodingChallenge;

import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.introspect.WithMember;
import com.google.common.collect.Comparators;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.nio.file.WatchEvent;
import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bouncycastle.pqc.jcajce.provider.rainbow.SignatureSpi.withSha224;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.openqa.selenium.support.locators.RelativeLocator;

public class SauceLabDemoTest {

	WebDriver driver;
	final static String URL = "https://www.saucedemo.com/";
	final static By userName = By.id("user-name");
	final static By pwd = By.id("password");
	final static By loginBtn = By.id("login-button");
	final static By addTocart = By.xpath("//button[text()='Add to cart']");
	final static By removeElem = By.xpath("//button[text()='Remove']");

	final static By invItmPrices = By.xpath("//div[@class='inventory_item_price']");

	@Test
	public void testAddtoCart_UsingSelenium4() {

		maxPrice_Using_List_Selenium4_relative_locators();

	}

	@Test
	public void testAddtoCart_UsingSelenium3() {

		maxPrice_Using_Tree_Map_Selenium3_dynamic_xpath();
	}

	private void maxPrice_Using_Tree_Map_Selenium3_dynamic_xpath() {

		// get list of all the Prices Webelements
		List<WebElement> prices = driver.findElements(invItmPrices);

		// add that into a reversely sorted tree map.
		Map<Double, WebElement> priceMap = new TreeMap<Double, WebElement>(Comparator.reverseOrder());

		// for each price , create a dynamic webelement and store it in a TreeMap with
		// price as key and webelement as value
		prices.forEach(p -> {
			Double price = Double.valueOf(p.getText().replace("$", "").trim());
			// create dynamic WebElement
			String webElemStr = "//div[text()='" + price + "']/following-sibling::button";
			WebElement webElement = driver.findElement(By.xpath(webElemStr));
			System.out.println(webElemStr);
			priceMap.put(price, webElement);
		});

		// get the first element (max priced prod since the map was reversly sorted and
		// click;
		WebElement maxPricedProd = priceMap.entrySet().iterator().next().getValue();
		maxPricedProd.click();

	}

	private void maxPrice_Using_List_Selenium4_relative_locators() {
		List<WebElement> prices = driver.findElements(invItmPrices);

		// find the max price from the list
		double maxprice = prices.stream().map(p -> p.getText().replace("$", "")).map(Double::valueOf)
				.mapToDouble(val -> val).max().getAsDouble();

		// make the dynamic webelem
		String mxpricStr = "//div[text()='" + maxprice + "']";
		WebElement maxpriceElem = driver.findElement(By.xpath(mxpricStr));

		System.out.println("maxpriceStr=" + maxpriceElem.getText());

		// click on add to cart button usng relative locator
		WebElement addtoCart = driver.findElement(RelativeLocator.with(addTocart).toRightOf(maxpriceElem));
		addtoCart.click();

		// verify the ADD to cart has been changed to Remove
		WebElement remove = driver.findElement(RelativeLocator.with(removeElem).toRightOf(maxpriceElem));

		Assert.assertEquals("REMOVE", remove.getText(), "mismatch");
	}

	private void loginToSauceLabs() {
		driver.findElement(userName).sendKeys("standard_user");
		driver.findElement(pwd).sendKeys("secret_sauce");
		driver.findElement(loginBtn).click();

	}

	@BeforeMethod
	public void setup() {

		WebDriverManager.chromedriver().setup();
		WebDriverManager ins = WebDriverManager.getInstance();
		String path = ins.getBrowserPath().get().toString();
		System.out.println("path of chromeDriver =" + path);

		driver = new ChromeDriver();
		driver.get(URL);
		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(1));

		loginToSauceLabs();
	}

	@AfterMethod
	public void tearDown() {
		//driver.quit();
	}

}
