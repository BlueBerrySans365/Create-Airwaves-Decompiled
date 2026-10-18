/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.fml.ModList
 *  net.neoforged.fml.loading.FMLPaths
 */
package com.airwaves.client;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.Properties;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;

public final class VoiceConfig {
    public static boolean muted = false;
    public static boolean pushToTalk = false;
    public static boolean monitor = false;
    public static boolean deafened = false;
    public static float input = 1.0f;
    public static float voice = 1.0f;
    public static float radio = 1.0f;
    public static float threshold = 0.004f;
    public static String device = "";
    public static boolean useSvc = true;

    public static boolean svcInstalled() {
        return ModList.get() != null && ModList.get().isLoaded("voicechat");
    }

    public static boolean svcActive() {
        return useSvc && VoiceConfig.svcInstalled();
    }

    private VoiceConfig() {
    }

    private static Path file() {
        return FMLPaths.CONFIGDIR.get().resolve("airwaves-voice.properties");
    }

    private static float f(Properties p, String k, float d) {
        try {
            return Float.parseFloat(p.getProperty(k, String.valueOf(d)));
        }
        catch (Exception e) {
            return d;
        }
    }

    public static void load() {
        try (FileInputStream in = new FileInputStream(VoiceConfig.file().toFile());){
            Properties p = new Properties();
            p.load(in);
            muted = Boolean.parseBoolean(p.getProperty("muted", "false"));
            pushToTalk = Boolean.parseBoolean(p.getProperty("pushToTalk", "false"));
            input = VoiceConfig.f(p, "input", 1.0f);
            voice = VoiceConfig.f(p, "voice", 1.0f);
            radio = VoiceConfig.f(p, "radio", 1.0f);
            threshold = VoiceConfig.f(p, "threshold2", 0.004f);
            device = p.getProperty("device", "");
            useSvc = Boolean.parseBoolean(p.getProperty("useSvc", "true"));
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static void save() {
        try (FileOutputStream out = new FileOutputStream(VoiceConfig.file().toFile());){
            Properties p = new Properties();
            p.setProperty("muted", String.valueOf(muted));
            p.setProperty("pushToTalk", String.valueOf(pushToTalk));
            p.setProperty("input", String.valueOf(input));
            p.setProperty("voice", String.valueOf(voice));
            p.setProperty("radio", String.valueOf(radio));
            p.setProperty("threshold2", String.valueOf(threshold));
            p.setProperty("device", device);
            p.setProperty("useSvc", String.valueOf(useSvc));
            p.store(out, "Create: Airwaves voice settings");
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static void reset() {
        muted = false;
        pushToTalk = false;
        monitor = false;
        deafened = false;
        input = 1.0f;
        voice = 1.0f;
        radio = 1.0f;
        threshold = 0.004f;
    }
}
