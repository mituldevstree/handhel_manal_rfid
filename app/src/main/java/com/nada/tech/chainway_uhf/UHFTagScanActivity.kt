package com.nada.tech.chainway_uhf

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import com.nada.tech.R
import com.nada.tech.chainway_uhf.InventoryReadType.CONTINUES
import com.nada.tech.chainway_uhf.InventoryReadType.ONE_TIME
import com.nada.tech.common.ActionBarActivity
import com.nada.tech.utility.Log
import com.nada.tech.utility.Utils
import com.nada.tech.utility.justTry
import com.rscja.deviceapi.RFIDWithUHFUART
import com.rscja.deviceapi.entity.UHFTAGInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
abstract class UHFTagScanActivity : ActionBarActivity() {
    private var mReader: RFIDWithUHFUART? = null
    var iuhfInventoryCallback: UhfInventoryCallback? = null

    protected var isInventoryRunning = false
    protected var inventoryReadType = ONE_TIME
    protected var isLocationMode = false
    private var inventoryJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSound()
        setupUHFReader()
    }

    /**
     * Reader connection and initiation
     * */
    private fun setupUHFReader() {
        try {
            mReader = RFIDWithUHFUART.getInstance()
            initializeUHFReader()
        } catch (ex: Exception) {
            toast(ex.message)
            return
        }
    }

    private fun initializeUHFReader() {
        if (mReader == null) return
        Log.e("UHF reader initiation started...")
        showProgressDialog("UHF Reader initiation..")
        CoroutineScope(Dispatchers.Default + exceptionHandler).launch {
            if (!mReader!!.init()) {
                Log.e("UHF reader initiation failure...")
                Utils.executeOnMain { toast("UHF reader initiation failure...") }
            } else {
                setPower()
                Log.e("UHF reader initiation success...")
            }
            hideProgressDialog()
        }
    }

    private fun setPower() {
        Log.e("setPower old :${mReader?.power}")
//        val ver: String? = mReader?.version
//        if (ver != null && ver.contains("RLM")) mReader?.power = 24
//        else mReader?.power = 30
//        Log.e("setPower new :${mReader?.power}")
    }

    private val exceptionHandler = CoroutineExceptionHandler { context, throwable ->
        mReader = null
        hideProgressDialog()
        Utils.executeOnMain { toast(throwable.message) }
    }

    override fun onDestroy() {
        mReader?.free()
        super.onDestroy()
    }

    /**
     * scan UHF tag
     * */
    protected fun startInventoryRead() {
        when (inventoryReadType) {
            ONE_TIME -> {
                Log.e("Started one-time scanning")
                val uhfTAGInfo: UHFTAGInfo? = mReader?.inventorySingleTag()
                if (uhfTAGInfo == null) {
                    toast("Inventory failure")
                    return
                }
                if (!isLocationMode) playSound()
                iuhfInventoryCallback?.onFoundItem(UHFInventoryItem.get(uhfTAGInfo))
            }
            CONTINUES -> {
                if (mReader == null) return
                val success = mReader!!.startInventoryTag()
                if (success) {
                    isInventoryRunning = true
                    continuesReadInventory()
                    if (isLocationMode) startSoundPool()
                } else {
                    Log.e("Error in Continues scanning")
                    mReader?.stopInventory()
                    toast("Open failure")
                }
            }
        }
    }

    private fun continuesReadInventory() {
        Thread {
            while (isInventoryRunning) {
                val uhfTAGInfo = mReader?.readTagFromBuffer()
                if (uhfTAGInfo != null) {
                    Log.e("uhfTAGInfo : ${uhfTAGInfo.epc}")
                    if (!isLocationMode) playSound()
                    runOnUiThread {
                        iuhfInventoryCallback?.onFoundItem(UHFInventoryItem.get(uhfTAGInfo))
                    }
                } else {
                    justTry { Thread.sleep(100) }
                }
            }
        }.start()
    }

    protected fun stopInventoryRead() {
        Log.e("stopInventoryRead")
        inventoryJob?.cancel()
        if (mReader == null) return
        if (!isInventoryRunning) return
        if (mReader!!.stopInventory()) isInventoryRunning = false
        else toast("Stop failure")
    }

    /*
    * Sound Management
    * */
    private var soundMap = HashMap<Int, Int>()
    private var soundPool: SoundPool? = null
    private var volumeRatio = 0f
    private var audioManager: AudioManager? = null

    private fun initSound() {
        val audioAttributes = AudioAttributes.Builder()
            .setLegacyStreamType(AudioManager.STREAM_MUSIC)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(10)
            .setAudioAttributes(audioAttributes)
            .build()

        soundMap[1] = soundPool!!.load(this, R.raw.barcodebeep, 1)
        soundMap[2] = soundPool!!.load(this, R.raw.serror, 1)
        soundMap[3] = soundPool!!.load(this, R.raw.beep_sound, 1)
        audioManager =
            this.getSystemService(AUDIO_SERVICE) as AudioManager // Instantiate the AudioManager object
    }

    /**
     * Play the beep
     *
     * @param id success 1, failure 2
     * // priority, 0 is the lowest
     */
    protected fun playSound(success: Boolean = true) {
        if (audioManager == null) return
        if (soundPool == null) return
        val audio: Int = soundMap[1.takeIf { success } ?: 2] ?: return
        val maxVolume = audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
        val currentVolume = audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
        volumeRatio = currentVolume / maxVolume
        justTry { soundPool!!.play(audio, volumeRatio, volumeRatio, 1, 0, 1f) }
    }


    protected fun playSound1() {
        if (audioManager == null) return
        if (soundPool == null) return
        val audio: Int = soundMap[3] ?: return
        soundPool?.play(audio, leftVolume, rightVolume, 1, 0, 1f)
    }

    private var isNeedToPlaySound = false
    private var leftVolume: Float = 1f
    private var rightVolume: Float = 1f
    private var soundWaitTime: Long = 50L

    protected fun setSoundConfig(leftVolume: Float, rightVolume: Float, soundWaitTime: Long) {
        isNeedToPlaySound = true
        this.leftVolume = leftVolume
        this.rightVolume = rightVolume
        this.soundWaitTime = soundWaitTime
    }

    private fun startSoundPool() {
        Thread {
            while (isInventoryRunning) {
                if (isNeedToPlaySound) {
                    playSound1()
                    isNeedToPlaySound = false
                }
                justTry { Thread.sleep(soundWaitTime) }
            }
        }.start()
    }
}