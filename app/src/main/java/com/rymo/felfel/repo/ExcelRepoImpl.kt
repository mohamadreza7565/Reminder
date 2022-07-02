package com.rymo.felfel.repo

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.ajts.androidmads.library.SQLiteToExcel
import com.rymo.felfel.common.BaseExceptionExportExcel
import com.rymo.felfel.common.convertListToMutableList
import com.rymo.felfel.configuration.AlarmApplication
import com.rymo.felfel.database.RoomAppDatabase
import com.rymo.felfel.model.Contact
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Workbook
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.OutputStreamWriter


class ExcelRepoImpl(private val roomAppDatabase: RoomAppDatabase, private var onResult: (() -> Unit)? = null) : ExcelRepository {

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
                    exportContactTxt(file)
                    exportExcelLiveData.postValue(
                        BaseExceptionExportExcel(
                            BaseExceptionExportExcel.ExcelExceptionType.SUCCESS, file.path
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

        val file = File("${direct.path}/$saveFileName")
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
                    exportReportsTxt(file, date)
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

    override fun exportWorkshop() {

        val direct = File("${AlarmApplication.instance!!.getExternalFilesDir(null)!!.path}/Workshop")
        if (!direct.exists()) {
            direct.mkdirs()
        }

        val file = File("${direct.path}/Workshop.xls")
        if (file.exists()) {
            file.delete()
        }

        var saveFileName = "Workshop.xls"
        var saveTableName = "tbl_workshop"

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
                    exportTxtWorkshop(file)
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

    private fun exportTxtWorkshop(file: File) {


        val txtFile = File(file.path.replace("xls", "txt"))

        if (txtFile.exists())
            txtFile.delete()

        txtFile.createNewFile()

        val contacts = roomAppDatabase.contactDao().getWorkshopList().convertListToMutableList()

        FileWriter(txtFile.path).use { writer ->

            writer.append("\t\t\tکارگاه\t\t\t")
            writer.append("\n\n")


            contacts.forEach {

                writer.append(it.name)
                writer.append(" - ")

                writer.append(it.phone)

                writer.append("\n")

                writer.append("----------------------------------------------------")

                writer.append("\n")

            }
        }


    }

    private fun exportContactTxt(file: File) {

        val txtFile = File(file.path.replace("xls", "txt"))

        if (txtFile.exists())
            txtFile.delete()

        txtFile.createNewFile()

        val contacts = roomAppDatabase.contactDao().getContact().convertListToMutableList()

        FileWriter(txtFile.path).use { writer ->

            writer.append("\t\t\tمخاطبین\t\t\t")
            writer.append("\n\n")


            contacts.forEach {

                writer.append(it.nameAndFamily)
                writer.append(" - ")

                writer.append(it.phone)
                writer.append(" - ")

                writer.append(it.companyName)
                writer.append(" - ")

                writer.append("\n")

                writer.append("----------------------------------------------------")

                writer.append("\n")

            }
        }

    }

    private fun exportReportsTxt(file: File, date: String) {

        val txtFile = File(file.path.replace("xls", "txt"))

        if (txtFile.exists())
            txtFile.delete()

        txtFile.createNewFile()

        val messages = roomAppDatabase.smsMessageDao().getAllExportMessages()

        FileWriter(txtFile.path).use { writer ->

            writer.append("\t\t\t$date\t\t\t")
            writer.append("\n")

            messages.forEach {

                writer.append(it.contactName)
                writer.append(" - ")

                writer.append(it.phoneNumber)
                writer.append(" - ")

                writer.append(it.companyName)
                writer.append(" - ")

                writer.append("\n")

                writer.append("پیام ارسالی :")
                writer.append("\n")
                writer.append(it.textSms)

                writer.append("\n")

                writer.append("پاسخ دریافتی :")
                writer.append("\n")
                writer.append(it.replayText)

                writer.append("\n")

                writer.append("----------------------------------------------------")

                writer.append("\n")
            }
        }

        onResult?.invoke()
    }

}
