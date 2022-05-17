/*
 * © 2022. Johannes Hiry
 */

package com.github.johanneshiry.linkedin.scraper.model;

import static com.github.johanneshiry.linkedin.scraper.model.LinkedInConstants.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public record SimpleContact(
    Optional<String> title,
    String name,
    String occupation,
    URL profileUrl,
    Optional<URL> smallPictureUrl)
    implements Contact {

  public static Optional<SimpleContact> from(WebElement webElement) {
    WebElement cardDetails = webElement.findElement(CONNECTION_CARD_DETAILS);
    String name =
        ContactUtils.nameWithoutTitle(
            cardDetails.findElement(CONNECTION_CARD_NAME).getText().strip());
    Optional<String> title =
        ContactUtils.titleFromName(cardDetails.findElement(CONNECTION_CARD_NAME).getText().strip());

    Optional<URL> maybeProfileUrl = Optional.empty();
    try {
      maybeProfileUrl =
          Optional.of(new URL(webElement.findElement(CONNECTION_CARD_URL).getAttribute("href")));
    } catch (MalformedURLException e) {
      log.error("Cannot parse url for contact '{}' - skipping contact.", name, e);
    }

    String occupation = cardDetails.findElement(CONNECTION_CARD_OCCUPATION).getText().strip();
    return maybeProfileUrl.map(
        profileUrl ->
            new SimpleContact(
                title, name, occupation, profileUrl, smallPictureUrl(name, webElement)));
  }

  private static Optional<URL> smallPictureUrl(String name, WebElement webElement) {
    try {
      return Optional.of(
          new URL(
              webElement
                  .findElement(CONNECTION_CARD_PICTURE)
                  .findElement(By.className("presence-entity__image"))
                  .getAttribute("src")));
    } catch (MalformedURLException e) {
      log.error(
          "Cannot parse url for small contact picture of contact '{}'. Ignoring picture!", name, e);
      return Optional.empty();
    }
  }
}
