package fr.ariloxe.arena.utils.worlds;

import fr.ariloxe.arena.utils.Reflection;
import fr.kanao.core.utils.reflection.SimpleReflection;
import net.minecraft.server.v1_8_R3.BiomeBase;
import net.minecraft.server.v1_8_R3.BiomeDecorator;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

public class BiomeSwapper {

    /**
     * Should only be called at startup, before generation even start
     */

    public static void patchBiomes(Map<String, String> toPatch) {

        Field biomesField;
        try {
            biomesField = BiomeBase.class.getDeclaredField("biomes");
            biomesField.setAccessible(true);

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(biomesField, biomesField.getModifiers() & ~Modifier.FINAL);

            if (biomesField.get(null) instanceof BiomeBase[]) {
                BiomeBase[] biomes = (BiomeBase[]) biomesField.get(null);
                for (Map.Entry<String, String> entry : toPatch.entrySet()) {

                    Field biome_field = SimpleReflection.getField(BiomeBase.class, entry.getKey());
                    BiomeBase biome = SimpleReflection.getFieldValue(biome_field, (Object) null);

                    Field replace_field = SimpleReflection.getField(BiomeBase.class, entry.getValue());
                    BiomeBase replace = SimpleReflection.getFieldValue(replace_field, (Object) null);

                    biomes[biome.id] = replace;
                }

                biomesField.set(null, biomes);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
