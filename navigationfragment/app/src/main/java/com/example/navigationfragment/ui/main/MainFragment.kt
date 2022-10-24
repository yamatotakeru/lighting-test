package com.example.navigationfragment.ui.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.TRANSLATION_X
import android.view.View.TRANSLATION_Y
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.navigationfragment.R
import kotlin.math.floor


class MainFragment : Fragment(), SensorEventListener {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    private var mSensorManager: SensorManager? = null
    private var mRotationVector: Sensor? = null
    private var mAccelerometer: Sensor? = null
    private var mMagneticField: Sensor? = null

    private lateinit var mLightReflection: ImageView
    private lateinit var mLightReflection2: ImageView
    private lateinit var mLightReflection3: ImageView

    private var mPattern: Int = 0
    private var mEnableSensor = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        mRotationVector = mSensorManager?.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)
        mAccelerometer = mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mMagneticField = mSensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mLightReflection = view.findViewById(R.id.light_reflection)
        mLightReflection2 = view.findViewById(R.id.light_reflection_2)
        mLightReflection3 = view.findViewById(R.id.light_reflection_3)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        view?.findViewById<Button>(R.id.pattern_1)?.setOnClickListener {
            mPattern = 0
            updateVisibilityLightReflection()
            mEnableSensor = true
        }
        view?.findViewById<Button>(R.id.pattern_2)?.setOnClickListener {
            mPattern = 1
            updateVisibilityLightReflection()
            mEnableSensor = true
        }
        view?.findViewById<Button>(R.id.pattern_3)?.setOnClickListener {
            mPattern = 2
            updateVisibilityLightReflection()
            mEnableSensor = true
        }
        view?.findViewById<Button>(R.id.pattern_4)?.setOnClickListener {
            mPattern = 3
            updateVisibilityLightReflection()
            mEnableSensor = false

            val moveX = mLightReflection.width.toFloat()
            val view1_animation = ObjectAnimator.ofFloat(mLightReflection, TRANSLATION_X, -moveX, moveX).apply {
            }

            val view2_animation = ObjectAnimator.ofFloat(mLightReflection2, TRANSLATION_X, -moveX, moveX).apply {
            }

            val view3_animation = ObjectAnimator.ofFloat(mLightReflection3, TRANSLATION_X, -moveX, moveX).apply {
            }

            val animatorSet = AnimatorSet()
            animatorSet.playTogether(view1_animation, view2_animation, view3_animation)
            animatorSet.duration = 200
            animatorSet.start()
        }
        updateVisibilityLightReflection()
    }

    fun updateVisibilityLightReflection() {
        when(mPattern) {
            0 -> {
                mLightReflection.visibility = View.VISIBLE
                mLightReflection2.visibility = View.INVISIBLE
                mLightReflection3.visibility = View.INVISIBLE
            }
            1 -> {
                mLightReflection.visibility = View.INVISIBLE
                mLightReflection2.visibility = View.VISIBLE
                mLightReflection3.visibility = View.INVISIBLE
            }
            2 -> {
                mLightReflection.visibility = View.INVISIBLE
                mLightReflection2.visibility = View.INVISIBLE
                mLightReflection3.visibility = View.VISIBLE
            }
            3 -> {
                mLightReflection.visibility = View.VISIBLE
                mLightReflection2.visibility = View.VISIBLE
                mLightReflection3.visibility = View.VISIBLE
            }
        }
        mLightReflection.x = 0f
        mLightReflection2.x = 0f
        mLightReflection3.x = 0f
    }

    override fun onResume() {
        super.onResume()
        mSensorManager?.registerListener(this, mRotationVector, SensorManager.SENSOR_DELAY_NORMAL)
        mSensorManager?.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        mSensorManager?.registerListener(this, mMagneticField, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        mSensorManager?.unregisterListener(this)
    }

    private var preSensorValues = floatArrayOf(0f, 0f, 0f)
    private var mGravity: FloatArray? = null
    private var mGeomagnetic: FloatArray? = null

    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private val mRotationMatrix = FloatArray(16)

    override fun onSensorChanged(event: SensorEvent?) {

        if (!mEnableSensor) return

        if (event == null) return

        val sensorX: Float = event.values[0]
        val sensorY: Float = event.values[1]
        val sensorZ: Float = event.values[2]
//        val strTmp = "X: $sensorX Y: $sensorY Z: $sensorZ Type: ${event.sensor?.type}"
//        println(strTmp)
//        println("X: ${preSensorValues[0] - sensorX} Y: ${preSensorValues[1]- sensorY} Z: ${preSensorValues[2] - sensorZ}")

        when (event.sensor.type) {
            Sensor.TYPE_GAME_ROTATION_VECTOR -> {
                val strTmp = "GX: $sensorX Y: $sensorY Z: $sensorZ"
                SensorManager.getRotationMatrixFromVector(mRotationMatrix , event.values)
                SensorManager.getOrientation(mRotationMatrix, orientationAngles)
//                println("$strTmp, ### ${radianToDegree(orientationAngles[0])}, ${radianToDegree(orientationAngles[1])}, ${radianToDegree(orientationAngles[2])}")
                // -180 ~ 180
                // -90 ~ 90
                val zDeg = radianToDegree(orientationAngles[2])
                val xCoeDeg = zDeg.toFloat() / 90
                val viewX = ((mLightReflection.width.times(2).div(3) ?: 0) * xCoeDeg)
                println("$zDeg, $xCoeDeg, $viewX")
                mLightReflection.x = viewX
                mLightReflection2.x = viewX
                mLightReflection3.x = viewX

                val yDeg = radianToDegree(orientationAngles[1])
                val yCoeDeg = yDeg.toFloat() / 90
                val viewY = ((mLightReflection.height.times(2).div(3) ?: 0) * yCoeDeg)
                mLightReflection2.y = viewY
                return
            }
            Sensor.TYPE_ROTATION_VECTOR -> {
//                val strTmp = "X: $sensorX Y: $sensorY Z: $sensorZ"
//                println(strTmp)
                return
            }
            Sensor.TYPE_ACCELEROMETER -> {
                mGravity = event.values
//                System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
//                updateOrientationAngles()
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                mGeomagnetic = event.values
//                System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
//                updateOrientationAngles()
            }
        }

        if (mGravity != null && mGeomagnetic != null) {
            val R = FloatArray(9)
            val I = FloatArray(9)
            val O = FloatArray(9)
            val success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)
            if (success) {
                val orientation = FloatArray(3)
                SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_Z, SensorManager.AXIS_MINUS_X, O);
                SensorManager.getOrientation(O, orientation)
                var degreeLandscape = radianToDegree(orientation[0])
                if (degreeLandscape < 0) {
                    degreeLandscape += 360
                }
                SensorManager.remapCoordinateSystem(
                    R,
                    SensorManager.AXIS_X,
                    SensorManager.AXIS_Z,
                    O
                )
                SensorManager.getOrientation(O, orientation)
                var degreePortrait = radianToDegree(orientation[0])
                if (degreePortrait < 0) {
                    degreePortrait += 360
                }
//                println("port deg = $degreePortrait, land deg = $degreeLandscape")
//                val azimuth = orientation[0]
//                val pitch = orientation[1]
//                val roll = orientation[2]
//                val strTmp = "azimuth: $azimuth pitch: $pitch roll: $roll"
//                println(strTmp)
            }
        }

        preSensorValues[0] = sensorX
        preSensorValues[1] = sensorY
        preSensorValues[2] = sensorZ
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    fun updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            accelerometerReading,
            magnetometerReading
        )

        // "mRotationMatrix" now has up-to-date information.

        SensorManager.getOrientation(rotationMatrix, orientationAngles)

        // "mOrientationAngles" now has up-to-date information.
    }

    private fun radianToDegree(radian: Float): Int {
        return floor(Math.toDegrees(radian.toDouble())).toInt()
    }
}