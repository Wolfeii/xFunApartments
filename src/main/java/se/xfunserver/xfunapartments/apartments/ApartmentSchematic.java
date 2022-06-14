package se.xfunserver.xfunapartments.apartments;

import org.bukkit.Material;

import java.io.File;
import java.util.List;
import java.util.Objects;

public abstract class ApartmentSchematic<S> {

    private final String name;
    private final Material loadBlock;
    protected final File file;

    protected ApartmentSchematic(String name, File file, Material loadBlock) {
        this.name = name;
        this.file = file;
        this.loadBlock = loadBlock;
    }

    public abstract S getSchematic();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApartmentSchematic)) return false;
        ApartmentSchematic<?> that = (ApartmentSchematic<?>) o;
        return Objects.equals(getName(), that.getName()) &&
                Objects.equals(getFile(), that.getFile()) &&
                Objects.equals(getLoadBlock(), that.getLoadBlock());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getFile(), getLoadBlock());
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }

    public Material getLoadBlock() {
        return loadBlock;
    }
}
