package molokoka.project.n

import platform.UIKit.UIDevice
import package_Test.*
import kotlinx.cinterop.ExperimentalForeignApi


class IOSPlatform: Platform {
    @ExperimentalForeignApi()
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion + " " + Test().test()
}

actual fun getPlatform(): Platform = IOSPlatform()
