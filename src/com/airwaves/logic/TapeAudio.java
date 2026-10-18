/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.minecraft.Util
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.world.level.storage.LevelResource
 *  org.slf4j.Logger
 */
package com.airwaves.logic;

import com.mojang.logging.LogUtils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.minecraft.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;

public final class TapeAudio {
    private static final Logger LOG = LogUtils.getLogger();
    public static final int MAX_FRAMES = 15000;
    private static final Map<String, List<Frame>> CACHE = Collections.synchronizedMap(new LinkedHashMap<String, List<Frame>>(16, 0.75f, true){

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, List<Frame>> e) {
            return this.size() > 12;
        }
    });

    private TapeAudio() {
    }

    private static Path file(MinecraftServer s, String id) {
        return s.getWorldPath(LevelResource.ROOT).resolve("airwaves_tapes").resolve(id.replaceAll("[^a-zA-Z0-9-]", "") + ".bin");
    }

    public static void save(MinecraftServer s, String id, List<Frame> frames) {
        List<Frame> copy = List.copyOf(frames);
        CACHE.put(id, copy);
        Path f = TapeAudio.file(s, id);
        Util.ioPool().execute(() -> {
            try {
                Files.createDirectories(f.getParent(), new FileAttribute[0]);
                try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(Files.newOutputStream(f, new OpenOption[0]))));){
                    out.writeInt(1);
                    out.writeInt(copy.size());
                    for (Frame fr : copy) {
                        out.writeInt(fr.ms());
                        out.writeShort(fr.data().length);
                        out.write(fr.data());
                    }
                }
            }
            catch (IOException e) {
                LOG.warn("Could not save tape {}", (Object)id, (Object)e);
            }
        });
    }

    public static List<Frame> load(MinecraftServer s, String id) {
        List<Frame> list;
        if (id == null || id.isEmpty()) {
            return List.of();
        }
        List<Frame> hit = CACHE.get(id);
        if (hit != null) {
            return hit;
        }
        Path f = TapeAudio.file(s, id);
        if (!Files.exists(f, new LinkOption[0])) {
            return List.of();
        }
        DataInputStream in = new DataInputStream(new BufferedInputStream(new GZIPInputStream(Files.newInputStream(f, new OpenOption[0]))));
        try {
            in.readInt();
            int n = Math.min(in.readInt(), 15000);
            ArrayList<Frame> out = new ArrayList<Frame>(n);
            for (int i = 0; i < n; ++i) {
                int ms = in.readInt();
                byte[] d = new byte[Math.min(in.readUnsignedShort(), 1024)];
                in.readFully(d);
                out.add(new Frame(ms, d));
            }
            List<Frame> r = List.copyOf(out);
            CACHE.put(id, r);
            list = r;
        }
        catch (Throwable throwable) {
            try {
                try {
                    in.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException e) {
                LOG.warn("Could not read tape {}", (Object)id, (Object)e);
                return List.of();
            }
        }
        in.close();
        return list;
    }

    public static void clear() {
        CACHE.clear();
    }

    public record Frame(int ms, byte[] data) {
    }
}
