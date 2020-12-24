package com.snaggly.ksw_toolkit.gui

import android.animation.ObjectAnimator
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.ListView
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.snaggly.ksw_toolkit.R
import com.snaggly.ksw_toolkit.gui.viewmodels.EventManagerSelectActionViewModel

class EventManagerSelectAction(name: String?) : Fragment() {
    private lateinit var mViewModel: EventManagerSelectActionViewModel
    private lateinit var listKeyEvents: RecyclerView
    private lateinit var listApps: RecyclerView

    private lateinit var leaveToTopAnimation : Animation
    private lateinit var leaveToButtomAnimation : Animation
    private lateinit var enterFromTopAnimation : Animation
    private lateinit var enterFromButtomAnimation : Animation

    private var mode: ActionMode = ActionMode.DoNothing

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.event_manager_select_action_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(EventManagerSelectActionViewModel::class.java)

        loadAnimations(300)

        listKeyEvents = requireView().findViewById(R.id.availableKeyEventsListView)
        listKeyEvents.setHasFixedSize(true)
        listKeyEvents.layoutManager = LinearLayoutManager(context)
        listKeyEvents.adapter = context?.let { mViewModel.getListKeyEventsAdapter(it) }

        listApps = requireView().findViewById(R.id.availableAppsListView)
        listApps.setHasFixedSize(true)
        listApps.layoutManager = LinearLayoutManager(context)
        listApps.adapter = context?.let { mViewModel.getAvailableAppsAdapter(it) }

        val doNothingButton = requireView().findViewById<RadioButton?>(R.id.unnassignBtn)
        val invokeKeyButton = requireView().findViewById<RadioButton?>(R.id.invokeKeyeventRadioButton)
        val startAppButton = requireView().findViewById<RadioButton?>(R.id.startAppRadioButton)

        doNothingButton.requestFocus()

        doNothingButton.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                when (mode) {
                    ActionMode.InvokeKeyEvent -> {
                        invokeKeyButton.isChecked = false
                        listKeyEvents.clearAnimation()
                        listKeyEvents.startAnimation(leaveToTopAnimation)
                        listKeyEvents.visibility = View.GONE
                    }
                    ActionMode.StartApp -> {
                        startAppButton.isChecked = false
                        listApps.clearAnimation()
                        listApps.startAnimation((leaveToTopAnimation))
                        listApps.visibility = View.GONE
                    }
                }
                mode = ActionMode.DoNothing
            }
        }
        invokeKeyButton.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                listKeyEvents.visibility = View.VISIBLE
                listKeyEvents.startAnimation(enterFromTopAnimation)

                if (mode == ActionMode.StartApp) {
                    startAppButton.isChecked = false
                    listApps.clearAnimation()
                    listApps.visibility = View.GONE
                    listApps.startAnimation(leaveToButtomAnimation)
                }
                else
                    doNothingButton.isChecked = false

                listKeyEvents.requestFocus()
                mode = ActionMode.InvokeKeyEvent
            }
        }
        startAppButton.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                listApps.visibility = View.VISIBLE
                listApps.requestFocus()
                if (mode == ActionMode.InvokeKeyEvent) {
                    invokeKeyButton.isChecked = false
                    listKeyEvents.clearAnimation()
                    listKeyEvents.startAnimation(leaveToTopAnimation)
                    listKeyEvents.visibility = View.GONE
                    listApps.startAnimation(enterFromButtomAnimation)
                }
                else {
                    doNothingButton.isChecked = false
                    listApps.startAnimation(enterFromTopAnimation)
                }

                listApps.requestFocus()
                mode = ActionMode.StartApp
            }
        }
    }

    private fun loadAnimations(duration: Long) {
        leaveToTopAnimation = AnimationUtils.loadAnimation(context, R.anim.leave_to_top)
        leaveToTopAnimation.duration = duration
        leaveToButtomAnimation = AnimationUtils.loadAnimation(context, R.anim.leave_to_buttom)
        leaveToButtomAnimation.duration = duration
        enterFromTopAnimation = AnimationUtils.loadAnimation(context, R.anim.enter_from_top)
        enterFromTopAnimation.duration = duration
        enterFromButtomAnimation = AnimationUtils.loadAnimation(context, R.anim.enter_from_buttom)
        enterFromButtomAnimation.duration = duration
    }

    companion object {
        fun newInstance(name: String?): EventManagerSelectAction? {
            return EventManagerSelectAction(name)
        }
    }

    enum class ActionMode {DoNothing, InvokeKeyEvent, StartApp}
}