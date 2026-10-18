/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform$Sided
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour$ValueSettings
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter
 *  com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 */
package com.airwaves.be;

import com.airwaves.ModRegistry;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import java.util.List;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class CreativeAntennaBE
extends SmartBlockEntity
implements IHaveGoggleInformation {
    public static final int MAX = 9999;
    public static final int DEFAULT = 1000;
    public ScrollValueBehaviour range;

    public CreativeAntennaBE(BlockPos p, BlockState s) {
        super(ModRegistry.CREATIVE_ANTENNA_BE.get(), p, s);
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        this.range = new RangeValue((Component)Component.translatable((String)"airwaves.creative_antenna.range"), this, (ValueBoxTransform)new BaseBox());
        this.range.between(0, 9999);
        this.range.value = 1000;
        this.range.withFormatter(v -> v + " m");
        behaviours.add((BlockEntityBehaviour)this.range);
    }

    public int range() {
        return this.range == null ? 1000 : this.range.getValue();
    }

    public boolean addToGoggleTooltip(List<Component> tip, boolean sneaking) {
        tip.add((Component)Component.literal((String)"    Creative Antenna").withStyle(ChatFormatting.LIGHT_PURPLE));
        tip.add((Component)Component.literal((String)("    range " + this.range() + " m (hold right-click on the base to change)")).withStyle(ChatFormatting.GRAY));
        return true;
    }

    static class RangeValue
    extends ScrollValueBehaviour {
        RangeValue(Component label, CreativeAntennaBE be, ValueBoxTransform slot) {
            super(label, (SmartBlockEntity)be, slot);
        }

        public ValueSettingsBoard createBoard(Player player, BlockHitResult hit) {
            return new ValueSettingsBoard(this.label, 99, 10, List.of(Component.translatable((String)"airwaves.creative_antenna.hundreds"), Component.translatable((String)"airwaves.creative_antenna.units")), new ValueSettingsFormatter(vs -> Component.literal((String)(vs.row() == 0 ? vs.value() * 100 + this.value % 100 + " m" : this.value / 100 * 100 + vs.value() + " m"))));
        }

        public void setValueSettings(Player player, ValueSettingsBehaviour.ValueSettings vs, boolean ctrlDown) {
            int v;
            int n = v = vs.row() == 0 ? vs.value() * 100 + this.value % 100 : this.value / 100 * 100 + vs.value();
            if (v == this.value) {
                return;
            }
            this.setValue(Math.max(0, Math.min(9999, v)));
            this.playFeedbackSound((BlockEntityBehaviour)this);
        }

        public ValueSettingsBehaviour.ValueSettings getValueSettings() {
            return new ValueSettingsBehaviour.ValueSettings(0, this.value / 100);
        }
    }

    static class BaseBox
    extends ValueBoxTransform.Sided {
        BaseBox() {
        }

        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace((double)8.0, (double)2.0, (double)13.5);
        }

        protected boolean isSideActive(BlockState state, Direction d) {
            return d.getAxis().isHorizontal();
        }

        public float getScale() {
            return 0.4f;
        }
    }
}
