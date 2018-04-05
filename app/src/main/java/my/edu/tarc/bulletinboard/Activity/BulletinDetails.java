package my.edu.tarc.bulletinboard.Activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import my.edu.tarc.bulletinboard.Class.Bulletin;
import my.edu.tarc.bulletinboard.Class.BulletinDetail;
import my.edu.tarc.bulletinboard.Class.EventBulletin;
import my.edu.tarc.bulletinboard.Class.EventRegistration;
import my.edu.tarc.bulletinboard.Class.Student;
import my.edu.tarc.bulletinboard.MQTT.MqttHelper;
import my.edu.tarc.bulletinboard.R;
import my.edu.tarc.bulletinboard.SaveSharedPreference;

public class BulletinDetails extends AppCompatActivity {
    public TextView textViewTitle,textViewDetails,textViewBy,textViewTo,textViewLink,DetailTittle,textViewEventStartDate,textViewEventDueDate,textViewPaymentDueDate,textViewRegistrationStartDate,textViewRegistrationDueDate,textViewFees;
    private Toolbar toolbar;
    RequestQueue queue;
    public ImageButton imageButtonDetail;
//    private static String Post_Bookmark_URL = "https://cash-on-wise.000webhostapp.com/Bulletin/Bookmark.php";
//    private static String Post_unBookmark_URL = "https://cash-on-wise.000webhostapp.com/Bulletin/unBookmark.php";
//    private static String Post_DEL_URL = "https://cash-on-wise.000webhostapp.com/Bulletin/HideBulletin.php";
//    private static String Post_Read_URL = "https://cash-on-wise.000webhostapp.com/Bulletin/ReadBulletin.php";
    private Bulletin bulletin;
    private static String URL = "https://rayl2c96.000webhostapp.com/KaiSung//Event.php";

    private BulletinDetail bulletinDetail;
    private EventRegistration EventRegistration;
    private EventBulletin  EventBulletinArray;
    private int bookmarkStatus,readStatus;
    public String bulletinID,EventRegistrationID,EventBulletinID;
    EventRegistration eventRegistration = new EventRegistration();
    EventBulletin eventBulletin = new EventBulletin();
    public ImageView imageViewPic;
    MqttHelper mqttHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulletin_details);
        bulletin = (Bulletin) getIntent().getSerializableExtra("bulletin");
        bulletinDetail = (BulletinDetail) getIntent().getSerializableExtra("bulletinDetail");
