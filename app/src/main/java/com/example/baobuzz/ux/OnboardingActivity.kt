package com.example.baobuzz.ux

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.viewpager2.widget.ViewPager2
import com.example.baobuzz.HomeActivity
import com.example.baobuzz.R
import com.example.baobuzz.adapter.OnboardingAdapter
import com.example.baobuzz.databinding.ActivityOnboardingBinding
import com.example.baobuzz.models.OnboardingItem

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var adapter: OnboardingAdapter
    private val onboardingItems = listOf(
        OnboardingItem(
            R.drawable.onboarding_1,
            "Live Scores",
            "Get real-time updates on all football matches"
        ),
        OnboardingItem(
            R.drawable.onboarding_2,
            "League Tables",
            "Stay updated with the latest standings across all leagues"
        ),
        OnboardingItem(
            R.drawable.onboarding_1,
            "Personalized Alerts",
            "Set notifications for your favorite teams and matches"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupButtons()
        setupIndicators()
    }

    private fun setupViewPager() {
        adapter = OnboardingAdapter(onboardingItems)
        binding.viewPager.adapter = adapter
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateIndicators(position)
                updateButtonsVisibility(position)
            }
        })
    }

    private fun setupButtons() {
        binding.prevButton.setOnClickListener {
            binding.viewPager.currentItem = binding.viewPager.currentItem - 1
        }

        binding.nextButton.setOnClickListener {
            if (binding.viewPager.currentItem < adapter.itemCount - 1) {
                binding.viewPager.currentItem = binding.viewPager.currentItem + 1
            } else {
                finishOnboarding()
            }
        }

        binding.skipButton.setOnClickListener { finishOnboarding() }
    }

    private fun setupIndicators() {
        val indicators = arrayOfNulls<ImageView>(adapter.itemCount)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(8, 0, 8, 0) }

        indicators.forEachIndexed { index, _ ->
            indicators[index] = ImageView(applicationContext).apply {
                setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.default_indicator))
                this.layoutParams = layoutParams
            }
            binding.onbindicator.addView(indicators[index])
        }
    }

    private fun updateIndicators(position: Int) {
        binding.onbindicator.children.forEachIndexed { index, view ->
            if (view is ImageView) {
                view.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        if (index == position) R.drawable.selected_dot else R.drawable.default_dot
                    )
                )
            }
        }
    }

    private fun updateButtonsVisibility(position: Int) {
        binding.prevButton.visibility = if (position == 0) View.INVISIBLE else View.VISIBLE
        binding.nextButton.text = if (position == adapter.itemCount - 1) "Get Started" else "Next"
        binding.skipButton.visibility = if (position == adapter.itemCount - 1) View.GONE else View.VISIBLE
    }

    private fun finishOnboarding() {
        getSharedPreferences("Onboarding", Context.MODE_PRIVATE).edit {
            putBoolean("Shown", true)
        }
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
