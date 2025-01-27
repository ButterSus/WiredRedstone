package com.kneelawk.wiredredstone

import com.kneelawk.wiredredstone.block.WRBlocks
import com.kneelawk.wiredredstone.blockentity.WRBlockEntities
import com.kneelawk.wiredredstone.compat.cc.CCIntegrationHandler
import com.kneelawk.wiredredstone.compat.create.CreateCompatHandler
import com.kneelawk.wiredredstone.compat.emi.EMIIntegrationHandler
import com.kneelawk.wiredredstone.config.CommonConfig
import com.kneelawk.wiredredstone.item.WRItems
import com.kneelawk.wiredredstone.logic.RedstoneLogic
import com.kneelawk.wiredredstone.logic.phantom.PhantomRedstone
import com.kneelawk.wiredredstone.net.WRNetworking
import com.kneelawk.wiredredstone.node.WRBlockNodeDiscoverer
import com.kneelawk.wiredredstone.node.WRBlockNodes
import com.kneelawk.wiredredstone.part.WRParts
import com.kneelawk.wiredredstone.recipe.WRRecipes
import com.kneelawk.wiredredstone.screenhandler.WRScreenHandlers
import com.kneelawk.wiredredstone.keybinding.WRKeyBindings

@Suppress("unused")
fun init() {
    WRLog.log.info("[Wired Redstone] [v${WRConstants.MOD_VERSION}] Initializing...")

    CommonConfig.ensureInit()
    WRParts.init()
    WRBlocks.init()
    WRItems.init()
    WRBlockEntities.init()
    WRBlockNodes.init()
    WRRecipes.init()
    WRScreenHandlers.init()
    WRNetworking.init()
    WRKeyBindings.init()

    CCIntegrationHandler.init()
    EMIIntegrationHandler.init()
    CreateCompatHandler.init()

    RedstoneLogic.init()
    PhantomRedstone.init()

    WRLog.log.info("[Wired Redstone] Initialized.")
}
