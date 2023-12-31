package com.example.expensex.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.expensex.DateValidator;
import com.example.expensex.DbHelper;
import com.example.expensex.R;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateExpenseFragment extends Fragment {

    private Spinner typeSpinner;
    private EditText expenseAmountET, expenseDateET, expenseTimeET;
    private Button updateExpenseBtn, cancelExpenseBtn;
    private ImageView datePickerBtn, timePickerBtn;
    private DbHelper helper;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
    private String typeOfExpense;
    private Context context;

    private Calendar calendar;
    private int hour, minute;

    private int selectposition = 0;

    //get Bundle
    String rId = "";
    String rDate = "";
    String rTime = "";
    String rAmount = "";

    public UpdateExpenseFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_expense, container, false);

        context = container.getContext();
        calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);

        init(view);
        process(view);

        return view;
    }

    private void init(View view) {

        typeSpinner = view.findViewById(R.id.typeSpinner);
        expenseAmountET = view.findViewById(R.id.expenseAmountET);
        expenseDateET = view.findViewById(R.id.expenseDateET);
        expenseTimeET = view.findViewById(R.id.expenseTimeET);
        updateExpenseBtn = view.findViewById(R.id.updateExpenseBtn);
        datePickerBtn = view.findViewById(R.id.datePickerBtn);
        timePickerBtn = view.findViewById(R.id.timePickerBtn);
        helper = new DbHelper(context);
        cancelExpenseBtn = view.findViewById(R.id.cancelExpenseBtn);
    }

    private void process(View view) {

        Bundle bundle = getArguments();

        if (bundle != null) {
            rId = bundle.getString("id");
            rDate = bundle.getString("date");
            rAmount = bundle.getString("amount");
            rTime = bundle.getString("time");
            typeOfExpense = bundle.getString("type");
        }

        expenseDateET.setText(rDate);
        expenseTimeET.setText(rTime);
        expenseAmountET.setText(rAmount);

        final String[] typeExpense = { "Food", "Tour", "Medicine", "Net Bill", "Transport", "Shopping", "Gift"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_activated_1, typeExpense);
        typeSpinner.setAdapter(arrayAdapter);

        for (int i = 0; i < typeExpense.length; i++) {
            if (typeOfExpense.equals(typeExpense[i])) {
                selectposition = i;
            }
        }

        typeSpinner.setSelection(selectposition);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeOfExpense = typeExpense[position];
                //Toast.makeText(getApplicationContext(),typeOfExpense+" is selected",Toast.LENGTH_SHORT).show();
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

                        String fdate = dateFormat.format(d);
                        expenseDateET.setText(fdate);
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
                String usertime = timeFormat.format(calendar.getTime());
                expenseTimeET.setText(usertime);
            }
        };

        timePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog timePickerDialog = new TimePickerDialog(context, timePick, hour, minute, false);
                timePickerDialog.show();
            }
        });

        updateExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uTime = expenseTimeET.getText().toString();
                String amountGet = expenseAmountET.getText().toString();
                String userDate = expenseDateET.getText().toString();
                DateValidator dv = new DateValidator();
                dv.matcher = Pattern.compile(dv.DATE_PATTERN).matcher(userDate);
                if (amountGet.equals("") || userDate.equals("")) {
                    if (amountGet.equals("")) {
                        expenseAmountET.setError("please enter  amount first");
                        expenseAmountET.requestFocus();
                    } else if (userDate.equals("")) {
                        expenseDateET.setError("please enter date first");
                        expenseDateET.requestFocus();
                    }
                } else if (!dv.matcher.matches()) {
                    expenseDateET.setError("Enter a valid Date format : yyyy/MM/dd");
                    expenseDateET.requestFocus();
                    Toast.makeText(context, "please select date from calender button", Toast.LENGTH_LONG).show();
                } else {
                    Date d = null;
                    try {
                        d = dateFormat.parse(userDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long mdate = d.getTime();

                    int uAmount = Integer.parseInt(amountGet);
                    helper.update(rId, typeOfExpense, uAmount, mdate, uTime);
                    Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show();

                    //finish();

                    ExpenseFragment expenseFragment = new ExpenseFragment();
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = manager.beginTransaction();
                    ft.replace(R.id.frameLayoutID, expenseFragment);
                    ft.addToBackStack(null);
                    ft.commit();
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

}
