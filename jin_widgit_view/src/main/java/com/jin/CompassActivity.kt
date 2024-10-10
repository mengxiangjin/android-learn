package com.jin

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import com.jin.databinding.ActivityCompassBinding
import java.security.Permission


class CompassActivity: AppCompatActivity(),SensorEventListener {


    private lateinit var binding: ActivityCompassBinding
    private lateinit var sensorManager: SensorManager

    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityBarStyle(this)
        binding = ActivityCompassBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    var provider = ""

    private fun initView() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager


        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
            requestPermissions(permissions,200)
        } else {
            getLocation()
        }




//        val magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
//        val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
//
//        sensorManager.registerListener(this,magneticSensor,SensorManager.SENSOR_DELAY_NORMAL)
//        sensorManager.registerListener(this,accelerometerSensor,SensorManager.SENSOR_DELAY_NORMAL)

    }


    @SuppressLint("MissingPermission")
    private fun getLocation() {
        val providers = locationManager.getProviders(true)

        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER
        } else {
            Toast.makeText(this,"打开",Toast.LENGTH_LONG).show()
        }

        if (provider.isNotEmpty()) {
            val location = locationManager.getLastKnownLocation(provider)
            if (location != null) {
                updateLocationUI(location)
            }
            locationManager.requestLocationUpdates(provider,1000L,5f,object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    updateLocationUI(location)
                }

            })
        }
    }

    private fun updateLocationUI(location: Location) {
        Log.d("zyz", "updateLocationUI: ")
        binding.tvLocation.text = "纬度${location.latitude}  经度${location.longitude}"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 200 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

    private fun setActivityBarStyle(activity: AppCompatActivity) {
        val decorView = activity.window.decorView
        val option = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN //注释掉这行代码
                //View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        decorView.systemUiVisibility = option
        //设置导航栏（顶部和底部）颜色为透明，注释掉这行代码
        //getWindow().setNavigationBarColor(Color.TRANSPARENT);
        //设置通知栏颜色为透明
        activity.window.statusBarColor = Color.TRANSPARENT
        val actionBar = activity.supportActionBar
        actionBar?.hide()
    }

    var accelerometerValues = FloatArray(3)
    var magneticValues = FloatArray(3)
    var lastRotateDegree = 0f



    override fun onSensorChanged(event: SensorEvent) {
//        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
//            accelerometerValues = event.values.clone()
//        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
//            magneticValues = event.values.clone()
//        }
//
//        val rotation = FloatArray(9)
//        val values = FloatArray(9)
//        SensorManager.getRotationMatrix(rotation,null,accelerometerValues,magneticValues)
//        SensorManager.getOrientation(rotation,values)
//        Log.d("TAG", "onSensorChanged: " + Math.toDegrees(values[0].toDouble()))
//
//        val rotateDegree = -Math.toDegrees(values[0].toDouble()).toFloat()
//
//        if (abs(rotateDegree - lastRotateDegree) > 6) {
//            binding.imgCompass.rotation = rotateDegree
//            binding.imgBg.rotation = rotateDegree
//            lastRotateDegree = rotateDegree
//        }


        //0：正北  90:正东   -90:正西  +-180：正南
        //保持设备水平，使用水平传感器

        // 0 北边  90 东   180 南 西 270
        var degress = 0
        if(event.sensor.type ==Sensor.TYPE_ORIENTATION){
            //取围绕Z轴转过的角度
            val degree = event.values[0]
            val azimuth = (degree + 360) % 360
            var str = ""
            Log.d("TAG", "onSensorChanged: " + azimuth)
            if (azimuth <= 15 || azimuth >= 360) {
                degress = 180
                degress = (180 - (azimuth - 0) * 3).toInt()
                str = "北"
                Log.d("TAG", "onSensorChanged: 北" )
            } else if (azimuth > 15 && azimuth < 75) {
                degress = 135
                degress = (135 - (azimuth - 15) * 0.75).toInt()
                str = "东北"
                Log.d("TAG", "onSensorChanged: 东北" )
            } else if (azimuth >= 75 && azimuth <= 105) {
                degress = 90
                degress = (90 - (azimuth - 75) * 1.5).toInt()
                str = "东"
                Log.d("TAG", "onSensorChanged: 东" )
            } else if (azimuth > 105 && azimuth < 165) {
                degress = 45
                degress = (45 - (azimuth - 105) * 0.75).toInt()
                str = "东南"
                Log.d("TAG", "onSensorChanged: 东南" )
            } else if (azimuth >= 165 && azimuth <= 195) {
                degress = 0
                degress = (0 - (azimuth - 165) * 1.5).toInt()
                str = "南"
                Log.d("TAG", "onSensorChanged: 南" )
            } else if (azimuth > 195 && azimuth < 225) {
                degress = -45
                degress = (-45 - (azimuth - 195) * 1.5).toInt()
                str = "西南"
                Log.d("TAG", "onSensorChanged: 西南" )
            } else if (azimuth >= 225 && azimuth <= 285) {
                degress = -90
                degress = (-90 - (azimuth - 225) * 0.75).toInt()
                str = "西"
                Log.d("TAG", "onSensorChanged: 西" )
            } else if (azimuth > 285 && azimuth < 360) {
                degress = -135
                degress = (-135 - (azimuth - 285) * 0.6).toInt()
                str = "西北"
                Log.d("TAG", "onSensorChanged: 西北" )
            }
            binding.tvContent.text = "$azimuth -------> $str ----> $degress"
            binding.imgCompass.rotation = degress.toFloat()
            binding.imgBg.rotation = degress.toFloat()
            Log.d("TAG", "onSensorChanged: " + degree)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_GAME);
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this);
    }
}