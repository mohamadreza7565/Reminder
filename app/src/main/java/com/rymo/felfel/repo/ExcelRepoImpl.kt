package com.rymo.felfel.repo

import androidx.lifecycle.MutableLiveData
import com.ajts.androidmads.library.SQLiteToExcel
import com.rymo.felfel.common.BaseExceptionExportExcel
import com.rymo.felfel.common.toast
import com.rymo.felfel.configuration.AlarmApplication
import com.rymo.felfel.model.Contact
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import kotlin.coroutines.suspendCoroutine

class ExcelRepoImpl : ExcelRepository {

    val exportExcelLiveData = MutableLiveData<BaseExceptionExportExcel>()

    override fun exportContacts(contacts: MutableList<Contact>) {

        val direct = File("${AlarmApplication.instance!!.getExternalFilesDir(null)!!.path}/Contacts")
        if (!direct.exists()) {
            direct.mkdirs()
        }

        val file = File("${direct.path}/Contacts.xls")
        if (file.exists()) {
            file.delete()
        }

        var saveFileName = "Contacts.xls"
        var saveTableName = "tbl_contact"

        val sqliteToExcel = SQLiteToExcel(AlarmApplication.instance!!, "db_felfel", direct.path)


        sqliteToExcel.exportSingleTable(saveTableName,
            saveFileName,
            object : SQLiteToExcel.ExportListener {
                override fun onStart() {
                    exportExcelLiveData.postValue(
                        BaseExceptionExportExcel(
                            BaseExceptionExportExcel.ExcelExceptionType.LOADING
                        )
                    )
                }


                override fun onCompleted(filePath: String?) {
                    exportExcelLiveData.postValue(
                        BaseExceptionExportExcel(
                            BaseExceptionExportExcel.ExcelExceptionType.SUCCESS, filePath
                        )
                    )
                }

                override fun onError(e: java.lang.Exception?) {
                    exportExcelLiveData.postValue(
                        BaseExceptionExportExcel(
                            BaseExceptionExportExcel.ExcelExceptionType.ERROR,
                            errorMessage = e!!.message
                        )
                    )
                }

            })


    }

    override fun exportReports(time: String, date: String) {

        val direct = File("${AlarmApplication.instance!!.getExternalFilesDir(null)!!.path}/Reports")
        if (!direct.exists()) {
            direct.mkdirs()
        }


        var saveFileName = "${date.replace("/", "-")}_${time}.xls"
        var saveTableName = "tbl_export_sms_message"

        val file = File("${direct.path}/$saveFileName.xls")
        if (file.exists()) {
            file.delete()
        }


        val sqliteToExcel = SQLiteToExcel(AlarmApplication.instance!!, "db_felfel", direct.path)

        sqliteToExcel.exportSingleTable(saveTableName,
            saveFileName,
            object : SQLiteToExcel.ExportListener {
                override fun onStart() {
                    exportExcelLiveData.postValue(
                        BaseExceptionExportExcel(
                            BaseExceptionExportExcel.ExcelExceptionType.LOADING
                        )
                    )
                }


                override fun onCompleted(filePath: String?) {
                    Timber.e("Export message complete")
                    exportExcelLiveData.postValue(
                        BaseExceptionExportExcel(
                            BaseExceptionExportExcel.ExcelExceptionType.SUCCESS, filePath
                        )
                    )
                }

                override fun onError(e: java.lang.Exception?) {
                    Timber.e("Export message error -> ${e!!.message} \n directory -> ${direct.path}")
                    exportExcelLiveData.postValue(
                        BaseExceptionExportExcel(
                            BaseExceptionExportExcel.ExcelExceptionType.ERROR,
                            errorMessage = e.message
                        )
                    )
                }

            })
    }

}
