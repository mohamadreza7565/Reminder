package com.rymo.felfel.features.alarm.alarmList

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import com.rymo.felfel.R
import com.rymo.felfel.configuration.Layout
import com.rymo.felfel.configuration.Prefs
import com.rymo.felfel.configuration.Store
import com.rymo.felfel.configuration.globalInject
import com.rymo.felfel.configuration.globalLogger
import com.rymo.felfel.interfaces.IAlarmsManager
import com.rymo.felfel.logger.Logger
import com.rymo.felfel.lollipop
import com.rymo.felfel.model.AlarmValue
import com.melnykov.fab.FloatingActionButton
import com.rymo.felfel.data.preferences.Setting
import com.rymo.felfel.database.RoomAppDatabase
import com.rymo.felfel.database.dao.ContactDao
import com.rymo.felfel.features.alarm.*
import com.rymo.felfel.view.BaseToolbar
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

/**
 * Shows a list of alarms. To react on user interaction, requires a strategy. An activity hosting
 * this fragment should provide a proper strategy for single and multi-pane modes.
 *
 * @author Yuriy
 */
class AlarmsListFragment : Fragment() {

    private val mViewModel: AlarmListViewModel by viewModel()
    private val alarms: IAlarmsManager by globalInject()
    private val store: Store by globalInject()
    private val uiStore: UiStore by globalInject()
    private val prefs: Prefs by globalInject()
    private val logger: Logger by globalLogger("AlarmsListFragment")
    private var sorted: List<AlarmValue> = ArrayList()

