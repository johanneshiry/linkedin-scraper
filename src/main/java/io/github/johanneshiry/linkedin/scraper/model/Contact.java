/*
 * © 2022. Johannes Hiry
 */

package io.github.johanneshiry.linkedin.scraper.model;

import java.net.URL;
import java.util.Optional;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public sealed interface Contact permits SimpleContact, ExtendedContact {

  Logger log = LoggerFactory.getLogger(Contact.class);

  @Nullable
  String title();

  Optional<String> getTitle();

  String name();

  URL profileUrl();

  String occupation();

  @Nullable
  URL smallPictureUrl();

  Optional<URL> getSmallPictureUrl();
}
