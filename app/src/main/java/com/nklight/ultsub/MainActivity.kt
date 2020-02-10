package com.nklight.ultsub

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import com.nklight.ultsub.Subtitle.Subtitle
import com.nklight.ultsub.Subtitle.SubtitleFile
import com.nklight.ultsub.Subtitle.Timestamp
import kotlinx.android.synthetic.main.activity_main.*
import java.io.InputStream
import java.util.*


class MainActivity : AppCompatActivity() {
    private val CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084
    private val FILE_SELECT_CODE = 100
    internal var state: AppState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()
        initializeViewOld()
    }

    private fun checkPermission(): Boolean {
        val isNotHavePermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)
        if (isNotHavePermission) {
            requestOverLayPermission()
        }
        return !isNotHavePermission
    }

    private fun requestOverLayPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName"))
        startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION)
    }


    private fun initializeViewOld() {
        btnCreate.setOnClickListener({
            try {
                if (checkPermission()) {
                    startService()
                    finish()
                }
            } catch (e: Exception) {
                showToast("Can't start service\n" + e.message)
            }
        })
    }

    private fun startService() {
        val i = Intent(this@MainActivity, UltSubService::class.java)
        i.putExtra(UltSubService.BUBBLE_SIZE_KEY, 55)
        startService(i)
    }

    private fun testTimer() {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {

            }
        }, 0L)
    }

    @Suppress("DEPRECATION")
    private fun show(message: String) {
        demo.text = Html.fromHtml(message)
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun showToast(@StringRes msgId: Int) {
        showToast(getString(msgId))
    }

    private fun loadSubs(testSubStream: InputStream, state: AppState) {
        val subsFile = SubtitleFile(testSubStream)
        val subsWithPadding = mutableListOf<Subtitle>()
        subsWithPadding.add(Subtitle(
                Timestamp.fromTotalMillis(0),
                Timestamp.fromTotalMillis(0),
                Collections.nCopies(30, "")))
        subsWithPadding.addAll(subsFile.subtitles)
        subsWithPadding.add(Subtitle(
                Timestamp.fromTotalMillis(999999999),
                Timestamp.fromTotalMillis(999999999),
                Collections.nCopies(30, "")))
        state.subtitles = subsWithPadding

        state.startingTimestamps = state.subtitles.map { it.startTime }
        state.listView.adapter = SubtitleListAdapter(state.subtitles)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            //Check if the permission is granted or not.
            if (resultCode == Activity.RESULT_OK) {
                initializeViewOld()
            } else { //Permission is not available
                checkPermission()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}

class SubtitleListAdapter(val objects: List<Subtitle>)
    : RecyclerView.Adapter<SimpleViewHolder>() {
    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        val sub = objects[position]
        (holder.itemView.findViewById(R.id.subtitle_text) as TextView).text =
                sub.lines.joinToString("\n")
        (holder.itemView.findViewById(R.id.subtitle_timestamp) as TextView).text =
                "${sub.startTime.compile()} --> ${sub.endTime.compile()}"
    }

    override fun getItemCount(): Int {
        return objects.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        return SimpleViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.subs_list_item, parent, false))
    }
}

class SimpleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

class ListScrollListener(val state: AppState) : RecyclerView.OnScrollListener() {
    override fun onScrolled(listView: RecyclerView, dx: Int, dy: Int) {
        if (state.toggleFollow.isChecked) {
            return
        }
        val watermarkY = state.watermark.top
        val itemView = listView.findChildViewUnder(10f, watermarkY.toFloat())
        if (itemView != null) {
            val itemIndex = listView.getChildAdapterPosition(itemView)
            val sub = state.subtitles[itemIndex]
            val startSeconds = sub.startTime.totalMillis
            val endSeconds = sub.endTime.totalMillis
            val relativeInItem = (watermarkY - itemView.y) / itemView.measuredHeight
            val time = startSeconds + (endSeconds - startSeconds) * relativeInItem
            state.timeLabel.text = Timestamp.fromTotalMillis(time.toLong()).compile()
        }
    }
}

data class AppState(
        // Widgets
        var listView: RecyclerView,
        var watermark: View,
        var timeLabel: TextView,
        var toggleFollow: ToggleButton,

        // Data
        var offsetMillis: Long,
        var subtitles: List<Subtitle>,
        var startingTimestamps: List<Timestamp>
)


