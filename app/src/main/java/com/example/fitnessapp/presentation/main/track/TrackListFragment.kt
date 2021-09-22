package com.example.fitnessapp.presentation.main.track

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import bolts.Task
import com.example.fitnessapp.DependencyProvider
import com.example.fitnessapp.R
import com.example.fitnessapp.data.model.point.PointDbo
import com.example.fitnessapp.data.model.point.PointDto
import com.example.fitnessapp.data.model.point.PointRequest
import com.example.fitnessapp.data.model.point.PointResponse
import com.example.fitnessapp.data.model.track.SaveTrackRequest
import com.example.fitnessapp.data.model.track.SaveTrackResponse
import com.example.fitnessapp.data.model.track.TrackDbo
import com.example.fitnessapp.data.model.track.TrackDto
import com.example.fitnessapp.data.model.track.TrackRequest
import com.example.fitnessapp.data.model.track.TrackResponse
import com.example.fitnessapp.data.network.ResponseStatus
import com.example.fitnessapp.presentation.FragmentContainerActivityCallback
import com.example.fitnessapp.presentation.authorization.AuthorizationActivity
import com.example.fitnessapp.presentation.main.MainActivity
import com.example.fitnessapp.presentation.run.RunActivity
import com.example.fitnessapp.setInvisible
import com.example.fitnessapp.setVisible
import com.example.fitnessapp.showMessage
import com.example.fitnessapp.toPointDto
import com.google.android.material.floatingactionbutton.FloatingActionButton


