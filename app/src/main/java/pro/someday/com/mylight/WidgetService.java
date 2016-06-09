package pro.someday.com.mylight;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

/**
 * 专门用于处理桌面工具数据更新的后台服务
 * Created by jie on 16-2-15.
 */
public class WidgetService extends Service
{
    private Camera camera;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        int mAppWidgetId = 0;
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        else {
            int[] appWidgetIds = AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this, WidgetProvider.class));
            if(null != appWidgetIds)
            {
                for (int appWidgetId : appWidgetIds)
                {
                    mAppWidgetId = appWidgetId;
                }
            }
        }
        boolean isopen = intent.getBooleanExtra("isopen",false);
        RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.app_widget_layout);
        if(null == camera)
        {
            camera = Camera.open();
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);//开启
            camera.setParameters(parameters);//直接关闭parameters.setFlashMode(Parameters.FLASH_MODE_OFF);//关闭
//            Intent intentOpen = new Intent(this, WidgetService.class);
//            intentOpen.putExtra("isopen",false);
//            PendingIntent pendingIntent = PendingIntent.getService(this, 0, intentOpen, 0);
//            remoteViews.setOnClickPendingIntent(R.id.light, pendingIntent);
        }
        else
        {
            try {

                camera.release();
            }catch (Exception e)
            {

            }
//            Intent intentOpen = new Intent(this, WidgetService.class);
//            intentOpen.putExtra("isopen",true);
//            PendingIntent pendingIntent = PendingIntent.getService(this, 0, intentOpen, 0);
//            remoteViews.setOnClickPendingIntent(R.id.light, pendingIntent);
            stopSelf();

        }
        //AppWidgetManager.getInstance(this).updateAppWidget(mAppWidgetId, remoteViews);
        return Service.START_NOT_STICKY;

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

/*
    private class WidgetUpdateTask extends AsyncTask<Void, Void, WidgetEntity>
    {
        RemoteViews remoteViews;
        AppWidgetManager appWidgetManager;
        int mAppWidgetId;
        String homeTeamId;
        Context mContext;
        Handler mHandler;

        WidgetUpdateTask(int appWidgetId ,Context context,Handler handler){
            this.appWidgetManager = AppWidgetManager.getInstance(context);
            this.mAppWidgetId =  appWidgetId;

            homeTeamId = ((Widget)ChannelClassUtil.getChannelClass(Widget.class)).getWidgetHomeTeamId(context);
            mContext = context;
            mHandler = handler;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.app_widget_layout);
        }

        @Override
        protected WidgetEntity doInBackground(Void... params) {

            if(ValidateUtil.isEmpty(homeTeamId))
            {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        remoteViews.setTextViewText(R.id.tv_score, "暂未设置主队");
                        appWidgetManager.updateAppWidget(mAppWidgetId, remoteViews);
                    }
                });
                return null;
            }
            LogUtil.d("homeTeamId:"+mAppWidgetId);
            WidgetEntity widgetEntity = null;
            try
            {

                widgetEntity = AppWidgerModel.getWidgetEntity(homeTeamId);
                if(ValidateUtil.isNotEmpty(widgetEntity.match))
                {
                    final String team_A_id = widgetEntity.match.get(0).team_A_id;
                    final String team_B_id = widgetEntity.match.get(0).team_B_id;
                    final Bitmap teamABitmap = ImageUtil.getBitmapFromUrl(Constants.mapTeamLogo(team_A_id));
                    final Bitmap teamBBitmap = ImageUtil.getBitmapFromUrl(Constants.mapTeamLogo(team_B_id));
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            remoteViews.setImageViewBitmap(R.id.iv_team_a , teamABitmap );
                            if(team_A_id.equals(homeTeamId))
                                remoteViews.setImageViewBitmap(R.id.message_team_icon , teamABitmap );
                            else
                                remoteViews.setImageViewBitmap(R.id.message_team_icon , teamBBitmap );
                            remoteViews.setImageViewBitmap(R.id.iv_team_b , teamBBitmap );
                        }
                    });
                }
                return widgetEntity;
            } catch (NetworkErrorException e) {
                e.printStackTrace();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(WidgetEntity widgetEntity) {
            super.onPostExecute(widgetEntity);
            try {
                if(null != widgetEntity)
                {
                    //点击背景事件
                    Intent intent1 = new Intent(mContext, StartPagesActivity.class);
                    PendingIntent pendingIntent1 = PendingIntent.getActivity(mContext, 0, intent1, 0);
                    remoteViews.setOnClickPendingIntent(R.id.weibo_layout, pendingIntent1);

                    if(ValidateUtil.isNotEmpty(widgetEntity.match))
                    {
                        remoteViews.setTextViewText(R.id.tv_team_a, widgetEntity.match.get(0).team_A_name);
                        remoteViews.setTextViewText(R.id.tv_team_b, widgetEntity.match.get(0).team_B_name);
                        remoteViews.setTextViewText(R.id.tv_date, widgetEntity.match.get(0).date_utc);

                        //点击比赛事件
                        Intent intentMatchInfoMore = new Intent(mContext, MatchInfoMoreMainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(MatchInfoMoreMainActivity.Serializable_Key_Match_Id, widgetEntity.match.get(0).match_id);
                        intentMatchInfoMore.putExtra("Bundle", bundle);
                        PendingIntent pendingIntentHomeTeam = PendingIntent.getActivities(mContext, 0, new Intent[]{intent1,intentMatchInfoMore}, 0);
                        remoteViews.setOnClickPendingIntent(R.id.match_layout, pendingIntentHomeTeam);
                        String score = MatchUtil.getScore(widgetEntity.match.get(0));
                        if (ValidateUtil.isNotEmpty(score))
                        {
                            remoteViews.setTextViewText(R.id.tv_score,score);
                        } else {
                            if ("Postponed".equals(widgetEntity.match.get(0).status)){
                                remoteViews.setTextViewText(R.id.tv_score, "延期");
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    remoteViews.setTextViewTextSize(R.id.tv_score, TypedValue.COMPLEX_UNIT_DIP,12f);
                                }
                            }else {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    remoteViews.setTextViewTextSize(R.id.tv_score, TypedValue.COMPLEX_UNIT_DIP,18f);
                                }
                                remoteViews.setTextViewText(R.id.tv_score, "V.S");
                            }
                        }
                    }
                    //点击刷新按钮事件
                    Intent intent = new Intent(mContext, WidgetService.class);
                    PendingIntent pendingIntent = PendingIntent.getService(mContext, 0, intent, 0);
                    remoteViews.setOnClickPendingIntent(R.id.refresh, pendingIntent);



                    remoteViews.setTextViewText(R.id.tv_new_content, widgetEntity.weibo.statuses.get(0).text);
                    appWidgetManager.updateAppWidget(mAppWidgetId, remoteViews);
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            stopSelf();
        }
    }
*/
}
