package com.example.expensex.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.example.expensex.DbHelper;
import com.example.expensex.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;

public class DashboardFragment extends Fragment {

    private Spinner typeSpinner;
    private TextView fromDateTV, toDateTV, totalExpenseTV;
    private ImageView fromDatePickerBtn, toDatePickerBtn;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
    private String type;
    String[] typeExpense;

    private Calendar calendar;
    private int year, month, fromDay, toDay, hour, minute;
    private Context context;
    private int totalAmount = 0;
    private int count = 0;
    int currentPosition = 0;
    private long fromDate=0;
    private long toDate=9;
    private DbHelper db;


    public DashboardFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        context = container.getContext();

//        typeSpinner = view.findViewById(R.id.dashBoardTypeSpinner);
        fromDatePickerBtn = view.findViewById(R.id.dashBoardFromDateCalenderBtn);
        toDatePickerBtn = view.findViewById(R.id.dashBoardToDateCalenderBtn);
        fromDateTV = view.findViewById(R.id.dashBoardFromDateTV);
        toDateTV = view.findViewById(R.id.dashBoardToDateTV);
//        totalExpenseTV = view.findViewById(R.id.totalExpenseTV);
        // initial database
        db = new DbHelper(context);

        process(view);


        PieChart pieChart = view.findViewById(R.id.chart);
        ArrayList<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(700, "Shopping"));
        entries.add(new PieEntry(900, "Medicine"));
        entries.add(new PieEntry(1000, "Food"));

        PieDataSet pieDataSet = new PieDataSet(entries, "subjects");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);

        pieChart.getDescription().setEnabled(false);
        pieChart.animateY(1000);
//        pieChart.invalidate();

        return view;
    }

    private void process(View View) {

//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.expense_type, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        typeSpinner.setAdapter(adapter);
//        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
//                String text = parent.getItemAtPosition(position).toString();
//                Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });





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
            public void onDateSet(DatePicker datePicker, int year, int month, int fromDay) {
                calendar.set(year, month, fromDay);
                String userFromDate = dateFormat.format(calendar.getTime());
                //Toast.makeText(context,userFromDate+" is selected",Toast.LENGTH_LONG).show();
                fromDateTV.setText(userFromDate);
            }
        };
        fromDatePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, fromDateListener, year, month, fromDay);
                datePickerDialog.show();
            }
        });




        String nToDate = year + "/" + indexMonth + "/" + toDay;
        toDateTV.setText(nToDate);
        final DatePickerDialog.OnDateSetListener toDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int toDay) {
                calendar.set(year, month, toDay);
                String userFromDate = dateFormat.format(calendar.getTime());
                toDateTV.setText(userFromDate);
            }
        };
        toDatePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, toDateListener, year, month, toDay);
                datePickerDialog.show();
            }
        });






    }


}