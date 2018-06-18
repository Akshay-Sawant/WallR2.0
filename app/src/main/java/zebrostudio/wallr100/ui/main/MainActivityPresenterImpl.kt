package zebrostudio.wallr100.ui.main

import android.os.Handler
import zebrostudio.wallr100.data.DataRepository

class MainActivityPresenterImpl
constructor(dataRepository: DataRepository) : MainContract.MainPresenter {

  private val EXPLORE_FRAGMENT_TAG = "Explore"
  private var dataRepository: DataRepository = dataRepository
  private var backPressedOnce: Boolean = false
  private var mainView: MainContract.MainView? = null
  private var isGuillotineMenuOpen = false

  override fun attachView(view: MainContract.MainView) {
    mainView = view
  }

  override fun detachView() {
    mainView = null
  }

  override fun handleBackPress() {
    if (isGuillotineMenuOpen) {
      mainView?.closeGuillotineMenu()
    } else {
      if (dataRepository.retrieveCurrentFragmentName() == EXPLORE_FRAGMENT_TAG) {
        if (backPressedOnce) {
          mainView?.exitApp()
        } else {
          backPressedOnce = true
          mainView?.showExitConfirmation()
          Handler().postDelayed({ backPressedOnce = false }, 2000)
        }
      } else {
        mainView?.showPreviousFragment()
      }
    }
  }

  override fun notifyNavigationMenuOpened() {
    isGuillotineMenuOpen = true
  }

  override fun notifyNavigationMenuClosed() {
    isGuillotineMenuOpen = false
  }

}