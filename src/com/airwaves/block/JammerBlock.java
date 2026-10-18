/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.airwaves.block;

import com.airwaves.ModRegistry;
import com.airwaves.be.JammerBE;
import com.airwaves.kinetic.KineticDeviceBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class JammerBlock
extends KineticDeviceBlock<JammerBE> {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private static final double[][] TERMINALS = new double[][]{{11.0, 1.0, 2.0, 13.0, 3.0, 3.0}, {3.0, 1.0, 2.0, 5.0, 3.0, 3.0}};

    @Override
    public double[][] terminals() {
        return TERMINALS;
    }

    public JammerBlock(BlockBehaviour.Properties p) {
        super(p, new double[]{0.0, 0.0, 2.0, 16.0, 13.0, 14.0});
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue((Property)POWERED, (Comparable)Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        super.createBlockStateDefinition(b);
        b.add(new Property[]{POWERED});
    }

    public Class<JammerBE> getBlockEntityClass() {
        return JammerBE.class;
    }

    public BlockEntityType<? extends JammerBE> getBlockEntityType() {
        return ModRegistry.JAMMER_BE.get();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext c) {
        return (BlockState)super.getStateForPlacement(c).setValue((Property)POWERED, (Comparable)Boolean.valueOf(c.getLevel().hasNeighborSignal(c.getClickedPos())));
    }

    protected void neighborChanged(BlockState s, Level l, BlockPos p, Block b, BlockPos from, boolean moved) {
        boolean pw;
        if (!l.isClientSide && (pw = l.hasNeighborSignal(p)) != (Boolean)s.getValue((Property)POWERED)) {
            l.setBlock(p, (BlockState)s.setValue((Property)POWERED, (Comparable)Boolean.valueOf(pw)), 3);
        }
    }
}
