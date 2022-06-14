package se.xfunserver.xfunapartments.worldedit.hooks;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import org.bukkit.Material;
import se.xfunserver.xfunapartments.apartments.ApartmentSchematic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class ModernWEApartmentSchematic extends ApartmentSchematic<Clipboard> {

    protected ModernWEApartmentSchematic(String name, File file, Material loadBlock) {
        super(name, file, loadBlock);
    }

    @Override
    public Clipboard getSchematic() {
        final ClipboardFormat format = ClipboardFormats.findByFile(file);
        if (format != null) {
            try (final ClipboardReader reader = format.getReader(new FileInputStream(file))) {
                return reader.read();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        return null;
    }
}
