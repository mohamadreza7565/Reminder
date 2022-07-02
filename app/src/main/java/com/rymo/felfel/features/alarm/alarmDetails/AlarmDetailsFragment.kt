/*
 * Copyright (C) 2017 Yuriy Kulikov yuriy.kulikov.87@gmail.com
 * Copyright (C) 2012 Yuriy Kulikov yuriy.kulikov.87@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rymo.felfel.features.alarm.alarmDetails

import android.annotation.SuppressLint
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.rymo.felfel.R
import com.rymo.felfel.checkPermissions
import com.rymo.felfel.common.*
import com.rymo.felfel.configuration.Layout
import com.rymo.felfel.configuration.Prefs
import com.rymo.felfel.configuration.globalInject
import com.rymo.felfel.configuration.globalLogger
import com.rymo.felfel.features.alarm.*
import com.rymo.felfel.features.alarm.alarmDetails.dialog.SelectSimCardDialog
import com.rymo.felfel.features.alarm.alarmDetails.dialog.SetDelayDialog
import com.rymo.felfel.features.alarm.alarmList.AlarmsListActivity
import com.rymo.felfel.features.common.dialog.ContactsListDialog
import com.rymo.felfel.interfaces.IAlarmsManager
import com.rymo.felfel.logger.Logger
import com.rymo.felfel.lollipop
import com.rymo.felfel.model.AlarmValue
import com.rymo.felfel.model.Alarmtone
import com.rymo.felfel.util.Optional
import com.rymo.felfel.util.modify
import com.rymo.felfel.view.BaseToolbar
import com.rymo.felfel.view.showDialog
import com.rymo.felfel.view.summary
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.Calendar

/** Details activity allowing for fine-grained alarm modification */
class AlarmDetailsFragment : Fragment() {

    private val mViewModel: AlarmDetailsViewModel by viewModel()
    private val alarms: IAlarmsManager by globalInject()
    private val logger: Logger by globalLogger("AlarmDetailsFragment")
    private val prefs: Prefs by globalInject()
    private var disposables = CompositeDisposable()
    private lateinit var detailsContacts: TextView
    private var backButtonSub: Disposable = Disposables.disposed()
    private var disposableDialog = Disposables.disposed()
    private lateinit var label: EditText

    private val alarmsListActivity by lazy { activity as AlarmsListActivity }
    private val store: UiStore by globalInject()

    private val rowHolder: RowHolder by lazy {
        RowHolder(fragmentView.findViewById(R.id.details_list_row_container), alarmId, prefs.layout())
    }

    private val editor: Observable<AlarmValue> by lazy {
        store.editing().filter { it.value.isPresent() }.map { it.value.get() }
    }

    private val alarmId: Int by lazy { store.editing().value!!.id }

    private val highlighter: ListRowHighlighter? by lazy {
        ListRowHighlighter.createFor(requireActivity().theme)
    }

    private lateinit var fragmentView: View

    private val ringtonePickerRequestCode = 42

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        logger.trace { "Showing details of ${store.editing().value}" }

        val view =
            inflater.inflate(
                when (prefs.layout()) {
                    Layout.CLASSIC -> R.layout.details_fragment_classic
                    Layout.COMPACT -> R.layout.details_fragment_compact
                    else -> R.layout.details_fragment_bold
                },
                container,
                false
            )
        this.fragmentView = view

        initView()

        disposables = CompositeDisposable()


        val toolbarView: BaseToolbar = view.findViewById(R.id.toolbarView)
        toolbarView.onBackButtonClickListener = View.OnClickListener {
            revert()
        }

        onCreateTopRowView()
        onCreateRepeatView()
        onCreateBottomView()
        onCreateLabelView()
        onCreateContactView()
        onCreateDelayView()
        onCreateSimCardView()
        handlerRingtonePickerResult()

