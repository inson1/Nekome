package com.chesire.malime

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private val sharedPref: SharedPref by lazy { SharedPref(this) }
    private var currentDisplayedFragmentTagBundleId = "currentFragment"
    private var currentDisplayedFragmentTag = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.activity_main_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item: MenuItem ->
            val fragment: Fragment
            val tag: String

            when (item.itemId) {
                R.id.menu_main_navigation_anime -> {
                    tag = AnimeFragment.tag
                    fragment = supportFragmentManager.findFragmentByTag(tag) ?: AnimeFragment.newInstance()
                }
                R.id.menu_main_navigation_manga -> {
                    tag = MangaFragment.tag
                    fragment = supportFragmentManager.findFragmentByTag(tag) ?: MangaFragment.newInstance()
                }
                else -> {
                    tag = SearchFragment.tag
                    fragment = supportFragmentManager.findFragmentByTag(tag) ?: SearchFragment.newInstance()
                }
            }

            setFragment(fragment, tag)
            true
        }

        if (savedInstanceState == null) {
            setFragment(AnimeFragment.newInstance(), AnimeFragment.tag)
        } else {
            currentDisplayedFragmentTag = savedInstanceState.getString(currentDisplayedFragmentTagBundleId)
            setFragment(supportFragmentManager.findFragmentByTag(currentDisplayedFragmentTag), currentDisplayedFragmentTag)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putString(currentDisplayedFragmentTagBundleId, currentDisplayedFragmentTag)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when {
            item?.itemId == R.id.menu_options_view_profile -> {
                CustomTabsIntent.Builder()
                        .build()
                        .launchUrl(this, Uri.parse("https://myanimelist.net/profile/${sharedPref.getUsername()}"))
                return true
            }
            item?.itemId == R.id.menu_options_log_out -> {
                AlertDialog.Builder(this)
                        .setTitle(R.string.options_log_out)
                        .setMessage(R.string.log_out_confirmation)
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, { _, _ ->
                            sharedPref.clearLoginDetails()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        })
                        .show()

                return true
            }
            item?.itemId == R.id.menu_options_filter -> {
                spawnFilterDialog()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }

    private fun spawnFilterDialog() {
        val states = sharedPref.getAnimeFilter()
        AlertDialog.Builder(this)
                .setTitle(R.string.filter_dialog_title)
                .setMultiChoiceItems(R.array.anime_states, states, { _, which, isChecked ->
                    states[which] = isChecked
                })
                .setPositiveButton(android.R.string.ok, { _, _ ->
                    if (states.all { !it }) {
                        Timber.w("User tried to set all filter states to false")
                        Snackbar.make(findViewById(R.id.activity_main_layout), R.string.filter_must_select, Snackbar.LENGTH_LONG)
                                .show()
                    } else {
                        sharedPref.setAnimeFilter(states)
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show()
    }

    private fun setFragment(fragment: Fragment, fragmentTag: String) {
        currentDisplayedFragmentTag = fragmentTag
        supportFragmentManager.beginTransaction()
                .replace(R.id.activity_main_frame, fragment, fragmentTag)
                .commit()
    }
}
