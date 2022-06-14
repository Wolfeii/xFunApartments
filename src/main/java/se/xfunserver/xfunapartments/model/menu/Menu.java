package se.xfunserver.xfunapartments.model.menu;

import lombok.Getter;
import lombok.Setter;
import se.xfunserver.xfunapartments.manager.menu.MenuManager;
import se.xfunserver.xfunapartments.xFunApartments;

import java.util.HashMap;
import java.util.Map;

public abstract class Menu {

    @Getter @Setter
    private String title;

    @Getter @Setter
    private int rows;

    @Getter @Setter
    private boolean modifiable;

    @Getter
    private MenuManager menuManager;
    @Getter
    private xFunApartments plugin;

    private final Map<String, Object> data = new HashMap<>();

    public Menu(String title, int rows) {
        this.title = title;
        this.rows = rows;
        this.modifiable = false;
    }


}
