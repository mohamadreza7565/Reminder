package com.rymo.felfel.repo

import androidx.lifecycle.MutableLiveData
import com.rymo.felfel.common.BaseExceptionExportExcel
import com.rymo.felfel.model.Contact
import kotlinx.coroutines.flow.Flow

interface ExcelRepository {

    fun exportContacts(contacts: MutableList<Contact>)

    fun exportReports(time: String, date: String)

    fun exportWorkshop()

}
