package com.example.myapplication.galleryTab

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.FragmentTab
import com.example.myapplication.PermissionChecker
import com.example.myapplication.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class GalleryTab(): FragmentTab(){
    private var galleryAdapter: GalleryAdapter? = null
    private val PERMISSION_REQUEST_CODE = 99
    //private val contentResolver: ContentResolver? = null

    companion object {
        private const val READ_EXTERNAL_STORAGE_REQUEST = 0x1045
        const val TAG = "GalleryTab"
        const val EXTERNAL_STORAGE_PERMISSION_REQUEST = 99
    }

    private val images = MutableLiveData<List<MediaStoreImage>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 여기부터 갤러리
        val view =inflater.inflate(R.layout.gallery_tab, container, false)
        galleryAdapter = GalleryAdapter()
        showImages()

        var recycleview = view.findViewById<RecyclerView>(R.id.gallery)
        recycleview.adapter = galleryAdapter
        recycleview.layoutManager = GridLayoutManager(activity,3)

        return view
    }

    private fun showImages() {
        val thisFragment = this
        GlobalScope.launch {
            var imageList = listOf<MediaStoreImage>()
            val isAllgranted =
                PermissionChecker.checkAndRequestPermissons(
                    thisFragment,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE
                )

            if(isAllgranted){
                imageList = queryImages()
            }

            images.postValue(imageList)
        }

        images.observe(this, Observer<List<MediaStoreImage>> { images ->
            galleryAdapter?.submitList(images)
        })
    }

    override fun onResume() {
        super.onResume()
        if(galleryAdapter != null && galleryAdapter?.itemCount == 0){
            showImages()
        }
    }

    private suspend fun queryImages(): List<MediaStoreImage> {
        val images = mutableListOf<MediaStoreImage>()
        val thisFragment = this

        withContext(Dispatchers.IO) {
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN
            )
//            val selection = "${MediaStore.Images.Media.DATE_TAKEN} >= ?"
//            val selectionArgs = arrayOf(
//                dateToTimestamp(day = 1, month = 1, year = 1970).toString()
//            )

            val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"


            val cursor = thisFragment.activity?.let{
                it.contentResolver.query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null, // selection
                    null, // selectionArgs
                    sortOrder)?.use { cursor ->

                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val dateTakenColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
                    val displayNameColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val dateTaken = Date(cursor.getLong(dateTakenColumn))
                        val displayName = cursor.getString(displayNameColumn)
                        val contentUri = Uri.withAppendedPath(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            id.toString()
                        )

                        val image =
                            MediaStoreImage(
                                id,
                                displayName,
                                dateTaken,
                                contentUri
                            )
                        images += image
                    }
                }
            }
        }

        return images
    }

    @SuppressLint("SimpleDateFormat")
    private fun dateToTimestamp(day: Int, month: Int, year: Int): Long =
        SimpleDateFormat("dd.MM.yyyy").let { formatter ->
            formatter.parse("$day.$month.$year")?.time ?: 0
        }

    private inner class GalleryAdapter :
        ListAdapter<MediaStoreImage, ImageViewHolder>(
            MediaStoreImage.DiffCallback
        ) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.gallery_recycler, parent, false)
            return ImageViewHolder(
                view
            )
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            val mediaStoreImage = getItem(position)


            Glide.with(holder.imageView)
                .load(mediaStoreImage.contentUri)
                .thumbnail(0.33f)
                .centerCrop()
                .into(holder.imageView)
        }

    }


    private class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.image)
    }
}
