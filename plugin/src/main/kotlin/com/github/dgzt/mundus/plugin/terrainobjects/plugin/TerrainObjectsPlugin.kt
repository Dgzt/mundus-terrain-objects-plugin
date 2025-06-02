package com.github.dgzt.mundus.plugin.terrainobjects.plugin

import com.badlogic.gdx.utils.Array
import com.github.dgzt.mundus.plugin.terrainobjects.plugin.creator.ComponentCreator
import com.github.dgzt.mundus.plugin.terrainobjects.plugin.creator.ComponentWidgetCreator
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.component.TerrainObjectsComponent
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.constant.PluginConstants
import com.github.dgzt.mundus.plugin.terrainobjects.runtime.converter.TerrainObjectsComponentConverter
import com.mbrlabs.mundus.commons.mapper.CustomComponentConverter
import com.mbrlabs.mundus.commons.scene3d.GameObject
import com.mbrlabs.mundus.commons.scene3d.components.Component
import com.mbrlabs.mundus.pluginapi.ComponentExtension
import com.mbrlabs.mundus.pluginapi.MenuExtension
import com.mbrlabs.mundus.pluginapi.ui.RootWidget
import org.pf4j.Extension
import org.pf4j.Plugin

class TerrainObjectsPlugin : Plugin() {

    @Extension
    class TerrainObjectsMenuExtension : MenuExtension {

        companion object {
            const val PAD = 5f
        }

        override fun getMenuName(): String = "Terrain Objects plugin"

        override fun setupDialogRootWidget(root: RootWidget) {
            root.addLabel("Terrain Objects plugin").setPad(PAD, PAD, PAD, PAD)
        }
    }

    @Extension
    class TerrainObjectsComponentExtension : ComponentExtension {
        override fun getSupportedComponentTypes(): Array<Component.Type> {
            val array = Array<Component.Type>()
            array.add(Component.Type.TERRAIN)
            return array
        }

        override fun getComponentType(): Component.Type = PluginConstants.TYPE

        override fun getComponentName(): String = "Terrain Objects"

        override fun createComponent(gameObject: GameObject): Component = ComponentCreator.create(gameObject)

        override fun setupComponentInspectorWidget(component: Component, rootWidget: RootWidget) =
            ComponentWidgetCreator.setup(component as TerrainObjectsComponent, rootWidget)

        override fun getConverter(): CustomComponentConverter = TerrainObjectsComponentConverter()

    }
}
