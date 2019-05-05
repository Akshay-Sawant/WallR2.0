package zebrostudio.wallr100.domain.interactor

import zebrostudio.wallr100.domain.WallrRepository

interface WidgetHintsUseCase {
  fun isNavigationMenuHamburgerHintShown(): Boolean
  fun saveNavigationMenuHamburgerHintShownState()
  fun isMultiColorImageModesHintShown(): Boolean
  fun saveMultiColorImageHintShownState()
}

class WidgetHintsInteractor(private val wallrRepository: WallrRepository) : WidgetHintsUseCase {
  override fun isNavigationMenuHamburgerHintShown(): Boolean {
    return wallrRepository.isAppOpenedForTheFirstTime()
  }

  override fun saveNavigationMenuHamburgerHintShownState() {
    wallrRepository.saveAppPreviouslyOpenedState()
  }

  override fun isMultiColorImageModesHintShown(): Boolean {
    return wallrRepository.isMultiColorModesHintShown()
  }

  override fun saveMultiColorImageHintShownState() {
    wallrRepository.saveMultiColorModesHintShownState()
  }

}