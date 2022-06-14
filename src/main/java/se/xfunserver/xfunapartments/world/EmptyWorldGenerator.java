package se.xfunserver.xfunapartments.world;

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class EmptyWorldGenerator extends ChunkGenerator {

    @Override
    public BiomeProvider getDefaultBiomeProvider(WorldInfo worldInfo) {
        return null;
    }
}
