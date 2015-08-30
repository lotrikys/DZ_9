package ua.org.lotrik.dz_9;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.TreeSet;


public class MainActivity extends AppCompatActivity {

    private static final int PICK_RESULT = 0;
    Integer number = 0;
    ArrayAdapter<?> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button contacts = (Button)findViewById(R.id.contacts);
        Button contact = (Button)findViewById(R.id.contact);
        Button call = (Button)findViewById(R.id.call);
        final Button map = (Button)findViewById(R.id.map);

        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contacts = new Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI);
                startActivity(contacts);
            }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(pickIntent, PICK_RESULT);

            }

        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (number != 0) {
                    Intent call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                    startActivity(call);

                }
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String coordinates = "geo:48.473043, 35.026825";
                Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(coordinates));
                startActivity(mapIntent);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final Spinner spinner = (Spinner)findViewById(R.id.spinner);
        TreeSet<String> choose = new TreeSet<String>();
        String phoneNumber = "";
        TextView textView = (TextView)findViewById(R.id.textView);
        if (requestCode == PICK_RESULT && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            Cursor c = getContentResolver().query(contactUri, null, null, null, null);
            c.moveToNext();
            String name = c.getString(c.getColumnIndexOrThrow(
                    ContactsContract.Contacts.DISPLAY_NAME));
            String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phones = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                    null,
                    null);
            while (phones.moveToNext()) {
                switch (phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))) {
                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE :
                        phoneNumber += "Mobile: " + phones.getString(phones.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if (!phones.isLast()) {
                            phoneNumber += ", ";
                        }
                        choose.add(phones.getString(phones.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER)));
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME :
                        phoneNumber += "Home: " + phones.getString(phones.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if (!phones.isLast()) {
                            phoneNumber += ", ";
                        }
                        choose.add(phones.getString(phones.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER)));
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK :
                        phoneNumber += "Work: " + phones.getString(phones.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if (!phones.isLast()) {
                            phoneNumber += ", ";
                        }
                        choose.add(phones.getString(phones.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER)));
                        break;
                }
            }
            String[] phones_array = {};
            phones_array = choose.toArray(new String[choose.size()]);
            adapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, phones_array);
            textView.setText("Имя: " + name + " " + phoneNumber);
            spinner.setAdapter(adapter);
            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    number = Integer.parseInt(spinner.getSelectedItem().toString().replaceAll(" ", ""));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

}
