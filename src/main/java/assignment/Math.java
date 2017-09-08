package assignment;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;

public class Math {

    public static final MathContext CONTEXT = new MathContext(32, RoundingMode.HALF_EVEN);

    public static BigDecimal nthRoot(int root, BigDecimal radicand) {
        return new BigDecimal(ApfloatMath.root(new Apfloat(radicand, 32), root).toString());
    }


}
