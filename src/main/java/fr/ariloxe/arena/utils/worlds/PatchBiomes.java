package fr.ariloxe.arena.utils.worlds;

import fr.ariloxe.arena.utils.Reflection;
import net.minecraft.server.v1_8_R3.BiomeBase;
import net.minecraft.server.v1_8_R3.BiomeDecorator;
import java.lang.reflect.Field;
import java.util.Map;

public class PatchBiomes {
    public void patchBiomes() throws ReflectiveOperationException {
        try {
            Field biomesField = BiomeBase.class.getDeclaredField("biomes");
            biomesField.setAccessible(true);


            if (biomesField.get(null) instanceof BiomeBase[]) {
                BiomeBase[] biomes = (BiomeBase[])biomesField.get((Object)null);
                Map<String, BiomeBase> biomesMap = BiomeBase.o;
                BiomeBase defaultBiome = BiomeBase.FOREST;

                Reflection.setFinalStatic(BiomeBase.class.getDeclaredField("ad"), defaultBiome);

                for (int i = 0; i < biomes.length; i++) {
                    if (biomes[i] != null) {
                        if (!biomesMap.containsKey((biomes[i]).ah))
                            biomes[i] = defaultBiome;

                    }
                }

                Reflection.setFinalStatic(biomesField, biomes);
            }
        } catch (Exception ignored) {}
    }

}
