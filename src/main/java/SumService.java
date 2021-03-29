import java.util.ArrayList;
import java.util.List;

public class SumService{
    private static List<Long> numbers = new ArrayList<>();
    private static volatile Long total = 0L;


    public void add(Long value){
        numbers.add(value);

    }
    public static void reset(){
        numbers = new ArrayList<>();
        total = 0L;
    }

    public Long getTotal() {
        return total;
    }
}
