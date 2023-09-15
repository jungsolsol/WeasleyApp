package sol.server.core.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    private String locationName;

    private Point locationPoint; // 장소 좌표

    private String locationType; //장소 타입

    @Builder
    public UserLocation(User user, String locationName, Point locationPoint, String locationType) {
        this.user = user;
        this.locationName = locationName;
        this.locationPoint = locationPoint;
        this.locationType = locationType;
    }

}
