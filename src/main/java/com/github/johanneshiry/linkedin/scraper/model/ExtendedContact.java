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
    String title,
    String name,
    String occupation,
    URL profileUrl,
    URL smallPictureUrl,
    URL largePictureUrl)
    implements Contact {

  public ExtendedContact(SimpleContact simpleContact, URL largePictureUrl) {
    this(
        simpleContact.title(),
        simpleContact.name(),
        simpleContact.occupation(),
        simpleContact.profileUrl(),
        simpleContact.smallPictureUrl(),
        largePictureUrl);
  }

  public static Optional<ExtendedContact> from(SimpleContact simpleContact, WebElement webElement) {

    URL pictureUrl = largePictureUrl(simpleContact.name(), webElement);

    return Optional.of(new ExtendedContact(simpleContact, pictureUrl));
  }

  private static URL largePictureUrl(String name, WebElement webElement) {
    try {
      return new URL(webElement.findElement(PROFILE_PICTURE).getAttribute("src"));
    } catch (MalformedURLException e) {
      log.error(
          "Cannot parse url for large contact picture of contact '{}'. Ignoring picture!", name, e);
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

  public Optional<URL> getLargePictureUrl() {
    return Optional.ofNullable(largePictureUrl);
  }
}
