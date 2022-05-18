/*
 * © 2022. Johannes Hiry
 */

package io.github.johanneshiry.linkedin.scraper.main;

import io.github.johanneshiry.linkedin.scraper.model.ExtendedContact;
import io.github.johanneshiry.linkedin.scraper.model.LinkedInConstants;
import io.github.johanneshiry.linkedin.scraper.model.SimpleContact;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Scraper {

  private static final Logger log = LoggerFactory.getLogger(Scraper.class);

  private static final Duration BROWSER_WAIT_TIMEOUT = Duration.of(10, ChronoUnit.SECONDS);

  public static List<SimpleContact> getSimpleConnections(
      List<String> names, String loginUsername, String loginPassword, ChromeDriver driver) {
    // used to reset provided driver afterwards
    String initialUrl = driver.getCurrentUrl();

    try {
      loginToLinkedIn(driver, loginUsername, loginPassword);
    } catch (Exception e) {
      log.error("Cannot login to LinkedIn. ", e);
      return Collections.emptyList();
    }

    List<SimpleContact> connections = getConnectionsByNames(names, driver);

    // reset webdriver
    driver.get(initialUrl);

    return connections;
  }

  public static Optional<SimpleContact> getSimpleConnection(
      String name, String loginUsername, String loginPassword, ChromeDriver driver) {
    // used to reset provided driver afterwards
    String initialUrl = driver.getCurrentUrl();

    try {
      loginToLinkedIn(driver, loginUsername, loginPassword);
    } catch (Exception e) {
      log.error("Cannot login to LinkedIn. ", e);
      return Optional.empty();
    }

    Optional<SimpleContact> maybeContact = getConnectionByName(name, driver);

    // reset webdriver
    driver.get(initialUrl);

    return maybeContact;
  }

  public static Optional<ExtendedContact> getExtendedConnection(
      String name, String loginUsername, String loginPassword, ChromeDriver driver) {

    return getSimpleConnection(name, loginUsername, loginPassword, driver)
        .flatMap(
            simpleContact -> {
              driver.get(simpleContact.profileUrl().toString());
              WebElement profilePage = driver.findElement(LinkedInConstants.PROFILE_MAIN);

              return ExtendedContact.from(simpleContact, profilePage);
            });
  }

  public static int getNoOfContacts(
      String loginUsername, String loginPassword, ChromeDriver driver) {
    // used to reset provided driver afterwards
    String initialUrl = driver.getCurrentUrl();

    try {
      loginToLinkedIn(driver, loginUsername, loginPassword);
    } catch (Exception e) {
      log.error("Cannot login to LinkedIn. ", e);
      return -1;
    }

    openConnectionsPage(driver);

    WebElement header =
        driver.findElement(LinkedInConstants.LINKED_IN_CONNECTIONS_HEADER_CLASS_NAME);
    String headerString = header.findElement(By.xpath("//header/h1")).getAccessibleName();

    // reset webdriver
    driver.get(initialUrl);

    return Integer.parseInt(headerString.replaceAll("[\\D]", ""));
  }

  private static void loginToLinkedIn(
      WebDriver driver, String loginUsername, String loginPassword) {

    driver.get(LinkedInConstants.LOGIN_URL);
    new WebDriverWait(driver, BROWSER_WAIT_TIMEOUT)
        .until(ExpectedConditions.presenceOfElementLocated(LinkedInConstants.LOGIN_USERNAME));

    WebElement eMail = driver.findElement(LinkedInConstants.LOGIN_USERNAME);
    eMail.sendKeys(loginUsername);
    WebElement password = driver.findElement(LinkedInConstants.LOGIN_PASSWORD);
    password.sendKeys(loginPassword);

    password.submit();
  }

  private static void openConnectionsPage(WebDriver driver) {
    driver.get(LinkedInConstants.CONNECTIONS_URL);

    new WebDriverWait(driver, BROWSER_WAIT_TIMEOUT)
        .until(
            ExpectedConditions.presenceOfElementLocated(
                LinkedInConstants.CONNECTIONS_SECTION_CLASS_NAME));
  }

  private static Optional<SimpleContact> getConnectionByName(String name, WebDriver driver) {
    try {
      List<SimpleContact> contacts = getConnectionsByNames(List.of(name), driver);
      if (contacts.size() > 1) {
        log.warn(
            "Multiple LinkedIn connections for name '{}' found. Returning the first one found!",
            name);
      }
      return Optional.ofNullable(contacts.get(0));
    } catch (IndexOutOfBoundsException e) {
      return Optional.empty();
    }
  }

  private static List<SimpleContact> getConnectionsByNames(List<String> names, WebDriver driver) {
    openConnectionsPage(driver);

    final WebElement search = driver.findElement(LinkedInConstants.CONNECTIONS_SEARCH_INPUT);

    final Wait<WebDriver> wait =
        new FluentWait<>(driver)
            .withTimeout(Duration.ofSeconds(3L))
            .pollingEvery(Duration.ofMillis(500L))
            .ignoring(NoSuchElementException.class)
            .ignoring(StaleElementReferenceException.class);

    return names.stream()
        .flatMap(
            name -> {

              // search for name
              search.sendKeys(name);

              List<SimpleContact> linkedInContacts = Collections.emptyList();
              try {
                linkedInContacts =
                    wait.until(
                        webDriver -> {
                          List<SimpleContact> foundContacts =
                              webDriver
                                  .findElements(LinkedInConstants.CONNECTIONS_CARD_CLASS_NAME)
                                  .stream()
                                  .map(SimpleContact::from)
                                  .flatMap(Optional::stream)
                                  .filter(simpleContact -> simpleContact.name().equals(name))
                                  .toList();
                          if (foundContacts.isEmpty()) {
                            return null; // keep waiting
                          }
                          return foundContacts;
                        });
              } catch (TimeoutException e) {
                log.info(
                    "Cannot query information for contact '{}'. No LinkedIn connection found!",
                    name);
              }

              // clear search input
              search.clear();

              return linkedInContacts.stream();
            })
        .toList();
  }
}
