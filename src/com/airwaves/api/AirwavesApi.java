/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 */
package com.airwaves.api;

import com.airwaves.ModRegistry;
import com.airwaves.Tuning;
import com.airwaves.api.MachinePower;
import com.airwaves.api.RadioStation;
import com.airwaves.api.Reception;
import com.airwaves.client.MuLaw;
import com.airwaves.logic.BoardAnalysis;
import com.airwaves.logic.BoardLayout;
import com.airwaves.logic.Emitter;
import com.airwaves.logic.RadioNetwork;
import com.airwaves.power.PowerSupply;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class AirwavesApi {
    public static final String VERSION = "1.0";
    public static final String MODID = "airwaves";
    public static final int SAMPLE_RATE = 16000;
    public static final int FRAME_SAMPLES = 320;

    private AirwavesApi() {
    }

    public static long stationId(BlockPos pos) {
        return pos.asLong();
    }

    public static List<RadioStation> stations(ServerLevel level) {
        ArrayList<RadioStation> out = new ArrayList<RadioStation>();
        for (Emitter e : RadioNetwork.emitters((Level)level)) {
            out.add(RadioStation.of(e));
        }
        return out;
    }

    public static Reception listen(ServerLevel level, BlockPos pos, double antennaTop, Tuning tuning, double gain) {
        List<Emitter> em = RadioNetwork.emitters((Level)level);
        if (em.isEmpty()) {
            return Reception.NOTHING;
        }
        RadioNetwork.Best b = RadioNetwork.best((Level)level, pos, antennaTop, tuning.clamped(), gain, em, RadioNetwork.jammers((Level)level));
        if (b.q() < 0.02f) {
            return new Reception(b.q(), 0L, "");
        }
        String name = "";
        for (Emitter e : em) {
            if (e.root() != b.root()) continue;
            name = e.name();
            break;
        }
        return new Reception(b.q(), b.root(), name);
    }

    public static boolean broadcast(ServerLevel level, long stationId, UUID speaker, byte[] frame, float gain) {
        if (frame == null || frame.length < 16 || frame.length > 960) {
            return false;
        }
        return RadioNetwork.broadcast(level, stationId, speaker, frame, Math.max(0.0f, Math.min(1.0f, gain)));
    }

    public static void playAt(ServerLevel level, BlockPos pos, UUID speaker, byte[] frame, float quality, boolean fm, float gain) {
        RadioNetwork.send(level, pos, speaker, frame, quality, fm, gain);
    }

    public static byte[] encode(short[] pcm) {
        byte[] out = new byte[pcm.length];
        for (int i = 0; i < pcm.length; ++i) {
            out[i] = MuLaw.enc(pcm[i]);
        }
        return out;
    }

    public static short[] decode(byte[] frame) {
        short[] out = new short[frame.length];
        for (int i = 0; i < frame.length; ++i) {
            out[i] = MuLaw.dec(frame[i]);
        }
        return out;
    }

    public static MachinePower power(Level level, BlockPos pos) {
        return PowerSupply.at(level, pos);
    }

    public static BoardLayout layout(ItemStack board) {
        BoardLayout l = (BoardLayout)board.get((DataComponentType)ModRegistry.BOARD_LAYOUT.get());
        return l;
    }

    public static BoardAnalysis.Program program(ItemStack board) {
        BoardLayout l = AirwavesApi.layout(board);
        if (l == null) {
            return null;
        }
        BoardAnalysis.Result r = l.analyse();
        return r.works() ? BoardAnalysis.program(r) : null;
    }
}
