package com.example.fitnessapp.presentation.main.track

import android.content.Context
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
import com.example.fitnessapp.data.model.track.Track
import com.example.fitnessapp.data.model.track.TrackRequest
import com.example.fitnessapp.data.model.track.TrackResponse
import com.example.fitnessapp.data.network.ResponseStatus
import com.example.fitnessapp.presentation.FragmentContainerActivityCallback
import com.example.fitnessapp.toBoolean
import java.lang.RuntimeException


class TrackListFragment : Fragment(R.layout.fragment_track_list),
    TrackListAdapter.OnItemClickListener {

    companion object {
        const val TAG = "TRACK_LIST_FRAGMENT"
        private const val ADAPTER_START_POSITION = 0
        private const val REFRESHING_FLAG = "REFRESHING"
        private const val SCROLL_POSITION = "SCROLL_POSITION"
        private const val TRACKS_COUNT = ""

        fun newInstance() = TrackListFragment().apply {
            val bundle = Bundle()
            arguments = bundle
        }
    }

    private var fragmentContainerActivityCallback: FragmentContainerActivityCallback? = null
    private lateinit var trackListRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var tracks = mutableListOf<Track>()
    private val trackListAdapter = TrackListAdapter(tracks, this)
    private val localRepository = FitnessApp.INSTANCE.localRepository
    private val remoteRepository = FitnessApp.INSTANCE.remoteRepository
    private val toastProvider = FitnessApp.INSTANCE.toastProvider
    private val preferencesStore = FitnessApp.INSTANCE.preferencesStore
    private var isRefreshing = false
    private var scrollPosition: Int = 0

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
        swipeRefreshLayout.setOnRefreshListener {
            onSwipeRefresh()
        }
        trackListRecyclerView.addOnScrollListener(createOnScrollChangeListener())

        if (arguments != null) {
            scrollPosition = arguments!!.getInt(SCROLL_POSITION)
        }
        getTracksFromDb()
    }

    private fun initViews(v: View) {
        trackListRecyclerView = v.findViewById(R.id.recycler_view_track_list)
        trackListRecyclerView.adapter = trackListAdapter
        trackListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        progressBar = v.findViewById(R.id.progress_bar)
        swipeRefreshLayout = v.findViewById(R.id.swipe_refresh_layout)
    }

    private fun createOnScrollChangeListener() = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            scrollPosition = layoutManager.findFirstVisibleItemPosition()
        }
    }

    private fun getTracksFromDb() {
        showProgress()
        localRepository.getTracks().continueWith({ task ->
            if (task.error != null) {
                toastProvider.showErrorMessage(error = task.error.message.toString())
            } else {
                tracks.addAll(task.result)
                tracks.sortByDescending { it.beginTime }
                trackListAdapter.notifyItemRangeInserted(
                    ADAPTER_START_POSITION, tracks.size
                )
                trackListRecyclerView.scrollToPosition(scrollPosition)
                hideProgress()
                checkTracks(tracks)
            }
        }, Task.UI_THREAD_EXECUTOR)
    }

    private fun checkTracks(tracks: List<Track>) {
        if (tracks.isNullOrEmpty()) {
            getTracksFromServer()
        } else {
            synchronizeDataWithServer()
        }
    }

    private fun getTracksFromServer() {
        showProgress()
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
                tracks.addAll(trackResponse.trackList)
                tracks.sortByDescending { it.beginTime }
                trackListAdapter.notifyItemRangeInserted(
                    ADAPTER_START_POSITION, tracks.size
                )
                trackListRecyclerView.scrollToPosition(scrollPosition)
                hideProgress()
                saveTracksInDb(tracks)
            }
            ResponseStatus.ERROR.toString() -> {
                toastProvider.showErrorMessage(error = trackResponse.errorCode)
            }
        }
    }

    private fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        progressBar.visibility = View.INVISIBLE
    }

    private fun saveTracksInDb(trackList: List<Track>) {
        for (track in trackList) {
            localRepository.saveTrack(track)
        }
    }

    private fun synchronizeDataWithServer() {
        for (track in tracks) {
            if (track.isNotSent.toBoolean()) {
                saveTrackOnServer(track)
            }
        }
    }

    private fun saveTrackOnServer(track: Track) {
        //TODO
    }

    private fun onSwipeRefresh() {
        swipeRefreshLayout.isRefreshing = true
        synchronizeDataWithServer()
    }

    override fun onItemClick(track: Track) {
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
        tracks.clear()
        trackListAdapter.notifyItemRangeRemoved(ADAPTER_START_POSITION, tracks.size)
        arguments?.apply {
            putBoolean(REFRESHING_FLAG, isRefreshing)
            putInt(SCROLL_POSITION, scrollPosition)
            putInt(TRACKS_COUNT, tracks.size)
        }
    }

    override fun onDetach() {
        fragmentContainerActivityCallback = null
        super.onDetach()
    }

}