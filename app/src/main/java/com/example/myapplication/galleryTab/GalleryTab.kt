package com.example.myapplication.galleryTab

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.FragmentTab
import com.example.myapplication.PermissionChecker
import com.example.myapplication.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class GalleryTab(): FragmentTab() {
    private var galleryAdapter: GalleryAdapter? = null
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

        val floatingButton = view.findViewById<FloatingActionButton>(R.id.galleryFloating)
        floatingButton.setOnClickListener {
            openCaemra()
        }

        return view
    }

    // ---------------- 여기부터 카메라 --------------- //
    fun openCaemra(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 99)
    }

    fun saveImageFile(filename: String, mimeType: String, bitmap: Bitmap) : Uri? {
        var values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)

        val c = Calendar.getInstance().time
        values.put(MediaStore.Images.Media.DATE_TAKEN, c.time)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            values.put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val uri = activity?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        try {
            if (uri != null){
                var descriptor = activity?.contentResolver?.openFileDescriptor(uri, "w")
                if (descriptor != null){
                    val fos = FileOutputStream(descriptor.fileDescriptor)
                    //quality 화질
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    fos.close()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                        values.clear()
                        values.put(MediaStore.Images.Media.IS_PENDING, 0)
                        if (uri != null) {
                            activity?.contentResolver?.update(uri, values, null, null)
                        }
                    }
                }
            }
        }catch (e:java.lang.Exception){
            Log.e("File", "error=${e.localizedMessage}")
        }
        return uri
    }

    fun newFileName() : String{
        val sdf = java.text.SimpleDateFormat("yyyyMMdd_HHmmss")
        val filename = sdf.format(System.currentTimeMillis())

        return "$filename.jpg"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            when(requestCode){
                99 -> {
                    if (data?.extras?.get("data") != null){
                        val bitmap = data?.extras?.get("data") as Bitmap
                        val uri = saveImageFile(newFileName(), "image/jpg", bitmap)

                        // 찍은 사진 크게 보기
                        val intent = Intent(activity, FullScreen::class.java)
                        intent.putExtra("img", uri.toString())
                        startActivity(intent)

                        // 찍은 사진 포함 갱신
                        showImages()
                    }
                }
            }
        }
    }
    // ----------------------여기까지 카메라-------------------- //

    private fun showImages() {
        val thisFragment = this
        GlobalScope.launch {
            var imageList = listOf<MediaStoreImage>()
            val isAllgranted =
                PermissionChecker.checkAndRequestPermissons(
                    thisFragment,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PermissionChecker.GALLERY_PERMISSION_REQUEST_CODE
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
        ListAdapter<MediaStoreImage, GalleryAdapter.ImageViewHolder>(
            MediaStoreImage.DiffCallback
        ) {

        private inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imageView: ImageView = view.findViewById(R.id.image)

            init{
                imageView.setOnClickListener {
                    var imageMediaStore = getItem(adapterPosition)
                    val str = adapterPosition.toString()
                    Log.d("tab position", str)


                    val uri = imageMediaStore.contentUri

                    val intent = Intent(activity, FullScreen::class.java)
                    intent.putExtra("img", uri.toString())
                    intent.putExtra("position", adapterPosition)
                    startActivity(intent)
                }

                imageView.setOnLongClickListener {
                    val mediaStoreImage = getItem(adapterPosition)
                    Toast.makeText(view.context, "${mediaStoreImage.displayName} 복사되었습니다.", Toast.LENGTH_LONG).show()
                    val prefix = "<Image>"
                    val postfix = "</Image>"
                    val clipText = prefix + mediaStoreImage.contentUri + postfix

                    val clipboard: ClipboardManager = view.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip: ClipData = ClipData.newPlainText("uri","$clipText")
                    clipboard.setPrimaryClip(clip)

                    true
                }

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.gallery_recycler, parent, false)

            return ImageViewHolder(view)
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
}
