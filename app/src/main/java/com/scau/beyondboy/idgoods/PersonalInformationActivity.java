package com.scau.beyondboy.idgoods;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Calendar;

/**
 * 个人信息
 */
public class PersonalInformationActivity extends AppCompatActivity {
    private RelativeLayout sex;
    private RelativeLayout birthday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);
        sex = (RelativeLayout) findViewById(R.id.sex_layout);
        sex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PersonalInformationActivity.this);
                builder.setTitle("性别");
                final String[] cities = {"男","女"};
                builder.setItems(cities, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(which==0){
                        }
                        else{
                        }
                    }
                });
                builder.show();
            }
        });
        birthday = (RelativeLayout) findViewById(R.id.birthday_layout);
        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(PersonalInformationActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
    }


}
