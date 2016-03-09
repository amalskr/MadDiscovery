package amal.com.maddiscovery;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import amal.com.maddiscovery.db.DatabaseHandler;
import amal.com.maddiscovery.model.MadEvent;
import amal.com.maddiscovery.utill.EventUtil;

public class EventAddActivity extends AppCompatActivity implements OnMapReadyCallback {

    public final static String APP_PATH_SD_CARD = "/MadEvent/";
    public final static String APP_THUMBNAIL_PATH_SD_CARD = "thumbnails";
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private EditText event_name;
    private EditText event_date;
    private EditText event_time;
    private EditText event_organizer;
    private EditText event_location;
    private boolean isGoodValue = true;
    private ImageView eventIv;
    private EventUtil eventUtil;

    private GoogleMap mMap;

    private static final int INITIAL_REQUEST = 1987;
    private static final String[] EVENT_PERMS = {Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_add);

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        event_name = (EditText) findViewById(R.id.evtname);
        event_date = (EditText) findViewById(R.id.evtDate);
        event_time = (EditText) findViewById(R.id.evtTime);
        event_organizer = (EditText) findViewById(R.id.evtOrga);
        event_location = (EditText) findViewById(R.id.evtLocation);
        eventIv = (ImageView) findViewById(R.id.eventIv);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        eventUtil = new EventUtil();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //get Request permissions to OPEN CAMERA AND GPS
            if (!canAccessLocation() || !canAccessCamera() || !canAccessStorage()) {
                requestPermissions(EVENT_PERMS, INITIAL_REQUEST);
            }
        }

    }

    private boolean canAccessStorage() {
        return (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }

    private boolean canAccessCamera() {
        return (hasPermission(Manifest.permission.CAMERA));
    }

    private boolean canAccessLocation() {
        return (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    private boolean hasPermission(String perm) {
        return (PackageManager.PERMISSION_GRANTED == checkSelfPermission(perm));
    }

    public void showMapInAddress(View view) {
        getLocationAddress(event_location.getText().toString());

        //hide keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(event_location.getWindowToken(), 0);
    }


    public void setEventDate(View view) {
        Calendar cal = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(this, datePickerListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));

        datePicker.setCancelable(false);
        datePicker.setTitle("Select the Date");
        datePicker.show();
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            String YY = String.valueOf(selectedYear);
            String MM = String.valueOf(selectedMonth + 1);
            String DD = String.valueOf(selectedDay);
            event_date.setText(MM + "-" + DD + "-" + YY);
        }
    };

    public void setEventTime(View view) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(EventAddActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                event_time.setText(selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select the Time");
        mTimePicker.show();
    }

    // take a pic on
    public void getEventPicture(View view) {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (canAccessCamera()) {
                if (canAccessStorage()) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
                } else {
                    eventUtil.showToast(getApplicationContext(), "Sorry, you have been deny access save image in your device from this.");
                }

            } else {
                eventUtil.showToast(getApplicationContext(), "Sorry, you have been deny the camera access from this.");
            }
        } else {
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            saveImage(imageBitmap);
            eventIv.setImageBitmap(imageBitmap);
        }
    }

    public boolean saveImage(Bitmap image) {
        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "mad_event_" + timeStamp + ".png";

        try {
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            OutputStream fOut = null;
            File file = new File(fullPath, imageFileName);
            file.createNewFile();
            fOut = new FileOutputStream(file);

            image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();

            MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
            EventUtil.eventPicPath = file.getAbsolutePath();
            return true;

        } catch (Exception e) {
            Log.e("saveToExternalStorage()", e.getMessage());
            new EventUtil().showToast(getApplicationContext(), "Can't store photo in your device. try again!");
            return false;
        }
    }
    // take a pic end

    private void saveEvent() {

        if (event_name.getText().toString().length() == 0) {
            event_name.setError("First name is required!");
            isGoodValue = false;
        }

        if (event_date.getText().toString().length() == 0) {
            event_date.setError("MadEvent Date is required!");
            isGoodValue = false;
        }

        if (event_organizer.getText().toString().length() == 0) {
            event_organizer.setError("Organizer is required!");
            isGoodValue = false;
        }

        if (isGoodValue) {
            DatabaseHandler db = new DatabaseHandler(this);

            MadEvent madEvent = new MadEvent();
            madEvent.setEventName(event_name.getText().toString());
            madEvent.setEventDate(event_date.getText().toString());
            madEvent.setEventTime(event_time.getText().toString());
            madEvent.setEventOrganizer(event_organizer.getText().toString());
            madEvent.setEventLocation(event_location.getText().toString());
            madEvent.setEventPicturePath(EventUtil.eventPicPath);
            madEvent.setIsEventEnd("NO");

            if (db.isDuplicateEvent(madEvent)) {
                eventUtil.showToast(getApplicationContext(), "Sorry, Already added. A duplicate event");
            } else {
                long stautsId = db.addMadEvent(madEvent);

                if (stautsId != -1) {
                    eventUtil.showToast(getApplicationContext(), "Success");
                    EventUtil.eventPicPath = "NONE";

                    finish();
                } else {
                    eventUtil.showToast(getApplicationContext(), "Faild");
                }
            }


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_event_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.add_event:
                saveEvent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(getAddressMap(latLng))
                        .draggable(true));
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(getAddressMap(latLng))
                        .draggable(true));
            }
        });

        //Google Map option
        GoogleMapOptions mapOptions = new GoogleMapOptions();
        mapOptions.mapType(GoogleMap.MAP_TYPE_NORMAL)
                .compassEnabled(false)
                .rotateGesturesEnabled(false)
                .tiltGesturesEnabled(false)
                .liteMode(true);

        LatLng defaultPoint = new LatLng(EventUtil.dft_lat, EventUtil.dft_lng);
        showMapPoint(defaultPoint, "University of Greenwich");
    }

    private void showMapPoint(LatLng defaultPoint, String title) {

        MarkerOptions markerOptions = new MarkerOptions().position(defaultPoint).title(
                title).draggable(true);
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(defaultPoint));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultPoint, 20));
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

        if (canAccessLocation()) {
            mMap.setMyLocationEnabled(true);
        }
    }

    private String getAddressMap(LatLng latLng) {
        String addr = "";
        Geocoder gc = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = gc.getFromLocation(latLng.latitude, latLng.longitude, 1);

            StringBuilder sb = new StringBuilder();

            if (addresses.size() > 0) {
                Address address = addresses.get(0);

                // for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                sb.append(address.getAddressLine(0)).append("\n");
                sb.append(address.getLocality()).append("\n");
                sb.append(address.getPostalCode()).append("\n");
                sb.append(address.getCountryName());
                //}
            }

            addr = sb.toString();

        } catch (Exception e) {
            addr = "address not found";
        }

        event_location.setText(addr);

        return addr;
    }

    public void getLocationAddress(String address) {
        System.out.println(address);
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
                eventUtil.showToast(getApplicationContext(), "Sorry address not found");
            }

        } catch (Exception e) {
            latLng = new LatLng(EventUtil.dft_lat, EventUtil.dft_lng);
            eventUtil.showToast(getApplicationContext(), "Service not avaliable");

            System.out.println("Error get location " + e.toString());
        }
    }
}
