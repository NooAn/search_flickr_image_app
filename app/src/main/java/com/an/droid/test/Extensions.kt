package com.an.droid.test

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.an.droid.test.model.PhotoJSONParser


inline fun <reified T> String.takePhotos(parser: PhotoJSONParser<T>): T {
    return parser.parsePhotosFromJSONObject(this)
}
inline fun <reified V : View> ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): V {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot) as V
}
fun Activity.hideKeyboard() {
    hideKeyboard(if (currentFocus == null) View(this) else currentFocus)
}

fun Context.hideKeyboard(view: View?) {
    val inputMethodManager =
        getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
}

fun SearchView.textChange(block: (s:String) -> Unit) {
    setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(value: String): Boolean {
            if (value.isNotEmpty()) {
                block(value)
            }
            return true
        }

        override fun onQueryTextChange(query: String): Boolean {
            return true
        }
    })
}

fun RecyclerView.loadMore(function: () -> Unit) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dy > 0) {
                val visibleThreshold = 3
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val lastItem = layoutManager.findLastCompletelyVisibleItemPosition()
                if ((layoutManager.itemCount <= lastItem + visibleThreshold)) {
                    function()
                }
            }
        }
    })
}