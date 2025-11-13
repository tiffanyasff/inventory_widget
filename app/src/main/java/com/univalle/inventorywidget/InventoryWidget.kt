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

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val repository = InventoryRepository(context)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val inventoryList = repository.getListInventory()
                    val totalItems = inventoryList.size
                    var totalQuantity = 0
                    var totalValue = 0.0

                    inventoryList.forEach { inventory ->
                        totalQuantity += inventory.quantity
                        totalValue += (inventory.price * inventory.quantity)
                    }

                    withContext(Dispatchers.Main) {
                        val views = RemoteViews(context.packageName, R.layout.inventory_widget)

                        views.setTextViewText(R.id.widget_total_items, totalItems.toString())
                        views.setTextViewText(R.id.widget_total_quantity, totalQuantity.toString())
                        views.setTextViewText(
                            R.id.widget_total_value,
                            String.format("$%.2f", totalValue)
                        )

                        val intent = Intent(context, MainActivity::class.java)
                        val pendingIntent = PendingIntent.getActivity(
                            context,
                            0,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                        views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)

                        val updateIntent = Intent(context, InventoryWidget::class.java).apply {
                            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
                        }
                        val updatePendingIntent = PendingIntent.getBroadcast(
                            context,
                            0,
                            updateIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                        views.setOnClickPendingIntent(R.id.widget_refresh_button, updatePendingIntent)

                        appWidgetManager.updateAppWidget(appWidgetId, views)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        val views = RemoteViews(context.packageName, R.layout.inventory_widget)
                        views.setTextViewText(R.id.widget_total_items, "0")
                        views.setTextViewText(R.id.widget_total_quantity, "0")
                        views.setTextViewText(R.id.widget_total_value, "$0.00")
                        appWidgetManager.updateAppWidget(appWidgetId, views)
                    }
                }
            }
        }
    }
}