//        eventBulletin = (EventBulletin) getIntent().getSerializableExtra("eventBulletin");
//        eventRegistration = (EventRegistration) getIntent().getSerializableExtra("eventRegistration");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        imageButtonDetail = findViewById(R.id.imageButtonDetail);
        imageButtonDetail.setOnClickListener(v -> {
            finish();
            mqttHelper.disconnect();
        });

        DetailTittle =  findViewById(R.id.DetailTittle);
        textViewTitle =  findViewById(R.id.textViewTitle);
        textViewDetails =  findViewById(R.id.textViewDetails);
        textViewBy =  findViewById(R.id.textViewBy);
        textViewTo =  findViewById(R.id.textViewTo);
        textViewLink =  findViewById(R.id.textViewLink);

        textViewEventDueDate =  findViewById(R.id.textViewEventDueDate);
        textViewEventStartDate =  findViewById(R.id.textViewEventStartDate);
        textViewPaymentDueDate =  findViewById(R.id.textViewPaymentDueDate);

        textViewRegistrationStartDate =  findViewById(R.id.textViewRegistrationStartDate);
        textViewRegistrationDueDate =  findViewById(R.id.textViewRegistrationDueDate);
        textViewFees = findViewById(R.id.textViewFees);

        imageViewPic = findViewById(R.id.imageViewPic);
        imageViewPic.setVisibility(View.GONE);
        bulletinID = bulletin.getBulletinID();
        textViewTitle.setText(bulletin.getBulletinTitle());
        DetailTittle.setText(bulletin.getBulletinTitle());
        textViewDetails.setText(bulletin.getDescription());
        textViewBy.setText("Posted By : " + bulletin.getPostedBy());
        textViewTo.setText("To : " + bulletin.getSendTo());

        bookmarkStatus = bulletinDetail.getBookmarkStatus();
        readStatus = bulletinDetail.getReadStatus();
        EventBulletinID = bulletin.getEventBulletinID();
        EventRegistrationID = bulletin.getEventRegistrationID();

        textViewRegistrationDueDate.setVisibility(View.GONE);
        textViewRegistrationStartDate.setVisibility(View.GONE);
        textViewFees.setVisibility(View.GONE);

        textViewLink.setVisibility(View.GONE);
        textViewEventStartDate.setVisibility(View.GONE);
        textViewEventDueDate.setVisibility(View.GONE);
        textViewPaymentDueDate.setVisibility(View.GONE);

        startMqtt(this);


    }

    private void requestReadStatusLink() {
        try {
            mqttHelper.publish("SC_BULLETIN_GET_READ,"+SaveSharedPreference.getUserID(getApplication())+",MY/TARUC/BBS/00005003");

        } catch (MqttException e) {
            e.printStackTrace();
        }
        mqttHelper.subscribeToTopic("MY/TARUC/BBS/00005003");
    }
    private void requestDeleteLink() {
        try {
            mqttHelper.publish("SC_BULLETIN_DELETE,"+SaveSharedPreference.getUserID(getApplication())+",MY/TARUC/BBS/00005004");

        } catch (MqttException e) {
            e.printStackTrace();
        }
        mqttHelper.subscribeToTopic("MY/TARUC/BBS/00005004");
    }
    private void requestBookmarkLink() {
        try {
            mqttHelper.publish("SC_BULLETIN_BOOKMARK,"+SaveSharedPreference.getUserID(getApplication())+",MY/TARUC/BBS/00005005");

        } catch (MqttException e) {
            e.printStackTrace();
        }
//        mqttHelper.subscribeToTopic("MY/TARUC/BBS/00005005");
    }
    private void requestUnMarkLink() {
        try {
            mqttHelper.publish("SC_BULLETIN_UNMARK,"+SaveSharedPreference.getUserID(getApplication())+",MY/TARUC/BBS/00005006");
        } catch (MqttException e) {
            e.printStackTrace();
        }
        mqttHelper.subscribeToTopic("MY/TARUC/BBS/00005006");
    }
    private void requestEventDetail() {
        try {
            mqttHelper.publish("SC_BULLETIN_EVENT_DETAIL,"+SaveSharedPreference.getUserID(getApplication())+",MY/TARUC/BBS/00005017");
        } catch (MqttException e) {
            e.printStackTrace();
        }
        mqttHelper.subscribeToTopic("MY/TARUC/BBS/00005017");
    }
    private void downloadBulletin(final Context context, String url) {
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                url,
                response -> {
                    try {


                        for (int i = 0; i < response.length(); i++) {

                            JSONObject bulletinTable = (JSONObject) response.get(i);
                            eventBulletin.setEventBulletinID(bulletinTable.getString("EventBulletinID"));
                            eventRegistration.setEventRegistrationID(bulletinTable.getString("EventRegistrationID"));

                            if ((bulletin.getEventBulletinID().equalsIgnoreCase(eventBulletin.getEventBulletinID())   )) {


                                    String dateEventStartDate = bulletinTable.getString("EventStartDate");
                                    SimpleDateFormat sdfEventStartDate = new SimpleDateFormat("yyyy-MM-dd");
                                    Date EventStartDate = sdfEventStartDate.parse(dateEventStartDate);

                                    String dateEventDueDate = bulletinTable.getString("EventDueDate");
                                    SimpleDateFormat sdfEventDueDate = new SimpleDateFormat("yyyy-MM-dd");
                                    Date EventDueDate = sdfEventDueDate.parse(dateEventDueDate);

                                    String datePaymentDueDate = bulletinTable.getString("PaymentDueDate");
                                    SimpleDateFormat sdfPaymentDueDate = new SimpleDateFormat("yyyy-MM-dd");
                                    Date PaymentDueDate = sdfPaymentDueDate.parse(datePaymentDueDate);


                                    eventBulletin.setEventStartDate(EventStartDate);
                                    eventBulletin.setEventDueDate(EventDueDate);
                                    eventBulletin.setPaymentDueDate(PaymentDueDate);
                                    eventBulletin.setFees(bulletinTable.getDouble("Fees"));
                                    eventBulletin.setAttachment(bulletinTable.getString("Attachment"));

//                                    EventRegistrationArrayList.add(eventRegistration);
//                                    EventBulletinArrayList.add(eventBulletin);
                                }
                                if(bulletin.getEventRegistrationID().equalsIgnoreCase(eventRegistration.getEventRegistrationID())){

                                    String dateRegistrationStartDate = bulletinTable.getString("RegistrationStartDate");
                                    SimpleDateFormat sdfRegistrationStartDate = new SimpleDateFormat("yyyy-MM-dd");
                                    Date RegistrationStartDate = sdfRegistrationStartDate.parse(dateRegistrationStartDate);

                                    String dateRegistrationDueDate = bulletinTable.getString("RegistrationDueDate");
                                    SimpleDateFormat sdfRegistrationDueDate = new SimpleDateFormat("yyyy-MM-dd");
                                    Date RegistrationDueDate = sdfRegistrationDueDate.parse(dateRegistrationDueDate);
                                    eventRegistration.setRegistrationStartDate(RegistrationStartDate);
                                    eventRegistration.setRegistrationDueDate(RegistrationDueDate);
                                    eventRegistration.setLINK(bulletinTable.getString("LINK"));

                            }
                            if (EventRegistrationID.length() > 5 && EventBulletinID.length() > 5) {
                                String link = eventRegistration.getLINK();
                                Date RegistrationStartDate =  eventRegistration.getRegistrationStartDate();
                                Date RegistrationDueDate =  eventRegistration.getRegistrationDueDate();
                                Date EventStartDate =  eventBulletin.getEventStartDate();
                                Date EventDueDate =  eventBulletin.getEventDueDate();
                                Date PaymentDueDate = eventBulletin.getPaymentDueDate();
                                double fees = eventBulletin.getFees();

                                textViewRegistrationStartDate.setVisibility(View.VISIBLE);
                                textViewRegistrationDueDate.setVisibility(View.VISIBLE);
                                textViewFees.setVisibility(View.VISIBLE);

                                textViewLink.setVisibility(View.VISIBLE);
                                textViewEventStartDate.setVisibility(View.VISIBLE);
                                textViewEventDueDate.setVisibility(View.VISIBLE);
                                textViewPaymentDueDate.setVisibility(View.VISIBLE);
                                imageViewPic.setVisibility(View.VISIBLE);

                                textViewEventStartDate.setText(String.format("Event Start Date : %1$tY %1$tb %1$td", EventStartDate));
                                textViewEventDueDate.setText(String.format("Event Due Date : %1$tY %1$tb %1$td", EventDueDate));
                                textViewPaymentDueDate.setText(String.format("Payment Due Date : %1$tY %1$tb %1$td", PaymentDueDate));
                                textViewFees.setText("Fees : "+ fees);

                                textViewRegistrationStartDate.setText(String.format("Registration Due Date : %1$tY %1$tb %1$td", RegistrationStartDate));
                                textViewRegistrationDueDate.setText(String.format("Registration Start Date : %1$tY %1$tb %1$td", RegistrationDueDate));
                                textViewLink.setText("Click here to register : " + link);
                                imageViewPic.setImageBitmap(StringToBitMap(eventBulletin.getAttachment()));


                            }else if (EventRegistrationID.length() > 5 && EventBulletinID.length() < 5) {
                                String link = eventRegistration.getLINK();
                                Date StartDate =  eventRegistration.getRegistrationStartDate();
                                Date DueDate =  eventRegistration.getRegistrationDueDate();

                                textViewLink.setVisibility(View.VISIBLE);
                                textViewRegistrationStartDate.setVisibility(View.VISIBLE);
                                textViewRegistrationDueDate.setVisibility(View.VISIBLE);

                                textViewLink.setText("Click here to register: " + link);
                                textViewRegistrationStartDate.setText(String.format("Registration Start Date : %1$tY %1$tb %1$td", StartDate));
                                textViewRegistrationDueDate.setText(String.format("Registration Due Date : %1$tY %1$tb %1$td", DueDate));

                            }else if (EventRegistrationID.length() < 5 && EventBulletinID.length() > 5) {
                                double Fees = eventBulletin.getFees();
                                Date StartDate = eventBulletin.getEventStartDate();
                                Date DueDate = eventBulletin.getEventDueDate();
                                Date PaymentDueDate = eventBulletin.getPaymentDueDate();
                                textViewFees.setVisibility(View.VISIBLE);
                                textViewEventStartDate.setVisibility(View.VISIBLE);
                                textViewEventDueDate.setVisibility(View.VISIBLE);
                                textViewPaymentDueDate.setVisibility(View.VISIBLE);
                                    imageViewPic.setVisibility(View.VISIBLE);
                                    imageViewPic.setImageBitmap(StringToBitMap(eventBulletin.getAttachment()));

                                textViewFees.setText("Fees : RM " + Fees);
                                textViewEventStartDate.setText(String.format("Event Start Date : %1$tY %1$tb %1$td", StartDate));
                                textViewEventDueDate.setText(String.format("Event Due Date : %1$tY %1$tb %1$td", DueDate));
                                textViewPaymentDueDate.setText(String.format("Payment Due Date : %1$tY %1$tb %1$td", PaymentDueDate));

                            }

                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                volleyError -> Toast.makeText(getApplicationContext(), "Error: " + volleyError.getMessage(), Toast.LENGTH_LONG).show());

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }
    public void getReplyMessage(String data, Context context) throws Exception{
        String dataFromMQTT = data;
        String[] dataAfterProcess = dataFromMQTT.split(",");

        String topicFromMQTT = dataAfterProcess[0];//topic
        String msgFromMQTT = dataAfterProcess[1];//link
        String idFromMQTT = dataAfterProcess[2];//student ID
//        JSONObject jsonMsg = new JSONObject(data.toString());

        //only looking for receive bulletin command
        if ((SaveSharedPreference.getUserID(context).equals(idFromMQTT))) {
            if (topicFromMQTT.equalsIgnoreCase("MY/TARUC/BBS/00005003")) {//change read status
                makeServiceCall(this, msgFromMQTT, SaveSharedPreference.getUserID(this), bulletinID);
                mqttHelper.unSubscribeToTopic(topicFromMQTT);
            }else if (topicFromMQTT.equalsIgnoreCase("MY/TARUC/BBS/00005004")){//delete bulletin
                makeServiceCall(this, msgFromMQTT, SaveSharedPreference.getUserID(this), bulletinID);
                mqttHelper.unSubscribeToTopic(topicFromMQTT);
            }else if (topicFromMQTT.equalsIgnoreCase("MY/TARUC/BBS/00005005")){//bookmark bulletin
                makeServiceCall(this, msgFromMQTT, SaveSharedPreference.getUserID(this), bulletinID);
                Toast.makeText(context, "Bookmarked!", Toast.LENGTH_SHORT).show();
                mqttHelper.unSubscribeToTopic(topicFromMQTT);
            }else if (topicFromMQTT.equalsIgnoreCase("MY/TARUC/BBS/00005006")){//unmark bulletin
                makeServiceCall(this, msgFromMQTT, SaveSharedPreference.getUserID(this), bulletinID);
                Toast.makeText(context, "Removed bookmark!", Toast.LENGTH_SHORT).show();
                mqttHelper.unSubscribeToTopic(topicFromMQTT);
            }else if (topicFromMQTT.equalsIgnoreCase("MY/TARUC/BBS/00005017")){//load bulletin
                Toast.makeText(context, "Loading, Please wait!", Toast.LENGTH_SHORT).show();
                downloadBulletin(getApplicationContext(),msgFromMQTT);
//                Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
                mqttHelper.unSubscribeToTopic(topicFromMQTT);
            }
        }
//        if ((SaveSharedPreference.getUserID(context).equals(idFromMQTT))) {
//            if (topicFromMQTT.equalsIgnoreCase("SC_BULLETIN_GET_READ")) {//change read status
//                makeServiceCall(this, msgFromMQTT, SaveSharedPreference.getUserID(this), bulletinID);
//            }else if (topicFromMQTT.equalsIgnoreCase("SC_BULLETIN_DELETE")){//delete bulletin
//                makeServiceCall(this, msgFromMQTT, SaveSharedPreference.getUserID(this), bulletinID);
//            }else if (topicFromMQTT.equalsIgnoreCase("SC_BULLETIN_BOOKMARK")){//bookmark bulletin
//                makeServiceCall(this, msgFromMQTT, SaveSharedPreference.getUserID(this), bulletinID);
//                Toast.makeText(context, "Bookmarked!", Toast.LENGTH_SHORT).show();
//            }else if (topicFromMQTT.equalsIgnoreCase("SC_BULLETIN_UNMARK")){//unmark bulletin
//                makeServiceCall(this, msgFromMQTT, SaveSharedPreference.getUserID(this), bulletinID);
//                Toast.makeText(context, "Removed bookmark!", Toast.LENGTH_SHORT).show();
//            }else if (topicFromMQTT.equalsIgnoreCase("SC_BULLETIN_EVENT_DETAIL")){//load bulletin
//                Toast.makeText(context, "Loading, Please wait!", Toast.LENGTH_SHORT).show();
//                downloadBulletin(getApplicationContext(),msgFromMQTT);
//                Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
//            }
//        }
    }
    private void startMqtt(final Context context) {
        mqttHelper = new MqttHelper(getApplicationContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
//                Toast.makeText(getApplicationContext(),"Connected to MQTT",Toast.LENGTH_SHORT).show();
                if (readStatus == 0){
                    requestReadStatusLink();

                }
                requestEventDetail();
            }

            @Override
            public void connectionLost(Throwable throwable) {
//                Toast.makeText(getApplicationContext(),"Lost",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debug", mqttMessage.toString());
                getReplyMessage(mqttMessage.toString(),context);

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        if(bookmarkStatus == 1) {
            toolbar.getMenu().clear();
            menuInflater.inflate(R.menu.bulletin_detail_bookmark,menu);
        }else {
            toolbar.getMenu().clear();
            menuInflater.inflate(R.menu.bulletin_detail_menu,menu);

        }


        return super.onCreateOptionsMenu(menu);
    }
    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mqttHelper.disconnect();
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_Bookmark){
            requestBookmarkLink();
//                makeServiceCall(this, Post_Bookmark_URL, SaveSharedPreference.getUserID(this), bulletinID);

        }else if (id == R.id.action_Delete) {

                new AlertDialog.Builder(this)
                        .setTitle("Delete Bulletin")
                        .setMessage("Are you sure want to delete this bulletin?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
//                            makeServiceCall(getApplicationContext(), Post_DEL_URL, SaveSharedPreference.getUserID(getApplication()), bulletinID);
//                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                            getApplicationContext().startActivity(intent);
                            requestDeleteLink();
                            finish();
                        })
                        .setNegativeButton(android.R.string.no, null).show();

        }else if(id == R.id.action_unBookmark){
            requestUnMarkLink();
//            makeServiceCall(this, Post_unBookmark_URL, SaveSharedPreference.getUserID(this), bulletinID);

        }
        return super.onOptionsItemSelected(item);
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
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
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

//    @Override
//    public void onPause() {
//        super.onPause();
//        mqttHelper.disconnect();
//    }
}
