/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  org.patryk3211.powergrid.electricity.base.IElectricEntity$CircuitBuilder
 *  org.patryk3211.powergrid.electricity.sim.ElectricWire
 *  org.patryk3211.powergrid.electricity.sim.node.IElectricNode
 */
package com.airwaves.compat.powergrid;

import com.airwaves.power.ElectricLoad;
import com.airwaves.power.PowerSupply;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import org.patryk3211.powergrid.electricity.base.IElectricEntity;
import org.patryk3211.powergrid.electricity.sim.ElectricWire;
import org.patryk3211.powergrid.electricity.sim.node.IElectricNode;

public static final class PGMachine.Load {
    public final ElectricLoad logic = new ElectricLoad();
    private ElectricWire wire;

    public void build(IElectricEntity.CircuitBuilder b) {
        b.setTerminalCount(2);
        this.wire = b.connect(100000.0f, (IElectricNode)b.terminalNode(0), (IElectricNode)b.terminalNode(1));
    }

    public void tick(SmartBlockEntity be) {
        if (this.wire == null || be.getLevel() == null || be.getLevel().isClientSide) {
            return;
        }
        double v = this.wire.potentialDifference();
        double i = this.wire.current();
        double joules = i * i * this.wire.getResistance() / 20.0;
        this.logic.update(PowerSupply.at(be.getLevel(), be.getBlockPos()), v, joules, be.getLevel().getGameTime());
        float want = (float)this.logic.resistance();
        float had = (float)this.wire.getResistance();
        if (Math.abs(want - had) > Math.max(0.01f, had * 0.03f)) {
            this.wire.setResistance((double)want);
        }
    }
}
