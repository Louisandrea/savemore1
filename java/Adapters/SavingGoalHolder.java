package Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.louisa.savemore.R;

import Models.SavingGoals;


public class SavingGoalHolder extends RecyclerView.ViewHolder {
    View mView;
    Context mContext;

    //Connect with RecyclerView in Home Page
    public SavingGoalHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = mView.getContext();
    }//End of SavingGoalHolder method

    //Set content for RecyclerView
    public void setContent(SavingGoals savingGoals) {
        TextView description = (TextView) mView.findViewById(R.id.description);
        TextView amountToShare = (TextView) mView.findViewById(R.id.product_amount);
        TextView receiverEmail = (TextView) mView.findViewById(R.id.receiver);

        description.setText(savingGoals.getDescription());
        amountToShare.setText(String.valueOf(savingGoals.getRemainAmount()));
        receiverEmail.setText(savingGoals.getEmail());

    }//End of setContent method

}