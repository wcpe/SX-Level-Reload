package github.saukiya.sxlevel.sql;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Bkm016
 * @since 复制日期: 2018年5月8日
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
