import android.app.Application
import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.etip.mobile.android.claimroom.viewstate.CameraXViewState
import io.etip.mobile.models.MimeTypes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class CameraXViewModel(private val application: Application) : ViewModel() {
    private var _viewState = MutableStateFlow(CameraXViewState())
    val viewState = _viewState.asStateFlow()
    private val imageCapture = ImageCapture.Builder().build()

    private val barcodeScanningOptions = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()
    private val scanner = BarcodeScanning.getClient(barcodeScanningOptions)

    fun getImageCapture() = imageCapture

    private val barcodeScanning = object : ImageCapture.OnImageSavedCallback {
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            Log.i("CameraXViewModel", "Photo capture succeeded: ${outputFileResults.savedUri}")
            val image =
                outputFileResults.savedUri?.let { InputImage.fromFilePath(application, it) }
                    ?: throw Exception("NULL URI EXCEPTION")
            scanner.process(image)
                .addOnSuccessListener {
                    Log.i("CameraXViewModel", "Success Scanning")
                    deleteGalleryImage(outputFileResults.savedUri!!)
                    onSuccessfulScan(it)
                }
                .addOnFailureListener {
                    onFailedScan(it)
                }
        }

        override fun onError(exception: ImageCaptureException) {
            Log.i("CameraXViewModel", "Error Saving Picture")
        }
    }

    fun takePicture() {
        Log.i("CameraXViewModel", "Taking Picture")
        val name = SimpleDateFormat(DateFormat.FULL.toString(), Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, MimeTypes.IMAGE_JPEG.id)
        }
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                application.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(application.baseContext),
            barcodeScanning
        )
    }

    fun onPermissionsReceived(permissions: MutableMap<String, Boolean>) {
        permissions.entries.forEach {
            Log.i(this.toString(), "${it.key} = ${it.value}")
        }
        _viewState.value = _viewState.value.copy(isAskingForPermissions = false)
    }

    fun askForPermissions() {
        _viewState.value = _viewState.value.copy(isAskingForPermissions = true)
    }

    fun goBackToHomeLandingPage() {
        _viewState.value = CameraXViewState(isUserDoneWithCamera = true)
    }

    fun openCameraViewAfterFailedScan() {
        _viewState.value =
            CameraXViewState(isAskingForPermissions = true, doesUserWantToRetakePhoto = true)
    }

    private fun onSuccessfulScan(scannedQrCodes: List<Barcode>) {
        if (scannedQrCodes.isEmpty()) {
            _viewState.value = CameraXViewState(
                isQRCodeURL = false,
                didUserScanQrCode = true
            )
            return
        }
        _viewState.value = CameraXViewState(
            isQRCodeURL = true,
            didUserScanQrCode = true,
            qrCodeURL = scannedQrCodes[0].displayValue!!
        )
    }

    private fun onFailedScan(exception: Exception) {
        Log.e(this.toString(), exception.message.toString())
        _viewState.value = _viewState.value.copy(
            isQRCodeURL = false,
            didUserScanQrCode = true
        )
    }

    private fun deleteGalleryImage(uri: Uri) {
        application.contentResolver.delete(uri, null, null)
    }
}
