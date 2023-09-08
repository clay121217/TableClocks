package com.example.tableclocks

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.example.tableclocks.databinding.SettingsActivityBinding


class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: SettingsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, ThemeSettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //戻るボタン
        binding.backButton.setOnClickListener {
            val intent = Intent(application, MainActivity::class.java)
            startActivity(intent)
        }

        //リストで所持テーマのライブラリと、購入誘導をやりたい

    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }

//    val pref = getSharedPreferences("com.tableClocks.settings", Context.MODE_PRIVATE)
//    val themeName = pref.getString("themeName","jpseasons")
}