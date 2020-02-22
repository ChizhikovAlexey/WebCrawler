package crawler;

public class URLAndDepth {
    private String url;
    private int depth;

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getUrl() {
        return url;
    }

    public int getDepth() {
        return depth;
    }

    public URLAndDepth(String url, int depth) {
        this.url = url;
        this.depth = depth;
    }

    @Override
    public String toString() {
        return "URLAndDepth{" +
                "url='" + url + '\'' +
                ", depth=" + depth +
                '}';
    }
}
