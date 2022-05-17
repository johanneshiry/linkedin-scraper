/*
 * © 2022. Johannes Hiry
 */

package com.github.johanneshiry.linkedin.scraper.model;

import java.net.URL;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public sealed interface Contact permits SimpleContact, ExtendedContact {

  Logger log = LoggerFactory.getLogger(Contact.class);

  Optional<String> title();

  String name();

  URL url();

  String occupation();
}
