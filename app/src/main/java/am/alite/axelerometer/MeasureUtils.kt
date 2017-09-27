package am.alite.axelerometer

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.jvm.internal.Ref

/**
 * Created by axm on 27/09/17.
 */

fun absolute(x: Double, y: Double, z: Double): Double {
    val abs = Math.sqrt(x * x + y * y + z * z)
    return sign(z) * abs
}

fun sign(x: Double): Int {
    if (x < 0) {
        return -1;
    } else if (x > 0) {
        return 1;
    } else {
        return 0;
    }
}

fun exportGnuPlot(data: List<MeasurePoint>, starttime: Long): String {
    val sb = StringBuilder()

    sb.appendln("#${datestr(starttime)}")
    sb.appendln("#Timestamp(ms since start) acc(m/s^2)")

    for (dataPoint in data) {
        sb.append(dataPoint.timestamp)
        sb.append(" ")
        sb.append(dataPoint.acc)
        sb.appendln()
    }
    return sb.toString()
}

fun datestr(timestamp: Long): String {
    val gc: GregorianCalendar = GregorianCalendar(Locale("sv", "SE"))
    gc.timeInMillis = timestamp

    val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S Z")
    fmt.calendar = gc

    return fmt.format(gc.time)
}

fun exportCsv(data: List<MeasurePoint>): String {
    val sb = StringBuilder()

    for (dataPoint in data) {
        sb.append(dataPoint.timestamp)
        sb.append(",")
        sb.append(dataPoint.acc)
        sb.appendln()
    }
    return sb.toString()
}

