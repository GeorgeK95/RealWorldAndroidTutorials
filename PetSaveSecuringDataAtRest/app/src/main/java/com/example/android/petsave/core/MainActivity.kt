package com.example.android.petsave.core

import android.os.Bundle
import android.util.Base64
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.android.petsave.R
import com.example.android.petsave.animalsnearyou.presentation.AnimalsNearYouFragmentViewModel
import com.example.android.petsave.core.data.api.Authenticator
import com.example.android.petsave.core.data.api.ReportManager
import com.example.android.petsave.core.domain.model.user.User
import com.example.android.petsave.core.domain.repositories.UserRepository
import com.example.android.petsave.core.utils.Encryption.Companion.createLoginPassword
import com.example.android.petsave.core.utils.Encryption.Companion.decryptPassword
import com.example.android.petsave.core.utils.Encryption.Companion.generateSecretKey
import com.example.android.petsave.core.utils.FileConstants
import com.example.android.petsave.core.utils.PreferencesHelper
import com.example.android.petsave.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream
import java.util.concurrent.Executors

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

  private lateinit var biometricPrompt: BiometricPrompt
  private lateinit var promptInfo: BiometricPrompt.PromptInfo

  val clientAuthenticator = Authenticator()
  var serverPublicKeyString = ""
  val reportManager = ReportManager()

  override fun onCreate(savedInstanceState: Bundle?) {
    // Switch to AppTheme for displaying the activity
    setTheme(R.style.AppTheme)

    super.onCreate(savedInstanceState)
    window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

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
    val biometricManager = BiometricManager.from(this)
    when (biometricManager.canAuthenticate()) {
      BiometricManager.BIOMETRIC_SUCCESS ->
        displayLogin(view, false)
      BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
        displayLogin(view, true)
      BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
        toast("Biometric features are currently unavailable.")
      BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
        toast("Please associate a biometric credential with your account.")
      else ->
        toast("An unknown error occurred. Please check your Biometric settings")
    }
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
    val executor = Executors.newSingleThreadExecutor()
    biometricPrompt = BiometricPrompt(this, executor,
      object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(
          errorCode: Int,
          errString: CharSequence
        ) {
          super.onAuthenticationError(errorCode, errString)
          runOnUiThread {
            toast("Authentication error: $errString")
          }
        }

        override fun onAuthenticationFailed() {
          super.onAuthenticationFailed()
          runOnUiThread {
            toast("Authentication failed")
          }
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
          super.onAuthenticationSucceeded(result)

          runOnUiThread {
            toast("Authentication succeeded!")
            if (!isSignedUp) {
              generateSecretKey()
            }
            performLoginOperation(view)
          }
        }
      })

    if (fallback) {
      promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Biometric login for my app")
        .setSubtitle("Log in using your biometric credential")
        // Cannot call setNegativeButtonText() and
        // setDeviceCredentialAllowed() at the same time.
        // .setNegativeButtonText("Use account password")
        .setDeviceCredentialAllowed(true)
        .build()
    } else {
      promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Biometric login for my app")
        .setSubtitle("Log in using your biometric credential")
        .setNegativeButtonText("Use account password")
        .build()
    }
    biometricPrompt.authenticate(promptInfo)
  }

  private fun performLoginOperation(view: View) {
    var success = false

    workingFile?.let {
      //Check if already signed up
      if (isSignedUp) {
        val fileInputStream = FileInputStream(it)
        val objectInputStream = ObjectInputStream(fileInputStream)
        val list = objectInputStream.readObject() as ArrayList<User>
        val firstUser = list.first() as? User
        if (firstUser is User) { //2
          val userToken = decryptPassword(
            this,
            Base64.decode(firstUser.password, Base64.NO_WRAP)
          )
          if (userToken.isNotEmpty()) {
            //NOTE: Send credentials to authenticate with server
            serverPublicKeyString = reportManager.login(
              Base64.encodeToString(userToken, Base64.NO_WRAP),
              clientAuthenticator.publicKey()
            )
            success = serverPublicKeyString.isNotEmpty()
          }
        }

        if (success) {
          toast("Last login: ${PreferencesHelper.lastLoggedIn(this)}")
        } else {
          toast("Please check your credentials and try again.")
        }

        objectInputStream.close()
        fileInputStream.close()
      } else {
        val encryptedInfo = createLoginPassword(this)
        UserRepository.createDataSource(applicationContext, it, encryptedInfo)
        success = true
      }
    }

    if (success) {
      PreferencesHelper.saveLastLoggedInTime(this)
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

  override fun onPause() {
    cacheDir.deleteRecursively()
    externalCacheDir?.deleteRecursively()

    super.onPause()
  }

  override fun onDestroy() {
    super.onDestroy()
    _binding = null
  }
}
