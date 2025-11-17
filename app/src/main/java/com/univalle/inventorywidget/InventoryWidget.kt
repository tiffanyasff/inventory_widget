package com.univalle.inventorywidget
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.app.PendingIntent
import android.widget.RemoteViews
import com.univalle.inventorywidget.repository.InventoryRepository
import com.univalle.inventorywidget.view.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InventoryWidget : AppWidgetProvider() {

    companion object {
        private const val ACTION_TOGGLE_VISIBILITY = "com.univalle.inventorywidget.TOGGLE_VISIBILITY"
        private const val PREFS_NAME = "InventoryWidgetPrefs"
        private const val PREF_VISIBILITY = "visibility_"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val repository = InventoryRepository(context)
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val isVisible = prefs.getBoolean(PREF_VISIBILITY + appWidgetId, true)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val inventoryList = repository.getListInventory()
                    var totalValue = 0.0

                    inventoryList.forEach { inventory ->
                        totalValue += (inventory.price * inventory.quantity)
                    }

                    withContext(Dispatchers.Main) {
                        val views = RemoteViews(context.packageName, R.layout.inventory_widget)

                        if (isVisible) {
                            views.setTextViewText(
                                R.id.widget_total_value,
                                String.format("$ %,.2f", totalValue)
                            )
                            views.setImageViewResource(
                                R.id.widget_visibility_button,
                                android.R.drawable.ic_menu_view
                            )
                        } else {
                            views.setTextViewText(
                                R.id.widget_total_value,
                                "$ ••••••"
                            )
                            // Cambiar ícono a "ojo cerrado/tachado"
                            views.setImageViewResource(
                                R.id.widget_visibility_button,
                                android.R.drawable.ic_menu_close_clear_cancel
                            )
                        }

                        val intent = Intent(context, MainActivity::class.java)
                        val pendingIntent = PendingIntent.getActivity(
                            context,
                            0,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                        views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)

                        val toggleIntent = Intent(context, InventoryWidget::class.java).apply {
                            action = ACTION_TOGGLE_VISIBILITY
                            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                        }
                        val togglePendingIntent = PendingIntent.getBroadcast(
                            context,
                            appWidgetId,
                            toggleIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                        views.setOnClickPendingIntent(R.id.widget_visibility_button, togglePendingIntent)

                        appWidgetManager.updateAppWidget(appWidgetId, views)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        val views = RemoteViews(context.packageName, R.layout.inventory_widget)
                        views.setTextViewText(R.id.widget_total_value, "$ 0,00")
                        appWidgetManager.updateAppWidget(appWidgetId, views)
                    }
                }
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == ACTION_TOGGLE_VISIBILITY) {
            val appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )

            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val currentVisibility = prefs.getBoolean(PREF_VISIBILITY + appWidgetId, true)
                prefs.edit().putBoolean(PREF_VISIBILITY + appWidgetId, !currentVisibility).apply()

                val appWidgetManager = AppWidgetManager.getInstance(context)
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        for (appWidgetId in appWidgetIds) {
            editor.remove(PREF_VISIBILITY + appWidgetId)
        }
        editor.apply()
    }
}