package com.nada.tech.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.filepickersample.listener.FilePickerCallback
import com.filepickersample.model.Media
import com.nada.tech.R
import com.nada.tech.chainway_uhf.UHFInventoryItem
import com.nada.tech.common.ActionBarActivity
import com.nada.tech.databinding.ActivityAssetRegistrationBinding
import com.nada.tech.model.PartModel
import com.nada.tech.model.PopupModel
import com.nada.tech.network.typeCall
import com.nada.tech.ui.adapter.ImageAdapter
import com.nada.tech.ui.adapter.ScanTagAdapter
import com.nada.tech.ui.bottomsheet.*
import com.nada.tech.utility.*
import com.nada.tech.utility.DateTimeUtil.DDMMYYYY
import com.nada.tech.viewmodel.AssetViewModel
import com.stfalcon.imageviewer.StfalconImageViewer
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AssetRegistrationActivity : ActionBarActivity() {

    private lateinit var binding: ActivityAssetRegistrationBinding

    private var mAssetImages = ArrayList<Media>()
    private var mLocations = ArrayList<PopupModel>()
    private var mPartList = ArrayList<PartModel>()

    @Inject
    lateinit var assetImageAdapter: ImageAdapter
    private val viewModel by viewModels<AssetViewModel>()

    private var tagAdapter: ScanTagAdapter? = null
    private var mTagList = ArrayList<UHFInventoryItem>()
    private var selectedDate: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAssetRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initUi() {
        setUpToolbar(getString(R.string.assets_registration), true)
        binding.clickListener = this

        tagAdapter = ScanTagAdapter(mTagList, true)
        val spanCount = if (isTablet()) 2 else 1
        binding.rvTag.layoutManager = GridLayoutManager(this, spanCount)
        binding.rvTag.adapter = tagAdapter

        assetImageAdapter.arrayList = mAssetImages
        assetImageAdapter.clickCallback = this::onAssetImageClick
        assetImageAdapter.deleteCallback = this::onAssetImageDelete
        binding.rvPhoto.adapter = assetImageAdapter

//        getPartNumbers()
//        fetchLocations()

        mLocations.addAll(localDataHelper.locations)
        mPartList.addAll(localDataHelper.partList)
    }

    private fun onAssetImageClick(media: Media, position: Int) {
        StfalconImageViewer.Builder(this, mAssetImages) { view, image ->
            Glide.with(binding.rvPhoto).load(image.url).into(view)
        }.withStartPosition(position).show()
    }

    private fun onAssetImageDelete(media: Media, position: Int) {
        mAssetImages.removeAt(position)
        assetImageAdapter.notifyItemRemoved(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onClick(view: View?) {
        super.onClick(view)
        when (view) {
            binding.btnTagScan -> {
                openScanRFIDActivity {
                    if (it.resultCode == RESULT_OK) {
                        if (it.data == null) return@openScanRFIDActivity
                        val newTagList: ArrayList<UHFInventoryItem>? =
                            it.data!!.getParcelableArrayListExtra(Constants.EXTRA_TAG_LIST)

                        if (newTagList.isNullOrEmpty()) return@openScanRFIDActivity
                        newTagList.addAll(mTagList)
                        mTagList.clear()
                        mTagList.addAll(newTagList.distinctBy { tag -> tag.epc })
                        tagAdapter?.notifyDataSetChanged()
                        manageTagList()
                    }
                }
            }
            binding.btnReset -> {
                mTagList.clear()
                tagAdapter?.notifyDataSetChanged()
                manageTagList()
            }
            binding.btnViewAllTag -> {
                BottomSheetTagList.newInstance(mTagList).show(this)
            }
            binding.edtPartNumber -> {
                partNumberBottomSheet()
            }
            binding.edtExpiryDate -> {
                openDatePickerDialog(
                    "",
                    selectedDate,
                    { _, year, month, dayOfMonth ->
                        val calendar: Calendar = Calendar.getInstance()
                        calendar.set(year, month, dayOfMonth)
                        selectedDate = calendar
                        binding.edtExpiryDate.setText(DDMMYYYY.format(calendar.time))
                    },
                    isFuture = true,
                    isPast = false
                )
            }
            binding.edtLocation -> {
                locationBottomSheet()
            }
            binding.btnCreate -> {
                if (validate()) {
                    hideKeyBoard()
                    registerAsset()
                }
            }
            binding.cardAddImage -> {
                openImageFilePicker(object : FilePickerCallback {
                    override fun onSuccess(media: Media?) {
                        if (media == null) return
                        mAssetImages.add(0, media)
                        assetImageAdapter.notifyDataSetChanged()
                    }

                    override fun onSuccess(mediaList: ArrayList<Media>?) {
                        if (mediaList.isNullOrEmpty()) return
                        mAssetImages.addAll(0, mediaList)
                        assetImageAdapter.notifyDataSetChanged()
                    }

                    override fun onError(error: String?) {
                        toast(error)
                    }
                })
            }
        }
    }


    /*
    * Part Number
    * */
    private fun getPartNumbers() {
        viewModel.getParts().observe(this) {
            it.status.typeCall(
                success = {
                    actionView.toolbarProgressBar.gone()
                    if (it.data != null) {
                        if (it.data.success) {
                            mPartList.clear()
                            mPartList.addAll(it.data.data)
                            BottomSheetPartNumber.newInstance(
                                getString(R.string.part_number),
                                mPartList,
                                object : BottomSheetPartNumber.BottomSheetItemClickListener {
                                    override fun onItemClick(data: PartModel) {
                                        binding.partModel = data
                                    }
                                }).show(this)
                        }
                    }
                },
                error = {
                    actionView.toolbarProgressBar.gone()
                },
                loading = {
                    actionView.toolbarProgressBar.visible()
                }
            )
        }
    }

    private fun partNumberBottomSheet() {
        if (mPartList.isEmpty()) {
            toast(getString(R.string.no_part_found))
            return
        }
       getPartNumbers()
        /*openPartListActivity {
            if (it.data == null) return@openPartListActivity
            val data: PartModel? =
                it.data?.getSerializableExtra(Constants.PART_DATA) as PartModel
            binding.partModel = data
        }*/
        /*  BottomSheetPartNumber.newInstance(
              getString(R.string.part_number),
              mPartList,
              object : BottomSheetPartNumber.BottomSheetItemClickListener {
                  override fun onItemClick(data: PartModel) {
                      binding.partModel = data
                  }
              }).show(this)*/
    }


    /*
    * Locations
    * */
    private fun fetchLocations() {
        viewModel.fetchLocation().observe(this) {
            it.status.typeCall(
                success = {
                    actionView.toolbarProgressBar.gone()
                    if (it.data != null) {
                        if (it.data.success) {
                            mLocations.clear()
                            mLocations.addAll(it.data.data)
                        }
                    }
                },
                error = {
                    actionView.toolbarProgressBar.gone()
                },
                loading = {
                    actionView.toolbarProgressBar.visible()
                }
            )
        }
    }

    private fun locationBottomSheet() {
        if (mLocations.isNullOrEmpty()) {
            toast(getString(R.string.no_locations_found))
            return
        }
        BottomSheetPopupMenu.newInstance(
            getString(R.string.location),true,
            mLocations,
            object : BottomSheetPopupMenu.BottomSheetItemClickListener {
                override fun onItemClick(data: PopupModel) {
                    binding.location = data
                }
            }).show(this)
    }

    private fun manageTagList() {
        if (mTagList.isNullOrEmpty()) {
            binding.linTagDataView.gone()
            binding.viewReset.gone()
            binding.btnReset.gone()

        } else {
            binding.linTagDataView.visible()
            binding.viewReset.visible()
            binding.btnReset.visible()

            if (mTagList.size > 5) binding.btnViewAllTag.visible()
            else binding.btnViewAllTag.gone()

            binding.txtTotalTags.text =
                String.format(getString(R.string.total_scanned_s), mTagList.size.toString())
        }
    }

    private fun validate(): Boolean {
        var isValid = true

        if (binding.edtPartNumber.isEmpty) {
            binding.tvInputPartNumber.error = getString(R.string.please_enter_part_number)
            isValid = false
        }

        if (binding.edtAssetName.isEmpty) {
            binding.tvInputAssetName.error = getString(R.string.please_enter_asset_id_name)
            isValid = false
        }

        if (binding.edtLocation.isEmpty) {
            binding.tvInputLocation.error = getString(R.string.please_select_location)
            isValid = false
        }

        if (isValid && mTagList.isEmpty()) {
            toast(getString(R.string.please_scan_asset))
            isValid = false
        }

        return isValid
    }

    /*
    * Asset registration api calling
    * */
    private fun registerAsset() {
        val requestParam = HashMap<String, RequestBody>()
        requestParam["PartId"] = binding.edtPartNumber.tag.toString().requestBody()
        requestParam["AssetName"] = binding.edtAssetName.toRequestBody()
        requestParam["SerialNumber"] = binding.edtSrNo.toRequestBody()
        requestParam["ExpiryDate"] = binding.edtExpiryDate.toRequestBody()
        requestParam["Notes"] = binding.edtNote.toRequestBody()
        requestParam["LocationId"] = binding.edtLocation.tag.toString().requestBody()
        requestParam["TagData"] = mTagList.toJsonExcludeExpose().requestBody()
        requestParam["IsMoveable"] = binding.swMoveable.isChecked.toString().requestBody()

        val partList = ArrayList<MultipartBody.Part>()
        mAssetImages.forEachIndexed { index, media ->
            partList.add(media.mediaFile.toMultipartBody("AssetImages[$index]"))
        }

        viewModel.assetRegistration(requestParam, partList).observe(this) {
            it.status.typeCall(
                success = {
                    hideProgressDialog()
                    if (it.data != null) {
                        if (it.data.success) {
                            toast(it.data.message)
                            finish()
                        } else showAlertMessage(it.data.message)
                    }
                },
                error = {
                    hideProgressDialog()
                    showAlertMessage(it.message)
                },
                loading = {
                    showProgressDialog()
                },
            )
        }
    }
}