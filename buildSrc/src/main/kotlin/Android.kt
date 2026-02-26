/**
 * des 管理AdnroidX相关依赖
 * @author zs
 * @date   2020/9/15
 */
@Suppress("SpellCheckingInspection")
object Android {

    const val appcompat         = "androidx.appcompat:appcompat:1.6.1"
    const val coreKtx           = "androidx.core:core-ktx:1.12.0"
    const val constraintlayout  = "androidx.constraintlayout:constraintlayout:2.1.4"
    const val legacy            = "androidx.legacy:legacy-support-v4:1.0.0"
    const val paging            = "androidx.paging:paging-runtime:3.2.1"
    const val viewpager2        = "androidx.viewpager2:viewpager2:1.1.0-beta02"
    const val multidex          = "com.android.support:multidex:1.0.3"
    const val material          = "com.google.android.material:material:1.10.0"

    val lifecycle = Lifecycle
    object Lifecycle {
        private const val lifecycle_version = "2.6.2"
        const val extensions    = "androidx.lifecycle:lifecycle-extensions:2.2.0"
        const val liveDataKtx   = "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version"
        const val commonJava8   = "androidx.lifecycle:lifecycle-common-java8:$lifecycle_version"
        const val viewModelKtx  = "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version"
        const val runtimeKtx    = "androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version"
    }

    val navigation = Navigation
    object Navigation {
        private const val navigation_version = "2.7.6"
        const val fragmentKtx   = "androidx.navigation:navigation-fragment-ktx:$navigation_version"
        const val uiKtx         = "androidx.navigation:navigation-ui-ktx:$navigation_version"
        const val compose       = "androidx.navigation:navigation-compose:$navigation_version"
    }

    val room = Room
    object Room {
        private const val room_version = "2.6.1"
        const val roomRuntime   = "androidx.room:room-runtime:$room_version"
        const val roomCompiler  = "androidx.room:room-compiler:$room_version"
        const val roomKtx       = "androidx.room:room-ktx:$room_version"
    }

    val compose = Compose
    object Compose {
        private const val compose_version = "1.5.4"
        const val ui                    = "androidx.compose.ui:ui:$compose_version"
        const val uiTooling             = "androidx.compose.ui:ui-tooling:$compose_version"
        const val uiToolingPreview      = "androidx.compose.ui:ui-tooling-preview:$compose_version"
        const val foundation            = "androidx.compose.foundation:foundation:$compose_version"
        const val material              = "androidx.compose.material:material:$compose_version"
        const val material3             = "androidx.compose.material3:material3:1.1.2"
        const val materialIcons          = "androidx.compose.material:material-icons-extended:$compose_version"
        const val runtime               = "androidx.compose.runtime:runtime:$compose_version"
        const val runtimeLivedata       = "androidx.compose.runtime:runtime-livedata:$compose_version"
        const val activity              = "androidx.activity:activity-compose:${Version.activityComposeVersion}"
        const val viewModel             = "androidx.lifecycle:lifecycle-viewmodel-compose:${Version.lifecycleComposeVersion}"
        const val paging                = "androidx.paging:paging-compose:3.2.1"
        const val coil                  = "io.coil-kt:coil-compose:2.4.0"
    }
}
