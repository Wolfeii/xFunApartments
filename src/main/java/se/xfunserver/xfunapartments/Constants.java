package se.xfunserver.xfunapartments;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

public final class Constants {

    public static final String STORAGE_TYPE_FLATFILE = "flatfile";
    public static final String STORAGE_TYPE_MYSQL = "mysql";

    public static final String INFO_MESSAGE_PREFIX = ":- ";

    public static final DecimalFormat DEFAULT_DECIMAL_FORMAT = new DecimalFormat("#");
    public static final Pattern INTEGER_PATTERN = Pattern.compile("^\\d+$");
}
