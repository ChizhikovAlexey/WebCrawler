package crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static crawler.LinksAndTitles.linksAndTitles;

public class Crawler implements Runnable {
    private String url = null;
    private String title = null;
    private String HTMLCode = null;
    private int depth = 0;
    private static int maxDepth = Integer.MAX_VALUE;

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0";
    private URLConnection connection;

    Crawler(int maxDepth) {
        Crawler.maxDepth = maxDepth;
    }

    private void setConnection() {
        try {
            this.connection = new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", USER_AGENT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Crawler(String url, int maxDepth) throws IOException {
        linksAndTitles.clear();
        this.url = url;
        Crawler.maxDepth = maxDepth;
        setHTMLCode();
        setTitle();
        pushData();
        collectLinks();
    }

    private void collectLinks() {
        Matcher matcher = Pattern.compile("<a[^>]*href=[\"'][^\\s\"']*[\"']").matcher(HTMLCode);
        while (matcher.find()) {
            StringBuilder linkBuilder = new StringBuilder(matcher.group());
            linkBuilder.delete(0, linkBuilder.indexOf("href=") + 6);
            linkBuilder.deleteCharAt(linkBuilder.length() - 1);
            String link = LinkUtilities.makeAbsoluteLink(linkBuilder, url);
            if (!linksAndTitles.containsKey(link)) {
                LinksQueue.linksQueue.offerLast(new URLAndDepth(link, depth + 1));
            }
        }
    }

    private void setHTMLCode() throws IOException {
        final InputStream inputStream;
        setConnection();
        if (connection != null) {
            inputStream = connection.getInputStream();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            final StringBuilder stringBuilder = new StringBuilder();
            String nextLine;
            while ((nextLine = reader.readLine()) != null) {
                stringBuilder.append(nextLine);
                stringBuilder.append('\n');
            }
            HTMLCode = stringBuilder.toString();
        } else {
            HTMLCode = null;
        }
    }

    private void setTitle() {
        if (HTMLCode != null) {
            Pattern pattern = Pattern.compile("(?<=<title>).*(?=</title>)");
            Matcher matcher = pattern.matcher(HTMLCode);
            title = matcher.find() ? matcher.group() : "-";
        } else {
            title = null;
        }

    }

    private void pushData() {
        if (url != null && title != null && !linksAndTitles.containsKey(url)) {
            linksAndTitles.put(url, title);
            WebCrawler.updateCounter();
        }
    }

    @Override
    //Start crawling
    public void run() {
        int mistakes = 0;
        while (!LinksQueue.linksQueue.isEmpty() || LinksQueue.work) {
            try {
                URLAndDepth tmp = LinksQueue.linksQueue.pollFirst();
                if (tmp != null) {
                    mistakes = 0;
                    url = tmp.getUrl();
                    depth = tmp.getDepth();
                    if (depth <= maxDepth) {
                        setHTMLCode();
                        setTitle();
                        pushData();
                        if (depth < maxDepth) {
                            collectLinks();
                        }
                    }
                } else {
                    Thread.sleep(200);
                    if (++mistakes >= 5) {
                        break;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
