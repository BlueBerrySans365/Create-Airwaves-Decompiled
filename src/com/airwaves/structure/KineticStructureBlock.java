/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.IRotate
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntityTicker
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityTicker
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.airwaves.structure;

import com.airwaves.ModRegistry;
import com.airwaves.structure.StructureBlock;
import com.airwaves.structure.StructureDef;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntityTicker;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class KineticStructureBlock
extends StructureBlock
implements IRotate {
    private final Set<Integer> shaftParts = new HashSet<Integer>();

    public KineticStructureBlock(StructureDef def, BlockBehaviour.Properties p, BiFunction<BlockPos, BlockState, BlockEntity> be) {
        super(def, p, be);
        int[] c = def.parts[0];
        block0: for (int dir : new int[]{-1, 1}) {
            int k = 1;
            while (true) {
                int found = -1;
                for (int i = 1; i < def.parts.length; ++i) {
                    if (def.parts[i][0] != c[0] || def.parts[i][1] != c[1] || def.parts[i][2] != c[2] + dir * k) continue;
                    found = i;
                    break;
                }
                if (found < 0) continue block0;
                this.shaftParts.add(found);
                ++k;
            }
        }
    }

    public boolean onDriveLine(BlockState s) {
        int p = this.partOf(s);
        return p == 0 || this.shaftParts.contains(p);
    }

    public Direction.Axis getRotationAxis(BlockState s) {
        return s.getValue((Property)AXIS) == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
    }

    public boolean hasShaftTowards(LevelReader w, BlockPos p, BlockState s, Direction face) {
        return this.onDriveLine(s) && face.getAxis() == this.getRotationAxis(s);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos p, BlockState s) {
        if (this.shaftParts.contains(this.partOf(s))) {
            return new TowerShaftBE(p, s);
        }
        return super.newBlockEntity(p, s);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level lv, BlockState s, BlockEntityType<T> t) {
        if (!this.onDriveLine(s)) {
            return super.getTicker(lv, s, t);
        }
        return new SmartBlockEntityTicker();
    }

    protected void onPlace(BlockState state, Level lv, BlockPos pos, BlockState old, boolean moving) {
        super.onPlace(state, lv, pos, old, moving);
        BlockEntity blockEntity = lv.getBlockEntity(pos);
        if (blockEntity instanceof KineticBlockEntity) {
            KineticBlockEntity kbe = (KineticBlockEntity)blockEntity;
            kbe.preventSpeedUpdate = 0;
            if (old.getBlock() != state.getBlock() || state.hasBlockEntity() != old.hasBlockEntity()) {
                return;
            }
            kbe.preventSpeedUpdate = 2;
        }
    }

    public void updateIndirectNeighbourShapes(BlockState st, LevelAccessor lv, BlockPos pos, int flags, int count) {
        KineticBlockEntity kbe;
        block3: {
            block2: {
                BlockEntity blockEntity;
                if (lv.isClientSide() || !((blockEntity = lv.getBlockEntity(pos)) instanceof KineticBlockEntity)) break block2;
                kbe = (KineticBlockEntity)blockEntity;
                if (kbe.preventSpeedUpdate <= 0) break block3;
            }
            return;
        }
        kbe.warnOfMovement();
        kbe.clearKineticInformation();
        kbe.updateSpeed = true;
    }

    public static class TowerShaftBE
    extends KineticBlockEntity {
        public TowerShaftBE(BlockPos p, BlockState s) {
            super(ModRegistry.TOWER_SHAFT_BE.get(), p, s);
        }

        public float calculateStressApplied() {
            this.lastStressApplied = 0.0f;
            return 0.0f;
        }
    }
}
