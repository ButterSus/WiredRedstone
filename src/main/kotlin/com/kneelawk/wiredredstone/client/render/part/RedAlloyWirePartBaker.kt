package com.kneelawk.wiredredstone.client.render.part

import alexiil.mc.lib.multipart.api.render.PartModelBaker
import alexiil.mc.lib.multipart.api.render.PartRenderContext
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.kneelawk.wiredredstone.client.render.*
import com.kneelawk.wiredredstone.part.key.RedAlloyWirePartKey
import com.kneelawk.wiredredstone.util.requireNotNull
import com.kneelawk.wiredredstone.util.threadLocal
import net.fabricmc.fabric.api.renderer.v1.RendererAccess
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh

object RedAlloyWirePartBaker : PartModelBaker<RedAlloyWirePartKey> {
    private val builder by threadLocal {
        RendererAccess.INSTANCE.renderer.requireNotNull("Renderer is null").meshBuilder()
    }

    private val cache: LoadingCache<RedAlloyWirePartKey, Mesh> =
        CacheBuilder.newBuilder().build(CacheLoader.from(::makeMesh))

    private fun makeMesh(key: RedAlloyWirePartKey): Mesh {
        val emitter = TransformingQuadEmitter.Single(builder.emitter, SideQuadTransform(key.side))

        val spriteId = if (key.powered) {
            WRSprites.RED_ALLOY_WIRE_POWERED_ID
        } else {
            WRSprites.RED_ALLOY_WIRE_UNPOWERED_ID
        }

        val sprite = RenderUtils.getBlockSprite(spriteId)

        val material = if (key.powered) {
            WRMaterials.POWERED_MATERIAL
        } else {
            WRMaterials.UNPOWERED_MATERIAL
        }

        RenderUtils.emitWire(key.connections, key.side.axis, 2f, 2f, sprite, sprite, 7f / 16f, material, emitter)

        return builder.build()
    }

    override fun emitQuads(key: RedAlloyWirePartKey, ctx: PartRenderContext) {
        ctx.meshConsumer().accept(cache[key])
    }
}