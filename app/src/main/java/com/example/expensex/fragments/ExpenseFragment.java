package com.example.expensex.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import android.widget.Toast;

import com.example.expensex.DbHelper;
import com.example.expensex.Expense;
import com.example.expensex.ExpenseAdapter;
import com.example.expensex.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ExpenseFragment extends Fragment{

    private Spinner typeSpinner;
    private TextView fromDateTV, toDateTV;
    private ImageView fromDateBtn, toDateBtn;

    String[] typeExpense;
    private CharSequence charSequence;
    public String typeOfExpense, text;
    private int count = 0;
    private List<Expense> expenseList;
    private ExpenseAdapter expenseAdapter;

    private FloatingActionButton addFloatAB;
    private RecyclerView expenseRV;
    private DbHelper helper;
    private Context context;

    private Calendar calendar;
    private int year, month, fromDay, toDay, hour, minute;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");



    public ExpenseFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expense, container, false);
        context = container.getContext();

        final AppCompatImageButton popupBtn = view.findViewById(R.id.popupExpenseBtn);
        popupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, popupBtn);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        int id = item.getItemId();
                        if (id == R.id.daily) {
                            Toast.makeText(context, "Daily", Toast.LENGTH_SHORT).show();
                        } else if(id == R.id.monthly){
                            Toast.makeText(context, "Monthly", Toast.LENGTH_SHORT).show();
                            AddExpenseFragment addExpenseFragment = new AddExpenseFragment();

                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            ft.replace(R.id.frameLayoutID, addExpenseFragment);
                            ft.addToBackStack(null);
                            ft.commit();
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        init(view);
        process(view);
        Log.d("prob", "init");

        expenseRV.setLayoutManager(new LinearLayoutManager(context));
        expenseRV.setAdapter(expenseAdapter);
        try {
            getdata();
            Log.d("prob", "get data");
        } catch (ParseException e) {
            Log.d("prob", "get data parse");
//            throw new RuntimeException(e);
            e.printStackTrace();
        }

        return view;
    }

    private void process (View view){
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.expense_type, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        charSequence = adapter.getItem(0);
        typeExpense = new String[]{"All", "Food", "Tour", "Medicine", "Net Bill", "Transport", "Shopping", "Gift"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_activated_1, typeExpense);
        typeSpinner.setAdapter(arrayAdapter);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                text = parent.getItemAtPosition(position).toString();
                typeOfExpense = typeExpense[position];
                if (count > 0) {
                    try {
                        getdata();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        int indexMonth = month + 1;
        fromDay = 1;
        toDay = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);

        String nFromDate = year + "/" + indexMonth + "/" + fromDay;
        fromDateTV.setText(nFromDate);

        final DatePickerDialog.OnDateSetListener fromDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int fromDay) {
                calendar.set(year, month, fromDay);
                String userFromDate = dateFormat.format(calendar.getTime());
                // Toast.makeText(context,userFromDate+" is selected",Toast.LENGTH_LONG).show();
                fromDateTV.setText(userFromDate);
                if (count > 0) {
                    try {
                        getdata();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        fromDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, fromDateListener, year, month, fromDay);
                datePickerDialog.show();
            }
        });

        String nToDate = year + "/" + indexMonth + "/" + toDay;
        toDateTV.setText(nToDate);

        final DatePickerDialog.OnDateSetListener toDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int toDay) {
                calendar.set(year, month, toDay);
                String userFromDate = dateFormat.format(calendar.getTime());
                toDateTV.setText(userFromDate);

                if (count > 0) {
                    try {
                        getdata();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        toDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(context, toDateListener, year, month, toDay);
                datePickerDialog.show();
                if (count > 0) {
                    try {
                        getdata();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }

    @SuppressLint("NotifyDataSetChanged")
    private void getdata() throws ParseException {
        Date d1 = dateFormat.parse(fromDateTV.getText().toString());
        Date d2 = dateFormat.parse(toDateTV.getText().toString());
        assert d1 != null;
        long fromDate = d1.getTime();
        assert d2 != null;
        long toDate = d2.getTime();

        expenseList.clear();
        expenseAdapter.notifyDataSetChanged();

        Cursor cursor = helper.showAllData();
        while (cursor.moveToNext()){
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(DbHelper.COL_ID));
            @SuppressLint("Range") String timeTo = cursor.getString(cursor.getColumnIndex(DbHelper.COL_TIME));
            @SuppressLint("Range") int amount = cursor.getInt(cursor.getColumnIndex(DbHelper.COL_Amount));
            @SuppressLint("Range") String type = cursor.getString(cursor.getColumnIndex(DbHelper.COL_TYPE));

            @SuppressLint("Range") long dateFromDB =Long.parseLong(cursor.getString(cursor.getColumnIndex(DbHelper.COL_Date)));
            String dateTo = dateFormat.format(dateFromDB);
            count++;


            if((Objects.equals(typeOfExpense, "All"))&& dateFromDB >= fromDate && dateFromDB <= toDate){
                expenseList.add(new Expense(amount, id, type, dateTo, timeTo));
                expenseAdapter.notifyDataSetChanged();
            } else if ((Objects.equals(typeOfExpense, type))&& dateFromDB >= fromDate && dateFromDB <= toDate) {
                expenseList.add(new Expense(amount, id, type,dateTo,timeTo));
                expenseAdapter.notifyDataSetChanged();
             }  else {
                expenseList.clear();
                expenseAdapter.notifyDataSetChanged();
            }
        }
    }

    private void init(View view) {
        helper = new DbHelper(context);
        typeSpinner = view.findViewById(R.id.expenseTypeSpinner);
        expenseRV = view.findViewById(R.id.expenseRV);
        expenseList = new ArrayList<>();
        expenseAdapter = new ExpenseAdapter(context, expenseList, helper);
        fromDateBtn = view.findViewById(R.id.fromDateCalenderBtn);
        toDateBtn = view.findViewById(R.id.toDateCalenderBtn);
        fromDateTV = view.findViewById(R.id.fromDateTV);
        toDateTV = view.findViewById(R.id.toDateTV);
    }

}