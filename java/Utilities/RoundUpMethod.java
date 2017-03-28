package Utilities;

import java.math.BigDecimal;

//Method for round up decimal
public class RoundUpMethod {
    //Functions
    public static float roundUpMethod(float beforeRounded, int decimal, int roundUp)
    {
        BigDecimal bigDec = new BigDecimal(beforeRounded);
        BigDecimal afterRounded = bigDec.setScale(decimal, roundUp);
        return afterRounded.floatValue();
    }
}//End of class
