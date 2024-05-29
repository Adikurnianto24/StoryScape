package com.adiandroid.storyscape.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.adiandroid.storyscape.R
import com.adiandroid.storyscape.data.model.response.ListStoryItem
import com.adiandroid.storyscape.databinding.ActivityDetailStoryBinding
import com.adiandroid.storyscape.utility.dateFormat
import com.bumptech.glide.Glide


@Suppress("DEPRECATION")
class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    private var clicked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val storyDetail = intent.getParcelableExtra<ListStoryItem>(DETAIL_STORY) as ListStoryItem

        setupView(storyDetail)
        fabActionHandler()
    }

    private fun setupView(storyDetail: ListStoryItem) {
        Glide.with(this@DetailStoryActivity)
            .load(storyDetail.photoUrl)
            .fitCenter()
            .into(binding.storyDetailIv)

        storyDetail.apply {
            binding.nameDetailTv.text = name
            binding.descDetailTv.text = description
            binding.detailDateTv.text = createdAt.dateFormat()
        }
    }

    private fun fabActionHandler() {
        binding.apply {
            showBtn.setOnClickListener {
                onAddButtonClicked()
            }
            shareBtn.setOnClickListener {
                val shareDetail = intent.getParcelableExtra<ListStoryItem>(DETAIL_STORY)
                val intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(Intent.EXTRA_TEXT, ("Story From ${shareDetail?.name}"))
                intent.type = "text/plain"
                startActivity(Intent.createChooser(intent, "Send To"))
            }
            backHome.setOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun onAddButtonClicked() {
        fabSetVisibility(clicked)
        clicked = !clicked
    }

    private fun fabSetVisibility(clicked: Boolean) {
        binding.apply {
            if(!clicked){
                backHome.visibility = View.VISIBLE
                shareBtn.visibility = View.VISIBLE
            }else{
                backHome.visibility = View.INVISIBLE
                shareBtn.visibility = View.INVISIBLE
            }
        }
    }

    companion object {
        const val DETAIL_STORY = "DETAIL_STORY"
    }
}