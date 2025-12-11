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

                val target = if (hasSession) {
                    // Criterio 14: navegación directa a HomeInventory si hay sesión
                    Intent(context, MainActivity::class.java)
                        .putExtra("openHomeInventory", true)
                } else {
                    // Criterio 13: sin sesión → ir al login
                    Intent(context, LoginActivity::class.java)
                        .putExtra("fromWidget", true)
                }

                target.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(target)
            }

            ACTION_TOGGLE_VISIBILITY -> {
                val shared = context.getSharedPreferences("shared", Context.MODE_PRIVATE)
                val hasSession = shared.getBoolean("isLoggedIn", false)

                if (!hasSession) {
                    // Criterio 10: ojo sin sesión → login y volver al widget
                    val loginIntent = Intent(context, LoginActivity::class.java)
                    loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    loginIntent.putExtra("fromWidgetEye", true)
                    context.startActivity(loginIntent)
                    return
                }

                // Criterio 7: alternar visibilidad del saldo
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

        // ---------- Gestionar Inventario / Lápiz ----------
        val openAppIntent = Intent(context, InventoryWidget::class.java).apply {
            action = ACTION_OPEN_APP
        }

        val openAppPending = PendingIntent.getBroadcast(
            context,
            200,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        views.setOnClickPendingIntent(R.id.widget_manage_text, openAppPending)
        views.setOnClickPendingIntent(R.id.widget_manage_icon, openAppPending)

        // ---------- Ojo mostrar/ocultar ----------
        val toggleIntent = Intent(context, InventoryWidget::class.java).apply {
            action = ACTION_TOGGLE_VISIBILITY
        }

        val togglePending = PendingIntent.getBroadcast(
            context,
            201,
            toggleIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        views.setOnClickPendingIntent(R.id.widget_visibility_icon, togglePending)

        // ---------- Cargar saldo ----------
        CoroutineScope(Dispatchers.IO).launch {
            val repo = InventoryRepository(context)
            val list = repo.getListInventory()
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
}
