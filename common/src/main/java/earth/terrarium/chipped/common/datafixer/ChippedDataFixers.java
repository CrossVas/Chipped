package earth.terrarium.chipped.common.datafixer;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.schemas.Schema;
import earth.terrarium.chipped.Chipped;
import earth.terrarium.chipped.common.util.PlatformUtils;
import net.minecraft.util.datafix.fixes.BlockRenameFix;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class ChippedDataFixers {

    private static final List<String> FIXER_IDS = List.of(
        "misc",
        "amethyst_block",
        "ancient_debris",
        "andesite",
        "blue_ice",
        "calcite",
        "coal_block",
        "cobblestone",
        "crying_obsidian",
        "diorite",
        "dirt",
        "dripstone_block",
        "end_stone",
        "granite",
        "ice",
        "lapis_block",
        "magma_block",
        "misc",
        "mossy_cobblestone",
        "netherrack",
        "nether_bricks",
        "obsidian",
        "packed_ice",
        "raw_copper_block",
        "raw_gold_block",
        "raw_iron_block",
        "redstone_block",
        "red_nether_bricks",
        "red_sandstone",
        "smooth_stone",
        "stone",
        "tuff",
        "basalt",
        "snow_block",
        "dark_prismarine"
    );

    public static void addDataFixers(Schema schema, Consumer<DataFix> fixer) {
        for (CsvMapper mapper : loadJarResources("/data/chipped/datafixers", FIXER_IDS)) {
            fixer.accept(BlockRenameFix.create(schema, mapper.displayName(), mapper));
        }
    }

    public static List<CsvMapper> loadJarResources(@NotNull String devPath, List<String> resources) {
        final List<CsvMapper> fixers = new ArrayList<>();
        for (String resource : resources) {
            try (final var stream = ChippedDataFixers.class.getResourceAsStream(devPath + "/" + resource + ".csv")) {
                if (stream == null) {
                    Chipped.LOGGER.error("Failed to load data fixer csv: " + resource + ", skipping... expected things to break. Input stream was null.");
                    continue;
                }
                final String data = IOUtils.toString(stream, StandardCharsets.UTF_8);
                fixers.add(new CsvMapper(data, resource));
                if (PlatformUtils.isDevelopmentEnvironment()) {
                    Chipped.LOGGER.info("Loaded data fixer csv: " + resource);
                }
            }catch (Exception e) {
                Chipped.LOGGER.warn("Failed to load data fixer csv: " + resource + ", skipping... expected things to break.");
                e.printStackTrace();
            }
        }
        return fixers;
    }
}
