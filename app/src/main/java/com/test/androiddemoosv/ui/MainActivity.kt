package com.test.androiddemoosv.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.test.androiddemoosv.R
import com.test.androiddemoosv.databinding.ActivityMainBinding
import com.test.androiddemoosv.viewmodel.MainViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private lateinit var adapter: ModuleAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarTitle)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        WindowInsetsControllerCompat(window, window.decorView)
            .isAppearanceLightStatusBars = false   // light icons

        //window.statusBarColor = Color.TRANSPARENT

        // Apply status bar padding to toolbar
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbarTitle) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top

            view.setPadding(
                view.paddingLeft,
                statusBarHeight,
                view.paddingRight,
                view.paddingBottom
            )
            //view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryVariant))
            view.setBackgroundColor(
                ContextCompat.getColor(this, R.color.colorPrimaryVariant)
            )

            insets
        }

        adapter = ModuleAdapter(emptyList()) { module ->
            val result = viewModel.checkAccess(module)
            Toast.makeText(
                this,
                if (result.first)
                    "Navigating to ${module.title}"
                else
                    result.second,
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter


        viewModel.coolingState.observe(this) { state ->
            binding.tvCooling.text = state.message ?: ""
            binding.cardViewCooling.visibility = if (state.message == null) View.GONE else View.VISIBLE

            adapter.updateCoolingState(state.isActive)
        }

        viewModel.modules.observe(this) { modules ->
            adapter.submitList(modules)
        }
        viewModel.accessibleIds.observe(this) { modules ->
            adapter.accessableListModules(modules)
        }
    }
}
