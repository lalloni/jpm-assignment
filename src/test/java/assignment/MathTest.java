package assignment;

import static java.lang.Math.pow;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Percentage.withPercentage;

import java.math.BigDecimal;

import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

@RunWith(JUnitQuickcheck.class)
public class MathTest {

    @Property
    public void testNthRoot(
        @InRange(minInt = 1) int root,
        @InRange(min = "0.001") BigDecimal radicand) {
        BigDecimal result = Math.nthRoot(root, radicand);
        assertThat(result.doubleValue())
            .isCloseTo(pow(radicand.doubleValue(), 1.0d / root), withPercentage(0.5));
    }

}
