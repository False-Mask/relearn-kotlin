import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class Test {
    public static void main(String[] args) {
        TestKt.call(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                return Unit.INSTANCE;
            }
        });
    }
}
