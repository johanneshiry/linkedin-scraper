/*
 * © 2022. Johannes Hiry
 */

package com.github.johanneshiry.linkedin.scraper.model;

import static com.github.johanneshiry.linkedin.scraper.model.LinkedInConstants.PROFILE_PICTURE;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import org.openqa.selenium.WebElement;

public record ExtendedContact(
    Optional<String> title,
    String name,
    String occupation,
    URL profileUrl,
    Optional<URL> smallPictureUrl,
    Optional<URL> largePictureUrl)
    implements Contact {

  public ExtendedContact(SimpleContact simpleContact, Optional<URL> largePictureUrl) {
    this(
        simpleContact.title(),
        simpleContact.name(),
        simpleContact.occupation(),
        simpleContact.profileUrl(),
        simpleContact.smallPictureUrl(),
        largePictureUrl);
  }

  public static Optional<ExtendedContact> from(SimpleContact simpleContact, WebElement webElement) {

    Optional<URL> pictureUrl = largePictureUrl(simpleContact.name(), webElement);

    return Optional.of(new ExtendedContact(simpleContact, pictureUrl));
  }

  private static Optional<URL> largePictureUrl(String name, WebElement webElement) {
    try {
      return Optional.of(new URL(webElement.findElement(PROFILE_PICTURE).getAttribute("src")));
    } catch (MalformedURLException e) {
      log.error(
          "Cannot parse url for large contact picture of contact '{}'. Ignoring picture!", name, e);
      return Optional.empty();
    }
  }
}