    private val mAdapter: AlarmListAdapter by lazy {
        AlarmListAdapter(R.layout.list_row_classic, R.string.alarm_list_title, ArrayList())
    }
    private val inflater: LayoutInflater by lazy {
        requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    private var alarmsSub: Disposable = Disposables.disposed()
    private var layoutSub: Disposable = Disposables.disposed()
    private var backSub: Disposable = Disposables.disposed()
    private var timePickerDialogDisposable = Disposables.disposed()

    /** changed by [Prefs.listRowLayout] in [onResume] */
    private var listRowLayoutId = R.layout.list_row_classic

    /** changed by [Prefs.listRowLayout] in [onResume] */
    private var listRowLayout = prefs.layout()

    inner class AlarmListAdapter(alarmTime: Int, label: Int, private val values: List<AlarmValue>) :
        ArrayAdapter<AlarmValue>(requireContext(), alarmTime, label, values) {
        private val highlighter: ListRowHighlighter? by lazy {
            ListRowHighlighter.createFor(requireActivity().theme)
        }

        private fun recycleView(convertView: View?, parent: ViewGroup, id: Int): RowHolder {
            val tag = convertView?.tag
            return when {
                tag is RowHolder && tag.layout == listRowLayout -> RowHolder(convertView, id, listRowLayout)
                else -> {
                    val rowView = inflater.inflate(listRowLayoutId, parent, false)
                    RowHolder(rowView, id, listRowLayout).apply { digitalClock.setLive(false) }
                }
            }
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            // get the alarm which we have to display
            val alarm = values[position]

            logger.trace { "getView($position) $alarm" }

            val row = recycleView(convertView, parent, alarm.id)

            row.onOff.isChecked = alarm.isEnabled

            lollipop {
                row.digitalClock.transitionName = "clock" + alarm.id
                row.container.transitionName = "onOff" + alarm.id
                row.detailsButton.transitionName = "detailsButton" + alarm.id
            }

            // Delete add, skip animation
            if (row.idHasChanged) {
                row.onOff.jumpDrawablesToCurrentState()
            }

            row.container
                // onOff
                .setOnClickListener {
                    val enable = !alarm.isEnabled
                    logger.debug { "onClick: ${if (enable) "enable" else "disable"}" }
                    alarms.getAlarm(alarm.id)?.enable(enable)
                }

            val pickerClickTarget =
                with(row) { if (layout == Layout.CLASSIC) digitalClockContainer else digitalClock }
//            pickerClickTarget.setOnClickListener {
//                timePickerDialogDisposable =
//                    TimePickerDialogFragment.showTimePicker(parentFragmentManager).subscribe { picked ->
//                        if (picked.isPresent()) {
//                            alarms.getAlarm(alarm.id)?.also { alarm ->
//                                alarm.edit {
//                                    copy(isEnabled = true, hour = picked.get().hour, minutes = picked.get().minute)
//                                }
//                            }
//                        }
//                    }
//            }

            pickerClickTarget.setOnLongClickListener { false }

            // set the alarm text
            val c = Calendar.getInstance()
            c.set(Calendar.HOUR_OF_DAY, alarm.hour)
            c.set(Calendar.MINUTE, alarm.minutes)
            row.digitalClock.updateTime(c)

            val removeEmptyView = listRowLayout == Layout.CLASSIC || listRowLayout == Layout.COMPACT
            // Set the repeat text or leave it blank if it does not repeat.

            row.daysOfWeek.run {
                text = daysOfWeekStringWithSkip(alarm)
                visibility =
                    when {
                        text.isNotEmpty() -> View.VISIBLE
                        removeEmptyView -> View.GONE
                        else -> View.INVISIBLE
                    }
            }

            // Set the repeat text or leave it blank if it does not repeat.
            row.label.text = alarm.label

            row.label.visibility =
                when {
                    alarm.label.isNotBlank() -> View.VISIBLE
                    removeEmptyView -> View.GONE
                    else -> View.INVISIBLE
                }

            highlighter?.applyTo(row, alarm.isEnabled)

            return row.rowView
        }

        private fun daysOfWeekStringWithSkip(alarm: AlarmValue): String {
            val daysOfWeekStr = alarm.daysOfWeek.toString(context, false)
            return if (alarm.skipping) "$daysOfWeekStr (skipping)" else daysOfWeekStr
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterContextMenuInfo
        val alarm = mAdapter.getItem(info.position) ?: return false
        when (item.itemId) {
            R.id.delete_alarm -> {
                // Confirm that the alarm will be deleted.
                AlertDialog.Builder(activity)
                    .setTitle(getString(R.string.delete_alarm))
                    .setMessage(getString(R.string.delete_alarm_confirm))
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        mViewModel.deleteContactsAlarm(alarm.id)
                        alarms.getAlarm(alarm.id)?.delete()
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
            }
            R.id.list_context_enable -> {
                alarms.getAlarm(alarmId = alarm.id)?.run { edit { copy(isEnabled = true) } }
            }
            R.id.list_context_menu_disable -> {
                alarms.getAlarm(alarmId = alarm.id)?.run { edit { copy(isEnabled = false) } }
            }
            R.id.skip_alarm -> {
                alarms.getAlarm(alarmId = alarm.id)?.run {
                    if (isSkipping()) {
                        // removes the skip
                        edit { this }
                    } else {
                        requestSkip()
                    }
                }
            }
        }
        return true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.list_fragment, container, false)

        val listView = view.findViewById(R.id.list_fragment_list) as ListView

        listView.adapter = mAdapter

        listView.isVerticalScrollBarEnabled = false
        listView.setOnCreateContextMenuListener(this)
        listView.choiceMode = AbsListView.CHOICE_MODE_SINGLE

        listView.onItemClickListener =
            AdapterView.OnItemClickListener { _, listRow, position, _ ->
                mAdapter.getItem(position)?.id?.let { uiStore.edit(it, listRow.tag as RowHolder) }
            }

        registerForContextMenu(listView)

        setHasOptionsMenu(true)

        val fab: View = view.findViewById(R.id.fab)
        fab.setOnClickListener { uiStore.createNewAlarm() }

        val toolbarView: BaseToolbar = view.findViewById(R.id.toolbarView)
        toolbarView.onBackButtonClickListener = View.OnClickListener {
            requireActivity().finish()
        }

        lollipop { (fab as FloatingActionButton).attachToListView(listView) }

        alarmsSub =
            prefs.listRowLayout
                .observe()
                .switchMap { uiStore.transitioningToNewAlarmDetails() }
                .switchMap { transitioning ->
                    if (transitioning) Observable.never() else store.alarms()
                }
                .subscribe { alarms ->
                    sorted =
                        alarms //
                            .sortedBy { it.minutes }
                            .sortedBy { it.hour }
                            .sortedBy {
                                when (it.daysOfWeek.coded) {
                                    0x7F -> 1
                                    0x1F -> 2
                                    0x60 -> 3
                                    else -> 0
                                }
                            }
                    mAdapter.clear()

                    mAdapter.addAll(sorted)
                }

        if (Setting.firstOpenAlarmList) {
            sorted.forEach {
                Timber.e(it.hour.toString())
                alarms.getAlarm(it.id)?.delete()
            }
            Setting.firstOpenAlarmList = false
        }

        lollipop { configureBottomDrawer(view) }

        logger.trace { "onCreateView() { postponeEnterTransition() }" }
        postponeEnterTransition()
        view.doOnPreDraw {
            logger.trace { "onCreateView() { doOnPreDraw { startPostponedEnterTransition() } }" }
            startPostponedEnterTransition()
        }

        return view
    }

    private fun configureBottomDrawer(view: View) {
        val fab = view.findViewById<FloatingActionButton>(R.id.fab)

        val openColor = requireActivity().theme.resolveColor(R.attr.drawerBackgroundColor)
        val closedColor = requireActivity().theme.resolveColor(R.attr.drawerClosedBackgroundColor)

    }

    override fun onResume() {
        super.onResume()
        backSub = uiStore.onBackPressed().subscribe { requireActivity().finish() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutSub =
            prefs.listRowLayout.observe().subscribe {
                listRowLayout = prefs.layout()
                listRowLayoutId =
                    when (listRowLayout) {
                        Layout.COMPACT -> R.layout.list_row_compact
                        Layout.CLASSIC -> R.layout.list_row_classic
                        else -> R.layout.list_row_bold
                    }
            }
    }

    override fun onPause() {
        super.onPause()
        backSub.dispose()
        // dismiss the time picker if it was showing. Otherwise we will have to uiStore the state and it
        // is not nice for the user
        timePickerDialogDisposable.dispose()
    }

    override fun onDestroy() {
        super.onDestroy()
        alarmsSub.dispose()
        layoutSub.dispose()
    }

    override fun onCreateContextMenu(menu: ContextMenu, view: View, menuInfo: ContextMenuInfo?) {
        // Inflate the menu from xml.
        requireActivity().menuInflater.inflate(R.menu.list_context_menu, menu)

        // Use the current item to create a custom view for the header.
        val info = menuInfo as AdapterContextMenuInfo
        val alarm = mAdapter.getItem(info.position)

        // Construct the Calendar to compute the time.
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, alarm!!.hour)
        cal.set(Calendar.MINUTE, alarm.minutes)

        val visible =
            when {
                alarm.isEnabled ->
                    when {
                        alarm.skipping -> listOf(R.id.list_context_enable)
                        alarm.daysOfWeek.isRepeatSet -> listOf(R.id.skip_alarm)
                        else -> listOf(R.id.list_context_menu_disable)
                    }
                // disabled
                else -> listOf(R.id.list_context_enable)
            }

        listOf(R.id.list_context_enable, R.id.list_context_menu_disable, R.id.skip_alarm)
            .minus(visible)
            .forEach { menu.removeItem(it) }
    }
}
