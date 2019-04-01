package zebrostudio.wallr100.android.ui

import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import android.support.v4.app.Fragment
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.getbase.floatingactionbutton.FloatingActionButton
import com.ogaclejapan.smarttablayout.SmartTabLayout
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.zebrostudio.wallrcustoms.customtextview.WallrCustomTextView
import dagger.android.support.AndroidSupportInjection
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.utils.FragmentNameTagFetcher
import zebrostudio.wallr100.android.utils.FragmentNameTagFetcher.Companion.EXPLORE_TAG
import zebrostudio.wallr100.android.utils.checkDataConnection
import zebrostudio.wallr100.android.utils.gone
import zebrostudio.wallr100.android.utils.invisible
import zebrostudio.wallr100.android.utils.setMenuItemColorRed
import zebrostudio.wallr100.android.utils.setMenuItemColorWhite
import zebrostudio.wallr100.android.utils.stringRes
import zebrostudio.wallr100.android.utils.visible
import zebrostudio.wallr100.presentation.BaseView
import javax.inject.Inject

abstract class BaseFragment : Fragment(), BaseView {

  @Inject lateinit var fragmentNameTagFetcherImpl: FragmentNameTagFetcher
  internal var fragmentTag: String = EXPLORE_TAG

  private val menuItemIdList: List<Int> = listOf(
      R.string.explore_fragment_tag,
      R.string.top_picks_fragment_tag,
      R.string.categories_fragment_tag,
      R.string.minimal_fragment_tag,
      R.string.collection_fragment_tag
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    AndroidSupportInjection.inject(this)
  }

  @SuppressLint("ResourceType")
  override fun onResume() {
    super.onResume()
    activity?.findViewById<WallrCustomTextView>(R.id.toolbarTitle)?.text =
        fragmentNameTagFetcherImpl.getFragmentName(fragmentTag)

    highlightCurrentMenuItem()
    showToolbarMenuIcon()
    configureTabs()
    hideBottomLayout()
  }

  private fun highlightCurrentMenuItem() {
    for (menuItem in menuItemIdList) {
      if (stringRes(menuItem) == fragmentTag) {
        activity?.findViewById<LinearLayout>(menuItem)?.setMenuItemColorRed(this.context!!)
      } else {
        activity?.findViewById<LinearLayout>(menuItem)?.setMenuItemColorWhite(this.context!!)
      }
    }
  }

  private fun showToolbarMenuIcon() {
    activity?.let {
      it.findViewById<ImageView>(R.id.toolbarMultiSelectIcon)?.gone()
      it.findViewById<ImageView>(R.id.toolbarSearchIcon)?.gone()
      when (fragmentTag) {
        stringRes(R.string.minimal_fragment_tag) ->
          it.findViewById<ImageView>(R.id.toolbarMultiSelectIcon)?.visible()
        stringRes(R.string.collection_fragment_tag) -> {  // Do nothing
        }
        else -> it.findViewById<ImageView>(R.id.toolbarSearchIcon)?.visible()
      }
    }
  }

  private fun configureTabs() {
    activity?.let {
      if (fragmentTag == stringRes(R.string.categories_fragment_tag) ||
          fragmentTag == stringRes(R.string.top_picks_fragment_tag)) {
        it.findViewById<SmartTabLayout>(R.id.tabLayout)?.visible()
      } else {
        it.findViewById<SmartTabLayout>(R.id.tabLayout)?.gone()
      }
    }
  }

  private fun hideBottomLayout() {
    activity?.let {
      it.findViewById<RelativeLayout>(R.id.minimalBottomLayout)?.invisible()
      it.findViewById<RelativeLayout>(R.id.minimalBottomLayout)?.isClickable = false
      it.findViewById<FloatingActionButton>(R.id.minimalBottomLayoutFab)?.invisible()
      it.findViewById<FloatingActionButton>(R.id.minimalBottomLayoutFab)?.isClickable = false
    }
  }

  override fun getScope(): ScopeProvider {
    return AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)
  }

  override fun internetAvailability() = activity?.checkDataConnection()!!

}