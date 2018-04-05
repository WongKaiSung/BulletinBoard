//package my.edu.tarc.bulletinboard.Fragment;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.SearchView;
//import android.widget.Spinner;
//import android.widget.Toast;
//
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonArrayRequest;
//import com.android.volley.toolbox.Volley;
//
//import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
//import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
//import org.eclipse.paho.client.mqttv3.MqttException;
//import org.eclipse.paho.client.mqttv3.MqttMessage;
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Date;
//
//import my.edu.tarc.bulletinboard.Adapter.BookmarkAdapter;
//import my.edu.tarc.bulletinboard.Class.Bulletin;
//import my.edu.tarc.bulletinboard.Class.BulletinDetail;
//import my.edu.tarc.bulletinboard.Class.EventBulletin;
//import my.edu.tarc.bulletinboard.Class.EventRegistration;
//import my.edu.tarc.bulletinboard.Class.Student;
//import my.edu.tarc.bulletinboard.MQTT.MqttHelper;
//import my.edu.tarc.bulletinboard.R;
//import my.edu.tarc.bulletinboard.SaveSharedPreference;
//
///**
// * Created by but on 17/1/2018.
// */
//
//public class BookmarkFragment extends android.support.v4.app.Fragment{
//    private static final String TAG = "BookmarkFragment";
////    private static String GET_BOOK_URL = "https://cash-on-wise.000webhostapp.com/Bulletin/BookmarkedBulletin.php";
//    ArrayList<Bulletin> BookmarkedBulletinArrayList = new ArrayList<Bulletin>();
//    ArrayList<BulletinDetail> BookmarkedBulletinDetail = new ArrayList<BulletinDetail>();
//    ArrayList<EventRegistration> EventRegistrationArrayList = new ArrayList<EventRegistration>();
//    ArrayList<EventBulletin> EventBulletinArrayList = new ArrayList<EventBulletin>();
//    ArrayList<Student> StudentArrayList = new ArrayList<Student>();
//
//    private ProgressDialog pDialog;
//    RecyclerView recyclerView1;
//    public static RecyclerView.Adapter adapter;
//    RequestQueue queue;
//    SwipeRefreshLayout mSwipeRefreshLayout;
//    public static BookmarkAdapter bulletinAdapter;
//
//    Spinner spinnerBookmark;
//    SearchView searchViewBookmark;
//    MqttHelper mqttHelper;
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.bookmark_fragment,container,false);
//
//        if (!isConnected()) {
//            Toast.makeText(getActivity().getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
//        }
////        startMqtt(view.getContext());
//        spinnerBookmark =  view.findViewById(R.id.spinnerBookmark);
//        searchViewBookmark = view.findViewById(R.id.searchViewBookmark);
//        searchViewBookmark.setQueryHint("Search");
//
//        spinnerBookmark.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
//                mSwipeRefreshLayout.setRefreshing(true);
//                if (position == 0) {
//                   mSwipeRefreshLayout.setRefreshing(false);
//                }else if (position == 1) {
//                    startMqtt(getContext());
//                }else if (position == 2) {
//                    sortByTitle();
//                    bulletinAdapter.notifyDataSetChanged();
//                }else if (position == 3){
//                    sortByTitleDesc();
//                    bulletinAdapter.notifyDataSetChanged();
//                }else if (position == 4) {
//                    sortByPostBy();
//                    bulletinAdapter.notifyDataSetChanged();
//                }else if (position == 5){
//                    sortByPostByDesc();
//                    bulletinAdapter.notifyDataSetChanged();
//                }
//                bulletinAdapter.notifyDataSetChanged();
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//        searchViewBookmark.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                spinnerBookmark.setVisibility(view.GONE);
//                newText = newText.toLowerCase();
//                ArrayList<Bulletin> newList = new ArrayList<>();
//                for(Bulletin bulletin : BookmarkedBulletinArrayList){
//                    String Title = bulletin.getBulletinTitle().toLowerCase();
//                    String Description = bulletin.getDescription().toLowerCase();
//
//                    if (Title.contains(newText) || Description.contains(newText)){
//                        newList.add(bulletin);
//                    }
//                }
//                bulletinAdapter.setFilter(newList);
//
//                return true;
//            }
//        });
//        searchViewBookmark.setOnCloseListener(() -> {
//            spinnerBookmark.setSelection(0);
//            spinnerBookmark.setVisibility(view.VISIBLE);
//            return false;
//        });
//        setupSort();
////        downloadBookmarkedBulletin(getActivity().getApplicationContext(), GET_BOOK_URL);
//        bulletinAdapter = new BookmarkAdapter(BookmarkedBulletinArrayList,BookmarkedBulletinDetail,EventRegistrationArrayList,EventBulletinArrayList,this.getActivity());
//        recyclerView1 =  view.findViewById(R.id.recyclerview1);
//        recyclerView1.setLayoutManager(new LinearLayoutManager(this.getActivity()));
//        //recyclerView.setAdapter(new BulletinAdapter(this.getActivity(),Items,Desc));
//        mSwipeRefreshLayout = view.findViewById(R.id.swipeContainer1);
//        mSwipeRefreshLayout.setOnRefreshListener(() -> {
//            requestData();
//            mSwipeRefreshLayout.setRefreshing(true);
//        });
//        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
//                R.color.colorBlue1,
//                R.color.colorBlue2,
//                R.color.colorBlue3
//        );
//
//        /**
//         * Showing Swipe Refresh animation on activity create
//         * As animation won't start on onCreate, post runnable is used
//         */
//        mSwipeRefreshLayout.post(() -> {
//            mSwipeRefreshLayout.setRefreshing(true);
//        });
//        return view;
//    }
//    private void sortByPostBy() {
//        try {
//            mqttHelper.publish("SC_BULLETINBOOKMARK_SORTBY_POSTBY,"+SaveSharedPreference.getUserID(getContext())+",MY/TARUC/BBS/00005012");
//
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
//        mqttHelper.subscribeToTopic("MY/TARUC/BBS/00005012");
//
//    }
//    private void sortByPostByDesc() {
//        try {
//            mqttHelper.publish("SC_BULLETINBOOKMARK_SORTBY_POSTBYDESC,"+SaveSharedPreference.getUserID(getContext())+",MY/TARUC/BBS/00005013");
//
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
//        mqttHelper.subscribeToTopic("MY/TARUC/BBS/00005013");
//    }
//    private void sortByTitle() {
//        try {
//            mqttHelper.publish("SC_BULLETINBOOKMARK_SORTBY_TITLE,"+SaveSharedPreference.getUserID(getContext())+",MY/TARUC/BBS/00005014");
//
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
//        mqttHelper.subscribeToTopic("MY/TARUC/BBS/00005014");
//    }
//    private void sortByTitleDesc() {
//        try {
//            mqttHelper.publish("SC_BULLETINBOOKMARK_SORTBY_TITLEDESC,"+SaveSharedPreference.getUserID(getContext())+",MY/TARUC/BBS/00005015");
//
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
//        mqttHelper.subscribeToTopic("MY/TARUC/BBS/00005015");
//    }
//    private void startMqtt(final Context context) {
//        mqttHelper = new MqttHelper(getContext());
//        mqttHelper.setCallback(new MqttCallbackExtended() {
//            @Override
//            public void connectComplete(boolean b, String s) {
//                requestData();
////                Toast.makeText(getContext(),"Connected to MQTT",Toast.LENGTH_SHORT).show();
//
//            }
//
//            @Override
//            public void connectionLost(Throwable throwable) {
//
//            }
//
//            @Override
//            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
//                Log.w("Debug", mqttMessage.toString());
////                setMessageNotification("New Bulletin", new String(mqttMessage.getPayload()));
////                onRefresh();
//                loadBulletinData(mqttMessage.toString(),getContext());
//
//            }
//
//            @Override
//            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
//
//            }
//        });
//    }
//    private void requestData() {
//        try {
//            mqttHelper.publish("SC_BULLETIN_GET_BOOKMARKEDBULLETINS,"+SaveSharedPreference.getUserID(getContext())+",MY/TARUC/BBS/00005002");
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
//        mqttHelper.subscribeToTopic("MY/TARUC/BBS/00005002");
//    }
//    private void loadBulletinData(String data, Context context) throws Exception {
//
//        String dataFromMQTT = data;
//        String[] dataAfterProcess = dataFromMQTT.split(",");
//
//        String topicFromMQTT = dataAfterProcess[0];
//        String msgFromMQTT = dataAfterProcess[1];
//        String idFromMQTT = dataAfterProcess[2];
//
//        if ((SaveSharedPreference.getUserID(getContext()).equalsIgnoreCase(idFromMQTT))) {
//            if (topicFromMQTT.equalsIgnoreCase("MY/TARUC/BBS/00005002")) {
//                downloadBookmarkedBulletin(getActivity().getApplicationContext(), msgFromMQTT);
//                mqttHelper.unSubscribeToTopic(topicFromMQTT);
//            }else if (topicFromMQTT.equalsIgnoreCase("MY/TARUC/BBS/00005012")) {
//                downloadBookmarkedBulletin(context, msgFromMQTT);
//                mqttHelper.unSubscribeToTopic(topicFromMQTT);
//            }else if (topicFromMQTT.equalsIgnoreCase("MY/TARUC/BBS/00005013")) {
//                downloadBookmarkedBulletin(context, msgFromMQTT);
//                mqttHelper.unSubscribeToTopic(topicFromMQTT);
//            }else if (topicFromMQTT.equalsIgnoreCase("MY/TARUC/BBS/00005014")) {
//                downloadBookmarkedBulletin(context, msgFromMQTT);
//                mqttHelper.unSubscribeToTopic(topicFromMQTT);
//            }else if (topicFromMQTT.equalsIgnoreCase("MY/TARUC/BBS/00005015")) {
//                downloadBookmarkedBulletin(context, msgFromMQTT);
//                mqttHelper.unSubscribeToTopic(topicFromMQTT);
//            }
//        }
//
//    }
//    private void setupSort() {
//        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
//                getContext(),
//                R.array.sort_types,
//                android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerBookmark.setAdapter(adapter);
//
//
//    }
//    private void downloadBookmarkedBulletin(final Context context, String url) {
//        // Instantiate the RequestQueue
//        queue = Volley.newRequestQueue(context);
//
//        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
//                url,
//                response -> {
//                    try {
//
//                        BookmarkedBulletinArrayList.clear();
//                        BookmarkedBulletinDetail.clear();
//                        EventRegistrationArrayList.clear();
//                        EventBulletinArrayList.clear();
//                        StudentArrayList.clear();
//                        for (int i = 0; i < response.length(); i++) {
//
//                            JSONObject bulletinTable = (JSONObject) response.get(i);
//                            String UserID = bulletinTable.getString("UserID");
//                            String SendTo = bulletinTable.getString("SendTo");
//
//                            String Name = bulletinTable.getString("Name");
//                            String ICNum = bulletinTable.getString("ICNum");
//                            String Contact = bulletinTable.getString("Contact");
//                            String Email = bulletinTable.getString("Email");
//                            String Address = bulletinTable.getString("Address");
//                            String UserType = bulletinTable.getString("UserType");
//                            String UserPassword = bulletinTable.getString("UserPassword");
//                            String ProgrammeID = bulletinTable.getString("ProgrammeID");
//                            String TutorialGroup = bulletinTable.getString("TutorialGroup");
//                            String INTAKE = bulletinTable.getString("INTAKE");
//                            String ProgrammeCode = bulletinTable.getString("ProgrammeCode");
//                            String FacultyID = bulletinTable.getString("FacultyID");
//                            Student student = new Student(UserID, Name, ICNum, Contact, Email, Address, UserType, UserPassword, ProgrammeID, TutorialGroup, INTAKE);
//                            StudentArrayList.add(student);
//                            String SendTo1 = "";
//                            String SendTo2 = "";
//                            if (SendTo.length() == 4) {
//                                SendTo1 = SendTo.substring(0, 4);
//                            } else if (SendTo.length() == 7) {
//                                SendTo1 = SendTo.substring(0, 4);
//                                SendTo2 = SendTo.substring(0, 7);
//                            }
//
//                            String studentInfo1 = FacultyID + ProgrammeCode;
//                            String studentInfo2 = FacultyID + ProgrammeCode + INTAKE;
////                            if (SendTo.length() == 3)
////                            Toast.makeText(context,SendTo.length(),Toast.LENGTH_SHORT);
//                            if (UserID.equalsIgnoreCase(SaveSharedPreference.getUserID(getActivity().getApplicationContext()))) {
//                                if (SendTo.equalsIgnoreCase(studentInfo2) || SendTo2.equalsIgnoreCase(studentInfo1) || SendTo1.equalsIgnoreCase(FacultyID) || SendTo.equalsIgnoreCase("All")) {
//                                    Bulletin bulletin = new Bulletin();
//                                    BulletinDetail bulletinDetail = new BulletinDetail();
////                                    EventRegistration eventRegistration = new EventRegistration();
////                                    EventBulletin eventBulletin = new EventBulletin();
//
//                                    bulletin.setBulletinTitle(bulletinTable.getString("Title"));
//                                    bulletin.setDescription(bulletinTable.getString("Remark"));
//
//                                    String dateStr = bulletinTable.getString("PostDate");
//                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//                                    Date PostDate = sdf.parse(dateStr);
//                                    bulletin.setPostDate(PostDate);
//
//                                    bulletin.setPostedBy(bulletinTable.getString("PostedBy"));
//                                    bulletin.setSendTo(bulletinTable.getString("SendTo"));
//                                    bulletin.setBulletinID(bulletinTable.getString("BulletinID"));
//                                    bulletin.setEventBulletinID(bulletinTable.getString("EventBulletinID"));
//                                    bulletin.setEventRegistrationID(bulletinTable.getString("EventRegistrationID"));
//
//                                    bulletinDetail.setBookmarkStatus(bulletinTable.getInt("BookmarkStatus"));
//                                    bulletinDetail.setReadStatus(bulletinTable.getInt("ReadStatus"));
//                                    bulletinDetail.setDeleteStatus(bulletinTable.getInt("DeleteStatus"));
//
////                                    String dateRegistrationStartDate = bulletinTable.getString("RegistrationStartDate");
////                                    SimpleDateFormat sdfRegistrationStartDate = new SimpleDateFormat("yyyy-MM-dd");
////                                    Date RegistrationStartDate = sdfRegistrationStartDate.parse(dateRegistrationStartDate);
////
////                                    String dateRegistrationDueDate = bulletinTable.getString("RegistrationDueDate");
////                                    SimpleDateFormat sdfRegistrationDueDate = new SimpleDateFormat("yyyy-MM-dd");
////                                    Date RegistrationDueDate = sdfRegistrationDueDate.parse(dateRegistrationDueDate);
////
////
////                                    String dateEventStartDate = bulletinTable.getString("EventStartDate");
////                                    SimpleDateFormat sdfEventStartDate = new SimpleDateFormat("yyyy-MM-dd");
////                                    Date EventStartDate = sdfEventStartDate.parse(dateEventStartDate);
////
////                                    String dateEventDueDate = bulletinTable.getString("EventDueDate");
////                                    SimpleDateFormat sdfEventDueDate = new SimpleDateFormat("yyyy-MM-dd");
////                                    Date EventDueDate = sdfEventDueDate.parse(dateEventDueDate);
////
////                                    String datePaymentDueDate = bulletinTable.getString("PaymentDueDate");
////                                    SimpleDateFormat sdfPaymentDueDate = new SimpleDateFormat("yyyy-MM-dd");
////                                    Date PaymentDueDate = sdfPaymentDueDate.parse(datePaymentDueDate);
////
////                                    eventRegistration.setRegistrationStartDate(RegistrationStartDate);
////                                    eventRegistration.setRegistrationDueDate(RegistrationDueDate);
////                                    eventRegistration.setLINK(bulletinTable.getString("LINK"));
////
////                                    eventBulletin.setEventStartDate(EventStartDate);
////                                    eventBulletin.setEventDueDate(EventDueDate);
////                                    eventBulletin.setPaymentDueDate(PaymentDueDate);
////                                    eventBulletin.setFees(bulletinTable.getDouble("Fees"));
////                                    eventBulletin.setAttachment(bulletinTable.getString("Attachment"));
//
//
//                                    BookmarkedBulletinArrayList.add(bulletin);
//                                    BookmarkedBulletinDetail.add(bulletinDetail);
////                                    EventRegistrationArrayList.add(eventRegistration);
////                                    EventBulletinArrayList.add(eventBulletin);
//                                }
//                            }
//
//                        }
//
//                        recyclerView1.setAdapter(bulletinAdapter);
//                        mSwipeRefreshLayout.setRefreshing(false);
//
//                    } catch (Exception e) {
//                        Toast.makeText(getActivity().getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                    }
//                },
//                volleyError -> Toast.makeText(getActivity().getApplicationContext(), "Error: " + volleyError.getMessage(), Toast.LENGTH_LONG).show());
//
//        // Set the tag on the request.
//        jsonObjectRequest.setTag(TAG);
//
//        // Add the request to the RequestQueue.
//        queue.add(jsonObjectRequest);
//    }
//    private boolean isConnected() {
//        ConnectivityManager cm =
//                (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
//
//    }
//    @Override
//    public void onResume()
//    {  // After a pause OR at startup
//        super.onResume();
//        mSwipeRefreshLayout.setRefreshing(true);
//        startMqtt(getContext());
//        //Refresh your stuff here
//    }
//
//}
