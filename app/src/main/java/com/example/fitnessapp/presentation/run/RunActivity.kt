package com.example.fitnessapp.presentation.run

import android.Manifest
import android.animation.AnimatorInflater
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import bolts.Task
import com.example.fitnessapp.DependencyProvider
import com.example.fitnessapp.R
import com.example.fitnessapp.data.model.point.PointDto
import com.example.fitnessapp.data.model.track.SaveTrackRequest
import com.example.fitnessapp.data.model.track.SaveTrackResponse
import com.example.fitnessapp.data.model.track.TrackDto
import com.example.fitnessapp.data.network.ResponseStatus
import com.example.fitnessapp.presentation.authorization.AuthorizationActivity
import com.example.fitnessapp.presentation.main.MainActivity
import com.example.fitnessapp.presentation.run.dialogs.PermissionsNotGrantedDialog
import com.example.fitnessapp.presentation.run.dialogs.TwiceDeniedPermissionDialog
import com.example.fitnessapp.setDisabled
import com.example.fitnessapp.setEnabled
import com.example.fitnessapp.showMessage


class RunActivity : AppCompatActivity(), CountUpTimer.OnTimerTickListener,
    RunActivityCallback, TrackInfoReceiver.OnTrackInfoReceivedListener {

    companion object {
        private const val TIMER_TICK_LENGTH = 10L
        private const val LOCATION_PERMISSIONS_REQUEST_CODE = 1
        private const val MIN_TRACK_POINTS = 2
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
    private val preferencesRepository = DependencyProvider.preferencesRepository
    private val localRepository = DependencyProvider.localRepository
    private val remoteRepository = DependencyProvider.remoteRepository
    private val points = mutableListOf<PointDto>()
    private var trackBeginTime: Long = 0L
    private var trackDistance: Int = 0
    private var trackDuration: Long = 0L
    private var trackId = 0
    private var trackInfoReceiver = TrackInfoReceiver()
    private var isGpsTrackingStopped = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_run)
        if (isTaskRoot) {
            checkAuthorizationToken()
        }
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
        trackInfoReceiver.setOnTrackInfoReceivedListener(this)
        registerReceiver(
            trackInfoReceiver,
            IntentFilter(LocationService.BROADCAST_ACTION_SEND_TRACK_INFO)
        )
    }

    private fun checkAuthorizationToken() {
        val token = preferencesRepository.getAuthorizationToken(context = this)
        if (token.isNullOrEmpty()) {
            moveToLogin()
        }
    }

    private fun moveToLogin() {
        val intent = Intent(this, AuthorizationActivity::class.java)
        startActivity(intent)
        finish()
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
        if (preferencesRepository.isLocationPermissionDeniedTwice(context = this)) {
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
            preferencesRepository.updateDeniedLocationPermissionCounter(context = this)
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
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun animateTransition(frontView: View, backView: View) {
        val flipInAnimator = AnimatorInflater.loadAnimator(this, R.animator.anim_flip_in)
        flipInAnimator.setTarget(frontView)
        flipInAnimator.start()
        frontView.cameraDistance = 10f * frontView.width
        val flipOutAnimator = AnimatorInflater.loadAnimator(this, R.animator.anim_flip_out)
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
        trackDuration = timer.getTickInMillis()
        animateTransition(frontView = runStartedLayout, backView = runFinishedLayout)
        finishBtn.setDisabled()
        backToMainBtn.setEnabled()
        stopGpsTracking()
        stopTimer()
    }

    private fun stopGpsTracking() {
        isGpsTrackingStopped = true
        val locationServiceIntent = Intent(this, LocationService::class.java)
        stopService(locationServiceIntent)
    }

    private fun stopTimer() {
        timer.stop()
        isTimerStopped = true
    }

    override fun onTrackInfoReceived(receivedPoints: List<PointDto>, distance: Int) {
        trackDistance = distance
        setupResultsLayout()
        if (receivedPoints.size >= MIN_TRACK_POINTS) {
            points.addAll(receivedPoints)
            saveTrackInDb()
        } else {
            showMessage(message = getString(R.string.no_points_error))
        }
        unregisterReceiver(trackInfoReceiver)
    }

    private fun setupResultsLayout() {
        stoppedTimerTextView.text = timer.getTicksInTimeFormat()
        distanceTextView.text = trackDistance.toString()
    }

    private fun saveTrackInDb() {
        val track = TrackDto(
            beginTime = trackBeginTime,
            distance = trackDistance,
            duration = trackDuration
        )
        localRepository.insertTrackList(listOf(track)).onSuccess {
            getLastTrackIdFromDb()
        }
    }

    private fun getLastTrackIdFromDb() {
        localRepository.getLastTrackId().onSuccess { task ->
            trackId = task.result
            savePoints(trackId)
        }
    }

    private fun savePoints(trackId: Int) {
        localRepository.insertPointList(points, trackId).onSuccess {
            saveTrackOnServer()
        }
    }

    private fun saveTrackOnServer() {
        remoteRepository.saveTrack(
            saveTrackRequest = SaveTrackRequest(
                token = preferencesRepository.getAuthorizationToken(context = this),
                beginTime = trackBeginTime,
                duration = trackDuration,
                distance = trackDistance,
                points = points
            )
        ).continueWith({ task ->
            if (task.error != null) {
                showMessage(message = getString(R.string.no_internet_connection_error))
            } else {
                handleSaveTrackResponse(task.result)
            }
        }, Task.UI_THREAD_EXECUTOR)
    }


    private fun handleSaveTrackResponse(saveTrackResponse: SaveTrackResponse) {
        when (saveTrackResponse.status) {
            ResponseStatus.OK.toString() -> {
                localRepository.updateTrackServerId(trackId, saveTrackResponse.serverId)
            }
            ResponseStatus.ERROR.toString() -> {
                showMessage(message = saveTrackResponse.errorCode)
            }
        }
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

    override fun onDestroy() {
        if (!isGpsTrackingStopped) {
            stopGpsTracking()
        }
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (isTimerStopped) {
            super.onBackPressed()
        } else {
            showMessage(message = getString(R.string.finish_run_message))
        }
    }

}