package com.timeofneedSOS;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.app.FragmentTransaction;
import android.app.Fragment;
import android.view.*;
import android.view.Menu;

public class PhoneNumbers extends Activity {
    private static final int new_contact_reqcode = 1;
    private static final String Contact_Fragment_Tag = "Contact_Fragment_Tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.numbers);
        RefreshFragment();
        setUpActionbar();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setUpActionbar() {
        ActionBar bar = getActionBar();
        bar.setTitle("Your Emergency Numbers");
        bar.setIcon(R.drawable.ic_action_persons);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.AddContact:
                // Getting Contact
                Intent intent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                intent.setType(Phone.CONTENT_TYPE);
                startActivityForResult(intent, new_contact_reqcode);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.numbers, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == new_contact_reqcode && resultCode == RESULT_OK) {
            UpdateDatabase(data.getData());
            RefreshFragment();
        }
    }

    public void RefreshFragment() {
        Fragment old_frag = getFragmentManager().findFragmentByTag(Contact_Fragment_Tag);

        FragmentTransaction trans = getFragmentManager().beginTransaction();
        if (old_frag != null) {
            trans.remove(old_frag);
            trans.commit();
        }

        FragmentTransaction transNew = getFragmentManager().beginTransaction();
        transNew.add(R.id.AddedContactsLayout, new ContactContainer(), Contact_Fragment_Tag);
        transNew.commit();
    }

    public void UpdateDatabase(Uri contactUri) {
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
 /*       Cursor Contacts = null;

        try {
            String Check_Query = "Select From numbers where Number_Uri = '" + contactUri.toString() + "'";
            Contacts = db.rawQuery(Check_Query, null);
        } catch (Exception e) {

        }
        if(Contacts.getCount() >= 1) {
            Toast.makeText(this, "Contacts Already Exists", Toast.LENGTH_SHORT);
        } else { */
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_NUMBERS_URI, contactUri.toString());
        db.insert(DBHelper.TABLE_NAME, null, values);
        //}
        db.close();
    }
}

class ContactContainer extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.contacts_container, container, false);
        ViewContacts();
        return v;
    }

    private void ViewContacts() {
        SQLiteDatabase db = new DBHelper(getActivity()).getReadableDatabase();
        Cursor cr = db.rawQuery("Select * from " + DBHelper.TABLE_NAME, null);

        FragmentTransaction trans = getFragmentManager().beginTransaction();

        while (cr.moveToNext()) {
            Fragment frag = new ContactView();
            Bundle bdl = new Bundle();
            bdl.putString(ContactView.CONTACT_URI, cr.getString(cr.getColumnIndex(DBHelper.COLUMN_NUMBERS_URI)));
            bdl.putInt(ContactView.ID, cr.getInt(cr.getColumnIndex(DBHelper.COLUMN_ID)));
            frag.setArguments(bdl);

            trans.add(R.id.ContactContainerLayout, frag, Integer.toString(cr.getInt(cr.getColumnIndex(DBHelper.COLUMN_ID))));
        }

        trans.commit();
        cr.close();
        db.close();
    }

}