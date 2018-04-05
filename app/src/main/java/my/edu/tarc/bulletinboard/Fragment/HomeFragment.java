package my.edu.tarc.bulletinboard.Fragment;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import my.edu.tarc.bulletinboard.Activity.BulletinDetails;
import my.edu.tarc.bulletinboard.Activity.MainActivity;
import my.edu.tarc.bulletinboard.Adapter.BulletinAdapter;
import my.edu.tarc.bulletinboard.Adapter.RecyclerItemClickListener;
import my.edu.tarc.bulletinboard.Class.Bulletin;
import my.edu.tarc.bulletinboard.Class.BulletinDetail;
import my.edu.tarc.bulletinboard.Class.EventBulletin;
import my.edu.tarc.bulletinboard.Class.EventRegistration;
import my.edu.tarc.bulletinboard.Class.Student;
import my.edu.tarc.bulletinboard.MQTT.MqttHelper;
import my.edu.tarc.bulletinboard.R;
import my.edu.tarc.bulletinboard.SaveSharedPreference;

/**
 * Created by but on 17/1/2018.
 */
public class HomeFragment extends android.support.v4.app.Fragment {
    private static final String TAG = "HomeFragment";
    RequestQueue queue;
    private ProgressDialog pDialog;
    public static SwipeRefreshLayout mSwipeRefreshLayout;
//    private static String GET_URL = "https://cash-on-wise.000webhostapp.com/Bulletin/Bulletin.php";
    private static String URL = "https://rayl2c96.000webhostapp.com/KaiSung/insertBulletinDetail.php";
    static RecyclerView recyclerView;
    public static RecyclerView.Adapter adapter;
    public static BulletinAdapter bulletinAdapter;
    static ArrayList<Bulletin> BulletinArrayList = new ArrayList<Bulletin>();
    ArrayList<BulletinDetail> BulletinDetailArrayList = new ArrayList<BulletinDetail>();
    ArrayList<EventRegistration> EventRegistrationArrayList = new ArrayList<EventRegistration>();
    ArrayList<EventBulletin> EventBulletinArrayList = new ArrayList<EventBulletin>();
    ArrayList<Student> StudentArrayList = new ArrayList<Student>();
    Spinner spinner;
    SearchView searchView;
    MqttHelper mqttHelper;
    public MqttAndroidClient mqttAndroidClient;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        if (!isConnected()) {
            Toast.makeText(getActivity().getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
        }


        bulletinAdapter = new BulletinAdapter(BulletinArrayList, BulletinDetailArrayList, EventRegistrationArrayList, EventBulletinArrayList, this.getActivity());
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setHasFixedSize(true);
//        recyclerView.addOnItemTouchListener(
//                new RecyclerItemClickListener(getContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override public void onItemClick(View view, int position) {
//                    Bulletin bulletin = BulletinArrayList.get(position);
//                    BulletinDetail bulletinDetail = BulletinDetailArrayList.get(position);
////                    Toast.makeText(context,position+bulletins.get(position).getBulletinID(),Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(getContext(), BulletinDetails.class);
//                    intent.putExtra("bulletin", bulletin);
//                    intent.putExtra("bulletinDetail", bulletinDetail);
//                    getActivity().startActivity(intent);
//                    }
//
//                    @Override public void onLongItemClick(View view, int position) {
//                        // do whatever
//                    }
//                })
//        );
        spinner = view.findViewById(R.id.spinner);
        searchView = view.findViewById(R.id.searchView);
        searchView.setQueryHint("Search");
        spinner.setSelection(0,false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSwipeRefreshLayout.setRefreshing(true);

                if (position == 0) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }else if (position == 1) {
                    requestData();
                } else if (position == 2) {
                    sortByTitle();
                } else if (position == 3) {
                    sortByTitleDesc();
                } else if (position == 4) {
                    sortByPostBy();
                } else if (position == 5) {
                    sortByPostByDesc();
                }
                bulletinAdapter.notifyDataSetChanged();
//                recyclerView.setAdapter(bulletinAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                spinner.setVisibility(view.GONE);
                newText = newText.toLowerCase();
                ArrayList<Bulletin> newList = new ArrayList<>();
                for (Bulletin bulletin : BulletinArrayList) {
                    String Title = bulletin.getBulletinTitle().toLowerCase();
                    String Description = bulletin.getDescription().toLowerCase();

                    if (Title.contains(newText) || Description.contains(newText)) {
                        newList.add(bulletin);

                    }
                }
                bulletinAdapter.setFilter(newList);
                recyclerView.setAdapter(bulletinAdapter);
//                bulletinAdapter.notifyDataSetChanged();
                return true;
            }
        });
        searchView.setOnCloseListener(() -> {
            spinner.setVisibility(view.VISIBLE);
            return false;
        });
        setupSort();
        mSwipeRefreshLayout = view.findViewById(R.id.swipeContainer);
        mSwipeRefreshLayout.setOnRefreshListener(() -> requestData());
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
//                android.R.color.holo_green_dark,
//                android.R.color.holo_orange_dark,
//                android.R.color.holo_blue_dark
                R.color.colorBlue1,
                R.color.colorBlue2,
                R.color.colorBlue3
        );

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
//        mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(true));
//        startMqtt(getContext());
        return view;
    }

    private void requestData() {
        try {
            mqttHelper.publish("SC_BULLETIN_GET_BULLETINS," + SaveSharedPreference.getUserID(getContext()) + ",MY/TARUC/BBS/00005001");
        } catch (MqttException e) {
            e.printStackTrace();
        }
        mqttHelper.subscribeToTopic("MY/TARUC/BBS/00005001");
    }
    private void startMqtt(final Context context) {
        mqttHelper = new MqttHelper(getContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                requestData();
//                Toast.makeText(getContext(),"Connected to MQTT",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void connectionLost(Throwable throwable) {
//                Toast.makeText(getContext(),"Lost",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debug", mqttMessage.toString());
//                setMessageNotification("New Bulletin", new String(mqttMessage.getPayload()));
//                onRefresh();
//                testData(mqttMessage.toString(),context);

                loadBulletinData(mqttMessage.toString(), context);

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }
    private void testData(String data, Context context) throws Exception {

        Toast.makeText(context, data.toString(), Toast.LENGTH_SHORT).show();

        JSONObject jsonMsg = new JSONObject(data);
        Toast.makeText(context, data, Toast.LENGTH_SHORT).show();
        JSONArray arr = jsonMsg.getJSONArray(data);
        BulletinArrayList.clear();
        BulletinDetailArrayList.clear();
        EventRegistrationArrayList.clear();
        EventBulletinArrayList.clear();
        for (int i = 0; i < arr.length(); ++i) {
            JSONObject bulletinTable = arr.getJSONObject(i);
            Toast.makeText(context, bulletinTable.getString("UserID"), Toast.LENGTH_SHORT).show();
            String UserID = bulletinTable.getString("UserID");
            //Toast.makeText(context,bulletinTable.getString("adminUser"),Toast.LENGTH_SHORT).show();
            if (UserID.matches(SaveSharedPreference.getUserID(getActivity().getApplicationContext()))) {
                Bulletin bulletin = new Bulletin();
                BulletinDetail bulletinDetail = new BulletinDetail();
                EventRegistration eventRegistration = new EventRegistration();
                EventBulletin eventBulletin = new EventBulletin();

                bulletin.setBulletinTitle(bulletinTable.getString("Title"));
                bulletin.setDescription(bulletinTable.getString("Remark"));

                String dateStr = bulletinTable.getString("PostDate");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date PostDate = sdf.parse(dateStr);
                bulletin.setPostDate(PostDate);

                bulletin.setPostedBy(bulletinTable.getString("PostedBy"));
                bulletin.setSendTo(bulletinTable.getString("SendTo"));
                bulletin.setBulletinID(bulletinTable.getString("BulletinID"));
                bulletin.setEventBulletinID(bulletinTable.getString("EventBulletinID"));
                bulletin.setEventRegistrationID(bulletinTable.getString("EventRegistrationID"));

                bulletinDetail.setBookmarkStatus(bulletinTable.getInt("BookmarkStatus"));
                bulletinDetail.setReadStatus(bulletinTable.getInt("ReadStatus"));
                bulletinDetail.setDeleteStatus(bulletinTable.getInt("DeleteStatus"));


                String dateRegistrationStartDate = bulletinTable.getString("RegistrationStartDate");
                SimpleDateFormat sdfRegistrationStartDate = new SimpleDateFormat("yyyy-MM-dd");
                Date RegistrationStartDate = sdfRegistrationStartDate.parse(dateRegistrationStartDate);

                String dateRegistrationDueDate = bulletinTable.getString("RegistrationDueDate");
                SimpleDateFormat sdfRegistrationDueDate = new SimpleDateFormat("yyyy-MM-dd");
                Date RegistrationDueDate = sdfRegistrationDueDate.parse(dateRegistrationDueDate);


                String dateEventStartDate = bulletinTable.getString("EventStartDate");
                SimpleDateFormat sdfEventStartDate = new SimpleDateFormat("yyyy-MM-dd");
                Date EventStartDate = sdfEventStartDate.parse(dateEventStartDate);

                String dateEventDueDate = bulletinTable.getString("EventDueDate");
                SimpleDateFormat sdfEventDueDate = new SimpleDateFormat("yyyy-MM-dd");
                Date EventDueDate = sdfEventDueDate.parse(dateEventDueDate);

                String datePaymentDueDate = bulletinTable.getString("PaymentDueDate");
                SimpleDateFormat sdfPaymentDueDate = new SimpleDateFormat("yyyy-MM-dd");
                Date PaymentDueDate = sdfPaymentDueDate.parse(datePaymentDueDate);

                eventRegistration.setRegistrationStartDate(RegistrationStartDate);
                eventRegistration.setRegistrationDueDate(RegistrationDueDate);
                eventRegistration.setLINK(bulletinTable.getString("LINK"));

                eventBulletin.setEventStartDate(EventStartDate);
                eventBulletin.setEventDueDate(EventDueDate);
                eventBulletin.setPaymentDueDate(PaymentDueDate);
                eventBulletin.setFees(bulletinTable.getDouble("Fees"));
                eventBulletin.setAttachment(bulletinTable.getString("Attachment"));
                BulletinArrayList.add(bulletin);
                BulletinDetailArrayList.add(bulletinDetail);
                EventRegistrationArrayList.add(eventRegistration);
                EventBulletinArrayList.add(eventBulletin);
            }

        }
        recyclerView.setAdapter(bulletinAdapter);
        mSwipeRefreshLayout.setRefreshing(false);
    }
    private void loadBulletinData(String dataFromMQTT, Context context) throws Exception {


        String[] dataAfterProcess = dataFromMQTT.split(",");

        String topicFromMQTT = dataAfterProcess[0];
        String msgFromMQTT = dataAfterProcess[1];
        String idFromMQTT = dataAfterProcess[2];

        if (idFromMQTT.equalsIgnoreCase("MY/TARUC/BBS/00005099")) {
            setMessageNotification("New Bulletins","You have a new message!");
            mqttHelper.unSubscribeToTopic(topicFromMQTT);
        }
        if ((SaveSharedPreference.getUserID(context).equalsIgnoreCase(idFromMQTT))) {
            if (topicFromMQTT.equalsIgnoreCase("MY/TARUC/BBS/00005001")) {
                downloadBulletin(context, msgFromMQTT);
                mqttHelper.unSubscribeToTopic(topicFromMQTT);
            } else if (topicFromMQTT.equalsIgnoreCase("MY/TARUC/BBS/00005008")) {
                downloadBulletin(context, msgFromMQTT);
                mqttHelper.unSubscribeToTopic(topicFromMQTT);
            } else if (topicFromMQTT.equalsIgnoreCase("MY/TARUC/BBS/00005009")) {
                downloadBulletin(context, msgFromMQTT);
                mqttHelper.unSubscribeToTopic(topicFromMQTT);
            } else if (topicFromMQTT.equalsIgnoreCase("MY/TARUC/BBS/00005010")) {
                downloadBulletin(context, msgFromMQTT);
                mqttHelper.unSubscribeToTopic(topicFromMQTT);
            } else if (topicFromMQTT.equalsIgnoreCase("MY/TARUC/BBS/00005011")) {
                downloadBulletin(context, msgFromMQTT);
                mqttHelper.unSubscribeToTopic(topicFromMQTT);
            }
        }
//        if ((SaveSharedPreference.getUserID(context).equalsIgnoreCase(idFromMQTT))) {
//            if (topicFromMQTT.equalsIgnoreCase("SC_BULLETIN_GET_BULLETINS")) {
//                downloadBulletin(context, msgFromMQTT);
//            } else if (topicFromMQTT.equalsIgnoreCase("SC_BULLETIN_SORTBY_POSTBY")) {
//                downloadBulletin(context, msgFromMQTT);
//            } else if (topicFromMQTT.equalsIgnoreCase("SC_BULLETIN_SORTBY_POSTBYDESC")) {
//                downloadBulletin(context, msgFromMQTT);
//            } else if (topicFromMQTT.equalsIgnoreCase("SC_BULLETIN_SORTBY_TITLE")) {
//                downloadBulletin(context, msgFromMQTT);
//            } else if (topicFromMQTT.equalsIgnoreCase("SC_BULLETIN_SORTBY_TITLEDESC")) {
//                downloadBulletin(context, msgFromMQTT);
//            }
//        }
    }
    private void setMessageNotification(@NonNull String topic, @NonNull String msg) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getContext())
                        .setSmallIcon(R.drawable.ic_message_black_24dp)
                        .setContentTitle(topic)
                        .setContentText(msg);
        Intent resultIntent = new Intent(getContext(), MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(100, mBuilder.build());
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);

    }
    private void setupSort() {
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.sort_types,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


    }
    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }
    private void downloadBulletin(final Context context, String url) {
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                url,
                response -> {
                    try {
                        BulletinArrayList.clear();
                        BulletinDetailArrayList.clear();
//                        EventRegistrationArrayList.clear();
//                        EventBulletinArrayList.clear();
                        StudentArrayList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            Bulletin bulletin = new Bulletin();
                            JSONObject bulletinTable = (JSONObject) response.get(i);
                            String UserID = bulletinTable.getString("UserID");
                            String SendTo = bulletinTable.getString("SendTo");

                            String Name = bulletinTable.getString("Name");
                            String ICNum = bulletinTable.getString("ICNum");
                            String Contact = bulletinTable.getString("Contact");
                            String Email = bulletinTable.getString("Email");
                            String Address = bulletinTable.getString("Address");
                            String UserType = bulletinTable.getString("UserType");
                            String UserPassword = bulletinTable.getString("UserPassword");
                            String ProgrammeID = bulletinTable.getString("ProgrammeID");
                            String TutorialGroup = bulletinTable.getString("TutorialGroup");
                            String INTAKE = bulletinTable.getString("INTAKE");
                            String ProgrammeCode = bulletinTable.getString("ProgrammeCode");
                            String FacultyID = bulletinTable.getString("FacultyID");
                            Student student = new Student(UserID, Name, ICNum, Contact, Email, Address, UserType, UserPassword, ProgrammeID, TutorialGroup, INTAKE);
                            StudentArrayList.add(student);
                            String SendTo1 = "";
                            String SendTo2 = "";
                            if (SendTo.length() == 3) {
                                SendTo = SendTo;
                            }else if (SendTo.length() == 4) {
                                SendTo1 = SendTo.substring(0, 4);
                            } else if (SendTo.length() == 7) {
                                SendTo1 = SendTo.substring(0, 4);
                                SendTo2 = SendTo.substring(0, 7);
                            }

                            String studentInfo1 = FacultyID + ProgrammeCode;
                            String studentInfo2 = FacultyID + ProgrammeCode + INTAKE;
//                            if (SendTo.length() == 3)
//                            Toast.makeText(context,SendTo.length(),Toast.LENGTH_SHORT);
                            if (UserID.equalsIgnoreCase(SaveSharedPreference.getUserID(getActivity().getApplicationContext()))) {
                                if (SendTo.equalsIgnoreCase("ALL") || SendTo.equalsIgnoreCase(studentInfo2) || SendTo2.equalsIgnoreCase(studentInfo1) || SendTo1.equalsIgnoreCase(FacultyID) ) {

                                    BulletinDetail bulletinDetail = new BulletinDetail();
//                                    EventRegistration eventRegistration = new EventRegistration();
//                                    EventBulletin eventBulletin = new EventBulletin();

                                    bulletin.setBulletinTitle(bulletinTable.getString("Title"));
                                    bulletin.setDescription(bulletinTable.getString("Remark"));

                                    String dateStr = bulletinTable.getString("PostDate");
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    Date PostDate = sdf.parse(dateStr);
                                    bulletin.setPostDate(PostDate);

                                    bulletin.setPostedBy(bulletinTable.getString("PostedBy"));
                                    bulletin.setSendTo(SendTo);
                                    bulletin.setBulletinID(bulletinTable.getString("BulletinID"));
                                    bulletin.setEventBulletinID(bulletinTable.getString("EventBulletinID"));
                                    bulletin.setEventRegistrationID(bulletinTable.getString("EventRegistrationID"));

                                    bulletinDetail.setBookmarkStatus(bulletinTable.getInt("BookmarkStatus"));
                                    bulletinDetail.setReadStatus(bulletinTable.getInt("ReadStatus"));
                                    bulletinDetail.setDeleteStatus(bulletinTable.getInt("DeleteStatus"));


//                                    String dateRegistrationStartDate = bulletinTable.getString("RegistrationStartDate");
//                                    SimpleDateFormat sdfRegistrationStartDate = new SimpleDateFormat("yyyy-MM-dd");
//                                    Date RegistrationStartDate = sdfRegistrationStartDate.parse(dateRegistrationStartDate);
//
//                                    String dateRegistrationDueDate = bulletinTable.getString("RegistrationDueDate");
//                                    SimpleDateFormat sdfRegistrationDueDate = new SimpleDateFormat("yyyy-MM-dd");
//                                    Date RegistrationDueDate = sdfRegistrationDueDate.parse(dateRegistrationDueDate);
//
//
//                                    String dateEventStartDate = bulletinTable.getString("EventStartDate");
//                                    SimpleDateFormat sdfEventStartDate = new SimpleDateFormat("yyyy-MM-dd");
//                                    Date EventStartDate = sdfEventStartDate.parse(dateEventStartDate);
//
//                                    String dateEventDueDate = bulletinTable.getString("EventDueDate");
//                                    SimpleDateFormat sdfEventDueDate = new SimpleDateFormat("yyyy-MM-dd");
//                                    Date EventDueDate = sdfEventDueDate.parse(dateEventDueDate);
//
//                                    String datePaymentDueDate = bulletinTable.getString("PaymentDueDate");
//                                    SimpleDateFormat sdfPaymentDueDate = new SimpleDateFormat("yyyy-MM-dd");
//                                    Date PaymentDueDate = sdfPaymentDueDate.parse(datePaymentDueDate);
//
//                                    eventRegistration.setRegistrationStartDate(RegistrationStartDate);
//                                    eventRegistration.setRegistrationDueDate(RegistrationDueDate);
//                                    eventRegistration.setLINK(bulletinTable.getString("LINK"));
//
//                                    eventBulletin.setEventStartDate(EventStartDate);
//                                    eventBulletin.setEventDueDate(EventDueDate);
//                                    eventBulletin.setPaymentDueDate(PaymentDueDate);
//                                    eventBulletin.setFees(bulletinTable.getDouble("Fees"));
//                                    eventBulletin.setAttachment(bulletinTable.getString("Attachment"));


                                    BulletinArrayList.add(bulletin);
                                    BulletinDetailArrayList.add(bulletinDetail);
//                                    EventRegistrationArrayList.add(eventRegistration);
//                                    EventBulletinArrayList.add(eventBulletin);
                                }
                            }

                        }

                        recyclerView.setAdapter(bulletinAdapter);
                        mSwipeRefreshLayout.setRefreshing(false);

                    } catch (Exception e) {
                        Toast.makeText(getActivity().getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                volleyError -> Toast.makeText(getActivity().getApplicationContext(), "Error: " + volleyError.getMessage(), Toast.LENGTH_LONG).show());

        // Set the tag on the request.
        jsonObjectRequest.setTag(TAG);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }
    private void sortByPostBy() {
        try {
            mqttHelper.publish("SC_BULLETIN_SORTBY_POSTBY,"+SaveSharedPreference.getUserID(getContext())+",MY/TARUC/BBS/00005008");

        } catch (MqttException e) {
            e.printStackTrace();
        }
        mqttHelper.subscribeToTopic("MY/TARUC/BBS/00005008");

    }
    private void sortByPostByDesc() {
        try {
            mqttHelper.publish("SC_BULLETIN_SORTBY_POSTBYDESC,"+SaveSharedPreference.getUserID(getContext())+",MY/TARUC/BBS/00005009");

        } catch (MqttException e) {
            e.printStackTrace();
        }
        mqttHelper.subscribeToTopic("MY/TARUC/BBS/00005009");
    }
    private void sortByTitle() {
        try {
            mqttHelper.publish("SC_BULLETIN_SORTBY_TITLE,"+SaveSharedPreference.getUserID(getContext())+",MY/TARUC/BBS/00005010");

        } catch (MqttException e) {
            e.printStackTrace();
        }
        mqttHelper.subscribeToTopic("MY/TARUC/BBS/00005010");
    }
    private void sortByTitleDesc() {
        try {
            mqttHelper.publish("SC_BULLETIN_SORTBY_TITLEDESC,"+SaveSharedPreference.getUserID(getContext())+",MY/TARUC/BBS/00005011");

        } catch (MqttException e) {
            e.printStackTrace();
        }
        mqttHelper.subscribeToTopic("MY/TARUC/BBS/00005011");
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        mqttHelper.disconnect();
//    }
    @Override
    public void onPause() {
        super.onPause();
        mqttHelper.disconnect();
    }
    @Override
    public void onResume() {
        super.onResume();
//        spinner.setSelection(0);
        mSwipeRefreshLayout.setRefreshing(true);
        startMqtt(getContext());
    }
}
