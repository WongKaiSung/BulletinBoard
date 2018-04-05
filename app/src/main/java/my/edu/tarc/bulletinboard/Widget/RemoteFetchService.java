package my.edu.tarc.bulletinboard.Widget;

/**
 * Created by but on 12/2/2018.
 */
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import my.edu.tarc.bulletinboard.Class.Bulletin;
import my.edu.tarc.bulletinboard.Class.BulletinDetail;
import my.edu.tarc.bulletinboard.SaveSharedPreference;

public class RemoteFetchService extends Service {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private AQuery aquery;
    private String remoteJsonUrl = "https://rayl2c96.000webhostapp.com/KaiSung/Bulletin.php";

    public static ArrayList<Bulletin> listItemList;
    public static ArrayList<BulletinDetail> bulletinDetailList;
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    /*
     * Retrieve appwidget id from intent it is needed to update widget later
     * initialize our AQuery class
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID))
            appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        aquery = new AQuery(getBaseContext());
        fetchDataFromWeb();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * method which fetches data(json) from web aquery takes params
     * remoteJsonUrl = from where data to be fetched String.class = return
     * format of data once fetched i.e. in which format the fetched data be
     * returned AjaxCallback = class to notify with data once it is fetched
     */
    private void fetchDataFromWeb() {
        aquery.ajax(remoteJsonUrl, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String result, AjaxStatus status) {
                processResult(result);
                super.callback(url, result, status);
            }
        });
    }

    /**
     * Json parsing of result and populating ArrayList<Bulletin> as per json
     * data retrieved from the string
     */
    private void processResult(String result) {
        Log.i("Resutl",result);
        listItemList = new ArrayList<Bulletin>();
        bulletinDetailList = new ArrayList<BulletinDetail>();
        try {
            JSONArray jsonArray = new JSONArray(result);
            int length = jsonArray.length();

//then
            for (int i = 0; i < length; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String UserID = jsonObject.getString("UserID");

                if(UserID.equalsIgnoreCase(SaveSharedPreference.getUserID(getApplicationContext()))) {

                    Bulletin bulletin = new Bulletin();
                    BulletinDetail bulletinDetail = new BulletinDetail();
                    bulletin.setBulletinID(jsonObject.getString("BulletinID"));
                    bulletin.setBulletinTitle(jsonObject.getString("Title"));
                    bulletin.setDescription(jsonObject.getString("Remark"));

                    String dateStr = jsonObject.getString("PostDate");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date PostDate = sdf.parse(dateStr);
                    bulletin.setPostDate(PostDate);

                    bulletin.setPostedBy(jsonObject.getString("PostedBy"));
                    bulletin.setSendTo(jsonObject.getString("SendTo"));

                    bulletinDetail.setBookmarkStatus(jsonObject.getInt("BookmarkStatus"));
                    bulletinDetail.setReadStatus(jsonObject.getInt("ReadStatus"));

//                Log.i("Heading", bulletin.getBulletinTitle());
//                Log.i("Content", bulletin.getDescription());
                    listItemList.add(bulletin);
                    bulletinDetailList.add(bulletinDetail);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        populateWidget();
    }

    /**
     * Method which sends broadcast to WidgetProvider
     * so that widget is notified to do necessary action
     * and here action == WidgetProvider.DATA_FETCHED
     */
    private void populateWidget() {

        Intent widgetUpdateIntent = new Intent();
        widgetUpdateIntent.setAction(WidgetProvider.DATA_FETCHED);
        widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                appWidgetId);
        sendBroadcast(widgetUpdateIntent);

        this.stopSelf();
    }
}
