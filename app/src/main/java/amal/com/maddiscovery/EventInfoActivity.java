package amal.com.maddiscovery;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import amal.com.maddiscovery.adapter.ReportsListAdapter;
import amal.com.maddiscovery.db.DatabaseHandler;
import amal.com.maddiscovery.model.EventReport;
import amal.com.maddiscovery.model.MadEvent;
import amal.com.maddiscovery.utill.EventUtil;

public class EventInfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String event_name;
    private int event_id;
    private EventUtil eventUtil;
    private List<EventReport> reportsList;
    private RecyclerView mRecyclerView;
    private ReportsListAdapter mAdapter;
    private String event_date;
    private String event_loca;
    private String event_org;
    private String event_time;
    private DatabaseHandler db;
    private boolean isStopPost;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);

        eventUtil = new EventUtil();

        MadEvent infoEvent = EventUtil.selectedEvent;
        setEventInfo(infoEvent);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(event_name);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReport(event_name);
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.reports_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        //map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.infoMap);
        mapFragment.getMapAsync(this);

        //show reports
        showAllReports();
    }

    private void showAllReports() {
        List<EventReport> dataList = new ArrayList<>();
        db = new DatabaseHandler(this);

        reportsList = db.getAllReportsForEvent(event_id);
        int dataSize = reportsList.size();

        if (dataSize == 0) {
            eventUtil.showToast(getApplicationContext(), "No Reports");
        } else {

            for (EventReport evtReport : reportsList) {
                dataList.add(evtReport);
                System.out.println("DATA " + evtReport.getMessage());
            }

            mAdapter = new ReportsListAdapter(EventInfoActivity.this, dataList);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private void addReport(String title) {
        final EditText reportEdtTxt = new EditText(this);
        reportEdtTxt.setHint("Just Stated...");
        reportEdtTxt.setImeOptions(EditorInfo.IME_ACTION_SEND);

        new AlertDialog.Builder(this)
                .setTitle("Report - " + title)
                .setView(reportEdtTxt)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String reportMsg = reportEdtTxt.getText().toString();

                        if (reportMsg.length() >= 2) {
                            sendReport(reportMsg);
                        } else {
                            eventUtil.showToast(getApplicationContext(), "Need a message to report. (Min 2 letters)");
                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }

    private void sendReport(String reportMsg) {

        if (!isStopPost) {
            db = new DatabaseHandler(this);

            DateFormat df = new SimpleDateFormat("dd-MM-yyyy, HH:mm:ss");
            String reportDate = df.format(Calendar.getInstance().getTime());

            EventReport eventReport = new EventReport();
            eventReport.setEventId(event_id);
            eventReport.setMessage(reportMsg);
            eventReport.setReportDateTime(reportDate);

            long stautsId = db.addEventReport(eventReport);

            if (stautsId != -1) {
                eventUtil.showToast(getApplicationContext(), "Added");
                showAllReports();
            } else {
                eventUtil.showToast(getApplicationContext(), "Failed!");
            }
        } else {
            showEventAlert(event_name, "Sorry, This event has been ended now. You can't post more report.");
        }

    }

    private void setEventInfo(MadEvent infoEvent) {
        ImageView header_iv = (ImageView) findViewById(R.id.headerIv);
        TextView orgname_tv = (TextView) findViewById(R.id.infoDataTv1);
        TextView dateTime_tv = (TextView) findViewById(R.id.infoDataTv2);
        TextView location_tv = (TextView) findViewById(R.id.infoDataTv3);
        final TextView yesNo_tv = (TextView) findViewById(R.id.infoDataTv4);
        final SwitchCompat event_Switch = (SwitchCompat) findViewById(R.id.eventSwitch);

        event_name = infoEvent.getEventName();
        event_date = infoEvent.getEventDate();
        event_time = infoEvent.getEventTime();
        event_loca = infoEvent.getEventLocation();
        event_org = infoEvent.getEventOrganizer();
        event_id = infoEvent.getId();

        EventUtil.selectedPicPath = infoEvent.getEventPicturePath();
        EventUtil.selectedEventName = infoEvent.getEventName();

        setPic(EventUtil.selectedPicPath, header_iv);
        orgname_tv.setText(infoEvent.getEventOrganizer().toUpperCase());
        dateTime_tv.setText(infoEvent.getEventDate() + " @ " + infoEvent.getEventTime());
        location_tv.setText(infoEvent.getEventLocation().toUpperCase());
        event_Switch.setChecked(infoEvent.getIsEventEnd().equals("YES") ? true : false);
        yesNo_tv.setText(infoEvent.getIsEventEnd());
        isStopPost = infoEvent.getIsEventEnd().equals("YES") ? true : false;

        event_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public String message;

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                MadEvent onoffEvent = new MadEvent();
                onoffEvent.setId(Integer.valueOf(event_id));
                onoffEvent.setIsEventEnd(isChecked == true ? "YES" : "NO");

                db = new DatabaseHandler(getApplicationContext());
                db.updateEvent(onoffEvent);

                if (isChecked) {
                    isStopPost = true;
                    yesNo_tv.setText("YES");
                    message = "This event is end now. You can't send report more.";
                } else {
                    isStopPost = false;
                    yesNo_tv.setText("NO");
                    message = "This event is started again. You can send report more.";
                }
                showEventAlert(event_name, message);
            }
        });
    }

    private void showEventAlert(final String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(title.toUpperCase());
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", null);
        builder.show();
    }

    private void setPic(String eventPicturePath, ImageView eImage) {

        if (!eventPicturePath.equals("NONE")) {
            File imgFile = new File(eventPicturePath);

            try {
                if (imgFile.exists()) {
                    Bitmap bmp = BitmapFactory.decodeFile(eventPicturePath);
                    eImage.setImageBitmap(bmp);
                }
            } catch (OutOfMemoryError error) {
                new EventUtil().showToast(getApplicationContext(), "Can't load image. Low mamorry");
            }

        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Google Map option
        GoogleMapOptions mapOptions = new GoogleMapOptions();
        mapOptions.mapType(GoogleMap.MAP_TYPE_NORMAL)
                .compassEnabled(false)
                .rotateGesturesEnabled(false)
                .tiltGesturesEnabled(false)
                .liteMode(true);

        getLocationAddress(event_loca);
    }

    public void getLocationAddress(String address) {

        Geocoder gc = new Geocoder(this, Locale.getDefault());
        LatLng latLng = null;

        try {
            List<Address> addresses = gc.getFromLocationName(address, 1);

            if (addresses.size() > 0) {
                double a = addresses.get(0).getLatitude();
                double b = addresses.get(0).getLongitude();
                latLng = new LatLng(a, b);

                mMap.clear();
                showMapPoint(latLng, address);

            } else {
                eventUtil.showToast(getApplicationContext(), "Sorry, Address not found");
            }

        } catch (Exception e) {
            latLng = new LatLng(EventUtil.dft_lat, EventUtil.dft_lng);
            showMapPoint(latLng, "University of Greenwich");

            eventUtil.showToast(getApplicationContext(), "Service not avaliable");
            System.out.println("Error get location " + e.toString());
        }
    }

    private void showMapPoint(LatLng defaultPoint, String title) {

        MarkerOptions markerOptions = new MarkerOptions().position(defaultPoint).title(
                title).draggable(true);
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(defaultPoint));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultPoint, 20));
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_event_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_share:
                String msg = event_name + " organized by " + event_org + ", in " + event_loca + " @ " + event_date + " " + event_time;
                new EventUtil().shareNow(EventInfoActivity.this, msg, EventUtil.selectedPicPath);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
