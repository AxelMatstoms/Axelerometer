package am.alite.axelerometer

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.selector

class MainActivity : AppCompatActivity(), SensorEventListener {

    var started = false;
    lateinit var sensorManager: SensorManager
    lateinit var accelerationSensor: Sensor
    val measurePoints: MutableList<MeasurePoint> = mutableListOf()
    var starttime: Long = 0
    val formats = listOf<String>("csv", "gnuplot")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startUi()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

        startstop.setOnClickListener { _ ->
            started = !started
            if (started) {
                stopUi()
                start()
            } else {
                startUi()
                stop()
                selector("Choose export format", formats, { dialogInterface, i ->
                    var exportedStr: String? = null
                    when(formats[i]) {
                        "csv" -> {
                            exportedStr = exportCsv(measurePoints)
                        }
                        "gnuplot" -> {
                            exportedStr = exportGnuPlot(measurePoints, starttime)
                        }
                    }
                    exportedStr?.let {
                        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "your@email.com", null))
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Axelerometer data ${datestr(starttime)}")
                        emailIntent.putExtra(Intent.EXTRA_TEXT, it)
                        startActivity(Intent.createChooser(emailIntent, "Send data to..."))
                    }
                })
            }
        }
    }

    fun start() {
        measurePoints.clear()
        starttime = System.currentTimeMillis()
        sensorManager.registerListener(this, accelerationSensor, 10000)
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    fun startUi() {
        startstop.text = getString(R.string.start)
        startstop.background.setColorFilter((0xFF4CAF50).toInt(), PorterDuff.Mode.MULTIPLY)
    }

    fun stopUi() {
        startstop.text = getString(R.string.stop)
        startstop.background.setColorFilter((0xFFF44336).toInt(), PorterDuff.Mode.MULTIPLY)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        sensorEvent?.let {
            val x = sensorEvent.values[0]
            val y = sensorEvent.values[1]
            val z = sensorEvent.values[2]
            val time = System.currentTimeMillis()
            val point = MeasurePoint(time - starttime, absolute(x.toDouble(), y.toDouble(), z.toDouble()))
            measurePoints.add(point)
        }
    }

    override fun onResume() {
        super.onResume()
        if (started) {
            start()
        }
    }

    override fun onPause() {
        super.onPause()
        if (started) {
            stop()
        }
    }

}