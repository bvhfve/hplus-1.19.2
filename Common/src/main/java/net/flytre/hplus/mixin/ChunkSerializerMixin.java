package net.flytre.hplus.mixin;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Migrates old format to new format of hoppers
 */
@Mixin(ChunkSerializer.class)
public class ChunkSerializerMixin {

    @Inject(method = "deserialize", at = @At("HEAD"))
    private static void hplus$migrate(ServerWorld world, PointOfInterestStorage poiStorage, ChunkPos chunkPos, NbtCompound nbt, CallbackInfoReturnable<ProtoChunk> cir) {
        NbtList sections = nbt.getList("sections", 10);
        for (int i = 0; i < sections.size(); i++) {

            NbtCompound section = sections.getCompound(i);

            if (!section.contains("block_states"))
                continue;

            NbtCompound blockStates = section.getCompound("block_states");

            if (blockStates.isEmpty())
                continue;

            NbtList palette = blockStates.getList("palette", 10);

            if (palette.size() < 2)
                continue;

            for (int j = 0; j < palette.size(); j++) {
                NbtCompound entry = palette.getCompound(j);
                if (!entry.getString("Name").equals("minecraft:hopper"))
                    continue;
                NbtCompound properties = entry.getCompound("Properties");
                if (properties.contains("facing") && !properties.contains("from")) {
                    String facing = properties.getString("facing");
                    properties.putString("from", facing.equals("up") ? "down" : "up");
                }
            }
        }
    }
}
