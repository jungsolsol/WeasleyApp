package sol.server.core.entity.dto;

import lombok.*;
import org.springframework.data.geo.Point;
import sol.server.core.entity.User;
import sol.server.core.entity.UserLocation;

@Getter
@Setter
@Builder
public class LocRequestDto {
    @NonNull
    private String latitude;
    @NonNull
    private String longitude;
    private String locationName;
    private User user;
//    @User로 분리
//    private String userKey; /**사용자키**/
//    private String authKey; /**제품키**/


    public static UserLocation toUserLocation(Point point, LocRequestDto dto,String locationType) {

        UserLocation userLocation = UserLocation.builder().
                locationType(locationType)
                .user(dto.getUser())
                .locationName(dto.getLocationName())
                .locationPoint(point).build();
        return userLocation;
    }

}
