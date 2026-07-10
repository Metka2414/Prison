package de.prison.plugin.world;

import de.prison.plugin.data.PrisonBlockType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;

public class PrisonWorldGenerator {

    public static final int SIZE = 20;      // 20x20 Fläche
    public static final int DEPTH = 10;     // Anzahl Ebenen nach unten
    public static final int BASE_Y = 100;   // Y-Höhe der obersten Abbau-Ebene

    /**
     * Erstellt (falls nicht vorhanden) die Welt für eine Blockart und befüllt sie:
     * Rand + Boden + Decke = Bedrock, Rest = das jeweilige Abbau-Material.
     */
    public World generateOrLoad(PrisonBlockType blockType) {
        World world = Bukkit.getWorld(blockType.getWorldName());
        boolean isNew = world == null;

        if (isNew) {
            WorldCreator creator = new WorldCreator(blockType.getWorldName());
            creator.generator(new VoidGenerator());
            creator.generateStructures(false);
            creator.type(WorldType.FLAT);
            world = creator.createWorld();
        }

        if (world == null) {
            throw new IllegalStateException("Konnte Welt " + blockType.getWorldName() + " nicht erstellen!");
        }

        world.setSpawnFlags(false, false);
        world.setDifficulty(org.bukkit.Difficulty.PEACEFUL);
        world.setAutoSave(true);

        if (isNew) {
            fillMine(world, blockType);
        }

        return world;
    }

    /**
     * Füllt den kompletten Minenbereich neu (z.B. beim ersten Erstellen oder für einen kompletten Reset).
     */
    public void fillMine(World world, PrisonBlockType blockType) {
        int half = SIZE / 2;

        for (int x = -half - 1; x <= half + 1; x++) {
            for (int z = -half - 1; z <= half + 1; z++) {
                for (int y = BASE_Y - DEPTH - 1; y <= BASE_Y + 1; y++) {

                    boolean isBorder = (x == -half - 1 || x == half + 1 || z == -half - 1 || z == half + 1);
                    boolean isFloorOrCeiling = (y == BASE_Y - DEPTH - 1 || y == BASE_Y + 1);

                    Material material;
                    if (isBorder || isFloorOrCeiling) {
                        material = Material.BEDROCK;
                    } else {
                        material = blockType.getMaterial();
                    }

                    world.getBlockAt(x, y, z).setType(material, false);
                }
            }
        }
    }

    /**
     * Ersetzt nur den Abbau-Bereich (ohne Bedrock-Rand) durch das Material neu - für Respawn.
     */
    public void respawnMine(World world, PrisonBlockType blockType) {
        int half = SIZE / 2;
        for (int x = -half; x <= half; x++) {
            for (int z = -half; z <= half; z++) {
                for (int y = BASE_Y - DEPTH; y <= BASE_Y; y++) {
                    world.getBlockAt(x, y, z).setType(blockType.getMaterial(), false);
                }
            }
        }
    }

    public Location getSpawnLocation(World world) {
        return new Location(world, 0.5, BASE_Y + 1.5, 0.5);
    }

    public boolean isInsideMine(Location location, World world) {
        if (!location.getWorld().equals(world)) return false;
        int half = SIZE / 2;
        return location.getBlockX() >= -half && location.getBlockX() <= half
                && location.getBlockZ() >= -half && location.getBlockZ() <= half
                && location.getBlockY() >= BASE_Y - DEPTH && location.getBlockY() <= BASE_Y;
    }

    /**
     * Leerer Chunk-Generator, damit die Welt komplett leer startet (wir füllen selbst).
     */
    public static class VoidGenerator extends ChunkGenerator {
        // Standard-Implementierung reicht - erzeugt komplett leere Chunks
    }
}
