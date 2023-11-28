package com.example.expensex.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import com.example.expensex.DateValidator;
import com.example.expensex.DbHelper;
import com.example.expensex.R;

public class AddExpenseFragment extends Fragment  {

    private Spinner typeSpinner;
    private Context context;
    private EditText expenseAmountET, expenseDateET, expenseTimeET;
    private Button addExpenseBtn, cancelExpenseBtn;
    private ImageView datePickerBtn, timePickerBtn;

    private Calendar calendar;
    private int hour, minute;

    private String typeOfExpense;
    private DbHelper helper;

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
    public AddExpenseFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_expense, container, false);
        context = container.getContext();

        calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);

        init(view);
        process(view);

        return view;
    }

    private void process(View view) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.add_expense_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);


        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeOfExpense = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        datePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                View view = getLayoutInflater().inflate(R.layout.custom_date_picker, null);

                Button done = view.findViewById(R.id.doneButton);
                final DatePicker datePicker = view.findViewById(R.id.datePicker);
                builder.setView(view);
                final Dialog dialog = builder.create();
                dialog.show();
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int day = datePicker.getDayOfMonth();
                        int month = datePicker.getMonth();
                        month = month + 1;
                        int year = datePicker.getYear();

                        String cDate = year + "/" + month + "/" + day;
                        Date d = null;
                        try {
                            d = dateFormat.parse(cDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String dataFormat = dateFormat.format(d);
                        expenseDateET.setText(dataFormat);
                        dialog.dismiss();
                    }
                });
            }
        });

        final TimePickerDialog.OnTimeSetListener timePick = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                Time time = new Time(hour, minute, 0);
                calendar.setTime(time);
                String usertime = null;
                try {
                    usertime = timeFormat.format(calendar.getTime());
                } catch (Exception e) {
                    Toast.makeText(context, "Please take the time first : " + e, Toast.LENGTH_SHORT).show();
                }
                expenseTimeET.setText(usertime);
            }
        };

        timePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(context, timePick, hour, minute, false);
                timePickerDialog.updateTime(hour, minute);
                timePickerDialog.show();
            }
        });

        addExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getdate = expenseDateET.getText().toString();
                DateValidator dv = new DateValidator();
                dv.matcher = Pattern.compile(dv.DATE_PATTERN).matcher(getdate);
                if (expenseAmountET.getText().toString().equals("") || expenseDateET.getText().toString().equals("")) {
                    if (expenseAmountET.getText().toString().equals("")) {
                        expenseAmountET.setError("Please enter amount");
                        expenseAmountET.requestFocus();
                    } else if (expenseDateET.getText().toString().equals("")) {
                        expenseDateET.setError("Please enter date from date picker");
                        expenseDateET.requestFocus();
                    }
                } else if (!dv.matcher.matches()) {
                    expenseDateET.setError("Enter a valid Date format : yyyy/MM/dd");
                    expenseDateET.requestFocus();
                    Toast.makeText(context, "Wrong Date format according to : yyyy/MM/dd", Toast.LENGTH_LONG).show();
                } else {
                    int uamount = Integer.valueOf(expenseAmountET.getText().toString());
                    String userDate = expenseDateET.getText().toString();
                    Date d = null;
                    try {
                        d = dateFormat.parse(userDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long mdate = d.getTime();
                    String userTime = expenseTimeET.getText().toString();

                    long id = helper.insertData(typeOfExpense, uamount, mdate, userTime);

                    if (id == -1) {
                        Toast.makeText(context, "Error : Data  can not Inserted.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Expense Data : " + id + " is Inserted.", Toast.LENGTH_SHORT).show();

                        ExpenseFragment expenseFragment = new ExpenseFragment();
                        FragmentManager manager = getActivity().getSupportFragmentManager();
                        FragmentTransaction ft = manager.beginTransaction();
                        ft.replace(R.id.frameLayoutID, expenseFragment);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                }
            }
        });

        cancelExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpenseFragment expenseFragment = new ExpenseFragment();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                ft.replace(R.id.frameLayoutID, expenseFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }

    private void init(View view) {

        typeSpinner = view.findViewById(R.id.addTypeSpinner);
        expenseAmountET = view.findViewById(R.id.expenseAmountET);
        expenseDateET = view.findViewById(R.id.expenseDateET);
        expenseTimeET = view.findViewById(R.id.expenseTimeET);
        addExpenseBtn = view.findViewById(R.id.addExpenseBtn);
        datePickerBtn = view.findViewById(R.id.datePickerBtn);
        timePickerBtn = view.findViewById(R.id.timePickerBtn);
        cancelExpenseBtn = view.findViewById(R.id.cancelExpenseBtn);

        helper = new DbHelper(context);
    }

}