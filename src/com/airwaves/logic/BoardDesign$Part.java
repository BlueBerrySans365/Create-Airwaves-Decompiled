/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 */
package com.airwaves.logic;

import com.airwaves.ModRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public static enum BoardDesign.Part {
    RESISTOR('r'),
    CAPACITOR('c'),
    LED('l'),
    TRANSISTOR('t'),
    TUBE('u'),
    COIL('k'),
    CRYSTAL('x'),
    MEMORY_CHIP('m'),
    COUNTER_CHIP('n'),
    PULSE_CHIP('q'),
    LIGHT_SENSOR('d'),
    RAIN_SENSOR('w'),
    MOTION_SENSOR('o');

    public final char letter;

    private BoardDesign.Part(char letter) {
        this.letter = letter;
    }

    public Item item() {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> (Item)ModRegistry.RESISTOR.get();
            case 1 -> (Item)ModRegistry.CAPACITOR.get();
            case 2 -> (Item)ModRegistry.LED.get();
            case 3 -> (Item)ModRegistry.TRANSISTOR.get();
            case 4 -> ModRegistry.electronTube();
            case 5 -> (Item)ModRegistry.COPPER_COIL.get();
            case 6 -> (Item)ModRegistry.CRYSTAL_OSCILLATOR.get();
            case 7 -> (Item)ModRegistry.MEMORY_CHIP.get();
            case 8 -> (Item)ModRegistry.COUNTER_CHIP.get();
            case 9 -> (Item)ModRegistry.PULSE_CHIP.get();
            case 10 -> (Item)ModRegistry.LIGHT_SENSOR.get();
            case 11 -> (Item)ModRegistry.RAIN_SENSOR.get();
            case 12 -> (Item)ModRegistry.MOTION_SENSOR.get();
        };
    }

    public static BoardDesign.Part of(ItemStack s) {
        if (s.isEmpty()) {
            return null;
        }
        for (BoardDesign.Part p : BoardDesign.Part.values()) {
            if (!s.is(p.item())) continue;
            return p;
        }
        return null;
    }

    public boolean standalone() {
        return this.ordinal() >= MEMORY_CHIP.ordinal();
    }

    public static BoardDesign.Part byLetter(char c) {
        for (BoardDesign.Part p : BoardDesign.Part.values()) {
            if (p.letter != c) continue;
            return p;
        }
        throw new IllegalArgumentException("no part '" + c + "'");
    }
}
