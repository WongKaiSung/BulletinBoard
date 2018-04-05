package my.edu.tarc.bulletinboard.Activity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import my.edu.tarc.bulletinboard.Adapter.BookmarkAdapter;
//import my.edu.tarc.bulletinboard.Fragment.BookmarkFragment;
import my.edu.tarc.bulletinboard.Adapter.BulletinAdapter;
import my.edu.tarc.bulletinboard.Class.Bulletin;
import my.edu.tarc.bulletinboard.Class.BulletinDetail;
import my.edu.tarc.bulletinboard.Class.EventBulletin;
import my.edu.tarc.bulletinboard.Class.EventRegistration;
import my.edu.tarc.bulletinboard.Fragment.HomeFragment;
import my.edu.tarc.bulletinboard.MQTT.MqttHelper;
import my.edu.tarc.bulletinboard.R;
import my.edu.tarc.bulletinboard.SaveSharedPreference;

public class MainActivity extends AppCompatActivity{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    //private static final String = "MainActivity";
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private String TAG = "MainActivity";
    public static boolean is_in_action_mode = false;
    MqttHelper mqttHelper;
    ArrayList<Bulletin> BulletinArrayList = new ArrayList<Bulletin>();
    ArrayList<BulletinDetail> BulletinDetailArrayList = new ArrayList<BulletinDetail>();
    ArrayList<EventRegistration> EventRegistrationArrayList = new ArrayList<EventRegistration>();
    ArrayList<EventBulletin> EventBulletinArrayList = new ArrayList<EventBulletin>();

    public static TextView textViewCount,textViewTitle;
    public static Toolbar toolbar;
    public static CheckBox checkBox;
    public static ImageButton imageButton;
    public String bulletinID;
//    private static String Post_DEL_URL = "https://cash-on-wise.000webhostapp.com/Bulletin/HideBulletin.php";
//    private static String Post_Bookmark_URL = "https://cash-on-wise.000webhostapp.com/Bulletin/Bookmark.php";
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    BulletinAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (SaveSharedPreference.getUserID(MainActivity.this).length() == 0) {
            Intent goToLogin = new Intent(this, LoginActivity.class);
            startActivity(goToLogin);
        } else {
            // Stay at the current activity.
            assignAppWidgetId();
//            startMqtt(this);
            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
            textViewCount = findViewById(R.id.textViewCount);
            textViewTitle = findViewById(R.id.textViewTitle);
            imageButton = findViewById(R.id.imageButton);
            checkBox = findViewById(R.id.checkBox);

            textViewCount.setVisibility(View.GONE);
            checkBox.setVisibility(View.GONE);
            imageButton.setVisibility(View.GONE);
            imageButton.setOnClickListener(view -> clearActionMode());
            // Set up the ViewPager with the sections adapter.
            mViewPager =  findViewById(R.id.container);
            //mViewPager.setAdapter(mSectionsPagerAdapter);
            setupViewPages(mViewPager);

            TabLayout tabLayout =  findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);
            //mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            //tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

//            FloatingActionButton fab = findViewById(R.id.fab);
//            fab.setOnClickListener(view -> {
//                new AlertDialog.Builder(this)
//                        .setTitle("Exit Bulletin")
//                        .setMessage("Continue to exit?")
//                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
//                            finish();
//                        })
//                        .setNegativeButton(android.R.string.no, null).show();
////                SaveSharedPreference.clearUserName(getApplication());
////                Intent goToLogin = new Intent(getApplication(), LoginActivity.class);
////                startActivity(goToLogin);
////                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                            .setAction("Action", null).show();
//            });

        }
    }
    private void setMessageNotification(@NonNull String topic, @NonNull String msg) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_message_black_24dp)
                        .setContentTitle(topic)
                        .setContentText(msg);
        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(100, mBuilder.build());
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);

    }
    private void assignAppWidgetId() {
        Bundle extras = getIntent().getExtras();
        if (extras != null)
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
    }
    public void setupViewPages(ViewPager viewPager){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(),"Home");
//        adapter.addFragment(new BookmarkFragment(),"Bookmark");
        //adapter.addFragment(new BinFragment(),"Bin");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
