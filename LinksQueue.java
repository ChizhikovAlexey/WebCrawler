package crawler;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Thread-safe queue of tasks for crawler
 * 3 methods: add (Pair<String, Integer>), add(String, Integer), get() to work with the queue
 * clear() – to clear the queue
 */

public class LinksQueue {
    public static boolean work = true;
    public static ConcurrentLinkedDeque<URLAndDepth> linksQueue = new ConcurrentLinkedDeque<>();

    public static void clear() {
        linksQueue.clear();
    }
}
