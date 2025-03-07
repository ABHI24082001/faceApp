package com.facedetectionapp

import android.graphics.Rect
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.LifecycleOwner
import androidx.core.content.ContextCompat
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FaceDetectionModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    private val faceDetector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .build()
    )
    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private var cameraProvider: ProcessCameraProvider? = null

    override fun getName(): String {
        return "FaceDetectionModule"
    }

    @ReactMethod
    fun startCamera(promise: Promise) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(reactApplicationContext)

        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                    .build()

                val preview = Preview.Builder().build()
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(cameraExecutor) { image ->
                    processFaceDetection(image) // Call the updated function here
                }

                val activity = reactApplicationContext.currentActivity
                if (activity is LifecycleOwner) {
                    cameraProvider?.unbindAll()
                    cameraProvider?.bindToLifecycle(
                        activity,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                    promise.resolve(true)
                } else {
                    promise.reject("LIFECYCLE_ERROR", "Activity is not a LifecycleOwner")
                }
            } catch (e: Exception) {
                promise.reject("CAMERA_ERROR", e)
            }
        }, ContextCompat.getMainExecutor(reactApplicationContext))
    }

    @OptIn(androidx.camera.core.ExperimentalGetImage::class)
    private fun processFaceDetection(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: return
        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        faceDetector.process(inputImage)
            .addOnSuccessListener { faces ->
                val faceData = Arguments.createArray()
                for (face in faces) {
                    val faceMap = Arguments.createMap()
                    val bounds: Rect = face.boundingBox
                    faceMap.putInt("x", bounds.left)
                    faceMap.putInt("y", bounds.top)
                    faceMap.putInt("width", bounds.width())
                    faceMap.putInt("height", bounds.height())
                    faceData.pushMap(faceMap)
                }
                sendEvent("onFaceDetected", faceData)
            }
            .addOnFailureListener { e -> Log.e("FaceDetection", "Error: $e") }
            .addOnCompleteListener { imageProxy.close() }
    }

    private fun sendEvent(eventName: String, params: WritableArray) {
        reactApplicationContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            ?.emit(eventName, params)
    }
}
