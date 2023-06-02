package com.kneelawk.wiredredstone.node

import com.kneelawk.graphlib.api.graph.NodeContext
import com.kneelawk.graphlib.api.graph.user.BlockNode
import com.kneelawk.graphlib.api.graph.user.BlockNodeDecoder
import com.kneelawk.graphlib.api.graph.user.SidedBlockNode
import com.kneelawk.graphlib.api.util.HalfLink
import com.kneelawk.graphlib.api.wire.CenterWireBlockNode
import com.kneelawk.graphlib.api.wire.WireConnectionDiscoverers
import com.kneelawk.wiredredstone.logic.RedstoneCarrierFilter
import com.kneelawk.wiredredstone.logic.RedstoneWireType
import com.kneelawk.wiredredstone.util.NetNode
import net.minecraft.nbt.NbtByte
import net.minecraft.nbt.NbtElement
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction

data class PowerlineConnectorBlockNode(val side: Direction) : CenterWireBlockNode, RedstoneCarrierBlockNode {
    override fun getTypeId(): Identifier = WRBlockNodes.POWERLINE_CONNECTOR

    override fun toTag(): NbtElement = NbtByte.of(side.id.toByte())

    override fun findConnections(ctx: NodeContext): Collection<HalfLink> {
        return WireConnectionDiscoverers.centerWireFindConnections(this, ctx, RedstoneCarrierFilter)
    }

    override fun canConnect(ctx: NodeContext, other: HalfLink): Boolean {
        return WireConnectionDiscoverers.centerWireCanConnect(this, ctx, other, RedstoneCarrierFilter)
    }

    override fun canConnect(ctx: NodeContext, onSide: Direction, link: HalfLink): Boolean {
        return onSide == side && link.other.node is SidedBlockNode
    }

    override fun onConnectionsChanged(ctx: NodeContext) = Unit

    override val redstoneType = RedstoneWireType.RedAlloy

    override fun putPower(world: ServerWorld, self: NetNode, power: Int) = Unit

    override fun sourcePower(world: ServerWorld, self: NetNode): Int = 0

    object Decoder : BlockNodeDecoder {
        override fun decode(tag: NbtElement?): BlockNode? {
            val byte = tag as? NbtByte ?: return null
            return PowerlineConnectorBlockNode(Direction.byId(byte.intValue()))
        }
    }
}
