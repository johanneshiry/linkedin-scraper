/*
 * © 2022. Johannes Hiry
 */

package com.github.johanneshiry.linkedin.scraper.model;

import static com.github.johanneshiry.linkedin.scraper.model.LinkedInConstants.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import org.openqa.selenium.WebElement;

public record SimpleContact(Optional<String> title, String name, String occupation, URL url)
    implements Contact {

  public static Optional<SimpleContact> from(WebElement webElement) {
    WebElement cardDetails = webElement.findElement(CONNECTION_CARD_DETAILS);
    String name =
        ContactUtils.nameWithoutTitle(
            cardDetails.findElement(CONNECTION_CARD_NAME).getText().strip());
    Optional<String> title =
        ContactUtils.titleFromName(cardDetails.findElement(CONNECTION_CARD_NAME).getText().strip());

    Optional<URL> maybeUrl = Optional.empty();
    try {
      maybeUrl =
          Optional.of(new URL(webElement.findElement(CONNECTION_CARD_URL).getAttribute("href")));
    } catch (MalformedURLException e) {
      log.error("Cannot parse url for contact '{}' - skipping contact.", name, e);
    }

    String occupation = cardDetails.findElement(CONNECTION_CARD_OCCUPATION).getText().strip();
    return maybeUrl.map(url -> new SimpleContact(title, name, occupation, url));
  }
}
