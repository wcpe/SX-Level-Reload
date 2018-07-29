package github.saukiya.sxlevel.sql;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Saukiya
 * @since 2018年5月9日
 */

public class MySQLExecutorService {

    private static ExecutorService thread;

    public static ExecutorService getThread() {
        return thread;
    }

    public static void setupExecutorService() {
        thread = Executors.newFixedThreadPool(1);
    }

}
