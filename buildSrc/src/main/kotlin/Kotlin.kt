/**
 * des 管理kotlin相关依赖
 * @author zs
 * @date   2020/9/15
 */
object Kotlin {
    const val kotlinVersion = "1.8.22"
    const val jdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"

    val coroutines = Coroutines
    object Coroutines {
        private const val coroutines_version = "1.7.3"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutines_version"
    }
}
