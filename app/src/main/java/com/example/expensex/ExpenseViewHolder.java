package com.example.expensex;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ExpenseViewHolder extends RecyclerView.ViewHolder {
    public TextView typeView, amountView, dateView;
    ImageView popupBtn;
    public ExpenseViewHolder(@NonNull View itemView) {
        super(itemView);
        typeView = itemView.findViewById(R.id.expenseTypeTV);
        amountView = itemView.findViewById(R.id.expenseAmountTV);
        dateView = itemView.findViewById(R.id.expenseDateTV);
        popupBtn = itemView.findViewById(R.id.popupMenuBtn);

    }
}
