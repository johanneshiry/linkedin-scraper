/*
 * © 2022. Johannes Hiry
 */

package com.github.johanneshiry.linkedin.scraper.model;

import org.openqa.selenium.By;

public class LinkedInConstants {

  /** login window */
  public static final String LOGIN_URL = "https://www.linkedin.com/login";

  public static final By LOGIN_USERNAME = By.id("username");

  public static final By LOGIN_PASSWORD = By.id("password");

  /** network / connections window */
  public static final String CONNECTIONS_URL =
      "https://www.linkedin.com/mynetwork/invite-connect/connections/";

  public static final By CONNECTIONS_SECTION_CLASS_NAME = By.className("mn-connections");

  public static final By LINKED_IN_CONNECTIONS_HEADER_CLASS_NAME =
      By.className("mn-connections__header");

  public static final By CONNECTIONS_SEARCH_INPUT = By.className("mn-connections__search-input");

  public static final By CONNECTIONS_CARD_CLASS_NAME = By.className("mn-connection-card");

  public static final By CONNECTION_CARD_DETAILS = By.className("mn-connection-card__details");

  public static final By CONNECTION_CARD_NAME = By.className("mn-connection-card__name");

  public static final By CONNECTION_CARD_URL = By.className("mn-connection-card__link");

  public static final By CONNECTION_CARD_PICTURE = By.className("mn-connection-card__picture");

  public static final By CONNECTION_CARD_OCCUPATION =
      By.className("mn-connection-card__occupation");
}
