package com.example.expensex;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensex.fragments.UpdateExpenseFragment;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseViewHolder> {

    private Context context;
    private List<Expense> expenseList;
    private DbHelper helper;
    private View getView;

    public ExpenseAdapter(Context context, List<Expense> expenseList, DbHelper helper) {
        this.context = context;
        this.expenseList = expenseList;
        this.helper = helper;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.expense_model_design, parent, false);
        getView = view;
        return new ExpenseViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final Expense currentExpense = expenseList.get(position);
        holder.typeView.setText(currentExpense.getType());
        holder.dateView.setText(currentExpense.getDate());
        holder.amountView.setText("" + currentExpense.getAmount());

        holder.popupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu menu = new PopupMenu(context, v);
                menu.getMenuInflater().inflate(R.menu.update_delete, menu.getMenu());
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.deleteItem) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Are you sure to delete ?");
                                builder.setCancelable(false);
                                builder.setIcon(R.drawable.ic_delete_forever_black_24dp);

                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                                    @SuppressLint("NotifyDataSetChanged")
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        helper = new DbHelper(context);
                                        helper.deleteData(currentExpense.getId());
                                        expenseList.remove(position);
                                        notifyDataSetChanged();
                                        dialog.cancel();
                                    }
                                });

                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                builder.create();
                                builder.show();


//                            case R.id.updateItem:
//                                requestUpdate(currentExpense);
//                                break;

                        } else if (id == R.id.updateItem){
                            expenseUpdate(currentExpense);
                        }

                        return true;
                    }
                });
                menu.show();
            }
        });

    }

    private void expenseUpdate(Expense currentExpense) {

        UpdateExpenseFragment updateExpenseFragment = new UpdateExpenseFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", currentExpense.getType());
        bundle.putString("id", String.valueOf(currentExpense.getId()));
        bundle.putString("date", currentExpense.getDate());
        bundle.putString("time", currentExpense.getTime());
        bundle.putString("amount", String.valueOf(currentExpense.getAmount()));
        updateExpenseFragment.setArguments(bundle);

        AppCompatActivity activity = (AppCompatActivity) getView.getContext();
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutID, updateExpenseFragment).addToBackStack(null).commit();
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }
}
