package com.tunjid.androidbootstrap.baseclasses

import android.annotation.SuppressLint
import android.transition.*
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.snackbar.Snackbar
import com.tunjid.androidbootstrap.activities.MainActivity
import com.tunjid.androidbootstrap.activities.MainActivity.Companion.ANIMATION_DURATION
import com.tunjid.androidbootstrap.core.components.FragmentStateViewModel
import com.tunjid.androidbootstrap.view.util.InsetFlags

abstract class AppBaseFragment(
        @LayoutRes contentLayoutId: Int = 0
) : Fragment(contentLayoutId), FragmentStateViewModel.FragmentTagProvider {

    open val insetFlags: InsetFlags = InsetFlags.ALL

    override val stableTag: String
        get() = javaClass.simpleName

    private val hostingActivity: MainActivity
        get() = requireActivity() as MainActivity

    override fun onDestroyView() {
        super.onDestroyView()
        arguments?.putBoolean(VIEW_DESTROYED, true)
    }

    fun showFragment(fragment: AppBaseFragment): Boolean =
            fragment.let { hostingActivity.fragmentStateViewModel.showFragment(it, it.stableTag, provideFragmentTransaction(it)) }

    protected fun showSnackbar(consumer: (Snackbar) -> Unit) =
            hostingActivity.showSnackBar(consumer)

    protected fun baseSharedTransition(): Transition = TransitionSet()
            .setOrdering(TransitionSet.ORDERING_TOGETHER)
            .addTransition(ChangeImageTransform())
            .addTransition(ChangeTransform())
            .addTransition(ChangeBounds())
            .setDuration(ANIMATION_DURATION.toLong())

    @SuppressLint("CommitTransaction")
    open fun provideFragmentTransaction(fragmentTo: AppBaseFragment): FragmentTransaction? =
            requireActivity().supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                            android.R.anim.fade_in, android.R.anim.fade_out)

    /**
     * Checks whether this fragment was shown before and it's view subsequently
     * destroyed by placing it in the back stack
     */
    fun restoredFromBackStack(): Boolean {
        val args = arguments
        return (args != null && args.containsKey(VIEW_DESTROYED)
                && args.getBoolean(VIEW_DESTROYED))
    }

    companion object {
        const val BACKGROUND_TINT_DURATION = 1200
        private const val VIEW_DESTROYED = "com.tunjid.androidbootstrap.core.abstractclasses.basefragment.view.destroyed"

        val NO_BOTTOM: InsetFlags = InsetFlags.create(true, true, true, false)
    }

}