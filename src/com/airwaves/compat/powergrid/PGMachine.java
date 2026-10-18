/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.patryk3211.powergrid.electricity.base.IElectricEntity$CircuitBuilder
 *  org.patryk3211.powergrid.electricity.base.ITerminalPlacement
 *  org.patryk3211.powergrid.electricity.base.TerminalBoundingBox
 *  org.patryk3211.powergrid.electricity.sim.ElectricWire
 *  org.patryk3211.powergrid.electricity.sim.node.IElectricNode
 */
package com.airwaves.compat.powergrid;

import com.airwaves.power.ElectricLoad;
import com.airwaves.power.PowerSupply;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.level.block.state.BlockState;
import org.patryk3211.powergrid.electricity.base.IElectricEntity;
import org.patryk3211.powergrid.electricity.base.ITerminalPlacement;
import org.patryk3211.powergrid.electricity.base.TerminalBoundingBox;
import org.patryk3211.powergrid.electricity.sim.ElectricWire;
import org.patryk3211.powergrid.electricity.sim.node.IElectricNode;

public final class PGMachine {
    private static final Map<BlockState, TerminalBoundingBox[]> BOXES = new ConcurrentHashMap<BlockState, TerminalBoundingBox[]>();

    private PGMachine() {
    }

    /*
     * Exception decompiling
     */
    public static ITerminalPlacement terminal(BlockState st, int index) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * java.lang.UnsupportedOperationException
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.NewAnonymousArray.getDimSize(NewAnonymousArray.java:142)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.isNewArrayLambda(LambdaRewriter.java:455)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteDynamicExpression(LambdaRewriter.java:409)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteDynamicExpression(LambdaRewriter.java:167)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:105)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.rewriters.ExpressionRewriterHelper.applyForwards(ExpressionRewriterHelper.java:12)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriterToArgs(AbstractMemberFunctionInvokation.java:101)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:88)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.ArrayIndex.applyExpressionRewriter(ArrayIndex.java:92)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.structured.statement.StructuredReturn.rewriteExpressions(StructuredReturn.java:99)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewrite(LambdaRewriter.java:88)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.rewriteLambdas(Op04StructuredStatement.java:1137)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:912)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doClass(Driver.java:84)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:78)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public static final class Load {
        public final ElectricLoad logic = new ElectricLoad();
        private ElectricWire wire;

        public void build(IElectricEntity.CircuitBuilder b) {
            b.setTerminalCount(2);
            this.wire = b.connect(100000.0f, (IElectricNode)b.terminalNode(0), (IElectricNode)b.terminalNode(1));
        }

        public void tick(SmartBlockEntity be) {
            if (this.wire == null || be.getLevel() == null || be.getLevel().isClientSide) {
                return;
            }
            double v = this.wire.potentialDifference();
            double i = this.wire.current();
            double joules = i * i * this.wire.getResistance() / 20.0;
            this.logic.update(PowerSupply.at(be.getLevel(), be.getBlockPos()), v, joules, be.getLevel().getGameTime());
            float want = (float)this.logic.resistance();
            float had = (float)this.wire.getResistance();
            if (Math.abs(want - had) > Math.max(0.01f, had * 0.03f)) {
                this.wire.setResistance((double)want);
            }
        }
    }
}
