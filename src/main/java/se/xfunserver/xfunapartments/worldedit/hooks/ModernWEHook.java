package se.xfunserver.xfunapartments.worldedit.hooks;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.inventory.ItemStack;
import se.xfunserver.xfunapartments.apartments.ApartmentSchematic;
import se.xfunserver.xfunapartments.worldedit.ApartmentFactoryCompat;
import se.xfunserver.xfunapartments.worldedit.WorldEditHook;
import se.xfunserver.xfunapartments.worldedit.WorldEditRegion;
import se.xfunserver.xfunapartments.worldedit.WorldEditVector;
import se.xfunserver.xfunapartments.worldedit.compat.ModernApartmentFactoryCompat;
import se.xfunserver.xfunapartments.xFunApartments;

import java.io.File;
import java.util.List;

public class ModernWEHook implements WorldEditHook<Clipboard> {

    protected final xFunApartments plugin;

    public ModernWEHook(xFunApartments plugin) {
        this.plugin = plugin;
    }

    public static Region transform(WorldEditRegion region) {
        return new CuboidRegion(
                BukkitAdapter.adapt(region.getWorld()),
                transform(region.getMinimumPoint()),
                transform(region.getMaximumPoint())
        );
    }

    public static BlockVector3 transform(WorldEditVector vector) {
        return BlockVector3.at(vector.getX(), vector.getY(), vector.getZ());
    }

    public static WorldEditVector transform(BlockVector3 vector) {
        return new WorldEditVector(vector.getX(), vector.getY(), vector.getZ());
    }

    @Override
    public void fill(WorldEditRegion region, List<ItemStack> blocks) {
        try (EditSession session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(region.getWorld()))) {
            final RandomPattern pattern = new RandomPattern();
            final Region weRegion = transform(region);

            for (ItemStack itemStack : blocks) {
                Pattern pat = (Pattern) BukkitAdapter.adapt(itemStack.getType().createBlockData());
                pattern.add(pat, 1.0);
            }

            session.setBlocks(weRegion, pattern);
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ApartmentFactoryCompat<Clipboard> createApartmentFactoryCompat() {
        return new ModernApartmentFactoryCompat(plugin);
    }

    @Override
    public ApartmentSchematic<Clipboard> loadApartmentSchematic(String name, File file, Material loadBlock) {
        return new ModernWEApartmentSchematic(name, file, loadBlock);
    }
}
