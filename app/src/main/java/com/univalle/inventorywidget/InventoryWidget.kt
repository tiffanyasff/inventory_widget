package com.univalle.inventorywidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
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
        private const val ACTION_OPEN_LOGIN = "ACTION_OPEN_LOGIN"
        private const val ACTION_TOGGLE_VISIBILITY = "ACTION_TOGGLE_VISIBILITY"
        private var isVisible = false
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

            ACTION_OPEN_LOGIN -> {
                val activityIntent = Intent(context, MainActivity::class.java)
                activityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                activityIntent.putExtra("goToLogin", true)
                context.startActivity(activityIntent)
            }

            ACTION_TOGGLE_VISIBILITY -> {
                isVisible = !isVisible
                val manager = AppWidgetManager.getInstance(context)
                val ids = manager.getAppWidgetIds(
                    context.getComponentName<InventoryWidget>()
                )
                ids.forEach {
                    updateWidget(context, manager, it)
                }
            }
        }
    }

    private fun <T> Context.getComponentName(): android.content.ComponentName {
        return android.content.ComponentName(this, InventoryWidget::class.java)
    }

    private fun updateWidget(context: Context, manager: AppWidgetManager, widgetId: Int) {

        val views = RemoteViews(context.packageName, R.layout.inventory_widget)

        // ---- CLICK EN GESTIONAR INVENTARIO ----
        val loginIntent = Intent(context, InventoryWidget::class.java).apply {
            action = ACTION_OPEN_LOGIN
        }

        val loginPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            loginIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        views.setOnClickPendingIntent(R.id.widget_manage_text, loginPendingIntent)
        views.setOnClickPendingIntent(R.id.widget_manage_icon, loginPendingIntent)

        // ---- CLICK EN ICONO DEL OJO ----
        val toggleIntent = Intent(context, InventoryWidget::class.java).apply {
            action = ACTION_TOGGLE_VISIBILITY
        }

        val togglePendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            toggleIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        views.setOnClickPendingIntent(R.id.widget_visibility_icon, togglePendingIntent)

        // ---- CALCULAR TOTAL ----
        CoroutineScope(Dispatchers.IO).launch {
            val repository = InventoryRepository(context)
            val list = repository.getListInventory()

            val total = list.sumOf { it.price * it.quantity }

            val formatted = NumberFormat
                .getNumberInstance(Locale("es", "CO"))
                .apply { minimumFractionDigits = 2 }
                .format(total)

            CoroutineScope(Dispatchers.Main).launch {

                // MOSTRAR / OCULTAR SALDO
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
