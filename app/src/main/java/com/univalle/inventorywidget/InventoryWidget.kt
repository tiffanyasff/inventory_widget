package com.univalle.inventorywidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.univalle.inventorywidget.repository.InventoryRepository
import com.univalle.inventorywidget.view.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class InventoryWidget : AppWidgetProvider() {

    companion object {
        private const val ACTION_TOGGLE_VISIBILITY = "ACTION_TOGGLE_VISIBILITY"
        private const val PREFS_NAME = "WidgetPrefs"
        private const val KEY_VISIBLE = "total_visible"
        private const val ACTION_OPEN_LOGIN = "ACTION_OPEN_LOGIN"
    }

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        super.onUpdate(context, manager, ids)

        ids.forEach { widgetId ->
            updateWidget(context, manager, widgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {

            ACTION_TOGGLE_VISIBILITY -> {
                val manager = AppWidgetManager.getInstance(context)
                val ids = manager.getAppWidgetIds(
                    ComponentName(context, InventoryWidget::class.java)
                )
                ids.forEach { updateWidget(context, manager, it, toggle = true) }
            }

            ACTION_OPEN_LOGIN -> {
                val mainIntent = Intent(context, MainActivity::class.java)
                mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                mainIntent.putExtra("goToLogin", true)
                context.startActivity(mainIntent)
            }
        }
    }

    private fun updateWidget(
        context: Context,
        manager: AppWidgetManager,
        widgetId: Int,
        toggle: Boolean = false
    ) {

        val views = RemoteViews(context.packageName, R.layout.inventory_widget)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        var visible = prefs.getBoolean(KEY_VISIBLE, false)

        if (toggle) {
            visible = !visible
            prefs.edit().putBoolean(KEY_VISIBLE, visible).apply()
        }

        // -------- Criterios 8 y 9: CALCULAR TOTAL DEL INVENTARIO ----------
        CoroutineScope(Dispatchers.IO).launch {
            val repository = InventoryRepository(context)
            val list = repository.getListInventory()

            val total = list.sumOf { it.price * it.quantity }

            val formatted = NumberFormat.getNumberInstance(Locale("es", "CO"))
                .apply { minimumFractionDigits = 2 }
                .format(total)

            CoroutineScope(Dispatchers.Main).launch {

                // -------- Criterios 7 y 10: MOSTRAR U OCULTAR SALDO ----------
                if (visible) {
                    views.setTextViewText(
                        R.id.widget_total_value,
                        "$ $formatted"
                    )
                    views.setImageViewResource(
                        R.id.widget_visibility_button,
                        R.drawable.ic_eye_off   // Tu icono ojo cerrado
                    )
                } else {
                    views.setTextViewText(
                        R.id.widget_total_value,
                        "$ ****"
                    )
                    views.setImageViewResource(
                        R.id.widget_visibility_button,
                        R.drawable.ic_eye_on   // Tu icono ojo abierto
                    )
                }

                // -------- Criterio 7: CLICK PARA MOSTRAR/OCULTAR ----------
                val toggleIntent = Intent(context, InventoryWidget::class.java)
                toggleIntent.action = ACTION_TOGGLE_VISIBILITY
                val togglePending = PendingIntent.getBroadcast(
                    context,
                    0,
                    toggleIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_visibility_button, togglePending)

                // -------- Criterios 11, 12 y 13: GESTIONAR INVENTARIO --------
                val openLoginIntent = Intent(context, InventoryWidget::class.java)
                openLoginIntent.action = ACTION_OPEN_LOGIN

                val openLoginPending = PendingIntent.getBroadcast(
                    context,
                    1,
                    openLoginIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                views.setOnClickPendingIntent(R.id.widget_manage_text, openLoginPending)
                views.setOnClickPendingIntent(R.id.widget_manage_icon, openLoginPending)

                manager.updateAppWidget(widgetId, views)
            }
        }
    }
}
