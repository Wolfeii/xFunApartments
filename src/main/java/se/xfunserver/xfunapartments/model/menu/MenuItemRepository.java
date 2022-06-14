package se.xfunserver.xfunapartments.model.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MenuItemRepository {

    private final List<MenuItem> items = new ArrayList<>();

    public MenuItemRepository(MenuItem... items) {
        this.items.addAll(Arrays.stream(items)
                .collect(Collectors.toList()));
    }

    public List<MenuItem> getItems() {
        return items;
    }

    private void removeIfExists(MenuItem item) {
        items.removeIf(menuItem -> item.getSlot() == menuItem.getSlot());
    }
}
