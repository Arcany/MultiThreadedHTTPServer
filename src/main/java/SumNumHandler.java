import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SumNumHandler extends HttpServlet {
    private List<Long> numbers = new ArrayList<>();
    private volatile Long total = 0L;
    private volatile String id;

    @Override
    protected synchronized void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String data = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            if (data.contains("end")) {
                this.id = data.split(" ")[1];
                resp.setStatus(HttpServletResponse.SC_OK);
                PrintWriter out = resp.getWriter();
                this.total = this.sum();
                out.println(this.total + " " + this.id);
            } else {
                this.numbers.add(Long.parseLong(data));
                wait();
                resp.setStatus(HttpServletResponse.SC_OK);
                PrintWriter out = resp.getWriter();
                out.println(this.total + " " + this.id);
            }
            this.reset();
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(400);
        }

    }

    public synchronized Long sum() {
        Long result = 0L;
        for (Long number :
                numbers) {
            result += number;
        }
        notifyAll();
        return result;
    }

    public synchronized void reset() {
        this.numbers = new ArrayList<>();
    }
}

