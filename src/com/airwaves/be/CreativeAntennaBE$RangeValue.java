/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour$ValueSettings
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard
 *  com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter
 *  com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.airwaves.be;

import com.airwaves.be.CreativeAntennaBE;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

static class CreativeAntennaBE.RangeValue
extends ScrollValueBehaviour {
    CreativeAntennaBE.RangeValue(Component label, CreativeAntennaBE be, ValueBoxTransform slot) {
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
