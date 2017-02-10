package Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.louisa.savemore.R;

import Models.SavingGoals;

/**
 * Created by Louisa on 02/02/2017.
 */
public class SavingGoalHolder extends RecyclerView.ViewHolder {
    View mView;
    Context mContext;


    public SavingGoalHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = mView.getContext();
    }


    public void setContent(SavingGoals savingGoals) {
        TextView description = (TextView) mView.findViewById(R.id.description);
        TextView amountToShare = (TextView) mView.findViewById(R.id.product_amount);
        TextView receiverEmail = (TextView) mView.findViewById(R.id.receiver);

        description.setText(savingGoals.getDescription());
        amountToShare.setText(String.valueOf(savingGoals.getGoalAmount()));
        receiverEmail.setText(savingGoals.getEmail());


    }

}