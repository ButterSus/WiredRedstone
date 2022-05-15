package com.kneelawk.wiredredstone.node

import com.kneelawk.graphlib.graph.BlockNode
import com.kneelawk.graphlib.graph.BlockNodeDecoder
import com.kneelawk.graphlib.graph.NodeView
import com.kneelawk.graphlib.util.SidedPos
import com.kneelawk.graphlib.wire.SidedWireBlockNode
import com.kneelawk.graphlib.wire.WireConnectionDiscoverers
import com.kneelawk.wiredredstone.part.BundledCablePart
import com.kneelawk.wiredredstone.part.SidedPart
import com.kneelawk.wiredredstone.util.NetNode
import com.kneelawk.wiredredstone.util.RedstoneCarrierFilter
import com.kneelawk.wiredredstone.util.RedstoneWireType
import com.kneelawk.wiredredstone.util.WireBlockageFilter
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView
import net.minecraft.world.World

data class BundledCableBlockNode(private val side: Direction, val color: DyeColor?, val inner: DyeColor) :
    SidedWireBlockNode, RedstoneCarrierBlockNode {

    private val filter =
        RedstoneCarrierFilter.and(WireBlockageFilter(side, BundledCablePart.WIRE_WIDTH, BundledCablePart.WIRE_HEIGHT))

    override val redstoneType = RedstoneWireType.Bundled(color, inner)

    override fun getSide(): Direction = side
    override fun getTypeId(): Identifier = WRBlockNodes.BUNDLED_CABLE_ID

    private fun getPart(world: BlockView, pos: BlockPos): BundledCablePart? {
        return SidedPart.getPart(world, SidedPos(pos, side)) as? BundledCablePart
    }

    override fun findConnections(world: ServerWorld, nv: NodeView, pos: BlockPos): Collection<NetNode> {
        return WireConnectionDiscoverers.wireFindConnections(this, world, nv, pos, filter)
    }

    override fun canConnect(
        world: ServerWorld, nodeView: NodeView, pos: BlockPos, other: NetNode
    ): Boolean {
        return WireConnectionDiscoverers.wireCanConnect(this, world, pos, filter, other)
    }

    override fun getState(world: World, self: NetNode): Int {
        return 0
    }

    override fun setState(world: World, self: NetNode, state: Int) {
    }

    override fun getInput(world: World, self: NetNode): Int {
        // TODO: BundledCableIO support
        return 0
    }

    override fun onChanged(world: ServerWorld, pos: BlockPos) {
        getPart(world, pos)?.handleUpdates()
    }

    override fun toTag(): NbtElement {
        val tag = NbtCompound()
        tag.putByte("side", side.id.toByte())
        color?.let { tag.putByte("color", it.id.toByte()) }
        tag.putByte("inner", inner.id.toByte())
        return tag
    }

    object Decoder : BlockNodeDecoder {
        override fun createBlockNodeFromTag(tag: NbtElement?): BlockNode? {
            if (tag !is NbtCompound) return null
            val side = Direction.byId(tag.getByte("side").toInt())
            val color = if (tag.contains("color")) DyeColor.byId(tag.getByte("color").toInt()) else null
            val inner = DyeColor.byId(tag.getByte("inner").toInt())
            return BundledCableBlockNode(side, color, inner)
        }
    }
}
