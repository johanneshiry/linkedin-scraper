/*
 * © 2022. Johannes Hiry
 */

package com.github.johanneshiry.linkedin.scraper.model;

import java.net.URL;
import java.util.Optional;

public record ExtendedContact(
    Optional<String> title, String name, String occupation, URL profileUrl) implements Contact {}
