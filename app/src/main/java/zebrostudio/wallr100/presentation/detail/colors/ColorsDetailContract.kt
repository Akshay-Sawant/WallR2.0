package zebrostudio.wallr100.presentation.detail.colors

import android.content.Intent
import android.graphics.Bitmap
import zebrostudio.wallr100.presentation.BasePresenter
import zebrostudio.wallr100.presentation.BaseView

interface ColorsDetailContract {

  interface ColorsDetailView : BaseView {
    fun throwIllegalStateException()
    fun exitView()
    fun hasStoragePermission(): Boolean
    fun requestStoragePermission(colorsActionType: ColorsActionType)
    fun showPermissionRequiredMessage()
    fun showUnsuccessfulPurchaseError()
    fun showImage(bitmap: Bitmap)
    fun showMainImageWaitLoader()
    fun hideMainImageWaitLoader()
    fun showImageLoadError()
    fun showNoInternetToShareError()
  }

  interface ColorsDetailPresenter : BasePresenter<ColorsDetailView> {
    fun setCalledIntent(intent: Intent)
    fun handlePermissionRequestResult(
      requestCode: Int, permissions: Array<String>, grantResults: IntArray
    )

    fun handleQuickSetClick()
    fun handleDownloadClick()
    fun handleEditSetClick()
    fun handleAddToCollectionClick()
    fun handleShareClick()
    fun handleBackButtonClick()
  }
}