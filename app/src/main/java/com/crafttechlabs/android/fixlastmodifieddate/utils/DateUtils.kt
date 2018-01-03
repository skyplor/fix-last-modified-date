package com.crafttechlabs.android.fixlastmodifieddate.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by sky on 3/1/18.
 *
 * A utility class for handling dates
 */

class DateUtils {
    companion object {
        fun dateStringToLong(datePattern: String, dateString: String): Long {

            val f = SimpleDateFormat(datePattern, Locale.getDefault())
            try {
                val d = f.parse(dateString)
                return d.time
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return 0

        }
    }

}
