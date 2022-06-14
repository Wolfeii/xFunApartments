package se.xfunserver.xfunapartments.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import se.xfunserver.xfunapartments.api.events.ApartmentCreateTypeEvent;
import se.xfunserver.xfunapartments.api.model.Apartment;
import se.xfunserver.xfunapartments.locale.Locale;
import se.xfunserver.xfunapartments.model.apartment.ApartmentType;
import se.xfunserver.xfunapartments.storage.StorageManager;
import se.xfunserver.xfunapartments.xFunApartments;


import java.io.File;
import java.util.List;
import java.util.UUID;

@CommandAlias("lägenheter|apartment|lgh|lägenhet")
public class ApartmentsCommand extends BaseCommand {

    private final xFunApartments plugin;
    private final Locale locale;

    public ApartmentsCommand(xFunApartments plugin) {
        this.plugin = plugin;
        this.locale = plugin.getLocale();
    }

    @SuppressWarnings({"unused"})
    @Default
    @Description("Hantera dina nuvarande lägenheter")
    public void main(Player player) {
        List<Apartment> apartments = plugin.getApartmentManager()
                .getApartmentsByOwner(player);

        if (apartments.size() > 0) {
            return;
        }

        locale.send(player, "generic.no-apartment", false);
    }

    @Subcommand("create")
    @CommandPermission("xfunapartments.create.type")
    @Description("Skapar en lägenhets typ som sparas i databasen.")
    public void createType(Player player, String name, String schematic, @Conditions("limits:min=0,max=1000000000") Double apartmentCost) {
        StorageManager storageManager = plugin.getStorageManager();

        if (storageManager.getTypeByName(name).isPresent()) {
            plugin.getLocale().send(player, "commands.admin.type.create.already_exists", true);
            return;
        }

        File file = new File(plugin.getDataFolder(), "schematics/" + schematic + ".schem");
        if (!file.exists()) {
            plugin.getLocale().send(player, "commands.admin.type.create.no_schematic", true);
            return;
        }

        ApartmentType type = ApartmentType.builder()
                .uniqueId(UUID.randomUUID())
                .name(name)
                .price(apartmentCost)
                .schematic(plugin.getWorldEditHook().loadApartmentSchematic(schematic, file, Material.RED_GLAZED_TERRACOTTA))
                .entranceLocation(player.getLocation())
                .build();

        ApartmentCreateTypeEvent event = new ApartmentCreateTypeEvent(player, type);
        if (event.isCancelled()) {
            return;
        }

        storageManager.storeType(
                ApartmentType.builder()
                        .uniqueId(UUID.randomUUID())
                        .name(name)
                        .price(apartmentCost)
                        .schematic(plugin.getWorldEditHook().loadApartmentSchematic(schematic, file, Material.RED_GLAZED_TERRACOTTA))
                        .entranceLocation(player.getLocation())
                        .build()
        );

        plugin.getLocale().send(player, "commands.admin.type.create.created", true,
                new Locale.Placeholder("name", name));
    }
}
