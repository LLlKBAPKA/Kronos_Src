package net.minecraft.network.play.client;

import lombok.AllArgsConstructor;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;

import java.io.IOException;

@AllArgsConstructor
public class CMoveVehiclePacket implements IPacket<IServerPlayNetHandler> {
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public CMoveVehiclePacket() {
    }

    public CMoveVehiclePacket(Entity entityIn) {
        this.x = entityIn.getPosX();
        this.y = entityIn.getPosY();
        this.z = entityIn.getPosZ();
        this.yaw = entityIn.rotationYaw;
        this.pitch = entityIn.rotationPitch;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.yaw = buf.readFloat();
        this.pitch = buf.readFloat();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeFloat(this.yaw);
        buf.writeFloat(this.pitch);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(IServerPlayNetHandler handler) {
        handler.processVehicleMove(this);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }
}
