package com.rymo.felfel.common

import androidx.annotation.StringRes

class BaseExceptionExportExcel(
    val type: ExcelExceptionType,
    val filePath: String? = null,
    val errorMessage: String? = null
) : Throwable() {

    enum class ExcelExceptionType {
        LOADING, SUCCESS, ERROR
    }

}
