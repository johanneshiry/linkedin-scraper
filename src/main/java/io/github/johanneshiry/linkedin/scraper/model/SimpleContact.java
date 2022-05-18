/*
 * © 2022. Johannes Hiry
 */

package io.github.johanneshiry.linkedin.scraper.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public record SimpleContact(
    String title, String name, String occupation, URL profileUrl, URL smallPictureUrl)
    implements Contact {

  public static Optional<SimpleContact> from(WebElement webElement) {
    WebElement cardDetails = webElement.findElement(LinkedInConstants.CONNECTION_CARD_DETAILS);
    String name =
        ContactUtils.nameWithoutTitle(
            cardDetails.findElement(LinkedInConstants.CONNECTION_CARD_NAME).getText().strip());
    String title =
        ContactUtils.titleFromName(
                cardDetails.findElement(LinkedInConstants.CONNECTION_CARD_NAME).getText().strip())
            .orElse(null);

    Optional<URL> maybeProfileUrl = Optional.empty();
    try {
      maybeProfileUrl =
          Optional.of(
              new URL(
                  webElement
                      .findElement(LinkedInConstants.CONNECTION_CARD_URL)
                      .getAttribute("href")));
    } catch (MalformedURLException e) {
      log.error("Cannot parse url for contact '{}' - skipping contact.", name, e);
    }

    String occupation =
        cardDetails.findElement(LinkedInConstants.CONNECTION_CARD_OCCUPATION).getText().strip();
    return maybeProfileUrl.map(
        profileUrl ->
            new SimpleContact(
                title, name, occupation, profileUrl, smallPictureUrl(name, webElement)));
  }

  private static URL smallPictureUrl(String name, WebElement webElement) {
    try {
      return new URL(
          webElement
              .findElement(LinkedInConstants.CONNECTION_CARD_PICTURE)
              .findElement(By.className("presence-entity__image"))
              .getAttribute("src"));
    } catch (MalformedURLException e) {
      log.error(
          "Cannot parse url for small contact picture of contact '{}'. Ignoring picture!", name, e);
      return null;
    }
  }

  @Override
  public Optional<String> getTitle() {
    return Optional.ofNullable(title);
  }

  @Override
  public Optional<URL> getSmallPictureUrl() {
    return Optional.ofNullable(smallPictureUrl);
  }
}
