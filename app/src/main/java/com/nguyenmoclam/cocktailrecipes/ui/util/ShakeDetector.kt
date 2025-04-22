package com.nguyenmoclam.cocktailrecipes.ui.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

/**
 * Utility class to detect shake gestures using the device's accelerometer.
 * @param context The application context
 * @param onShakeDetected Callback function to execute when a shake is detected
 * @param shakeSensitivity Sensitivity of the shake detection (lower = more sensitive)
 */
class ShakeDetector(
    private val context: Context,
    private val onShakeDetected: () -> Unit,
    private val shakeSensitivity: Float = 2.5f
) : SensorEventListener {
    
    // Sensor manager for accessing the device's sensors
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    
    // Accelerometer sensor instance
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    
    // Last values for calculating acceleration changes
    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f
    
    // Time of the last detected shake
    private var lastShakeTime = 0L
    
    // Minimum time between shake detection to prevent rapid firing
    private val shakeInterval = 1000L
    
    // Indicates if the detector is currently registered with the sensor manager
    private var isDetecting = false
    
    /**
     * Start detecting shake gestures
     */
    fun startDetecting() {
        if (!isDetecting && accelerometer != null) {
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_GAME
            )
            isDetecting = true
        }
    }
    
    /**
     * Stop detecting shake gestures
     */
    fun stopDetecting() {
        if (isDetecting) {
            sensorManager.unregisterListener(this)
            isDetecting = false
        }
    }
    
    /**
     * Called when sensor values change, used to detect shake gestures
     */
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val currentTime = System.currentTimeMillis()
            
            // Throttle shake detection to prevent multiple rapid detections
            if (currentTime - lastShakeTime < shakeInterval) {
                return
            }
            
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            
            // Calculate the change in acceleration
            val deltaX = x - lastX
            val deltaY = y - lastY
            val deltaZ = z - lastZ
            
            // Update last values for next detection
            lastX = x
            lastY = y
            lastZ = z
            
            // Calculate acceleration magnitude
            val acceleration = sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ)
            
            // If acceleration is above threshold, trigger shake detection
            if (acceleration > shakeSensitivity) {
                lastShakeTime = currentTime
                onShakeDetected()
            }
        }
    }
    
    /**
     * Called when sensor accuracy changes (not used in this implementation)
     */
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used in this implementation
    }
} 