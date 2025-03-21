package net.minecraft.network.play.client;

import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;

import java.io.IOException;

public class CEntityActionPacket implements IPacket<IServerPlayNetHandler> {
    private int entityID;
    private CEntityActionPacket.Action action;
    private int auxData;
    public static boolean lastUpdatedSprint;

    public CEntityActionPacket() {
    }

    public CEntityActionPacket(Entity entityIn, CEntityActionPacket.Action actionIn) {
        this(entityIn, actionIn, 0);
        if (actionIn == Action.START_SPRINTING) {
            lastUpdatedSprint = true;
        } else if (actionIn == Action.STOP_SPRINTING) {
            lastUpdatedSprint = false;
        }
    }

    public CEntityActionPacket(Entity entityIn, CEntityActionPacket.Action actionIn, int auxDataIn) {
        this.entityID = entityIn.getEntityId();
        this.action = actionIn;
        this.auxData = auxDataIn;
        if (actionIn == Action.START_SPRINTING) {
            lastUpdatedSprint = true;
        } else if (actionIn == Action.STOP_SPRINTING) {
            lastUpdatedSprint = false;
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.entityID = buf.readVarInt();
        this.action = buf.readEnumValue(CEntityActionPacket.Action.class);
        this.auxData = buf.readVarInt();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarInt(this.entityID);
        buf.writeEnumValue(this.action);
        buf.writeVarInt(this.auxData);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(IServerPlayNetHandler handler) {
        handler.processEntityAction(this);
    }

    public CEntityActionPacket.Action getAction() {
        return this.action;
    }

    public int getAuxData() {
        return this.auxData;
    }

    public enum Action {
        PRESS_SHIFT_KEY,
        RELEASE_SHIFT_KEY,
        STOP_SLEEPING,
        START_SPRINTING,
        STOP_SPRINTING,
        START_RIDING_JUMP,
        STOP_RIDING_JUMP,
        OPEN_INVENTORY,
        START_FALL_FLYING
    }
}