        store.transitioningToNewAlarmDetails().takeFirst { isNewAlarm ->
            if (isNewAlarm) {
                showTimePicker()
                mViewModel.getContacts()

            } else {
                mViewModel.getContacts(alarmId.toLong())
                val size = mViewModel.getSizeAlarmContact()
                if (size == 0) {
                    detailsContacts.text = getString(R.string.notSelectedContact)
                } else {
                    detailsContacts.text = "$size ${getString(R.string.contactSelected)}"
                }
            }
            store.transitioningToNewAlarmDetails().onNext(false)
        }

        return view
    }

    private fun initView() {
        detailsContacts = fragmentView.findViewById(R.id.details_contacts)
        label = fragmentView.findViewById<EditText>(R.id.details_label)
    }

    private fun onCreateBottomView() {
        fragmentView.findViewById<View>(R.id.details_activity_button_save).setOnClickListener {

            if (mViewModel.getSizeAlarmContact() == 0) {
                showSnackBar(fragmentView, getString(R.string.selectContactOne))
                return@setOnClickListener
            }
            if (label.text.toString().isEmpty()) {
                showSnackBar(fragmentView, getString(R.string.inputMessage))
                return@setOnClickListener
            }

            store.transitioningToNewAlarmDetails().takeFirst { isNewAlarm ->
                if (isNewAlarm) {
                    mViewModel.addAlarmContacts(alarmId.toLong())
                } else {
                    mViewModel.updateContactAlarm(alarmId.toLong())
                }
            }

            saveAlarm()

        }
        fragmentView.findViewById<View>(R.id.details_activity_button_revert).setOnClickListener {
            revert()
        }
    }

    private fun onCreateLabelView() {
        observeEditor { value ->
            if (value.label != label.text.toString()) {
                label.setText(value.label)
            }
        }

        label.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    editor.takeFirst {
                        if (it.label != s.toString()) {
                            modify("Label") { prev -> prev.copy(label = s.toString(), isEnabled = true) }
                        }
                    }
                }
            })
    }


    private fun onCreateDelayView() {

        val detailsDelayRow: LinearLayout = fragmentView.findViewById(R.id.details_delay_row)
        val detailsDelay: TextView = fragmentView.findViewById(R.id.details_delay)
        var stringTime = ""

        observeEditor { value ->

            Timber.e("Delay : selected from observer -> ${value.delay}")
            val time = value.delay.getTimeFromLong()
            mViewModel.delaySelected = value.delay
            // Check delay is Second  or is minute
            if (time.third == "00") {
                // is only minutes OR minutes and hour
                if (time.first == "00") {
                    if (time.second == "00") {
                        // not set any time
                        stringTime = getString(R.string.notInputDelay)
                    } else {
                        // is only minutes
                        stringTime = "${time.second} ${getString(R.string.minute)} "
                    }
                } else {
                    if (time.second == "00") {
                        // if only hour
                        stringTime = "${time.first} ${getString(R.string.hour)} "
                    } else {
                        // is minutes and hour
                        stringTime = "${time.first} ${getString(R.string.hour)} ${getString(R.string.andText)} ${time.second} ${
                            getString(
                                R.string.minute
                            )
                        } "
                    }
                }
            } else {
                // is seconds or minutes or hour or all of them
                if (time.first == "00") {
                    if (time.second == "00") {
                        // not set any time
                        stringTime = "${time.third} ${getString(R.string.second)}"
                    } else {
                        // is seconds and minutes
                        stringTime = "${time.second} ${getString(R.string.minute)} ${getString(R.string.andText)} ${time.third} ${
                            getString(
                                R.string.second
                            )
                        } "
                    }
                } else {
                    if (time.second == "00") {
                        // is seconds and hours
                        stringTime = "${time.first} ${getString(R.string.hour)} ${getString(R.string.andText)} ${time.third} ${
                            getString(
                                R.string.second
                            )
                        } "
                    } else {
                        // is seconds and minutes and hours
                        stringTime = "${time.first} ${getString(R.string.hour)} ${getString(R.string.andText)} ${time.second} ${
                            getString(
                                R.string.minute
                            )
                        } ${getString(R.string.andText)} ${time.third} ${
                            getString(
                                R.string.second
                            )
                        } "
                    }
                }
            }

            // Check delay typed in textView
            // If not typed setText delay in textView
            if (value.delay != 0L && detailsDelay.text.toString() != stringTime) {
                detailsDelay.text = stringTime
            }
        }

        // set on click for delay layout and show dialog
        // modify long in editor

        detailsDelayRow.setOnClickListener {
            SetDelayDialog(requireContext(), mViewModel.delaySelected) { _delay ->
                Timber.e("Delay : selected -> $_delay")
                editor.takeFirst {
                    modify("Delay") { prev -> prev.copy(delay = _delay, isEnabled = true) }
                }
            }.show(childFragmentManager, "SET_DELAY")
        }

    }

    private fun onCreateSimCardView() {

        val detailsSimRow: LinearLayout = fragmentView.findViewById(R.id.details_sim_row)
        val detailsSim: TextView = fragmentView.findViewById(R.id.details_sim)
        val sims = SimUtil.getSimCount()

        observeEditor { value ->
            if (value.simId == Constants.API_ID) {
                mViewModel.simCardSelected = Constants.API_ID
                detailsSim.text = "از طریق سرور"
            } else {
                val sim = sims.find { value.simId == it.subscriptionId }
                if (sim != null) {
                    detailsSim.text = sim.carrierName
                    mViewModel.simCardSelected = sim.subscriptionId
                } else {
                    val defaultSim = sims.first()
                    detailsSim.text = defaultSim.carrierName
                    mViewModel.simCardSelected = defaultSim.subscriptionId
                }
            }
        }

        // set on click for delay layout and show dialog
        // modify long in editor

        detailsSimRow.setOnClickListener {
            SelectSimCardDialog(requireContext(), mViewModel.simCardSelected) { _simCardId ->
                editor.takeFirst {
                    mViewModel.simCardSelected = _simCardId
                    modify("SimId") { prev -> prev.copy(simId = _simCardId, isEnabled = true) }
                }
            }.show(childFragmentManager, "SELECT_SIM_CARD")
        }

    }

    private fun onCreateRepeatView() {

        fragmentView.findViewById<LinearLayout>(R.id.details_repeat_row).setOnClickListener {
            editor
                .firstOrError()
                .flatMap { value -> value.daysOfWeek.showDialog(requireContext()) }
                .subscribe { daysOfWeek ->
                    modify("Repeat dialog") { prev -> prev.copy(daysOfWeek = daysOfWeek, isEnabled = true) }
                }
                .addTo(disposables)
        }

        val repeatSummary = fragmentView.findViewById<TextView>(R.id.details_repeat_summary)

        observeEditor { value -> repeatSummary.text = value.daysOfWeek.summary(requireContext()) }
    }

    @SuppressLint("SetTextI18n")
    private fun onCreateContactView() {

        fragmentView.findViewById<LinearLayout>(R.id.details_contacts_row).setOnClickListener {
            ContactsListDialog(requireContext(), mViewModel.contacts, mViewModel.groups) { _contacts, _groups ->
                mViewModel.contacts = _contacts
                mViewModel.groups = _groups
                val size = mViewModel.getSizeAlarmContact()
                if (size == 0) {
                    detailsContacts.text = getString(R.string.notSelectedContact)
                } else {
                    detailsContacts.text = "$size ${getString(R.string.contactSelected)}"
                }

            }.show(childFragmentManager, "CONTACT_LIST")
        }

    }

    private fun onCreateTopRowView() =
        rowHolder.apply {
            daysOfWeek.visibility = View.INVISIBLE
            label.visibility = View.INVISIBLE

            lollipop {
                digitalClock.transitionName = "clock$alarmId"
                container.transitionName = "onOff$alarmId"
                detailsButton.transitionName = "detailsButton$alarmId"
            }

            digitalClock.setLive(false)

            val pickerClickTarget =
                if (layout == Layout.CLASSIC) digitalClockContainer else digitalClock

            container.setOnClickListener {
                modify("onOff") { value -> value.copy(isEnabled = !value.isEnabled) }
            }

            pickerClickTarget.setOnClickListener { showTimePicker() }

            rowView.setOnClickListener { saveAlarm() }

            observeEditor { value ->
                rowHolder.digitalClock.updateTime(
                    Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, value.hour)
                        set(Calendar.MINUTE, value.minutes)
                    })

                rowHolder.onOff.isChecked = value.isEnabled

                highlighter?.applyTo(rowHolder, value.isEnabled)
            }

            animateCheck(check = true)
        }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.dispose()
    }

    private fun handlerRingtonePickerResult() {

        val alarmtone = Alarmtone.Silent

        checkPermissions(requireActivity(), listOf(alarmtone))

        modify("Ringtone picker") { prev -> prev.copy(alarmtone = Alarmtone.Silent, isEnabled = true) }
    }

    fun Ringtone?.title(): CharSequence {
        return try {
            context?.let { this?.getTitle(it) } ?: context?.getText(R.string.silent_alarm_summary)
        } catch (e: Exception) {
            context?.getText(R.string.silent_alarm_summary)
        } catch (e: NullPointerException) {
            null
        } ?: ""
    }

    override fun onResume() {
        super.onResume()
        backButtonSub = store.onBackPressed().subscribe { revert() }
    }

    override fun onPause() {
        super.onPause()
        disposableDialog.dispose()
        backButtonSub.dispose()
    }

    private fun saveAlarm() {
        editor.takeFirst { value ->
            value.alarmtone = Alarmtone.Silent
            value.isVibrate = false
            alarms.getAlarm(alarmId)?.run { edit { withChangeData(value) } }
            store.hideDetails(rowHolder)
            rowHolder.animateCheck(check = false)
        }

        store.editing().value?.let { edited ->
            if (edited.isNew) {

            } else {

            }
        }
    }

    private fun revert() {
        store.editing().value?.let { edited ->
            // "Revert" on a newly created alarm should delete it.
            if (edited.isNew) {
                alarms.getAlarm(edited.id)?.delete()
            }
            // else do not save changes
            store.hideDetails(rowHolder)
            rowHolder.animateCheck(check = false)
        }
    }

    private fun showTimePicker() {
        disposableDialog =
            TimePickerDialogFragment.showTimePicker(alarmsListActivity.supportFragmentManager)
                .subscribe { picked: Optional<PickedTime> ->
                    if (picked.isPresent()) {
                        modify("Picker") { value ->
                            value.copy(
                                hour = picked.get().hour, minutes = picked.get().minute, isEnabled = true
                            )
                        }
                    }
                }
    }

    private fun modify(reason: String, function: (AlarmValue) -> AlarmValue) {
        logger.debug { "Performing modification because of $reason" }
        store.editing().modify { copy(value = value.map { function(it) }) }
    }

    private fun Disposable.addTo(disposables: CompositeDisposable) {
        disposables.add(this)
    }

    private fun RowHolder.animateCheck(check: Boolean) {
        rowHolder.detailsCheckImageView.animate().alpha(if (check) 1f else 0f).setDuration(500).start()
        rowHolder.detailsImageView.animate().alpha(if (check) 0f else 1f).setDuration(500).start()
    }

    private fun observeEditor(block: (value: AlarmValue) -> Unit) {
        editor.distinctUntilChanged().subscribe { block(it) }.addTo(disposables)
    }

    private fun <T : Any> Observable<T>.takeFirst(block: (value: T) -> Unit) {
        take(1).subscribe { block(it) }.addTo(disposables)
    }
}
