package zebrostudio.wallr100.android.ui

import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import dagger.android.AndroidInjection
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.utils.checkDataConnection
import zebrostudio.wallr100.presentation.BaseView

abstract class BaseActivity : AppCompatActivity(), BaseView {

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    super.onCreate(savedInstanceState)
    overridePendingTransition(R.anim.slide_from_right, R.anim.no_change)
  }

  override fun onBackPressed() {
    overridePendingTransition(R.anim.no_change, R.anim.slide_to_right)
    super.onBackPressed()
  }

  override fun getScope(): ScopeProvider {
    return AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)
  }

  override fun internetAvailability() = this.checkDataConnection()

}