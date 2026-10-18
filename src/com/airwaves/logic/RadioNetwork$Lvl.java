/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 */
package com.airwaves.logic;

import com.airwaves.be.DimensionalRelayBE;
import com.airwaves.be.JammerBE;
import com.airwaves.be.MicrophoneBE;
import com.airwaves.be.RelayBE;
import com.airwaves.be.SpeakerBE;
import com.airwaves.be.TransmitterBE;
import com.airwaves.be.TunableBE;
import com.airwaves.logic.Emitter;
import com.airwaves.logic.RadioNetwork;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;

private static final class RadioNetwork.Lvl {
    final Set<TransmitterBE> tx = new LinkedHashSet<TransmitterBE>();
    final Set<RelayBE> relays = new LinkedHashSet<RelayBE>();
    final Set<TunableBE> sinks = new LinkedHashSet<TunableBE>();
    final Set<MicrophoneBE> mics = new LinkedHashSet<MicrophoneBE>();
    final Set<JammerBE> jammers = new LinkedHashSet<JammerBE>();
    final Set<SpeakerBE> speakers = new LinkedHashSet<SpeakerBE>();
    final Set<DimensionalRelayBE> links = new LinkedHashSet<DimensionalRelayBE>();
    List<Emitter> emitters = new ArrayList<Emitter>();
    List<BlockPos> jam = new ArrayList<BlockPos>();
    List<RadioNetwork.Export> exports = new ArrayList<RadioNetwork.Export>();
    boolean linkActive;

    private RadioNetwork.Lvl() {
    }
}
