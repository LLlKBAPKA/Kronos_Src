package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.template.ProcessorLists;

public class BastionRemnantsTreasurePools {
    public static void func_236262_a_() {
    }

    static {
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation("bastion/treasure/bases"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.func_242861_b("bastion/treasure/bases/lava_basin", ProcessorLists.field_244120_t), 1)), JigsawPattern.PlacementBehaviour.RIGID));
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation("bastion/treasure/stairs"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.func_242861_b("bastion/treasure/stairs/lower_stairs", ProcessorLists.field_244120_t), 1)), JigsawPattern.PlacementBehaviour.RIGID));
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation("bastion/treasure/bases/centers"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.func_242861_b("bastion/treasure/bases/centers/center_0", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/bases/centers/center_1", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/bases/centers/center_2", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/bases/centers/center_3", ProcessorLists.field_244120_t), 1)), JigsawPattern.PlacementBehaviour.RIGID));
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation("bastion/treasure/brains"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.func_242861_b("bastion/treasure/brains/center_brain", ProcessorLists.field_244120_t), 1)), JigsawPattern.PlacementBehaviour.RIGID));
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation("bastion/treasure/walls"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.func_242861_b("bastion/treasure/walls/lava_wall", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/walls/entrance_wall", ProcessorLists.field_244097_C), 1)), JigsawPattern.PlacementBehaviour.RIGID));
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation("bastion/treasure/walls/outer"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.func_242861_b("bastion/treasure/walls/outer/top_corner", ProcessorLists.field_244097_C), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/walls/outer/mid_corner", ProcessorLists.field_244097_C), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/walls/outer/bottom_corner", ProcessorLists.field_244097_C), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/walls/outer/outer_wall", ProcessorLists.field_244097_C), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/walls/outer/medium_outer_wall", ProcessorLists.field_244097_C), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/walls/outer/tall_outer_wall", ProcessorLists.field_244097_C), 1)), JigsawPattern.PlacementBehaviour.RIGID));
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation("bastion/treasure/walls/bottom"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.func_242861_b("bastion/treasure/walls/bottom/wall_0", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/walls/bottom/wall_1", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/walls/bottom/wall_2", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/walls/bottom/wall_3", ProcessorLists.field_244120_t), 1)), JigsawPattern.PlacementBehaviour.RIGID));
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation("bastion/treasure/walls/mid"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.func_242861_b("bastion/treasure/walls/mid/wall_0", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/walls/mid/wall_1", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/walls/mid/wall_2", ProcessorLists.field_244120_t), 1)), JigsawPattern.PlacementBehaviour.RIGID));
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation("bastion/treasure/walls/top"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.func_242861_b("bastion/treasure/walls/top/main_entrance", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/walls/top/wall_0", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/walls/top/wall_1", ProcessorLists.field_244120_t), 1)), JigsawPattern.PlacementBehaviour.RIGID));
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation("bastion/treasure/connectors"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.func_242861_b("bastion/treasure/connectors/center_to_wall_middle", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/connectors/center_to_wall_top", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/connectors/center_to_wall_top_entrance", ProcessorLists.field_244120_t), 1)), JigsawPattern.PlacementBehaviour.RIGID));
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation("bastion/treasure/entrances"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.func_242861_b("bastion/treasure/entrances/entrance_0", ProcessorLists.field_244120_t), 1)), JigsawPattern.PlacementBehaviour.RIGID));
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation("bastion/treasure/ramparts"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.func_242861_b("bastion/treasure/ramparts/mid_wall_main", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/ramparts/mid_wall_side", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/ramparts/bottom_wall_0", ProcessorLists.field_244119_s), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/ramparts/top_wall", ProcessorLists.field_244098_D), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/ramparts/lava_basin_side", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/ramparts/lava_basin_main", ProcessorLists.field_244120_t), 1)), JigsawPattern.PlacementBehaviour.RIGID));
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation("bastion/treasure/corners/bottom"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.func_242861_b("bastion/treasure/corners/bottom/corner_0", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/corners/bottom/corner_1", ProcessorLists.field_244120_t), 1)), JigsawPattern.PlacementBehaviour.RIGID));
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation("bastion/treasure/corners/edges"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.func_242861_b("bastion/treasure/corners/edges/bottom", ProcessorLists.field_244097_C), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/corners/edges/middle", ProcessorLists.field_244097_C), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/corners/edges/top", ProcessorLists.field_244097_C), 1)), JigsawPattern.PlacementBehaviour.RIGID));
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation("bastion/treasure/corners/middle"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.func_242861_b("bastion/treasure/corners/middle/corner_0", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/corners/middle/corner_1", ProcessorLists.field_244120_t), 1)), JigsawPattern.PlacementBehaviour.RIGID));
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation("bastion/treasure/corners/top"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.func_242861_b("bastion/treasure/corners/top/corner_0", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/corners/top/corner_1", ProcessorLists.field_244120_t), 1)), JigsawPattern.PlacementBehaviour.RIGID));
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation("bastion/treasure/extensions/large_pool"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.func_242861_b("bastion/treasure/extensions/empty", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/extensions/empty", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/extensions/fire_room", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/extensions/large_bridge_0", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/extensions/large_bridge_1", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/extensions/large_bridge_2", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/extensions/large_bridge_3", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/extensions/roofed_bridge", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/extensions/empty", ProcessorLists.field_244120_t), 1)), JigsawPattern.PlacementBehaviour.RIGID));
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation("bastion/treasure/extensions/small_pool"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.func_242861_b("bastion/treasure/extensions/empty", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/extensions/fire_room", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/extensions/empty", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/extensions/small_bridge_0", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/extensions/small_bridge_1", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/extensions/small_bridge_2", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/extensions/small_bridge_3", ProcessorLists.field_244120_t), 1)), JigsawPattern.PlacementBehaviour.RIGID));
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation("bastion/treasure/extensions/houses"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.func_242861_b("bastion/treasure/extensions/house_0", ProcessorLists.field_244120_t), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/extensions/house_1", ProcessorLists.field_244120_t), 1)), JigsawPattern.PlacementBehaviour.RIGID));
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation("bastion/treasure/roofs"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.func_242861_b("bastion/treasure/roofs/wall_roof", ProcessorLists.field_244096_B), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/roofs/corner_roof", ProcessorLists.field_244096_B), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/roofs/center_roof", ProcessorLists.field_244096_B), 1)), JigsawPattern.PlacementBehaviour.RIGID));
    }
}
