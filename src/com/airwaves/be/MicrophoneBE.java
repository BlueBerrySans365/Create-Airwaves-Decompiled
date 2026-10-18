/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package com.airwaves.be;

import com.airwaves.ModRegistry;
import com.airwaves.be.CableNode;
import com.airwaves.be.NodeBE;
import com.airwaves.be.TransmitterBE;
import com.airwaves.logic.Cables;
import com.airwaves.logic.RadioNetwork;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MicrophoneBE
extends NodeBE {
    public TransmitterBE feeds;
    public List<Cables.Seg> path = List.of();
    public long lastPulse;
    public float line = 1.0f;

    public MicrophoneBE(BlockPos p, BlockState s) {
        super(ModRegistry.MIC_BE.get(), p, s);
    }

    protected MicrophoneBE(BlockEntityType<?> t, BlockPos p, BlockState s) {
        super(t, p, s);
    }

    @Override
    public int maxLinks(int slot) {
        return 1;
    }

    @Override
    public Vec3 anchor(int slot) {
        return CableNode.facing(this, 8.0, 0.5, 15.0);
    }

    public void onLoad() {
        super.onLoad();
        if (this.level != null && !this.level.isClientSide) {
            RadioNetwork.register(this);
        }
    }

    public void setRemoved() {
        super.setRemoved();
        if (this.level != null && !this.level.isClientSide) {
            RadioNetwork.unregister(this);
        }
    }
}
