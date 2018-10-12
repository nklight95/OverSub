package com.nklight.ultsub

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
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
    private val  FILE_SELECT_CODE = 100
    internal var state: AppState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName"))
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION)
        } else {
            initializeViewOld()
        }
    }

    private fun initialView() {
        val state = AppState(
                watermark = findViewById(R.id.watermark) as View,
                timeLabel = findViewById(R.id.current_time) as TextView,
                offsetMillis = 0,
                toggleFollow = findViewById(R.id.toggle_follow) as ToggleButton,
                listView = findViewById(R.id.subs_list) as RecyclerView,
                subtitles = listOf(),
                startingTimestamps = listOf()
        )
        this.state = state

        val mLayoutManager = LinearLayoutManager(this)
        state.listView.layoutManager = mLayoutManager

        state.listView.setHasFixedSize(true)

        val testSubStream = this.resources.openRawResource(R.raw.one)

        loadSubs(testSubStream, state)

        state.listView.addOnScrollListener(ListScrollListener(state))

        var timer = null as Timer?
        val activity = this

        state.toggleFollow.setOnClickListener {
            val tb = it as ToggleButton
            if (tb.isChecked) {
                timer = Timer()
                val advanceTime = object : TimerTask() {
                    override fun run() {
                        activity.runOnUiThread {
                            val timestamp = Timestamp.fromTotalMillis(System.currentTimeMillis() - state.offsetMillis)
                            state.timeLabel.text = timestamp.compile()
                            // search for auto scroll
                            val binSearchIndex = state.startingTimestamps.binarySearch(timestamp) // position of sub in list
                            val subtitleToScrollTo = if (binSearchIndex > 0) {
                                if (binSearchIndex < 1) 1 else binSearchIndex
                            } else {
                                val previousItem = -2 - binSearchIndex
                                if (previousItem < 1) 1 else previousItem
                            }
                            val currentSubtitle = state.subtitles[subtitleToScrollTo]
                            var progress = (1.0 * (timestamp.totalMillis - currentSubtitle.startTime.totalMillis)
                                    / (currentSubtitle.endTime.totalMillis - currentSubtitle.startTime.totalMillis))
                            if (progress > 1) {
                                progress = 1.0
                                show("empty")
                            } else {
                                show(state.subtitles[subtitleToScrollTo].lines[0])
                            }
                            val viewHolder = state.listView.findViewHolderForAdapterPosition(subtitleToScrollTo)
                            if (viewHolder == null) {
                                state.listView.scrollToPosition(subtitleToScrollTo)
                            } else {
                                val watermarkY = state.watermark.top
                                state.listView.scrollBy(0, viewHolder.itemView.top + (viewHolder.itemView.height * progress).toInt() - watermarkY)
                            }

                        }

                    }

                }
                timer!!.schedule(advanceTime, 100 /* ms */, 100 /* ms */)
                state.offsetMillis = System.currentTimeMillis() - Timestamp(state.timeLabel.text).totalMillis
            } else {
                timer!!.cancel()
            }
        }
    }


    private fun initializeViewOld() {
        btnCreate.setOnClickListener({
            startService(Intent(this@MainActivity, UltSubService::class.java))
            finish()
        })
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

        state.startingTimestamps = state.subtitles.map {it.startTime}
        state.listView.adapter = SubtitleListAdapter(state.subtitles)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {

            //Check if the permission is granted or not.
            if (resultCode == Activity.RESULT_OK) {
                initializeViewOld()
            } else { //Permission is not available
                Toast.makeText(this,
                        "Draw over other app permission not available. Closing the application",
                        Toast.LENGTH_SHORT).show()

                finish()
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
    override fun onScrolled(listView: RecyclerView, dx: Int, dy: Int){
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
        var watermark : View,
        var timeLabel: TextView,
        var toggleFollow: ToggleButton,

        // Data
        var offsetMillis: Long,
        var subtitles : List<Subtitle>,
        var startingTimestamps: List<Timestamp>
)


