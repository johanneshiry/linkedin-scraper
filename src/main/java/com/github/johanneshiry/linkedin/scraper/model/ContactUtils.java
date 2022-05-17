/*
 * © 2022. Johannes Hiry
 */

package com.github.johanneshiry.linkedin.scraper.model;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class ContactUtils {

  // todo extend
  private static final List<String> knownTitles = Arrays.asList("Dr.", "Dr.-Ing.", "Prof.");

  private static final Predicate<String> hasTitle = knownTitles::contains;

  public static Optional<String> titleFromName(String fullName) {
    return Optional.ofNullable(nameFilter(fullName, hasTitle))
        .filter(Predicate.not(String::isEmpty));
  }

  public static String nameWithoutTitle(String fullName) {
    return nameFilter(fullName, hasTitle.negate());
  }

  private static String nameFilter(String name, Predicate<String> filter) {
    return Arrays.stream(name.split(" "))
        .filter(filter)
        .collect(Collectors.joining(" "))
        .trim()
        .strip();
  }
}
