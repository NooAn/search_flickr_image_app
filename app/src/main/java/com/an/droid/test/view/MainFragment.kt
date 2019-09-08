package com.an.droid.test.view

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.an.droid.test.R
import com.an.droid.test.model.Photos
import com.an.droid.test.model.Result
import com.an.droid.test.view.adapter.PhotoAdapter
import com.an.droid.test.viewmodel.MainViewModel

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var photoAdapter: PhotoAdapter
    private var refreshing = false

    private val photosObserver = Observer<Result<Photos>> {
        when (it) {
            is Error -> {
                Toast.makeText(activity, "Sorry, I have some error $it", Toast.LENGTH_SHORT).show()
            }
            is Result.Success -> {
                photoAdapter.addItems(it.data.photo)
            }
            is Result.Loading -> {
                refreshing = it.state
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.main_fragment, container, false)
        searchView = view.findViewById(R.id.search)
        recyclerView = view.findViewById(R.id.recycler)
        photoAdapter = PhotoAdapter()
        recyclerView.apply {
            adapter = photoAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        val visibleThreshold = 3
                        val layoutManager = recyclerView.layoutManager as GridLayoutManager
                        val lastItem = layoutManager.findLastCompletelyVisibleItemPosition()
                        if ((layoutManager.itemCount <= lastItem + visibleThreshold) && !refreshing) {
                            viewModel.search(null, true)
                            refreshing = true
                        }
                    }
                }
            })
        }

        setupSearchView()

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.photos.observe(this, photosObserver)
    }

    private fun setupSearchView() {
        searchView.onActionViewExpanded()
        searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(value: String): Boolean {
                    if (query.isNotEmpty()) {
                        search(value)
                    }
                    return true
                }

                override fun onQueryTextChange(query: String): Boolean {
                    return true
                }
            })
        }
    }

    private fun search(query: String) {
        activity?.hideKeyboard() //fixme internet check
        searchView.clearFocus()
        viewModel.search(query)
        photoAdapter.setItems(mutableListOf())
    }

    private fun Activity.hideKeyboard() {
        hideKeyboard(if (currentFocus == null) View(this) else currentFocus)
    }

    private fun Context.hideKeyboard(view: View?) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

}
