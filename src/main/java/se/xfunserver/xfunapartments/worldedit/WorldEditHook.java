package se.xfunserver.xfunapartments.worldedit;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import se.xfunserver.xfunapartments.apartments.ApartmentSchematic;

import java.io.File;
import java.util.Collections;
import java.util.List;

public interface WorldEditHook<S> {
    void fill(WorldEditRegion region, List<ItemStack> blocks);

    default void fillSingle(WorldEditRegion region, ItemStack block) {
        fill(region, Collections.singletonList(block));
    }

    default void fillAir(WorldEditRegion region) {
        fillSingle(region, new ItemStack(Material.AIR));
    }

    ApartmentFactoryCompat<S> createApartmentFactoryCompat();

    ApartmentSchematic<S> loadApartmentSchematic(String name, File file, Material loadBlock);
}