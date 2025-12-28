//package com.flutter
//
//import android.content.Context
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import com.jetpack.kobe.R
//import io.flutter.embedding.android.FlutterTextureView
//import io.flutter.embedding.android.FlutterView
//import io.flutter.embedding.engine.FlutterEngine
//import io.flutter.embedding.engine.dart.DartExecutor
//
///**
// * @author yuandunbin
// * @date 2022/12/16
// */
//abstract class HiFlutterFragment : HiBaseFragment() {
//    protected lateinit var flutterEngine: FlutterEngine
//    protected var flutterView: FlutterView? = null
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        flutterEngine = FlutterEngine(context)
//        flutterEngine.dartExecutor.executeDartEntrypoint(DartExecutor.DartEntrypoint.createDefault())
//    }
//
//    override fun getLayoutId(): Int {
//        return R.layout.dialog_confirm
//    }
//
//    fun setTitle(titleStr: String) {
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        (layoutView as ViewGroup).addView(createFlutterView(requireActivity()))
//    }
//
//    private fun createFlutterView(context: Context):FlutterView {
//        val flutterTextureView = FlutterTextureView(requireActivity())
//        flutterView = FlutterView(context, flutterTextureView)
//        return flutterView!!
//    }
//
//    override fun onStart() {
//        flutterView!!.attachToFlutterEngine(flutterEngine)
//        super.onStart()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        flutterEngine.lifecycleChannel.appIsResumed()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        flutterEngine!!.lifecycleChannel.appIsInactive()
//    }
//
//    override fun onStop() {
//        super.onStop()
//        flutterEngine!!.lifecycleChannel.appIsPaused()
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//        flutterEngine!!.lifecycleChannel.appIsDetached()
//    }
//}