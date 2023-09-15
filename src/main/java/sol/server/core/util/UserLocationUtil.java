package sol.server.core.util;


import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Point;
import sol.server.core.entity.User;
import sol.server.core.entity.UserLocation;
import sol.server.core.entity.dto.LocRequestDto;

@Slf4j
public class UserLocationUtil {
    public static UserLocation addLocationField(LocRequestDto dto, User user, String locationType) {
        dto.setUser(user);
        return toField(dto,locationType);

    }



    private static UserLocation toField(LocRequestDto dto, String locationType) {
        Point p = new Point(Double.parseDouble(dto.getLatitude()), Double.parseDouble(dto.getLongitude()));
        return LocRequestDto.toUserLocation(p, dto,locationType);
    }
}
