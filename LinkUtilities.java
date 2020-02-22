package crawler;

public class LinkUtilities {
    enum LinkType {
        Absolute,
        Relative,
        RelativeWithSlash,
        WithoutProtocol,
        WithoutProtocolWithoutSlashes
    }

    public static String makeAbsoluteLink(StringBuilder input, String currentURL) {
        LinkType linkType = getType(input.toString());
        StringBuilder result = new StringBuilder("");
        switch (linkType) {
            case Absolute:
                result.append(input);
                break;
            case Relative:
                result.append(currentURL.replaceAll("(?<=[^/])/[^ /]+", ""));
                if (!currentURL.endsWith("/")) {
                    result.append("/");
                }
                result.append(input);
                break;
            case RelativeWithSlash:
                result.append(currentURL);
                if (currentURL.endsWith("/")) {
                    input.deleteCharAt(0);
                }
                result.append(input);
                break;
            case WithoutProtocol:
                if (currentURL.startsWith("https")) {
                    result.append("https:");
                } else {
                    result.append("http:");
                }
                result.append(input);
                break;
            case WithoutProtocolWithoutSlashes:
                if (currentURL.startsWith("https")) {
                    result.append("https://");
                } else {
                    result.append("http://");
                }
                result.append(input);
                break;
        }
        return result.toString();
    }

    public static LinkType getType(String input) {
        if (input.startsWith("http")) {
            return LinkType.Absolute;
        } else if (input.startsWith("//")) {
            return LinkType.WithoutProtocol;
        } else if (input.startsWith("/")) {
            return LinkType.RelativeWithSlash;
        } else if (!input.contains("/")) {
            return LinkType.Relative;
        } else {
            return LinkType.WithoutProtocolWithoutSlashes;
        }
    }
}
