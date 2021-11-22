package com.example.android.petsave.core

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.android.petsave.R
import com.example.android.petsave.animalsnearyou.presentation.AnimalsNearYouFragmentViewModel
import com.example.android.petsave.core.data.preferences.PetSavePreferences
import com.example.android.petsave.core.data.preferences.Preferences
import com.example.android.petsave.core.domain.model.user.User
import com.example.android.petsave.core.domain.repositories.UserRepository
import com.example.android.petsave.core.utils.FileConstants
import com.example.android.petsave.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream


/**
 * Main Screen
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding get() = _binding!!
    private var _binding: ActivityMainBinding? = null

    private val navController by lazy { findNavController(R.id.nav_host_fragment) }
    private val appBarConfiguration by lazy {
        AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.animalsNearYou, R.id.search, R.id.report
            )
        )
    }

    private val viewModel: AnimalsNearYouFragmentViewModel by viewModels()
    private var isSignedUp = false
    private var workingFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // Switch to AppTheme for displaying the activity
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupFragment()
        setupActionBar()
        setupBottomNav()
        setupWorkingFiles()
        updateLoggedInState()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setupFragment() {
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
            .hide(nav_host_fragment)
            .commit()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun setupBottomNav() {
        bottom_navigation.visibility = View.GONE
        binding.bottomNavigation.setupWithNavController(navController)
    }

    private fun setupWorkingFiles() {
        workingFile = File(
            filesDir.absolutePath + File.separator +
                    FileConstants.DATA_SOURCE_FILE_NAME
        )
    }

    fun loginPressed(view: View) {
        displayLogin(view, false)
    }

    private fun updateLoggedInState() {
        val fileExists = workingFile?.exists() ?: false
        if (fileExists) {
            isSignedUp = true
            login_button.text = getString(R.string.login)
            login_email.visibility = View.INVISIBLE
        } else {
            login_button.text = getString(R.string.signup)
        }
    }

    private fun displayLogin(view: View, fallback: Boolean) {
        //TODO: Replace below
        performLoginOperation(view)
    }

    private fun performLoginOperation(view: View) {
        var success = false
        val preferences: Preferences = PetSavePreferences(this)

        workingFile?.let {
            //Check if already signed up
            if (isSignedUp) {
                val fileInputStream = FileInputStream(it)
                val objectInputStream = ObjectInputStream(fileInputStream)
                val list = objectInputStream.readObject() as ArrayList<User>
                val firstUser = list.first() as? User
                if (firstUser is User) { //2
                    //TODO: Replace below with implementation that decrypts password
                    success = true
                }

                if (success) {
                    toast("Last login: ${preferences.getLastLoggedIn()}")
                } else {
                    toast("Please check your credentials and try again.")
                }

                objectInputStream.close()
                fileInputStream.close()
            } else {
                //TODO: Replace with encrypted data source below
                UserRepository.createDataSource(applicationContext, it, ByteArray(0))
                success = true
            }
        }

        if (success) {
            preferences.putLastLoggedInTime()
            viewModel.setIsLoggedIn(true)

            //Show fragment
            login_email.visibility = View.GONE
            login_button.visibility = View.GONE
            val fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction()
                .show(nav_host_fragment)
                .commit()
            fragmentManager.executePendingTransactions()
            bottom_navigation.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
