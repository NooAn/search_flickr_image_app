package com.an.droid.test.view

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.an.droid.test.*
import com.an.droid.test.model.Photo
import com.an.droid.test.model.Photos
import com.an.droid.test.view.adapter.PhotoAdapter
import java.util.*

interface MainView {
    fun showError()
    fun addItems(photos: Photos)
    fun showProgress()
    fun hideProgress()
}

class MainFragment : Fragment(), MainView {

    companion object {
        const val PHOTOS = "photos"
        fun newInstance() = MainFragment()
    }

    private val presenter by lazy { Dependency.inject(this) }
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var photoAdapter: PhotoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.main_fragment, container, false)
        searchView = view.findViewById(R.id.search)
        recyclerView = view.findViewById(R.id.recycler)
        photoAdapter = PhotoAdapter()
        if (savedInstanceState != null) {
            val p = savedInstanceState.getParcelableArrayList<Photo>(PHOTOS)
            if (p != null) {
                photoAdapter.photos.addAll(p)
            }
        }
        recyclerView.apply {
            adapter = photoAdapter
            loadMore {
                presenter.search(null, true)
            }
        }

        setupSearchView()

        return view
    }


    private fun setupSearchView() {
        searchView.onActionViewExpanded()
        searchView.textChange {
            search(it)
        }
    }

    override fun onStop() {
        super.onStop()
        /**
         * Cancel our request to server
         */
        presenter.stop()
    }

    override fun showError() {
        Toast.makeText(activity, "Sorry, I have some error", Toast.LENGTH_SHORT).show()
    }

    override fun addItems(photos: Photos) {
        photoAdapter.addItems(photos.photo)
    }

    /*
       I'w add progress bar in future app
     */
    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(PHOTOS, photoAdapter.photos as ArrayList<out Parcelable>)
    }

    /*
       We should check connectivity state before search process
     */
    private fun search(query: String) {
        activity?.hideKeyboard()
        searchView.clearFocus()
        presenter.search(query)
        photoAdapter.setItems(mutableListOf())
    }
}
