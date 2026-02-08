package itacademy.pawalert.infrastructure.location;

import itacademy.pawalert.domain.alert.model.GeographicLocation;
import org.springframework.stereotype.Service;

@Service
public class HybridLocationProvider {

    private final IpLocationService ipLocationService;
    private GeographicLocation cachedGpsLocation;

    public HybridLocationProvider( IpLocationService ipLocationService) {
        this.ipLocationService = ipLocationService;
    }

    public void setGpsLocation(GeographicLocation location) {
        this.cachedGpsLocation = location;
    }

    public GeographicLocation getCurrentLocation() {
        if (cachedGpsLocation != null) {
            return cachedGpsLocation;
        }

        return ipLocationService.getClientLocation();
    }

    public ProviderType getProviderType() {
        if (cachedGpsLocation != null) {
            return ProviderType.GPS;
        }
        if (ipLocationService.getClientLocation() != null) {
            return ProviderType.IP_GEOLOCATION;
        }
        return ProviderType.NONE;
    }
}
