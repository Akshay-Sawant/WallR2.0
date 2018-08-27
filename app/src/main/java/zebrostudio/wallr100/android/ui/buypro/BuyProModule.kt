package zebrostudio.wallr100.android.ui.buypro

import android.content.Context
import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.domain.interactor.AuthenticatePurchaseUseCase
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.presentation.buypro.BuyProContract
import zebrostudio.wallr100.presentation.buypro.BuyProPresenterImpl

@Module
class BuyProModule {

  @Provides
  internal fun provideBuyProPresenter(
    authenticatePurchaseUseCase: AuthenticatePurchaseUseCase,
    userPremiumStatusUseCase: UserPremiumStatusUseCase
  ): BuyProContract.BuyProPresenter = BuyProPresenterImpl(
      authenticatePurchaseUseCase,
      userPremiumStatusUseCase)

}