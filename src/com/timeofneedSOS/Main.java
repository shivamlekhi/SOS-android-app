package com.timeofneedSOS;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class Main extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        setUpActionbar();
        ImageView send = (ImageView) findViewById(R.id.SendEmergency);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckAndSend();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setUpActionbar() {
        ActionBar actionBar = getActionBar();
        actionBar.setTitle("SOS");
        actionBar.setSubtitle("In Time Of Need");
    }

    private void CheckAndSend() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Confirm Sending...");

        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you have an Emergency Situation?");

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Toast.makeText(getApplicationContext(), "Emergency Message Sent", Toast.LENGTH_SHORT).show();
                GetCurrentLoaction();
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Message Not Sent", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    private void GetCurrentLoaction() {
        GPSTracker gps = new GPSTracker(getBaseContext());
        if (gps.canGetLocation) {
            gps.getLatitude(); // returns latitude
            gps.getLongitude(); // returns longitude

            String GoogleMapsLink = "https://maps.google.com/maps?q=loc:" + Double.toString(gps.getLatitude()) + "," + Double.toString(gps.getLongitude());

            // final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(GoogleMapsLink));
            // startActivity(intent);

            SharedPreferences sharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(this);

            String Message = sharedPrefs.getString("Message", getResources().getString(R.string.Emergency_message));
            String Compiled_Message = Message + "\n " + GoogleMapsLink;

            SendMessages(Compiled_Message);

        } else {
            gps.showSettingsAlert();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_open:
                Intent menu = new Intent("com.sos.Menu");
                startActivity(menu);
                break;
            default:
                break;
        }
        return true;
    }

    private void SendMessages(String Message) {
        SQLiteDatabase db = new DBHelper(this).getReadableDatabase();
        Cursor cr = db.rawQuery("Select * from " + DBHelper.TABLE_NAME, null);

        while (cr.moveToNext()) {
            Uri ContactsUri = Uri.parse(cr.getString(cr.getColumnIndex(DBHelper.COLUMN_NUMBERS_URI)));
            String Number = getNumber(ContactsUri);

            SendMessage(Number, Message);
        }
        db.close();
    }

    private String getNumber(Uri uri) {
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};

        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        cursor.moveToFirst();

        int Number_Column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        String Contact_Number = cursor.getString(Number_Column);

        return Contact_Number;
    }

    private void SendMessage(String number, String Message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number, null, Message, null, null);
    }

}
