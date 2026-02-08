package itacademy.pawalert.infrastructure.location;

import itacademy.pawalert.domain.alert.exception.LocationException;
import itacademy.pawalert.domain.alert.model.GeographicLocation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class IpLocationService {
    private static final String IP_API_URL = "http://ip-api.com/json/";

    public String getClientIp() {

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return null;

        HttpServletRequest request = attrs.getRequest();

        // 1. X-Forwarded-For: can contain multiples IPs
        // Format: "client, proxy1, proxy2"
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        // 2. X-Real-IP (used by nginx with proxy_set_header)
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        // 3. RemoteAddr (fallback)
        return request.getRemoteAddr();
    }

    public boolean isLocalOrPrivateIp(String ip) {
        if (ip == null) return true;

        return ip.equals("127.0.0.1") ||
                ip.equals("localhost") ||
                ip.equals("0.0.0.0") ||
                ip.equals("::1") ||
                ip.startsWith("192.168.") ||
                ip.startsWith("10.") ||
                ip.startsWith("172.16.") ||
                ip.startsWith("172.17.") ||
                ip.startsWith("172.18.") ||
                ip.startsWith("172.19.") ||
                ip.startsWith("172.20.") ||
                ip.startsWith("172.21.") ||
                ip.startsWith("172.22.") ||
                ip.startsWith("172.23.") ||
                ip.startsWith("172.24.") ||
                ip.startsWith("172.25.") ||
                ip.startsWith("172.26.") ||
                ip.startsWith("172.27.") ||
                ip.startsWith("172.28.") ||
                ip.startsWith("172.29.") ||
                ip.startsWith("172.30.") ||
                ip.startsWith("172.31.");
    }


    public GeographicLocation getLocationFromIp() {
        String ip = getClientIp();

        if (ip == null || isLocalOrPrivateIp(ip)) {
            throw new LocationException("Could not determine client IP address");
        }

        try {
            java.net.URL url = new java.net.URL(IP_API_URL + ip);

            try (java.io.BufferedReader reader =
                         new java.io.BufferedReader(
                                 new java.io.InputStreamReader(url.openStream()))) {

                String json = reader.lines()
                        .collect(java.util.stream.Collectors.joining());

                return parseApiResponse(json);
            }

        } catch (Exception e) {
            throw new LocationException("Failed to get location from IP: " + e.getMessage());

        }
    }

    private GeographicLocation parseApiResponse(String json) {
        try {
            // Verification if the API return "success"
            if (!json.contains("\"status\":\"success\"")) {
                throw new LocationException("IP geolocation API returned error");
            }

            // Extracting latitude
            int latIndex = json.indexOf("\"lat\":");
            if (latIndex == -1) {
                throw new LocationException("Could not parse latitude from API response");
            }

            int lonIndex = json.indexOf("\"lon\":");
            if (lonIndex == -1) {
                throw new LocationException("Could not parse longitude from API response");
            }

            double lat = extractDouble(json, latIndex);
            double lon = extractDouble(json, lonIndex);

            return GeographicLocation.of(lat, lon);

        } catch (LocationException e) {
            throw new LocationException("Failed to parse API response: " + e.getMessage());
        }
    }

    private double extractDouble(String json, int index) {

        int start = index + 6;
        int end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("}", start);
        return Double.parseDouble(json.substring(start, end).trim());
    }

    public GeographicLocation getClientLocation() {
        String ip = getClientIp();

        if (ip == null || isLocalOrPrivateIp(ip)) {
            return null;
        }

        return getLocationFromIp();
    }
}
