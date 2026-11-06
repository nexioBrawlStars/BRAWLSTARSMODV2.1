package hu.nexio.hud.overlay

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner

/** Service-ből hozzáadott ComposeView-hoz kell egy saját lifecycle + saved-state tulajdonos. */
class OverlayLifecycleOwner : LifecycleOwner, SavedStateRegistryOwner {
    private val registry = LifecycleRegistry(this)
    private val controller = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = registry
    override val savedStateRegistry: SavedStateRegistry get() = controller.savedStateRegistry

    fun onCreate() {
        controller.performRestore(null)
        registry.currentState = Lifecycle.State.CREATED
    }

    fun onResume() { registry.currentState = Lifecycle.State.RESUMED }
    fun onDestroy() { registry.currentState = Lifecycle.State.DESTROYED }
}
