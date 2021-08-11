package net.flytre.hplus.network;

import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class FilterC2SPacket implements Packet<ServerPlayPacketListener> {

    private final Type type;
    private final int state;

    public FilterC2SPacket(Type type, int state) {
        this.type = type;
        this.state = state;
    }

    public FilterC2SPacket(PacketByteBuf buf) {
        this.type = buf.readEnumConstant(Type.class);
        this.state = buf.readInt();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeEnumConstant(type);
        buf.writeInt(state);
    }

    @Override
    public void apply(ServerPlayPacketListener listener) {
        ServerPlayerEntity player = ((ServerPlayNetworkHandler) listener).getPlayer();
        player.server.execute(() -> {
            ItemStack stack = player.getMainHandStack();
            if (type == Type.MODE)
                stack.getOrCreateSubNbt("filter").putInt("type", state);
            if (type == Type.MOD)
                stack.getOrCreateSubNbt("filter").putInt("modMatch", state);
            if (type == Type.NBT)
                stack.getOrCreateSubNbt("filter").putInt("nbtMatch", state);
        });
    }


    public enum Type {
        MODE,
        MOD,
        NBT
    }
}
