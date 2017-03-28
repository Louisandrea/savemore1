package Models;

import com.firebase.client.utilities.Utilities;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class SavingGoals implements Serializable {


    String goalName;
    String email;
    String description;
    String sender_email;

    float goalAmount;
    float total_amount;
    float remainAmount =0;
    float percentage = 0;

    public SavingGoals() {

    }

    public String getGoalName() {return goalName;}

    public void setGoalName(String goalName) {this.goalName = goalName;}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public float getGoalAmount() {
        return goalAmount;
    }

    public void setGoalAmount(float goalAmount) {
        this.goalAmount = goalAmount;
    }


    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getSender_email() {
        return sender_email;
    }

    public void setSender_email(String sender_email) {
        this.sender_email = sender_email;
    }


    public float getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(float total_amount) {
        this.total_amount = total_amount;
    }

    public float getRemainAmount(){ return remainAmount; }

    public void setRemainAmount(float remainAmount) { this.remainAmount = remainAmount; }

    //Mapping values to Firebase
    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("goalName", goalName);
        result.put("email", email);
        result.put("sender_email", sender_email);
        result.put("goalAmount", goalAmount);
        result.put("description", description);
        result.put("percentage", percentage);
        result.put("remainAmount", remainAmount);
        result.put("total_amount", total_amount);
        result.put("timestamp", ServerValue.TIMESTAMP);

        return result;
    }//End of Mapping


}
