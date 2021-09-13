package com.example.fitnessapp.presentation.main.track

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import bolts.Task
import com.example.fitnessapp.FitnessApp
import com.example.fitnessapp.R
import com.example.fitnessapp.data.model.point.PointDbo
import com.example.fitnessapp.data.model.point.PointDto
import com.example.fitnessapp.data.model.point.PointRequest
import com.example.fitnessapp.data.model.point.PointResponse
import com.example.fitnessapp.data.model.track.*
import com.example.fitnessapp.data.network.ResponseStatus
import com.example.fitnessapp.presentation.FragmentContainerActivityCallback
import com.example.fitnessapp.presentation.authorization.AuthorizationActivity
import com.example.fitnessapp.presentation.run.RunActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton


class TrackListFragment : Fragment(R.layout.fragment_track_list),
    TrackListAdapter.OnItemClickListener {

    companion object {
        const val TAG = "TRACK_LIST_FRAGMENT"
        private const val ADAPTER_START_POSITION = 0
        private const val REFRESHING_FLAG = "REFRESHING"
        private const val DATA_FETCHING_FLAG = "DATA_FETCHING"
        private const val SCROLL_POSITION = "SCROLL_POSITION"
        private const val INVALID_TOKEN_ERROR = "INVALID_TOKEN"
        private const val SAVED_STATE = "SAVED_STATE"

        fun newInstance() = TrackListFragment().apply {
            arguments = Bundle()
        }
    }

    private var fragmentContainerActivityCallback: FragmentContainerActivityCallback? = null
    private lateinit var trackListRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var fabAddTrack: FloatingActionButton
    private var tracks = mutableListOf<TrackDbo>()
    private var points = mutableListOf<PointDbo>()
    private val trackListAdapter = TrackListAdapter(tracks, this)
    private val localRepository = FitnessApp.INSTANCE.localRepository
    private val remoteRepository = FitnessApp.INSTANCE.remoteRepository
    private val toastProvider = FitnessApp.INSTANCE.toastProvider
    private val preferencesStore = FitnessApp.INSTANCE.preferencesStore
    private var scrollPosition: Int = ADAPTER_START_POSITION
    private var isSynchronizing: Boolean = false
    private var isDataFetched: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentContainerActivityCallback) {
            fragmentContainerActivityCallback = context
        } else {
            throw RuntimeException(context.toString() + getString(R.string.no_callback_implementation_error))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)

        savedInstanceState?.let {
            arguments = it.getBundle(SAVED_STATE)
        } ?: getTracksFromDb()

        swipeRefreshLayout.setOnRefreshListener {
            onSwipeRefresh()
        }
        fabAddTrack.setOnClickListener {
            onAddTrack()
        }
    }

    override fun onResume() {
        super.onResume()
        restoreState(arguments)
    }

    private fun initViews(v: View) {
        trackListRecyclerView = v.findViewById(R.id.recycler_view_track_list)
        trackListRecyclerView.adapter = trackListAdapter
        trackListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        progressBar = v.findViewById(R.id.progress_bar)
        swipeRefreshLayout = v.findViewById(R.id.swipe_refresh_layout)
        fabAddTrack = v.findViewById(R.id.fab_add_track)
    }

    private fun restoreState(bundle: Bundle?) {
        bundle?.let {
            swipeRefreshLayout.isRefreshing = it.getBoolean(REFRESHING_FLAG)
            isDataFetched = it.getBoolean(DATA_FETCHING_FLAG)
            scrollPosition = it.getInt(SCROLL_POSITION)
        }
        if (tracks.isEmpty()) {
            getTracksFromDb()
        }
        if (swipeRefreshLayout.isRefreshing) {
            synchronizeDataWithServer()
        }
    }

    private fun getTracksFromDb() {
        localRepository.getTracks().continueWith({ task ->
            if (task.error != null) {
                toastProvider.showErrorMessage(error = task.error.message.toString())
            } else {
                tracks.clear()
                tracks.addAll(task.result)
                tracks.sortByDescending { it.beginTime }
                trackListAdapter.notifyItemRangeInserted(ADAPTER_START_POSITION, tracks.size)
                trackListRecyclerView.scrollToPosition(scrollPosition)
                checkTracks(tracks)
            }
        }, Task.UI_THREAD_EXECUTOR)
    }

    private fun checkTracks(trackList: List<TrackDbo>) {
        if (trackList.isNullOrEmpty() && !isDataFetched) {
            getTracksFromServer()
        } else if (trackList.isNotEmpty() && !isDataFetched) {
            synchronizeDataWithServer()
        }
    }

    private fun getTracksFromServer() {
        if (!isSynchronizing) {
            showProgress()
        }
        remoteRepository.getTracks(
            TrackRequest(token = preferencesStore.getAuthorizationToken())
        ).continueWith({ task ->
            if (task.error != null) {
                toastProvider.showErrorMessage(task.error.message.toString())
            } else {
                handleTracksServerResponse(task.result)
            }
        }, Task.UI_THREAD_EXECUTOR)
    }

    private fun handleTracksServerResponse(trackResponse: TrackResponse) {
        when (trackResponse.status) {
            ResponseStatus.OK.toString() -> {
                if (trackResponse.trackList.size > tracks.size) {
                    saveTracksInDb(trackResponse.trackList)
                }
                isDataFetched = true
                swipeRefreshLayout.isRefreshing = false
            }
            ResponseStatus.ERROR.toString() -> {
                checkResponseError(trackResponse.errorCode)
            }
        }
        hideProgress()
    }

    private fun saveTracksInDb(trackList: List<TrackDto>) {
        localRepository.insertTrackList(trackList).onSuccess {
            getTracksFromDb()
        }
        for (track in trackList) {
            getTrackPointsFromServer(track.serverId)
        }
    }

    private fun getTrackPointsFromServer(trackServerId: Int) {
        remoteRepository.getTrackPoints(
            PointRequest(
                token = preferencesStore.getAuthorizationToken(),
                trackId = trackServerId
            )
        ).continueWith { task ->
            if (task.error != null) {
                toastProvider.showErrorMessage(error = task.error.message.toString())
            } else {
                handlePointsRequest(task.result, trackServerId)
            }
        }
    }

    private fun handlePointsRequest(pointResponse: PointResponse, trackServerId: Int) {
        when (pointResponse.status) {
            ResponseStatus.OK.toString() -> {
                savePoints(pointResponse.pointList, trackServerId)
            }
            ResponseStatus.ERROR.toString() -> {
                checkResponseError(error = pointResponse.errorCode)
            }
        }
    }

    private fun savePoints(points: List<PointDto>, trackServerId: Int) {
        var trackId: Int
        localRepository.getTrackIdByServerId(trackServerId).onSuccess {
            trackId = it.result
            localRepository.insertPointList(points, trackId)
        }
    }

    private fun checkResponseError(error: String) {
        if (error == INVALID_TOKEN_ERROR) {
            preferencesStore.clearAuthorizationToken()
            val intent = Intent(context, AuthorizationActivity::class.java)
            startActivity(intent)
            fragmentContainerActivityCallback?.closeActivity()
        } else {
            toastProvider.showErrorMessage(error)
        }
    }

    private fun synchronizeDataWithServer() {
        isSynchronizing = true
        for (track in tracks) {
            if (track.serverId == null) {
                getTrackPoints(track)
            }
        }
        getTracksFromServer()
    }

    private fun getTrackPoints(track: TrackDbo) {
        localRepository.getTrackPoints(track.id).onSuccess {
            points = it.result
            saveTrackOnServer(track)
        }
    }

    private fun saveTrackOnServer(track: TrackDbo) {
        remoteRepository.saveTrack(
            SaveTrackRequest(
                token = preferencesStore.getAuthorizationToken(),
                beginTime = track.beginTime,
                duration = track.duration,
                distance = track.distance,
                points = points.toList()
            )
        ).continueWith { task ->
            if (task.error != null) {
                toastProvider.showErrorMessage(error = task.error.message.toString())
            } else {
                handleSaveTrackResponse(task.result, track)
            }
        }
    }

    private fun handleSaveTrackResponse(saveTrackResponse: SaveTrackResponse, track: TrackDbo) {
        when (saveTrackResponse.status) {
            ResponseStatus.OK.toString() -> {
                track.serverId = saveTrackResponse.serverId
            }
            ResponseStatus.ERROR.toString() -> {
                checkResponseError(saveTrackResponse.errorCode)
            }
        }
    }

    private fun onSwipeRefresh() {
        swipeRefreshLayout.isRefreshing = true
        synchronizeDataWithServer()
    }

    private fun onAddTrack() {
        val intent = Intent(context, RunActivity::class.java)
        startActivity(intent)
    }

    private fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        progressBar.visibility = View.INVISIBLE
    }

    override fun onItemClick(track: TrackDbo) {
        val arguments = Bundle().apply {
            putParcelable(TrackFragment.TRACK_ITEM, track)
        }
        fragmentContainerActivityCallback?.showFragment(
            fragmentTag = TrackFragment.TAG,
            args = arguments
        )
    }

    override fun onPause() {
        super.onPause()
        scrollPosition =
            (trackListRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        tracks.clear()
        trackListAdapter.notifyItemRangeRemoved(ADAPTER_START_POSITION, tracks.size)
        arguments?.apply {
            putBoolean(REFRESHING_FLAG, swipeRefreshLayout.isRefreshing)
            putBoolean(DATA_FETCHING_FLAG, isDataFetched)
            putInt(SCROLL_POSITION, scrollPosition)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBundle(SAVED_STATE, arguments)
    }

    override fun onDetach() {
        fragmentContainerActivityCallback = null
        super.onDetach()
    }

}