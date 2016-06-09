package pro.someday.com.mylight;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

/**
 * 小控件被创建
 * Created by jie on 16-1-14.
 */
public class AppWidgetActivityConfigure extends Activity
{
    private int mAppWidgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.app_widget_layout);

        Intent intentOpen = new Intent(this, WidgetService.class);
        intentOpen.putExtra("isopen",true);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intentOpen, 0);
        views.setOnClickPendingIntent(R.id.light, pendingIntent);


        appWidgetManager.updateAppWidget(mAppWidgetId, views);
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);

        finish();
    }
}
