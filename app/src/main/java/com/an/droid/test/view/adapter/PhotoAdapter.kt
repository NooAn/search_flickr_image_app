package com.an.droid.test.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.an.droid.network.PhotoLoader
import com.an.droid.test.R
import com.an.droid.test.inflate
import com.an.droid.test.model.Photo
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_image.view.*

class PhotoAdapter : RecyclerView.Adapter<PhotoViewHolder>() {
    val photos: ArrayList<Photo> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PhotoViewHolder(parent.inflate(R.layout.item_image))

    override fun getItemCount() = photos.size

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    fun setItems(photos: List<Photo>) {
        this.photos.clear()
        this.photos.addAll(photos)
        notifyDataSetChanged()
    }
    fun addItems(photos: List<Photo>) {
        this.photos.addAll(photos)
        notifyDataSetChanged()
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}

 class PhotoViewHolder(override val containerView: View) :
    RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(photo: Photo) {
        with(containerView) {
            image.loadPhotoFromServer(photo.imageURL)
            title.text = photo.title
        }
    }
}

private fun ImageView.loadPhotoFromServer(url: String) {
    PhotoLoader.with(context)
        .showImage(url, this)
        .default(R.drawable.ic_launcher_background)
}
