package se.xfunserver.xfunapartments.api.model;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

public enum Setting {

    GENERAL_STORAGE_TYPE("storage.type"),

    GENERAL_STORAGE_FLATFILE_APARTMENTS("storage.options.flatfile.files.apartment"),
    GENERAL_STORAGE_FLATFILE_TYPE("storage.options.flatfile.files.type"),

    GENERAL_STORAGE_MYSQL_PREFIX("storage.options.mysql.prefix"),
    GENERAL_STORAGE_MYSQL_SERVER("storage.options.mysql.server"),
    GENERAL_STORAGE_MYSQL_PORT("storage.options.mysql.port"),
    GENERAL_STORAGE_MYSQL_DATABASE("storage.options.mysql.database"),
    GENERAL_STORAGE_MYSQL_USERNAME("storage.options.mysql.username"),
    GENERAL_STORAGE_MYSQL_PASSWORD("storage.options.mysql.password"),
    GENERAL_STORAGE_MYSQL_MAXIMUM_POOLS("storage.options.mysql.max_pools"),

    WORLD_WORLD_NAME("apartments.world.world_name"),
    WORLD_APARTMENT_DISTANCE("apartments.world.apartments_distance"),

    APARTMENT_ENTER_DISTANCE("apartments.teleportation.enter_distance"),
    REQUIRE_SNEAK("apartments.teleportation.require_sneak")

    ;

    @Getter
    private final String key;

    Setting(String key){
        this.key = key;
    }

    public static Optional<Setting> getSettingByKey(String key){
        return Arrays.stream(Setting.values())
                .filter(setting -> setting.getKey().equals(key))
                .findAny();
    }

}
