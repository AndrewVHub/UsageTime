package ru.kaycom.usagetime.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import org.koin.androidx.fragment.android.setupKoinFragmentFactory
import ru.kaycom.usagetime.R
import ru.kaycom.usagetime.databinding.ActivityMainBinding
import ru.kaycom.usagetime.ui.MainNavigator
import ru.kaycom.usagetime.ui.viewBinding.viewBinding

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