package com.example.submissionstoryapp.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.submissionstoryapp.R
import com.example.submissionstoryapp.data.local.entity.Story
import com.example.submissionstoryapp.databinding.ActivityDetailBinding
import com.example.submissionstoryapp.utils.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : BaseActivity<ActivityDetailBinding>() {

    companion object {
        const val EXTRA_DETAIL = "extra_detail"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val story = intent.getParcelableExtra<Story>(EXTRA_DETAIL)
        if (story != null) {
            bindData(story)
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun inflateBinding(layoutInflater: LayoutInflater): ActivityDetailBinding {
        return ActivityDetailBinding.inflate(layoutInflater)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun bindData(story: Story) {
        binding.apply {
            Glide.with(this@DetailActivity)
                .load(story.photoUrl)
                .apply(
                    RequestOptions.placeholderOf(R.drawable.ic_loading)
                        .error(R.drawable.ic_error)
                )
                .into(ivStory)
            tvNama.text = story.name
            tvDesc.text = story.description
            tvTanggal.text = getString(R.string.hint_unggah, story.createdAt)
        }
    }
}