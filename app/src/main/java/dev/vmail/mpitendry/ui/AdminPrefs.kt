package dev.vmail.mpitendry.ui

import android.content.Context

object AdminPrefs {
    private const val PREF = "mpitendry_prefs"
    private const val KEY_ADMIN = "is_admin"

    fun isAdmin(ctx: Context): Boolean =
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE).getBoolean(KEY_ADMIN, false)

    fun setAdmin(ctx: Context, value: Boolean) {
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_ADMIN, value)
            .apply()
    }
}
