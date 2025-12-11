package com.univalle.inventorywidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.univalle.inventorywidget.repository.InventoryRepository
import com.univalle.inventorywidget.view.LoginActivity
import com.univalle.inventorywidget.view.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class InventoryWidget : AppWidgetProvider() {

    companion object {
        private const val ACTION_TOGGLE_VISIBILITY = "ACTION_TOGGLE_VISIBILITY"
        private const val ACTION_OPEN_APP = "ACTION_OPEN_APP"
        private var isVisible = false
    }

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { id -> updateWidget(context, manager, id) }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {

            ACTION_OPEN_APP -> {
                val shared = context.getSharedPreferences("shared", Context.MODE_PRIVATE)
                val hasSession = shared.getBoolean("isLoggedIn", false)

                val targetIntent = if (hasSession) {
                    // Ir directo al Home desde el widget
                    Intent(context, MainActivity::class.java)
                        .putExtra("openHomeInventory", true)
                } else {
                    // Ir al login si no hay sesión
                    Intent(context, LoginActivity::class.java)
                        .putExtra("fromWidget", true)
                }

                targetIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(targetIntent)
            }

            ACTION_TOGGLE_VISIBILITY -> {

                val shared = context.getSharedPreferences("shared", Context.MODE_PRIVATE)
                val hasSession = shared.getBoolean("isLoggedIn", false)

                if (!hasSession) {
                    val loginIntent = Intent(context, LoginActivity::class.java)
                    loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    loginIntent.putExtra("fromWidget", true)
                    context.startActivity(loginIntent)
                    return
                }

                // Si hay sesión → alternar visibilidad
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

        // ---------- CLICK EN "GESTIONAR INVENTARIO" Y LÁPIZ ----------
        val openAppIntent = Intent(context, InventoryWidget::class.java).apply {
            action = ACTION_OPEN_APP
        }

        val openAppPendingIntent = PendingIntent.getBroadcast(
            context,
            100,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        views.setOnClickPendingIntent(R.id.widget_manage_text, openAppPendingIntent)
        views.setOnClickPendingIntent(R.id.widget_manage_icon, openAppPendingIntent)

        // ---------- CLICK EN EL OJO ----------
        val toggleIntent = Intent(context, InventoryWidget::class.java).apply {
            action = ACTION_TOGGLE_VISIBILITY
        }

        val togglePendingIntent = PendingIntent.getBroadcast(
            context,
            101,
            toggleIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        views.setOnClickPendingIntent(R.id.widget_visibility_icon, togglePendingIntent)

        // ---------- CARGAR SALDO ----------
        CoroutineScope(Dispatchers.IO).launch {
            val repository = InventoryRepository(context)
            val list = repository.getListInventory()

            val total = list.sumOf { it.price * it.quantity }

            val formatted = NumberFormat
                .getNumberInstance(Locale("es", "CO"))
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
}
