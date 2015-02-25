package com.timeofneedSOS;

import android.app.FragmentTransaction;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Sam on 8/9/13.
 */
public class ContactView extends Fragment {
    ImageView removeContact, ContactImage;
    TextView Name, Number;
    public static final String CONTACT_URI = "Contact_Uri";
    public static final String ID = "ID";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.contact_layout, container , false);
        init(v);

        Uri ContactUri = Uri.parse(getArguments().getString(CONTACT_URI));

        fillUpDetails(ContactUri);
        return v;
    }

    private void init(View v) {
        removeContact = (ImageView) v.findViewById(R.id.RemoveContact);
        ContactImage = (ImageView) v.findViewById(R.id.ContactImage);

        Name = (TextView) v.findViewById(R.id.ContactName);
        Number = (TextView) v.findViewById(R.id.ContactNumber);

        Typeface Avant = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Avant.ttf");
        Name.setTypeface(Avant);
        Number.setTypeface(Avant);

        removeContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RemoveContact();
            }
        });
    }

    private void fillUpDetails(Uri uri) {
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};

        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        cursor.moveToFirst();

        int Name_Column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        String Contact_name = cursor.getString(Name_Column);
        Name.setText(Contact_name);

        int Number_Column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        String Contact_Number = cursor.getString(Number_Column);
        Number.setText(Contact_Number);
    }

    private void RemoveContact() {
        String ContactUri = getArguments().getString(CONTACT_URI);
        SQLiteDatabase db = new DBHelper(getActivity()).getWritableDatabase();
        db.execSQL("Delete from " + DBHelper.TABLE_NAME + " Where " + DBHelper.COLUMN_NUMBERS_URI + " = '" + ContactUri + "';");
        db.close();

        Fragment thisFrag = getFragmentManager().findFragmentByTag(Integer.toString(getArguments().getInt(ID)));
        FragmentTransaction transA = getFragmentManager().beginTransaction();
        transA.remove(thisFrag);
        transA.commit();
    }
}
