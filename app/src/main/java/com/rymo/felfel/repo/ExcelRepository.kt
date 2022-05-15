package com.rymo.felfel.repo

import com.rymo.felfel.common.BaseExceptionExportExcel
import com.rymo.felfel.model.Contact
import kotlinx.coroutines.flow.Flow

interface ExcelRepository {

     fun exportContacts(contacts: MutableList<Contact>)

}