class TrackListFragment : Fragment(R.layout.fragment_track_list),
    TrackListAdapter.OnItemClickListener {

    companion object {
        const val TAG = "TRACK_LIST_FRAGMENT"
        private const val ADAPTER_START_POSITION = 0
        private const val REFRESHING_FLAG = "REFRESHING"
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
    private lateinit var noTracksTextView: TextView
    private lateinit var noTracksImageView: ImageView
    private var tracks = mutableListOf<TrackDbo>()
    private var points = mutableListOf<PointDto>()
    private val trackListAdapter = TrackListAdapter(tracks, this)
    private val localRepository = DependencyProvider.localRepository
    private val remoteRepository = DependencyProvider.remoteRepository
    private val preferencesRepository = DependencyProvider.preferencesRepository
    private var scrollPosition = ADAPTER_START_POSITION
    private var isSynchronizing: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            fragmentContainerActivityCallback = context as MainActivity
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + getString(R.string.no_callback_implementation_error))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)

        savedInstanceState?.let {
            arguments = it.getBundle(SAVED_STATE)
            restoreState(arguments)
        }
        getTracksFromDb()

        swipeRefreshLayout.setOnRefreshListener {
            onSwipeRefresh()
        }
        fabAddTrack.setOnClickListener {
            onAddTrack()
        }
    }

    private fun initViews(v: View) {
        trackListRecyclerView = v.findViewById(R.id.recycler_view_track_list)
        trackListRecyclerView.adapter = trackListAdapter
        trackListRecyclerView.layoutManager = LinearLayoutManager(context)
        progressBar = v.findViewById(R.id.progress_bar)
        swipeRefreshLayout = v.findViewById(R.id.swipe_refresh_layout)
        fabAddTrack = v.findViewById(R.id.fab_add_track)
        noTracksImageView = v.findViewById(R.id.image_no_track_found)
        noTracksTextView = v.findViewById(R.id.text_no_tracks_found)
    }

    private fun restoreState(bundle: Bundle?) {
        bundle?.let {
            swipeRefreshLayout.isRefreshing = it.getBoolean(REFRESHING_FLAG)
            scrollPosition = it.getInt(SCROLL_POSITION)
        }
    }

    private fun getTracksFromDb() {
        if (tracks.isEmpty()) {
            localRepository.getTracks().continueWith({ task ->
                if (task.error != null) {
                    requireContext().showMessage(message = task.error.message.toString())
                } else {
                    swipeRefreshLayout.isRefreshing = false
                    progressBar.setInvisible()
                    isSynchronizing = false
                    tracks.addAll(task.result)
                    tracks.sortByDescending { it.beginTime }
                    trackListAdapter.notifyItemRangeInserted(ADAPTER_START_POSITION, tracks.size)
                    trackListRecyclerView.scrollToPosition(scrollPosition)
                    checkTracks()
                }
            }, Task.UI_THREAD_EXECUTOR)
        }
    }

    private fun checkTracks() {
        if (tracks.isEmpty()) {
            progressBar.setVisible()
        } else {
            isSynchronizing = true
            hideNoTracksLabel()
        }
        getTracksFromServer()
    }

    private fun getTracksFromServer() {
        remoteRepository.getTracks(
            TrackRequest(token = preferencesRepository.getAuthorizationToken(context = requireContext()))
        ).continueWith({ task ->
            if (task.error != null) {
                requireContext().showMessage(task.error.message.toString())
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
                if (trackResponse.trackList.isEmpty() && tracks.isEmpty()) {
                    showNoTracksLabel()
                }
            }
            ResponseStatus.ERROR.toString() -> {
                checkResponseError(trackResponse.errorCode)
            }
        }
    }

    private fun saveTracksInDb(trackList: List<TrackDto>) {
        localRepository.insertTrackList(trackList).onSuccess {
            getTracksFromDb()
        }
        for (track in trackList) {
            track.serverId?.let {
                getTrackPointsFromServer(it)
            }
        }
    }

    private fun getTrackPointsFromServer(trackServerId: Int) {
        remoteRepository.getTrackPoints(
            PointRequest(
                token = preferencesRepository.getAuthorizationToken(context = requireContext()),
                trackId = trackServerId
            )
        ).continueWith { task ->
            if (task.error != null) {
                requireContext().showMessage(message = task.error.message.toString())
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
        localRepository.getTrackIdByServerId(trackServerId).continueWith({ task ->
            if (task.error != null) {
                requireContext().showMessage(message = task.error.message.toString())
            } else {
                val trackId: Int = task.result
                localRepository.insertPointList(points, trackId)
            }
        }, Task.UI_THREAD_EXECUTOR)
    }

    private fun checkResponseError(error: String) {
        if (error == INVALID_TOKEN_ERROR) {
            logOutWithExplanationDialog()
        } else {
            requireContext().showMessage(message = error)
        }
    }

    private fun logOutWithExplanationDialog() {
        preferencesRepository.clearAuthorizationToken(context = requireContext())
        preferencesRepository.setTokenExpired(context = requireContext())
        localRepository.clearDb()
        val intent = Intent(context, AuthorizationActivity::class.java)
        startActivity(intent)
        fragmentContainerActivityCallback?.closeActivity()
    }

    private fun onSwipeRefresh() {
        swipeRefreshLayout.isRefreshing = true
        synchronizeDataWithServer()
    }

    private fun synchronizeDataWithServer() {
        isSynchronizing = true
        for (track in tracks) {
            if (track.serverId == 0) {
                getTrackPoints(track)
            }
        }
        val range = tracks.size
        tracks.clear()
        trackListAdapter.notifyItemRangeRemoved(ADAPTER_START_POSITION, range)
        getTracksFromDb()
    }

    private fun getTrackPoints(track: TrackDbo) {
        localRepository.getTrackPoints(track.id).onSuccess { task ->
            points = getPointsDtoFromDbo(task.result)
            saveTrackOnServer(track)
        }
    }

    private fun getPointsDtoFromDbo(pointList: List<PointDbo>): MutableList<PointDto> {
        val pointDtoList = mutableListOf<PointDto>()
        for (point in pointList) {
            pointDtoList.add(point.toPointDto())
        }
        return pointDtoList
    }

    private fun saveTrackOnServer(track: TrackDbo) {
        remoteRepository.saveTrack(
            SaveTrackRequest(
                token = preferencesRepository.getAuthorizationToken(context = requireContext()),
                beginTime = track.beginTime,
                duration = track.duration,
                distance = track.distance,
                points = points.toList()
            )
        ).continueWith({ task ->
            if (task.error != null) {
                requireContext().showMessage(message = getString(R.string.no_internet_connection_error))
            } else {
                handleSaveTrackResponse(task.result, track.id)
                points.clear()
            }
        }, Task.UI_THREAD_EXECUTOR)
    }

    private fun handleSaveTrackResponse(saveTrackResponse: SaveTrackResponse, trackId: Int) {
        when (saveTrackResponse.status) {
            ResponseStatus.OK.toString() -> {
                localRepository.updateTrackServerId(trackId, saveTrackResponse.serverId)
            }
            ResponseStatus.ERROR.toString() -> {
                checkResponseError(saveTrackResponse.errorCode)
            }
        }
    }

    private fun onAddTrack() {
        val intent = Intent(context, RunActivity::class.java)
        startActivity(intent)
    }

    private fun showNoTracksLabel() {
        noTracksTextView.setVisible()
        noTracksImageView.setVisible()
    }

    private fun hideNoTracksLabel() {
        noTracksTextView.setInvisible()
        noTracksImageView.setInvisible()
    }

    override fun onItemClick(track: TrackDbo) {
        val arguments = Bundle().apply {
            putParcelable(TrackFragment.TRACK_ITEM_EXTRA, track)
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
        arguments?.apply {
            putBoolean(REFRESHING_FLAG, swipeRefreshLayout.isRefreshing)
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