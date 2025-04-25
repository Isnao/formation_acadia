package isnao.acadiaformation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import isnao.acadiaformation.databinding.ActivityMainBinding
import isnao.acadiaformation.databinding.FragmentReminderBinding
import isnao.acadiaformation.databinding.MoreInfoCardBinding
import java.io.File

class MainActivity : AppCompatActivity() {
    object Actions {
        @Suppress("ktlint:standard:property-naming")
        const val id = "test_channel_01"
        const val TAG = "MainActivity"
    }

    object Niveau {
        var niveau1 =
            arrayOf(
                "Nom",
                "Touche",
                "Fini",
                "Assis",
                "A ta place",
                "On y va",
                "Olfact.REC",
                "Les besoins",
                "Couché",
                "A gauche",
                "Debout",
                "Viens",
            )
        var niveau2 = arrayOf("Olfact.DIS", "Dessous", "Poke", "Attends", "Tu laisse", "Saut/Desc", "Roule", "A droite", "Recule")
        var niveau3 =
            arrayOf(
                "T.A.P. environnement",
                "Alerte",
                "Genoux",
                "Va chercher de l'aide",
                "Apporte",
                "Bouton",
                "Ouvre la porte",
                "Habille-toi",
            )
    }

    private val sessionViewModel: SessionViewModel by viewModels()

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var popupWindow: PopupWindow

    @Suppress("ktlint:standard:property-naming")
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    private lateinit var rpl: ActivityResultLauncher<Array<String>>
    private lateinit var fileName: String
    lateinit var menu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        setPopupWindow()

        NotificationHelper(binding.root.context).also { helper: NotificationHelper ->
            helper.createchannel()
        }

        // for notifications permission now required in api 33
        // this allows us to check with multiple permissions, but in this case (currently) only need 1.
        rpl =
            registerForActivityResult<Array<String>, Map<String, Boolean>>(
                ActivityResultContracts.RequestMultiplePermissions(),
            ) { isGranted ->
                var granted = true
                for ((key, value) in isGranted) {
                    logthis("$key is $value")
                    if (!value) granted = false
                }
                if (granted) logthis("Permissions granted for api 33+")
            }

        if (!allPermissionsGranted()) {
            rpl.launch(REQUIRED_PERMISSIONS)
        }
    }

    // ask for permissions when we start.
    private fun allPermissionsGranted(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    fun logthis(msg: String) {
        Log.d(Actions.TAG, msg)
    }

    override fun onCreateOptionsMenu(menuI: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menuI)
        menu = menuI
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.itemId == R.id.action_bilan) {
            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.action_SessionFragment_to_BilanFragment)
        } else if (item.itemId == R.id.action_session) {
            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.action_BilanFragment_to_SessionFragment)
        } else if (item.itemId == R.id.action_session_csv) {
            val intent =
                Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "text/csv"
                    putExtra(Intent.EXTRA_TITLE, "formation_acadia")
                }
            fileName = "formation_acadia.csv"
            startActivityForResult(intent, 1)
        } else if (item.itemId == R.id.action_bilan_csv) {
            val intent =
                Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "text/csv"
                    putExtra(Intent.EXTRA_TITLE, "bilan_progres")
                }
            fileName = "bilan_progres.csv"
            startActivityForResult(intent, 1)
        } else if (item.itemId == R.id.action_session_import) {
            val intent =
                Intent(Intent.ACTION_GET_CONTENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "text/*"
                }
            startActivityForResult(intent, 2)
        } else if (item.itemId == R.id.action_bilan_import) {
            val intent =
                Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "text/*"
                }
            startActivityForResult(intent, 3)
        } else if (item.itemId == R.id.action_clean) {
            restLocal()
            Snackbar
                .make(binding.root.rootView, "Données local supprimé", Snackbar.LENGTH_LONG)
                .setDuration(5000)
                .setAnchorView(R.id.fab)
                .show()
        } else if (item.itemId == R.id.action_more) {
            val popupView: MoreInfoCardBinding = MoreInfoCardBinding.inflate(layoutInflater)
            val popupWindow2 =
                PopupWindow(popupView.root, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true)
            popupWindow2.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
        } else if (item.itemId == R.id.action_session_list) {
            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.action_to_SessionListFragment)
        }
        return when (item.itemId) {
            R.id.action_session_csv -> true
            R.id.action_bilan_csv -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) ||
            super.onSupportNavigateUp()
    }

    @Deprecated("Deprecated in Java")
    @Override
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == 1) {
            try {
                data?.data.let {
                    if (it != null) {
                        contentResolver.openOutputStream(it)?.let { outputStream ->
                            outputStream.write(getLocal().toByteArray(Charsets.UTF_8))
                            outputStream.flush()
                            outputStream.close()
                        }
                    }
                }
            } catch (exception: Exception) {
                logthis(exception.toString())
                throw exception
            }
        } else if (requestCode == 2) {
            try {
                data?.data.let {
                    if (it != null) {
                        contentResolver.openInputStream(it)?.let { inputStream ->
                            sessionViewModel.import(inputStream)
                            val file = File(applicationContext?.filesDir ?: binding.root.context.filesDir, "formation_acadia.csv")
                            file.writeText("Date;Comportement;Objectif;Remarques;Duree;Session Validee ?\n")
                            file.appendText(sessionViewModel.listToString())
                            inputStream.close()
                        }
                    }
                }
            } catch (exception: Exception) {
                logthis(exception.toString())
                throw exception
            }
        } else if (requestCode == 3) {
            try {
                data?.data.let {
                    if (it != null) {
                        contentResolver.openInputStream(it)?.let { inputStream ->
                            val file = File(applicationContext?.filesDir ?: binding.root.context.filesDir, "bilan_progres.csv")
                            file.writeBytes(inputStream.readBytes())
                            inputStream.close()
                        }
                    }
                }
            } catch (exception: Exception) {
                logthis(exception.toString())
                throw exception
            }
        } else {
            throw Exception("RequestCode not implemented: $requestCode")
        }
    }

    private fun getLocal(): String {
        val file = File(applicationContext.filesDir, fileName)

        return file.readText()
    }

    private fun restLocal() {
        sessionViewModel.clean()
        val file = File(applicationContext.filesDir, "formation_acadia.csv")
        file.delete()
        val file2 = File(applicationContext.filesDir, "bilan_progres.csv")
        file2.delete()
    }

    private fun setPopupWindow() {
        val popupView: FragmentReminderBinding = FragmentReminderBinding.inflate(layoutInflater)
        popupView.timePicker.setIs24HourView(true)
        popupView.button.setOnClickListener { _ ->
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, popupView.timePicker.hour)
            calendar.set(Calendar.MINUTE, popupView.timePicker.minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            NotificationHelper(binding.root.context).also { helper: NotificationHelper ->
                helper.setReminder(calendar.timeInMillis)
            }
            Snackbar
                .make(binding.root.rootView, "Reminder set to ${calendar.time}", Snackbar.LENGTH_LONG)
                .setDuration(5000)
                .setAnchorView(R.id.fab)
                .show()
            popupWindow.dismiss()
        }
        popupWindow = PopupWindow(popupView.root, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true)

        binding.fab.setOnClickListener { _ ->
            popupView.timePicker.hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + 1
            popupView.timePicker.minute = Calendar.getInstance().get(Calendar.MINUTE)
            popupWindow.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
        }
    }
}
