package zebrostudio.wallr100.presentation.wallpaper

import zebrostudio.wallr100.android.utils.FragmentTag
import zebrostudio.wallr100.presentation.BasePresenter
import zebrostudio.wallr100.presentation.BaseView
import zebrostudio.wallr100.presentation.wallpaper.model.ImagePresenterEntity

interface ImageListContract {

  interface ImageListView : BaseView {
    fun showLoader()
    fun hideLoader()
    fun showNoInternetMessageView()
    fun showImageList(list: List<ImagePresenterEntity>)
    fun hideRefreshing()
    fun hideAllLoadersAndMessageViews()
  }

  interface ImageListPresenter : BasePresenter<ImageListView> {
    fun setImageListType(fragmentTag: FragmentTag, position: Int)
    fun fetchImages(refresh: Boolean)
  }
}