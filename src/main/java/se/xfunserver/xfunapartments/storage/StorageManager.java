package se.xfunserver.xfunapartments.storage;

import lombok.Getter;
import se.xfunserver.xfunapartments.api.model.Apartment;
import se.xfunserver.xfunapartments.model.apartment.ApartmentType;
import se.xfunserver.xfunapartments.xFunApartments;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class StorageManager {

    protected final LinkedList<Apartment> cachedApartments;
    @Getter
    public final List<ApartmentType> cachedTypes;
    protected final Map<String, Object> cachedLocaleData;

    private final xFunApartments plugin;

    protected StorageManager(xFunApartments plugin) {
        cachedApartments = new LinkedList<>();
        cachedTypes = new ArrayList<>();
        cachedLocaleData = new HashMap<>();
        this.plugin = plugin;
    }

    public void load() {
        setup();
        cachedApartments.clear();
        cachedTypes.clear();
        cachedLocaleData.clear();

        Arrays.stream(StorageManager.Type.values())
                .filter(plugin::isPreload)
                .forEach(this::preload);
    }

    public LinkedList<Apartment> getApartmentsByOwner(UUID uniqueId) {
        Supplier<Stream<Apartment>> streamSupplier = () -> cachedApartments.stream()
                .filter(apartment -> apartment.getOwner().equals(uniqueId));

        LinkedList<Apartment> collect = streamSupplier.get()
                .sorted(Comparator.comparingInt(o -> o.getExpansions().size()))
                .collect(Collectors.toCollection(LinkedList::new));

        return collect;
    }

    public abstract void setup();

    public abstract void preload(Type type);

    public abstract void storeApartment(Apartment apartment, boolean save);

    public abstract void updateApartment(Apartment apartment, StorageManager.Action action);

    public abstract void storeType(ApartmentType type);

    public abstract void removeType(ApartmentType type);

    public Optional<ApartmentType> getTypeByUniqueId(UUID uniqueId) {
        return cachedTypes.stream()
                .filter(apartmentType -> apartmentType.getUniqueId().equals(uniqueId))
                .findAny();
    }

    public Optional<ApartmentType> getTypeByName(String name){
        return cachedTypes.stream()
                .filter(petType -> petType.getName().equalsIgnoreCase(name))
                .findAny();
    }

    public String getLocaleByKey(String key) {
        if (!cachedLocaleData.containsKey(key))
            return key;

        return (String) cachedLocaleData.get(key);
    }

    /* Model */

    public enum Type {
        TYPE,
        APARTMENT,
        LOCALE,
    }

    public enum Action {
        REMOVE,
        EXPANSION,
    }
}
