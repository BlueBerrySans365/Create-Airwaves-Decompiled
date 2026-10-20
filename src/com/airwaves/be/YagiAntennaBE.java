/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform
 *  com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package com.airwaves.be;

import com.airwaves.logic.AntennaUtil;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class YagiAntennaBE
extends SmartBlockEntity {
    public ScrollValueBehaviour bearing;
    public float shown = Float.NaN;

    public YagiAntennaBE(BlockEntityType<?> t, BlockPos p, BlockState s) {
        super(t, p, s);
    }

    public void addBehaviours(List<BlockEntityBehaviour> list) {
        this.bearing = new ScrollValueBehaviour((Component)Component.literal((String)"Bearing"), (SmartBlockEntity)this, (ValueBoxTransform)new TopBox()).between(0, 359).withFormatter(v -> v + "\u00b0 " + AntennaUtil.compass(v.intValue()));
        list.add((BlockEntityBehaviour)this.bearing);
    }

    public int bearing() {
        return this.bearing == null ? 0 : this.bearing.getValue();
    }

    public void setBearing(int deg) {
        if (this.bearing != null) {
            this.bearing.setValue(Math.floorMod(deg, 360));
        }
    }

    private static class TopBox
    extends CenteredSideValueBoxTransform {
        static final float TOP = 12.0f;

        TopBox() {
            super((state, d) -> d == Direction.UP);
        }

        public Vec3 getLocalOffset(LevelAccessor l, BlockPos p, BlockState s) {
            return new Vec3(0.5, 0.753125, 0.5);
        }
    }
}
