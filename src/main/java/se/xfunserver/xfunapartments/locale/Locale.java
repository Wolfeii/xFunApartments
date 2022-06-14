package se.xfunserver.xfunapartments.locale;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import se.xfunserver.xfunapartments.storage.StorageManager;
import se.xfunserver.xfunapartments.xFunApartments;

public class Locale {

    public static final char DEFAULT_COLOR_CHAR = '&';
    private final StorageManager storageManager;

    public Locale(xFunApartments plugin) {
        this.storageManager = plugin.getStorageManager();
    }

    public void send(CommandSender sender, String key, boolean prefix, Placeholder... placeholders) {
        sendRaw(sender, (prefix ? getPrefix() : "") + storageManager.getLocaleByKey(key), true, placeholders);
    }

    public void sendRaw(CommandSender sender, String message, boolean colorPlaceholders, Placeholder... placeholders) {
        String locale = color(message);

        if (placeholders.length > 0) {
            locale = transformPlaceholders(message, placeholders);
        }

        if (colorPlaceholders)
            locale = color(locale);

        sender.sendMessage(locale);
    }

    public String getText(String key, Placeholder... placeholders) {
        String locale = color(storageManager.getLocaleByKey(key));
        if (placeholders.length > 0) {
            locale = transformPlaceholders(locale, placeholders);
        }

        return locale;
    }

    public String transformPlaceholders(String text, Placeholder... placeholders){
        String transformed = text;
        for(Placeholder placeholder : placeholders){
            transformed = placeholder.transform(transformed);
        }

        return transformed;
    }


    public static class Placeholder {

        private final String name;
        private final String value;

        public Placeholder(String name, String value){
            this.name  = name;
            this.value = value;
        }

        public String transform(String text){
            return text.replace("%" + name + "%", value);
        }

    }

    private String getPrefix(){
        return storageManager.getLocaleByKey("generic.prefix");
    }

    public String color(String text){
        return ChatColor.translateAlternateColorCodes(DEFAULT_COLOR_CHAR, text);
    }
}
