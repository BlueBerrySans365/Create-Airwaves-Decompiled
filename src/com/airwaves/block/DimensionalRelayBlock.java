/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.airwaves.block;

import com.airwaves.ModRegistry;
import com.airwaves.be.DimensionalRelayBE;
import com.airwaves.kinetic.KineticDeviceBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class DimensionalRelayBlock
extends KineticDeviceBlock<DimensionalRelayBE> {
    public static final BooleanProperty ACTIVE = BooleanProperty.create((String)"active");
    private static final double[][] TERMINALS = new double[][]{{13.0, 2.0, 0.0, 15.0, 4.0, 1.0}, {1.0, 2.0, 0.0, 3.0, 4.0, 1.0}};

    @Override
    public double[][] terminals() {
        return TERMINALS;
    }

    public DimensionalRelayBlock(BlockBehaviour.Properties p) {
        super(p, new double[]{0.0, 0.0, 0.0, 16.0, 16.0, 16.0});
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue((Property)ACTIVE, (Comparable)Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        super.createBlockStateDefinition(b);
        b.add(new Property[]{ACTIVE});
    }

    public Class<DimensionalRelayBE> getBlockEntityClass() {
        return DimensionalRelayBE.class;
    }

    public BlockEntityType<? extends DimensionalRelayBE> getBlockEntityType() {
        return ModRegistry.DIM_RELAY_BE.get();
    }

    public void animateTick(BlockState s, Level lv, BlockPos p, RandomSource r) {
        if (!((Boolean)s.getValue((Property)ACTIVE)).booleanValue()) {
            return;
        }
        for (int i = 0; i < 2; ++i) {
            lv.addParticle((ParticleOptions)ParticleTypes.PORTAL, (double)p.getX() + 0.5 + (r.nextDouble() - 0.5) * 0.4, (double)p.getY() + 0.8 + r.nextDouble() * 0.3, (double)p.getZ() + 0.5 + (r.nextDouble() - 0.5) * 0.4, (r.nextDouble() - 0.5) * 0.6, r.nextDouble() * 0.4, (r.nextDouble() - 0.5) * 0.6);
        }
        if (r.nextInt(30) == 0) {
            lv.playLocalSound(p, SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.08f, 1.6f + r.nextFloat() * 0.2f, false);
        }
    }

    @Override
    public void onRemove(BlockState s, Level lv, BlockPos p, BlockState ns, boolean moved) {
        BlockEntity blockEntity;
        if (!s.is(ns.getBlock()) && !lv.isClientSide && (blockEntity = lv.getBlockEntity(p)) instanceof DimensionalRelayBE) {
            DimensionalRelayBE be = (DimensionalRelayBE)blockEntity;
            be.keepLoaded(false);
        }
        super.onRemove(s, lv, p, ns, moved);
    }
}
