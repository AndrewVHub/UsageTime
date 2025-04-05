package ru.andrewvhub.usagetime.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import org.koin.androidx.fragment.android.setupKoinFragmentFactory
import ru.andrewvhub.usagetime.R
import ru.andrewvhub.usagetime.databinding.ActivityMainBinding
import ru.andrewvhub.usagetime.ui.MainNavigator
import ru.andrewvhub.usagetime.ui.viewBinding.viewBinding

class MainActivity : AppCompatActivity(), MainNavigator {

    private val viewBinding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_UsageTime)
        enableEdgeToEdge()
        setupKoinFragmentFactory()
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
    }

    override fun getNavController(): NavController = findNavController(R.id.mainFragmentContainer)
}