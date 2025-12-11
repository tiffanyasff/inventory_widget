package com.univalle.inventorywidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.univalle.inventorywidget.repository.InventoryRepository
import com.univalle.inventorywidget.view.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class InventoryWidget : AppWidgetProvider() {

    companion object {
        private const val ACTION_TOGGLE_VISIBILITY = "ACTION_TOGGLE_VISIBILITY"
        private var isVisible = false
    }

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { widgetId ->
            updateWidget(context, manager, widgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {

            ACTION_TOGGLE_VISIBILITY -> {

                // 🔒 FORZAR lectura segura del sharedPreferences
                val shared = context.getSharedPreferences("shared", Context.MODE_PRIVATE)
                val hasSession = shared.getBoolean("isLoggedIn", false)

                // 🚫 Si NO hay sesión → ir al login
                if (!hasSession) {
                    val loginIntent = Intent(context, LoginActivity::class.java)
                    loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    loginIntent.putExtra("fromWidget", true)   // ← clave correcta
                    context.startActivity(loginIntent)
                    return
                }

                // ✔ Si HAY sesión → alternar el ojo
                isVisible = !isVisible

                val manager = AppWidgetManager.getInstance(context)
                val ids = manager.getAppWidgetIds(
                    android.content.ComponentName(context, InventoryWidget::class.java)
                )
                ids.forEach { updateWidget(context, manager, it) }
            }
        }
    }

    private fun updateWidget(context: Context, manager: AppWidgetManager, widgetId: Int) {

        val views = RemoteViews(context.packageName, R.layout.inventory_widget)

        // Evento del ojo
        val toggleIntent = Intent(context, InventoryWidget::class.java).apply {
            action = ACTION_TOGGLE_VISIBILITY
        }

        val togglePendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            toggleIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        views.setOnClickPendingIntent(R.id.widget_visibility_icon, togglePendingIntent)

        // Cargar saldo
        CoroutineScope(Dispatchers.IO).launch {
            val repository = InventoryRepository(context)
            val list = repository.getListInventory()

            val total = list.sumOf { it.price * it.quantity }
            val formatted = NumberFormat.getNumberInstance(Locale("es", "CO"))
                .apply { minimumFractionDigits = 2 }
                .format(total)

            CoroutineScope(Dispatchers.Main).launch {

                if (isVisible) {
                    views.setTextViewText(R.id.widget_total_value, "$ $formatted")
                    views.setImageViewResource(R.id.widget_visibility_icon, R.drawable.ic_eye_on)
                } else {
                    views.setTextViewText(R.id.widget_total_value, "$ ****")
                    views.setImageViewResource(R.id.widget_visibility_icon, R.drawable.ic_eye_off)
                }

                manager.updateAppWidget(widgetId, views)
            }
        }
    }
}+
