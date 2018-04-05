package my.edu.tarc.bulletinboard.Widget;

/**
 * Created by but on 12/2/2018.
 */
import java.util.ArrayList;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import my.edu.tarc.bulletinboard.Class.Bulletin;
import my.edu.tarc.bulletinboard.Class.BulletinDetail;
import my.edu.tarc.bulletinboard.R;

/**
 * If you are familiar with Adapter of ListView,this is the same as adapter
 * with few changes
 * here it now takes RemoteFetchService ArrayList<Bulletin> for data
 * which is a static ArrayList
 * and this example won't work if there are multiple widgets and
 * they update at same time i.e they modify RemoteFetchService ArrayList at same
 * time.
 * For that use Database or other techniquest
 */
public class BulletinProvider implements RemoteViewsFactory {
    private ArrayList<Bulletin> listItemList = new ArrayList<Bulletin>();
    private ArrayList<BulletinDetail> bulletinDetailList = new ArrayList<BulletinDetail>();
    private Context context = null;
    private int appWidgetId;
    public int count;
    public BulletinProvider(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        populateListItem();
    }

    private void populateListItem() {
        if(RemoteFetchService.listItemList !=null ) {
            listItemList = (ArrayList<Bulletin>) RemoteFetchService.listItemList.clone();
            bulletinDetailList = (ArrayList<BulletinDetail>) RemoteFetchService.bulletinDetailList.clone();
        }else
            listItemList = new ArrayList<Bulletin>();

    }

    @Override
    public int getCount() {
        return listItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
     *Similar to getView of Adapter where instead of View
     *we return RemoteViews
     *
     */
    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.list_row);
        Bulletin bulletin = listItemList.get(position);
        BulletinDetail bulletinDetail = bulletinDetailList.get(position);

        if (bulletinDetail.getReadStatus() == 0){
//            count = count + 1;
//            remoteView.setTextViewText(R.id.buttonWidget, count+"");
        remoteView.setInt(R.id.textViewTitleWidget, "setTextColor", Color.RED);
        }else {
            remoteView.setInt(R.id.textViewTitleWidget, "setTextColor", Color.BLUE);
        }

        remoteView.setTextViewText(R.id.textViewPostedBy, bulletin.getPostedBy());
        remoteView.setTextViewText(R.id.textViewTitleWidget, bulletin.getBulletinTitle());
        remoteView.setTextViewText(R.id.textViewDetails, bulletin.getDescription());
        remoteView.setTextViewText(R.id.textViewDate, String.format("%1$tY %1$tb %1$td",bulletin.getPostDate()));

//        remoteView.setOnClickPendingIntent(R.id.textViewTitle,new Intent(NativeActivity));
        return remoteView;
    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {
    }

}