//        getMenuInflater().inflate(R.menu.menu_main,menu);
//        MenuItem menuItem = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
//        searchView.setOnQueryTextListener(this);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

            if (id == R.id.action_delete){
//            is_in_action_mode = false;
//
//            new AlertDialog.Builder(this)
//                    .setTitle("Delete Bulletin")
//                    .setMessage("Are you sure want to delete "+BulletinAdapter.counter+ " bulletin?")
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
//                        for (int i = 0 ;i < BulletinAdapter.counter;i++) {
//                            bulletinID = BulletinAdapter.bulletinsSelectionList.get(i).getBulletinID();
//                            requestDeleteLink();
////                            makeServiceCall(getApplicationContext(), Post_DEL_URL, SaveSharedPreference.getUserID(getApplication()), bulletinID);
//                        }
//                        Toast.makeText(getApplicationContext(), "Bulletin deleted!", Toast.LENGTH_SHORT).show();
//                        clearActionMode();
//                    })
//                    .setNegativeButton(android.R.string.no, null).show();
        }else if (id == R.id.action_Bookmark){
//            is_in_action_mode = false;
//            new AlertDialog.Builder(this)
//                    .setTitle("Bookmark Bulletin")
//                    .setMessage("Are you sure want to bookmark "+BulletinAdapter.counter+ " bulletin?")
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
//                        mqttHelper.subscribeToTopic("MY/TARUC/BBS/00005016");
//
//                            for (int i = 0 ;i < BulletinAdapter.counter;i++) {
//                                bulletinID = BulletinAdapter.bulletinsSelectionList.get(i).getBulletinID();
//                                requestBookmarkLink();
////                                Toast.makeText(getApplicationContext(), "1!", Toast.LENGTH_SHORT).show();
//
////                                makeServiceCall(getApplicationContext(), Post_Bookmark_URL, SaveSharedPreference.getUserID(getApplication()), bulletinID);
//                            }
//                        clearActionMode();
//                    })
//                    .setNegativeButton(android.R.string.no, null).show();

        }else if (id == R.id.action_logout){
                new AlertDialog.Builder(this)
                        .setTitle("Logout Bulletin")
                        .setMessage("Continue to logout?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                            SaveSharedPreference.clearUserName(this);
                            Intent goToLogin = new Intent(this, LoginActivity.class);
                            startActivity(goToLogin);
                        })
                        .setNegativeButton(android.R.string.no, null).show();

        }

        return super.onOptionsItemSelected(item);
    }
    private void startMqtt(final Context context) {
        mqttHelper = new MqttHelper(context);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debug", mqttMessage.toString());
//                setMessageNotification("New Bulletin", new String(mqttMessage.getPayload()));
//                onRefresh();
//                testData(mqttMessage.toString(),context);
                getReplyMessage(mqttMessage.toString(), context);

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }
    private void requestBookmarkLink() {
        try {
            mqttHelper.publish("SC_BULLETIN_BOOKMARK,"+SaveSharedPreference.getUserID(getApplication())+",MY/TARUC/BBS/00005016");

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    private void requestDeleteLink() {
        try {
            mqttHelper.publish("SC_BULLETIN_DELETE,"+SaveSharedPreference.getUserID(getApplication())+",MY/TARUC/BBS/00005017");

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public void getReplyMessage(String data, Context context) throws Exception {
        String dataFromMQTT = data;
        String[] dataAfterProcess = dataFromMQTT.split(",");

        String topicFromMQTT = dataAfterProcess[0];
        String msgFromMQTT = dataAfterProcess[1];
        String idFromMQTT = dataAfterProcess[2];
//        JSONObject jsonMsg = new JSONObject(data.toString());

        //only looking for receive bulletin command
        if ((SaveSharedPreference.getUserID(context).equalsIgnoreCase(idFromMQTT))) {
            if (topicFromMQTT.equalsIgnoreCase("MY/TARUC/BBS/00005016")) {//bookmark bulletin
                makeServiceCall(this, msgFromMQTT, SaveSharedPreference.getUserID(this), bulletinID);
//                Toast.makeText(context, "receivedCMD!", Toast.LENGTH_SHORT).show();

            } else if (topicFromMQTT.equalsIgnoreCase("MY/TARUC/BBS/00005017")) {//delete bulletin
                makeServiceCall(this, msgFromMQTT, SaveSharedPreference.getUserID(this), bulletinID);
//                Toast.makeText(context, "receivedCMD!", Toast.LENGTH_SHORT).show();

            }

        }
    }
    public void clearActionMode(){
        is_in_action_mode = false;
        BulletinAdapter.clickStatus = true;
        BulletinAdapter.selectAllStatus = false;
//        BookmarkAdapter.clickStatus = true;
//        BookmarkAdapter.selectAllStatus = false;
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        textViewCount.setVisibility(View.GONE);
        imageButton.setVisibility(View.GONE);
        checkBox.setChecked(false);
        checkBox.setVisibility(View.GONE);
        textViewCount.setText("0 item");
        textViewTitle.setVisibility(View.VISIBLE);
        BulletinAdapter.counter = 0;
        BulletinAdapter.bulletinsSelectionList.clear();
        HomeFragment.bulletinAdapter.notifyDataSetChanged();
//        BookmarkFragment.bulletinAdapter.notifyDataSetChanged();
//        BookmarkAdapter.bookmark_counter = 0;
//        BookmarkAdapter.bulletinsSelectionList.clear();
    }

    @Override
    public void onBackPressed() {
        clearActionMode();
        //super.onBackPressed();
    }
    public void makeServiceCall(final Context context, String url,final String UserID,final String BulletinID) {
        //mPostCommentResponse.requestStarted();
        RequestQueue queue = Volley.newRequestQueue(context);

        //Send data
        try {
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    url,
                    response -> {
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response);
                            int success = jsonObject.getInt("success");
                            String message = jsonObject.getString("message");
                            if (success==0) {
                                //Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }else{
                                //Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> Toast.makeText(context, "Error. " + error.toString(), Toast.LENGTH_LONG).show()) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("UserID", UserID);
                    params.put("BulletinID", BulletinID);

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }
            };
            queue.add(postRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mfragmentList = new ArrayList<>();
        private final List<String> mfragmentTitleList = new ArrayList<>();

        public void addFragment(Fragment fragment, String title){
            mfragmentList.add(fragment);
            mfragmentTitleList.add(title);
        }
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return mfragmentList.get(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return mfragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mfragmentTitleList.get(position);
        }
    }


}
