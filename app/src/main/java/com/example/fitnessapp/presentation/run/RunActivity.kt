package com.example.fitnessapp.presentation.run

import android.Manifest
import android.animation.AnimatorInflater
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import com.example.fitnessapp.DependencyProvider
import com.example.fitnessapp.R
import com.example.fitnessapp.data.model.point.PointDto
import com.example.fitnessapp.data.model.track.TrackDto
import com.example.fitnessapp.presentation.main.MainActivity
import com.example.fitnessapp.presentation.run.dialogs.PermissionsNotGrantedDialog
import com.example.fitnessapp.presentation.run.dialogs.TwiceDeniedPermissionDialog
import com.example.fitnessapp.setDisabled
import com.example.fitnessapp.setEnabled
import com.example.fitnessapp.showMessage

class RunActivity : AppCompatActivity(), CountUpTimer.OnTimerTickListener,
    RunActivityCallback, PointReceiver.OnPointReceivedListener {

    companion object {
        private const val TIMER_TICK_LENGTH = 10L
        private const val LOCATION_PERMISSIONS_REQUEST_CODE = 1
    }

    private lateinit var startBtn: Button
    private lateinit var finishBtn: Button
    private lateinit var backToMainBtn: Button
    private lateinit var runStartedLayout: LinearLayout
    private lateinit var runFinishedLayout: ConstraintLayout
    private lateinit var runningTimerTextView: TextView
    private lateinit var stoppedTimerTextView: TextView
    private lateinit var distanceTextView: TextView
    private var timer = CountUpTimer(TIMER_TICK_LENGTH)
    private var isTimerStopped = true
    private var isPermissionsGranted = true
    private val fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    private val coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION
    private val preferencesStore = DependencyProvider.preferencesStore
    private val localRepository = DependencyProvider.localRepository
    private val remoteRepository = DependencyProvider.remoteRepository
    private val points = mutableListOf<PointDto>()
    private var trackBeginTime: Long = 0L
    private var trackDistance: Int = 0
    private var trackDuration: Long = 0L
    private var pointReceiver = PointReceiver()
    private var isGpsTrackingStopped = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_run)
        initViews()

        checkPermissions()

        startBtn.setOnClickListener {
            onStartRun()
        }
        finishBtn.setOnClickListener {
            onFinishRun()
        }
        backToMainBtn.setOnClickListener {
            onBackToMain()
        }
        timer.setTimerTickListener(this)
        pointReceiver.setOnPointReceivedListener(this)
        registerReceiver(pointReceiver, IntentFilter(LocationService.BROADCAST_ACTION_SEND_POINT))
    }

    private fun initViews() {
        startBtn = findViewById(R.id.btn_start)
        finishBtn = findViewById(R.id.btn_finish)
        backToMainBtn = findViewById(R.id.btn_back_to_main)
        runStartedLayout = findViewById(R.id.layout_run_started)
        runFinishedLayout = findViewById(R.id.layout_run_finished)
        runningTimerTextView = findViewById(R.id.text_running_timer)
        stoppedTimerTextView = findViewById(R.id.text_stopped_timer)
        distanceTextView = findViewById(R.id.text_running_distance)
    }

    private fun checkPermissions() {
        isPermissionsGranted = checkIfPermissionsGranted()
        if (!isPermissionsGranted) {
            checkIfLocationPermissionsWereDeniedBefore()
        }
    }

    private fun checkIfPermissionsGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            fineLocationPermission
        ) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
            this,
            coarseLocationPermission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkIfLocationPermissionsWereDeniedBefore() {
        if (preferencesStore.isLocationPermissionDeniedTwice(context = this)) {
            TwiceDeniedPermissionDialog().show(
                supportFragmentManager, TwiceDeniedPermissionDialog.TAG
            )
        } else {
            showNeededPermissionsExplanation()
        }
    }

    private fun showNeededPermissionsExplanation() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, fineLocationPermission)
            || ActivityCompat.shouldShowRequestPermissionRationale(this, coarseLocationPermission)
        ) {
            PermissionsNotGrantedDialog().show(
                supportFragmentManager,
                PermissionsNotGrantedDialog.TAG
            )
        } else {
            requestNeededPermissions()
        }
    }

    override fun requestNeededPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSIONS_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSIONS_REQUEST_CODE -> {
                isPermissionsGranted = checkGrantResults(grantResults)
                closeActivityIfPermissionsNotGranted()
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    private fun checkGrantResults(grantResults: IntArray): Boolean {
        var isGranted = true
        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                isGranted = false
                break
            }
        }
        return isGranted
    }

    private fun closeActivityIfPermissionsNotGranted() {
        if (!isPermissionsGranted) {
            preferencesStore.updateDeniedLocationPermissionCounter(context = this)
            onBackToMain()
        }
    }

    private fun onStartRun() {
        if (isGPSProviderAvailable()) {
            trackBeginTime = System.currentTimeMillis()
            animateTransition(frontView = startBtn, backView = runStartedLayout)
            startBtn.setDisabled()
            finishBtn.setEnabled()
            startTimer()
            startGPSTracking()
        }
    }

    private fun isGPSProviderAvailable(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isAvailable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!isAvailable) {
            this.showMessage(getString(R.string.enable_gps_message))
        }
        return isAvailable
    }

    private fun animateTransition(frontView: View, backView: View) {
        val flipInAnimator = AnimatorInflater.loadAnimator(this, R.animator.flip_in)
        flipInAnimator.setTarget(frontView)
        flipInAnimator.start()
        frontView.cameraDistance = 10f * frontView.width
        val flipOutAnimator = AnimatorInflater.loadAnimator(this, R.animator.flip_out)
        flipOutAnimator.setTarget(backView)
        flipOutAnimator.start()
    }

    private fun startTimer() {
        timer.start()
        isTimerStopped = false
    }

    private fun startGPSTracking() {
        val locationServiceIntent = Intent(this, LocationService::class.java)
        startService(locationServiceIntent)
        isGpsTrackingStopped = false
    }

    private fun onFinishRun() {
        trackDistance = calculateDistance(points)
        trackDuration = timer.getTickInMillis()
        animateTransition(frontView = runStartedLayout, backView = runFinishedLayout)
        finishBtn.setDisabled()
        backToMainBtn.setEnabled()
        stopTimer()
        stoppedTimerTextView.text = timer.getTicksInTimeFormat()
        distanceTextView.text = trackDistance.toString()
        stopGpsTracking()
        saveTrackInDb()
    }

    private fun calculateDistance(points: List<PointDto>): Int {
        val distance = floatArrayOf(0.0f)
        for ((index, point) in points.withIndex()) {
            if (index != points.size - 1) {
                Location.distanceBetween(
                    point.latitude,
                    point.longitude,
                    points[index + 1].latitude,
                    points[index + 1].longitude,
                    distance
                )
            }
        }
        return distance.sum().toInt()
    }

    private fun stopTimer() {
        timer.stop()
        isTimerStopped = true
    }

    private fun stopGpsTracking() {
        isGpsTrackingStopped = true
        val locationServiceIntent = Intent(this, LocationService::class.java)
        stopService(locationServiceIntent)
        unregisterReceiver(pointReceiver)
    }

    private fun saveTrackInDb() {
        val track = TrackDto(
            beginTime = trackBeginTime,
            distance = trackDistance,
            duration = trackDuration
        )
        localRepository.insertTrackList(listOf(track)).onSuccess {
            getLastTrackFromDb()
        }
    }

    private fun getLastTrackFromDb() {
        localRepository.getLastTrackId().onSuccess { task ->
            savePoints(task.result)
        }
    }

    private fun savePoints(trackId: Int) {
        localRepository.insertPointList(points, trackId)
    }

    override fun onBackToMain() {
        if (isTaskRoot) {
            moveToMain()
        } else {
            onBackPressed()
        }
    }

    private fun moveToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onTick() {
        runningTimerTextView.text = timer.getTicksInTimeFormat()
    }

    override fun onPointReceived(point: PointDto) {
        points.add(point)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isGpsTrackingStopped) {
            stopGpsTracking()
        }
    }

    override fun onBackPressed() {
        if (isTimerStopped) {
            super.onBackPressed()
        } else {
            this.showMessage(message = getString(R.string.finish_run_message))
        }
    }

}